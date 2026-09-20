package com.wuye.system.domain;

/**
 * 用户导入单元格校验错误。
 */
public class SysUserImportError
{
    private Integer row;
    private String column;
    private String cell;
    private String value;
    private String message;

    public SysUserImportError()
    {
    }

    public SysUserImportError(Integer row, String column, String cell, String value, String message)
    {
        this.row = row;
        this.column = column;
        this.cell = cell;
        this.value = value;
        this.message = message;
    }

    public Integer getRow()
    {
        return row;
    }

    public void setRow(Integer row)
    {
        this.row = row;
    }

    public String getColumn()
    {
        return column;
    }

    public void setColumn(String column)
    {
        this.column = column;
    }

    public String getCell()
    {
        return cell;
    }

    public void setCell(String cell)
    {
        this.cell = cell;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
