package com.wuye.common.core.text;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import com.wuye.common.utils.StringUtils;

/**
 * Charset helpers.
 *
 * @author wuye
 */
public class CharsetKit
{
    /** ISO-8859-1. */
    public static final String ISO_8859_1 = "ISO-8859-1";

    /** UTF-8. */
    public static final String UTF_8 = "UTF-8";

    /** GBK. */
    public static final String GBK = "GBK";

    /** ISO-8859-1 charset. */
    public static final Charset CHARSET_ISO_8859_1 = Charset.forName(ISO_8859_1);

    /** UTF-8 charset. */
    public static final Charset CHARSET_UTF_8 = Charset.forName(UTF_8);

    /** GBK charset. */
    public static final Charset CHARSET_GBK = Charset.forName(GBK);

    /**
     * Convert a charset name to a {@link Charset} object.
     *
     * @param charset charset name
     * @return charset object or the platform default when empty
     */
    public static Charset charset(String charset)
    {
        return StringUtils.isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset);
    }

    /**
     * Convert a string between two charset names.
     *
     * @param source source string
     * @param srcCharset source charset name
     * @param destCharset target charset name
     * @return converted string
     */
    public static String convert(String source, String srcCharset, String destCharset)
    {
        return convert(source, Charset.forName(srcCharset), Charset.forName(destCharset));
    }

    /**
     * Convert a string between two charsets.
     *
     * @param source source string
     * @param srcCharset source charset
     * @param destCharset target charset
     * @return converted string
     */
    public static String convert(String source, Charset srcCharset, Charset destCharset)
    {
        if (srcCharset == null)
        {
            srcCharset = StandardCharsets.ISO_8859_1;
        }

        if (destCharset == null)
        {
            destCharset = StandardCharsets.UTF_8;
        }

        if (StringUtils.isEmpty(source) || srcCharset.equals(destCharset))
        {
            return source;
        }
        return new String(source.getBytes(srcCharset), destCharset);
    }

    /**
     * Get the platform default charset name.
     *
     * @return charset name
     */
    public static String systemCharset()
    {
        return Charset.defaultCharset().name();
    }
}
