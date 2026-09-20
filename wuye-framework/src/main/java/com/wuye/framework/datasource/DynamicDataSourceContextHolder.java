package com.wuye.framework.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dynamic datasource context holder.
 *
 * @author wuye
 */
public class DynamicDataSourceContextHolder
{
    public static final Logger log = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);

    /** Thread-local datasource identifier. */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<String>();

    public static void setDataSourceType(String dsType)
    {
        log.info("Switch datasource to {}", dsType);
        CONTEXT_HOLDER.set(dsType);
    }

    public static String getDataSourceType()
    {
        return CONTEXT_HOLDER.get();
    }

    public static void clearDataSourceType()
    {
        CONTEXT_HOLDER.remove();
    }
}
