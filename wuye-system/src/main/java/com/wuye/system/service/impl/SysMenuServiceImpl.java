package com.wuye.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.wuye.common.constant.Constants;
import com.wuye.common.constant.UserConstants;
import com.wuye.common.core.domain.TreeSelect;
import com.wuye.common.core.domain.entity.SysMenu;
import com.wuye.common.core.domain.entity.SysRole;
import com.wuye.common.core.text.Convert;
import com.wuye.common.exception.ServiceException;
import com.wuye.common.utils.SecurityUtils;
import com.wuye.common.utils.StringUtils;
import com.wuye.system.domain.vo.MetaVo;
import com.wuye.system.domain.vo.RouterVo;
import com.wuye.system.mapper.SysMenuMapper;
import com.wuye.system.mapper.SysRoleMapper;
import com.wuye.system.mapper.SysRoleMenuMapper;
import com.wuye.system.service.ISysMenuService;

/**
 * Menu service implementation.
 *
 * @author wuye
 */
@Service
public class SysMenuServiceImpl implements ISysMenuService
{
    private static final Logger log = LoggerFactory.getLogger(SysMenuServiceImpl.class);

    public static final String PERMISSION_STRING = "perms[\"{0}\"]";
    public static final Long MENU_ROOT_ID = 0L;

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Override
    public List<SysMenu> selectMenuList(Long userId)
    {
        return selectMenuList(new SysMenu(), userId);
    }

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId)
    {
        if (SecurityUtils.isAdmin(userId))
        {
            return menuMapper.selectMenuList(menu);
        }
        menu.getParams().put("userId", userId);
        return menuMapper.selectMenuListByUserId(menu);
    }

    @Override
    public Set<String> selectMenuPermsByUserId(Long userId)
    {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<String>();
        for (String perm : perms)
        {
            if (StringUtils.isNotEmpty(perm))
            {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public Set<String> selectMenuPermsByRoleId(Long roleId)
    {
        List<String> perms = menuMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<String>();
        for (String perm : perms)
        {
            if (StringUtils.isNotEmpty(perm))
            {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId)
    {
        List<SysMenu> menus = SecurityUtils.isAdmin(userId)
            ? menuMapper.selectMenuTreeAll()
            : menuMapper.selectMenuTreeByUserId(userId);
        return getChildPerms(menus, MENU_ROOT_ID);
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long roleId)
    {
        SysRole role = roleMapper.selectRoleById(roleId);
        return menuMapper.selectMenuListByRoleId(roleId, role.isMenuCheckStrictly());
    }

    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus)
    {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (SysMenu menu : menus)
        {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));

            List<SysMenu> childrenMenus = menu.getChildren();
            if (StringUtils.isNotEmpty(childrenMenus) && UserConstants.TYPE_DIR.equals(menu.getMenuType()))
            {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(childrenMenus));
            }
            else if (isMenuFrame(menu))
            {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(getRouteName(menu.getRouteName(), menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            else if (menu.getParentId().intValue() == MENU_ROOT_ID && isInnerLink(menu))
            {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(getRouteName(menu.getRouteName(), routerPath));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus)
    {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        List<Long> tempList = menus.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
        for (Iterator<SysMenu> iterator = menus.iterator(); iterator.hasNext();)
        {
            SysMenu menu = iterator.next();
            if (!tempList.contains(menu.getParentId()))
            {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = menus;
        }
        return returnList;
    }

    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus)
    {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    @Override
    public SysMenu selectMenuById(Long menuId)
    {
        return menuMapper.selectMenuById(menuId);
    }

    @Override
    public boolean hasChildByMenuId(Long menuId)
    {
        return menuMapper.hasChildByMenuId(menuId) > 0;
    }

    @Override
    public boolean checkMenuExistRole(Long menuId)
    {
        return roleMenuMapper.checkMenuExistRole(menuId) > 0;
    }

    @Override
    public int insertMenu(SysMenu menu)
    {
        return menuMapper.insertMenu(menu);
    }

    @Override
    public int updateMenu(SysMenu menu)
    {
        return menuMapper.updateMenu(menu);
    }

    @Override
    @Transactional
    public void updateMenuSort(String[] menuIds, String[] orderNums)
    {
        try
        {
            for (int i = 0; i < menuIds.length; i++)
            {
                SysMenu menu = new SysMenu();
                menu.setMenuId(Convert.toLong(menuIds[i]));
                menu.setOrderNum(Convert.toInt(orderNums[i]));
                menuMapper.updateMenuSort(menu);
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("保存菜单排序失败");
        }
    }

    @Override
    public int deleteMenuById(Long menuId)
    {
        return menuMapper.deleteMenuById(menuId);
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu)
    {
        Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        SysMenu info = menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
        return StringUtils.isNull(info) || info.getMenuId().longValue() == menuId.longValue();
    }

    @Override
    public boolean checkRouteConfigUnique(SysMenu menu)
    {
        Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        Long parentId = menu.getParentId();
        String path = menu.getPath();
        String routeName = StringUtils.isEmpty(menu.getRouteName()) ? path : menu.getRouteName();
        List<SysMenu> dbMenus = menuMapper.selectMenusByPathOrRouteName(path, routeName);
        for (SysMenu dbMenu : dbMenus)
        {
            if (dbMenu.getMenuId().longValue() != menuId.longValue())
            {
                Long dbParentId = dbMenu.getParentId();
                String dbPath = dbMenu.getPath();
                String dbRouteName = StringUtils.isEmpty(dbMenu.getRouteName()) ? dbPath : dbMenu.getRouteName();
                if (StringUtils.equalsAnyIgnoreCase(path, dbPath) && parentId.longValue() == dbParentId.longValue())
                {
                    log.warn("Route path conflict under same parent: {}", dbPath);
                    return UserConstants.NOT_UNIQUE;
                }
                else if (StringUtils.equalsAnyIgnoreCase(path, dbPath) && parentId.longValue() == MENU_ROOT_ID)
                {
                    log.warn("Root route path must be unique: {}", path);
                    return UserConstants.NOT_UNIQUE;
                }
                else if (StringUtils.equalsAnyIgnoreCase(routeName, dbRouteName))
                {
                    log.warn("Route name must be globally unique: {}", routeName);
                    return UserConstants.NOT_UNIQUE;
                }
            }
        }
        return UserConstants.UNIQUE;
    }

    public String getRouteName(SysMenu menu)
    {
        if (isMenuFrame(menu))
        {
            return StringUtils.EMPTY;
        }
        return getRouteName(menu.getRouteName(), menu.getPath());
    }

    public String getRouteName(String name, String path)
    {
        String routerName = StringUtils.isNotEmpty(name) ? name : path;
        return StringUtils.capitalize(routerName);
    }

    public String getRouterPath(SysMenu menu)
    {
        String routerPath = menu.getPath();
        if (menu.getParentId().intValue() != MENU_ROOT_ID && isInnerLink(menu))
        {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        if (MENU_ROOT_ID == menu.getParentId().intValue()
                && UserConstants.TYPE_DIR.equals(menu.getMenuType())
                && UserConstants.NO_FRAME.equals(menu.getIsFrame()))
        {
            routerPath = "/" + menu.getPath();
        }
        else if (isMenuFrame(menu))
        {
            routerPath = "/";
        }
        return routerPath;
    }

    public String getComponent(SysMenu menu)
    {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu))
        {
            component = menu.getComponent();
        }
        else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != MENU_ROOT_ID && isInnerLink(menu))
        {
            component = UserConstants.INNER_LINK;
        }
        else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu))
        {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    public boolean isMenuFrame(SysMenu menu)
    {
        return menu.getParentId().intValue() == MENU_ROOT_ID
            && UserConstants.TYPE_MENU.equals(menu.getMenuType())
            && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    public boolean isParentView(SysMenu menu)
    {
        return menu.getParentId().intValue() != MENU_ROOT_ID
            && UserConstants.TYPE_DIR.equals(menu.getMenuType());
    }

    public boolean isInnerLink(SysMenu menu)
    {
        return menu.getIsFrame().equals(UserConstants.NO_FRAME) && StringUtils.ishttp(menu.getPath());
    }

    public List<SysMenu> getChildPerms(List<SysMenu> list, long parentId)
    {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        for (Iterator<SysMenu> iterator = list.iterator(); iterator.hasNext();)
        {
            SysMenu menu = iterator.next();
            if (menu.getParentId() == parentId)
            {
                recursionFn(list, menu);
                returnList.add(menu);
            }
        }
        return returnList;
    }

    private void recursionFn(List<SysMenu> list, SysMenu menu)
    {
        List<SysMenu> childList = getChildList(list, menu);
        menu.setChildren(childList);
        for (SysMenu child : childList)
        {
            if (hasChild(list, child))
            {
                recursionFn(list, child);
            }
        }
    }

    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu menu)
    {
        List<SysMenu> childList = new ArrayList<SysMenu>();
        for (Iterator<SysMenu> iterator = list.iterator(); iterator.hasNext();)
        {
            SysMenu item = iterator.next();
            if (item.getParentId().longValue() == menu.getMenuId().longValue())
            {
                childList.add(item);
            }
        }
        return childList;
    }

    private boolean hasChild(List<SysMenu> list, SysMenu menu)
    {
        return getChildList(list, menu).size() > 0;
    }

    public String innerLinkReplaceEach(String path)
    {
        return StringUtils.replaceEach(path,
            new String[] { Constants.HTTP, Constants.HTTPS, Constants.WWW, ".", ":" },
            new String[] { "", "", "", "/", "/" });
    }
}
