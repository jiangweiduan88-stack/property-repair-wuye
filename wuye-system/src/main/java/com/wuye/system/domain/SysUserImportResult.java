package com.wuye.system.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户导入校验及执行结果。
 */
public class SysUserImportResult
{
    private boolean passed;
    private int total;
    private int validCount;
    private int invalidCount;
    private int successCount;
    private String message;
    private List<SysUserImportError> errors = new ArrayList<SysUserImportError>();

    public boolean isPassed()
    {
        return passed;
    }

    public void setPassed(boolean passed)
    {
        this.passed = passed;
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal(int total)
    {
        this.total = total;
    }

    public int getSuccessCount()
    {
        return successCount;
    }

    public void setSuccessCount(int successCount)
    {
        this.successCount = successCount;
    }

    public int getValidCount()
    {
        return validCount;
    }

    public void setValidCount(int validCount)
    {
        this.validCount = validCount;
    }

    public int getInvalidCount()
    {
        return invalidCount;
    }

    public void setInvalidCount(int invalidCount)
    {
        this.invalidCount = invalidCount;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public List<SysUserImportError> getErrors()
    {
        return errors;
    }

    public void setErrors(List<SysUserImportError> errors)
    {
        this.errors = errors;
    }

    public void addError(SysUserImportError error)
    {
        errors.add(error);
    }
}
