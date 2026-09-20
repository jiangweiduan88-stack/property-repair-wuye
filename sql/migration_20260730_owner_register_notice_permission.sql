-- 开启业主自助注册。
update sys_config
set config_value = 'true', update_by = 'admin', update_time = now()
where config_key = 'sys.account.registerUser';

-- 物业管理员可进入“系统管理 > 通知公告”，并拥有完整公告管理权限。
insert into sys_role_menu(role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id = 1
left join sys_role_menu rm on rm.role_id = r.role_id and rm.menu_id = m.menu_id
where r.role_key = 'property_manager' and rm.role_id is null;

insert into sys_role_menu(role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id = 107 or m.perms in (
    'system:notice:query',
    'system:notice:add',
    'system:notice:edit',
    'system:notice:remove'
)
left join sys_role_menu rm on rm.role_id = r.role_id and rm.menu_id = m.menu_id
where r.role_key = 'property_manager' and rm.role_id is null;
