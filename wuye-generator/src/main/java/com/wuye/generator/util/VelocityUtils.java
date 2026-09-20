package com.wuye.generator.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.velocity.VelocityContext;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wuye.common.constant.GenConstants;
import com.wuye.common.utils.DateUtils;
import com.wuye.common.utils.StringUtils;
import com.wuye.generator.domain.GenTable;
import com.wuye.generator.domain.GenTableColumn;

/**
 * Velocity template helpers.
 *
 * @author wuye
 */
public class VelocityUtils
{
    /** Source path for generated Java files. */
    private static final String PROJECT_PATH = "main/java";

    /** Source path for generated MyBatis files. */
    private static final String MYBATIS_PATH = "main/resources/mapper";

    /** Default parent menu ID for generated modules. */
    private static final String DEFAULT_PARENT_MENU_ID = "3";

    /** Vue3 Element Plus templates. */
    private static final String ELEMENT_PLUS = "element-plus";

    /** Vue3 Element Plus TypeScript templates. */
    private static final String ELEMENT_PLUS_TYPESCRIPT = "element-plus-typescript";

    public static VelocityContext prepareContext(GenTable genTable)
    {
        String moduleName = genTable.getModuleName();
        String businessName = genTable.getBusinessName();
        String packageName = genTable.getPackageName();
        String tplCategory = genTable.getTplCategory();
        String functionName = genTable.getFunctionName();

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("tplCategory", tplCategory);
        velocityContext.put("tableName", genTable.getTableName());
        velocityContext.put("functionName", StringUtils.isNotEmpty(functionName) ? functionName : "Please enter a function name");
        velocityContext.put("ClassName", genTable.getClassName());
        velocityContext.put("className", StringUtils.uncapitalize(genTable.getClassName()));
        velocityContext.put("moduleName", moduleName);
        velocityContext.put("BusinessName", StringUtils.capitalize(businessName));
        velocityContext.put("businessName", businessName);
        velocityContext.put("basePackage", getPackagePrefix(packageName));
        velocityContext.put("packageName", packageName);
        velocityContext.put("author", genTable.getFunctionAuthor());
        velocityContext.put("colSpan", getColSpan(genTable.getFormColNum()));
        velocityContext.put("datetime", DateUtils.getDate());
        velocityContext.put("pkColumn", genTable.getPkColumn());
        velocityContext.put("importList", getImportList(genTable));
        velocityContext.put("permissionPrefix", getPermissionPrefix(moduleName, businessName));
        velocityContext.put("columns", genTable.getColumns());
        velocityContext.put("table", genTable);
        velocityContext.put("dicts", getDicts(genTable));
        setExtensionsContext(velocityContext, genTable.getOptions());
        setMenuVelocityContext(velocityContext, genTable);
        if (GenConstants.TPL_TREE.equals(tplCategory))
        {
            setTreeVelocityContext(velocityContext, genTable);
        }
        if (GenConstants.TPL_SUB.equals(tplCategory))
        {
            setSubVelocityContext(velocityContext, genTable);
        }
        return velocityContext;
    }

    public static void setExtensionsContext(VelocityContext context, String options)
    {
        JSONObject paramsObj = StringUtils.isEmpty(options) ? new JSONObject() : JSONObject.parseObject(options);
        context.put("genView", genView(paramsObj));
    }

    public static void setMenuVelocityContext(VelocityContext context, GenTable genTable)
    {
        String options = genTable.getOptions();
        JSONObject paramsObj = StringUtils.isEmpty(options) ? new JSONObject() : JSON.parseObject(options);
        context.put("parentMenuId", getParentMenuId(paramsObj));
    }

    public static void setTreeVelocityContext(VelocityContext context, GenTable genTable)
    {
        String options = genTable.getOptions();
        JSONObject paramsObj = StringUtils.isEmpty(options) ? new JSONObject() : JSON.parseObject(options);
        String treeCode = getTreecode(paramsObj);
        String treeParentCode = getTreeParentCode(paramsObj);
        String treeName = getTreeName(paramsObj);

        context.put("treeCode", treeCode);
        context.put("treeParentCode", treeParentCode);
        context.put("treeName", treeName);
        context.put("expandColumn", getExpandColumn(genTable));
        if (paramsObj.containsKey(GenConstants.TREE_PARENT_CODE))
        {
            context.put("tree_parent_code", paramsObj.getString(GenConstants.TREE_PARENT_CODE));
        }
        if (paramsObj.containsKey(GenConstants.TREE_NAME))
        {
            context.put("tree_name", paramsObj.getString(GenConstants.TREE_NAME));
        }
    }

