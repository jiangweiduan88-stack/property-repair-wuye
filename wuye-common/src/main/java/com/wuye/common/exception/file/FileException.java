package com.wuye.common.exception.file;

import com.wuye.common.exception.base.BaseException;

/**
 * 文件信息异常类
 * 
 * @author wuye
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args)
    {
        super("file", code, args, null);
    }

    public FileException(String defaultMessage)
    {
        super(defaultMessage);
    }

}
