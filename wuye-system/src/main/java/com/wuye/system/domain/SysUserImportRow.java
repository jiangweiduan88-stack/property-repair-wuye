package com.wuye.system.domain;

import com.wuye.common.annotation.Excel;
import com.wuye.common.annotation.Excel.ColumnType;

/**
 * 用户导入模板行。
 */
public class SysUserImportRow
{
    @Excel(name = "用户昵称", required = true, prompt = "必填，最长30个字符")
    private String nickName;

    @Excel(name = "用户名称", required = true, prompt = "必填，2至20个字符")
    private String userName;

    @Excel(name = "用户性别", required = true, prompt = "必填，只能填写男或女", combo = { "男", "女" })
    private String sex;

    @Excel(name = "手机号码", required = true, cellType = ColumnType.TEXT, prompt = "必填，11位数字")
    private String phonenumber;

    @Excel(name = "归属部门", prompt = "选填，须填写系统中有效的部门名称")
    private String deptName;

    @Excel(name = "账号状态", prompt = "选填，只能填写正常或停用，留空默认为正常", combo = { "正常", "停用" })
    private String status;

    @Excel(name = "岗位", prompt = "选填，多个岗位使用逗号分隔")
    private String postNames;

    @Excel(name = "角色", prompt = "选填，多个角色使用逗号分隔")
    private String roleNames;

    /** Excel 中的实际行号，由 ExcelUtil 自动写入。 */
    private Integer importRowNum;

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getPhonenumber()
    {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber)
    {
        this.phonenumber = phonenumber;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getPostNames()
    {
        return postNames;
    }

    public void setPostNames(String postNames)
    {
        this.postNames = postNames;
    }

    public String getRoleNames()
    {
        return roleNames;
    }

    public void setRoleNames(String roleNames)
    {
        this.roleNames = roleNames;
    }

    public Integer getImportRowNum()
    {
        return importRowNum;
    }

    public void setImportRowNum(Integer importRowNum)
    {
        this.importRowNum = importRowNum;
    }
}