    public static void setSubVelocityContext(VelocityContext context, GenTable genTable)
    {
        GenTable subTable = genTable.getSubTable();
        String subTableName = genTable.getSubTableName();
        String subTableFkName = genTable.getSubTableFkName();
        String subClassName = subTable.getClassName();
        String subTableFkClassName = StringUtils.convertToCamelCase(subTableFkName);

        context.put("subTable", subTable);
        context.put("subTableName", subTableName);
        context.put("subTableFkName", subTableFkName);
        context.put("subTableFkClassName", subTableFkClassName);
        context.put("subTableFkclassName", StringUtils.uncapitalize(subTableFkClassName));
        context.put("subClassName", subClassName);
        context.put("subclassName", StringUtils.uncapitalize(subClassName));
        context.put("subImportList", getImportList(subTable));
    }

    public static List<String> getTemplateList(GenTable table)
    {
        String tplWebType = table.getTplWebType();
        String tplCategory = table.getTplCategory();
        JSONObject paramsObj = StringUtils.isEmpty(table.getOptions()) ? new JSONObject() : JSONObject.parseObject(table.getOptions());
        boolean isView = genView(paramsObj);

        String useWebType = "vm/vue";
        String apiTemplate = "vm/js/api.js.vm";
        if (StringUtils.equals(ELEMENT_PLUS, tplWebType))
        {
            useWebType = "vm/vue/v3";
        }
        else if (StringUtils.equals(ELEMENT_PLUS_TYPESCRIPT, tplWebType))
        {
            useWebType = "vm/vue/v3ts";
            apiTemplate = "vm/ts/api.ts.vm";
        }

        List<String> templates = new ArrayList<String>();
        templates.add("vm/java/domain.java.vm");
        templates.add("vm/java/mapper.java.vm");
        templates.add("vm/java/service.java.vm");
        templates.add("vm/java/serviceImpl.java.vm");
        templates.add("vm/java/controller.java.vm");
        templates.add("vm/xml/mapper.xml.vm");
        templates.add("vm/sql/sql.vm");
        templates.add(apiTemplate);

        if (StringUtils.equals(ELEMENT_PLUS_TYPESCRIPT, tplWebType))
        {
            templates.add("vm/ts/type.ts.vm");
            templates.add("vm/ts/index.ts.vm");
        }
        if (GenConstants.TPL_CRUD.equals(tplCategory))
        {
            templates.add(useWebType + "/index.vue.vm");
        }
        else if (GenConstants.TPL_TREE.equals(tplCategory))
        {
            templates.add(useWebType + "/index-tree.vue.vm");
        }
        else if (GenConstants.TPL_SUB.equals(tplCategory))
        {
            templates.add(useWebType + "/index.vue.vm");
            templates.add("vm/java/sub-domain.java.vm");
        }
        if (isView)
        {
            templates.add(useWebType + "/view.vue.vm");
        }
        return templates;
    }

    public static String getFileName(String template, GenTable genTable)
    {
        String fileName = "";
        String packageName = genTable.getPackageName();
        String moduleName = genTable.getModuleName();
        String className = genTable.getClassName();
        String businessName = genTable.getBusinessName();

        String javaPath = PROJECT_PATH + "/" + StringUtils.replace(packageName, ".", "/");
        String mybatisPath = MYBATIS_PATH + "/" + moduleName;
        String vuePath = "vue/src";

        if (template.contains("domain.java.vm"))
        {
            fileName = StringUtils.format("{}/domain/{}.java", javaPath, className);
        }
        if (template.contains("sub-domain.java.vm") && StringUtils.equals(GenConstants.TPL_SUB, genTable.getTplCategory()))
        {
            fileName = StringUtils.format("{}/domain/{}.java", javaPath, genTable.getSubTable().getClassName());
        }
        else if (template.contains("mapper.java.vm"))
        {
            fileName = StringUtils.format("{}/mapper/{}Mapper.java", javaPath, className);
        }
        else if (template.contains("service.java.vm"))
        {
            fileName = StringUtils.format("{}/service/I{}Service.java", javaPath, className);
        }
        else if (template.contains("serviceImpl.java.vm"))
        {
            fileName = StringUtils.format("{}/service/impl/{}ServiceImpl.java", javaPath, className);
        }
        else if (template.contains("controller.java.vm"))
        {
            fileName = StringUtils.format("{}/controller/{}Controller.java", javaPath, className);
        }
        else if (template.contains("mapper.xml.vm"))
        {
            fileName = StringUtils.format("{}/{}Mapper.xml", mybatisPath, className);
        }
        else if (template.contains("sql.vm"))
        {
            fileName = businessName + "Menu.sql";
        }
        else if (template.contains("api.js.vm"))
        {
            fileName = StringUtils.format("{}/api/{}/{}.js", vuePath, moduleName, businessName);
        }
        else if (template.contains("api.ts.vm"))
        {
            fileName = StringUtils.format("{}/api/{}/{}.ts", vuePath, moduleName, businessName);
        }
        else if (template.contains("type.ts.vm"))
        {
            fileName = StringUtils.format("{}/types/api/{}/{}.ts", vuePath, moduleName, businessName);
        }
        else if (template.contains("index.ts.vm"))
        {
            fileName = StringUtils.format("{}/types/api/index.ts", vuePath);
        }
        else if (template.contains("index.vue.vm"))
        {
            fileName = StringUtils.format("{}/views/{}/{}/index.vue", vuePath, moduleName, businessName);
        }
        else if (template.contains("index-tree.vue.vm"))
        {
            fileName = StringUtils.format("{}/views/{}/{}/index.vue", vuePath, moduleName, businessName);
        }
        else if (template.contains("view.vue.vm"))
        {
            fileName = StringUtils.format("{}/views/{}/{}/view.vue", vuePath, moduleName, businessName);
        }
        return fileName;
    }

