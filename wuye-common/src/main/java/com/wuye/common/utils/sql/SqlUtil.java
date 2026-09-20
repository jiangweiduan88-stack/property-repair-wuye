package com.wuye.common.utils.sql;

import com.wuye.common.exception.UtilException;
import com.wuye.common.utils.StringUtils;

/**
 * SQL safety helpers.
 *
 * @author wuye
 */
public class SqlUtil
{
    /** Frequently abused SQL keywords and functions. */
    public static final String SQL_REGEX = "\\||and |extractvalue|updatexml|sleep|information_schema|exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |or |union |like |user\\(";

    /** Allow only letters, digits, underscore, space, comma, and dot in order-by content. */
    public static final String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    /** Maximum allowed order-by length. */
    private static final int ORDER_BY_MAX_LENGTH = 500;

    public static String escapeOrderBySql(String value)
    {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value))
        {
            throw new UtilException("排序参数包含非法字符");
        }
        if (StringUtils.length(value) > ORDER_BY_MAX_LENGTH)
        {
            throw new UtilException("排序参数超过最大长度");
        }
        return value;
    }

    public static boolean isValidOrderBySql(String value)
    {
        return value.matches(SQL_PATTERN);
    }

    public static void filterKeyword(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return;
        }
        String normalizedValue = value.replaceAll("\\p{Z}|\\s", "").toLowerCase();
        String[] sqlKeywords = SQL_REGEX.split("\\|");
        for (String sqlKeyword : sqlKeywords)
        {
            if (StringUtils.indexOfIgnoreCase(normalizedValue, sqlKeyword.trim()) > -1)
            {
                throw new UtilException("请求参数包含禁止使用的数据库关键字：" + sqlKeyword);
            }
        }
    }
}