    public static String getPackagePrefix(String packageName)
    {
        int lastIndex = packageName.lastIndexOf(".");
        return StringUtils.substring(packageName, 0, lastIndex);
    }

    public static HashSet<String> getImportList(GenTable genTable)
    {
        List<GenTableColumn> columns = genTable.getColumns();
        GenTable subGenTable = genTable.getSubTable();
        HashSet<String> importList = new HashSet<String>();
        if (StringUtils.isNotNull(subGenTable))
        {
            importList.add("java.util.List");
        }
        for (GenTableColumn column : columns)
        {
            if (!column.isSuperColumn() && GenConstants.TYPE_DATE.equals(column.getJavaType()))
            {
                importList.add("java.util.Date");
                importList.add("com.fasterxml.jackson.annotation.JsonFormat");
            }
            else if (!column.isSuperColumn() && GenConstants.TYPE_BIGDECIMAL.equals(column.getJavaType()))
            {
                importList.add("java.math.BigDecimal");
            }
        }
        return importList;
    }

    public static String getDicts(GenTable genTable)
    {
        List<GenTableColumn> columns = genTable.getColumns();
        Set<String> dicts = new HashSet<String>();
        addDicts(dicts, columns);
        if (StringUtils.isNotNull(genTable.getSubTable()))
        {
            addDicts(dicts, genTable.getSubTable().getColumns());
        }
        return StringUtils.join(dicts, ", ");
    }

    public static void addDicts(Set<String> dicts, List<GenTableColumn> columns)
    {
        for (GenTableColumn column : columns)
        {
            if (!column.isSuperColumn()
                && StringUtils.isNotEmpty(column.getDictType())
                && StringUtils.equalsAny(column.getHtmlType(),
                    GenConstants.HTML_SELECT, GenConstants.HTML_RADIO, GenConstants.HTML_CHECKBOX))
            {
                dicts.add("'" + column.getDictType() + "'");
            }
        }
    }

    public static String getPermissionPrefix(String moduleName, String businessName)
    {
        return StringUtils.format("{}:{}", moduleName, businessName);
    }

    public static String getParentMenuId(JSONObject paramsObj)
    {
        if (StringUtils.isNotNull(paramsObj)
                && paramsObj.containsKey(GenConstants.PARENT_MENU_ID)
                && StringUtils.isNotEmpty(paramsObj.getString(GenConstants.PARENT_MENU_ID)))
        {
            return paramsObj.getString(GenConstants.PARENT_MENU_ID);
        }
        return DEFAULT_PARENT_MENU_ID;
    }

    public static String getTreecode(JSONObject paramsObj)
    {
        if (paramsObj.containsKey(GenConstants.TREE_CODE))
        {
            return StringUtils.toCamelCase(paramsObj.getString(GenConstants.TREE_CODE));
        }
        return StringUtils.EMPTY;
    }

    public static String getTreeParentCode(JSONObject paramsObj)
    {
        if (paramsObj.containsKey(GenConstants.TREE_PARENT_CODE))
        {
            return StringUtils.toCamelCase(paramsObj.getString(GenConstants.TREE_PARENT_CODE));
        }
        return StringUtils.EMPTY;
    }

    public static boolean genView(JSONObject paramsObj)
    {
        return StringUtils.isNotNull(paramsObj)
            && paramsObj.containsKey(GenConstants.GEN_VIEW)
            && paramsObj.getBooleanValue(GenConstants.GEN_VIEW);
    }

    public static String getTreeName(JSONObject paramsObj)
    {
        if (paramsObj.containsKey(GenConstants.TREE_NAME))
        {
            return StringUtils.toCamelCase(paramsObj.getString(GenConstants.TREE_NAME));
        }
        return StringUtils.EMPTY;
    }

    public static int getExpandColumn(GenTable genTable)
    {
        String options = genTable.getOptions();
        JSONObject paramsObj = StringUtils.isEmpty(options) ? new JSONObject() : JSON.parseObject(options);
        String treeName = paramsObj.getString(GenConstants.TREE_NAME);
        int num = 0;
        for (GenTableColumn column : genTable.getColumns())
        {
            if (column.isList())
            {
                num++;
                if (column.getColumnName().equals(treeName))
                {
                    break;
                }
            }
        }
        return num;
    }

    public static String getColSpan(Integer formColNum)
    {
        if (formColNum == null)
        {
            return "24";
        }
        if (formColNum == 2)
        {
            return "12";
        }
        else if (formColNum == 3)
        {
            return "8";
        }
        return "24";
    }
}
