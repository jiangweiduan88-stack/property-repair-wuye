package com.wuye.common.core.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Set;
import com.wuye.common.utils.StringUtils;

/**
 * Type conversion helpers.
 *
 * @author wuye
 */
public class Convert
{
    public static String toStr(Object value, String defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof String)
        {
            return (String) value;
        }
        return value.toString();
    }

    public static String toStr(Object value)
    {
        return toStr(value, null);
    }

    public static Character toChar(Object value, Character defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Character)
        {
            return (Character) value;
        }

        String valueStr = toStr(value, null);
        return StringUtils.isEmpty(valueStr) ? defaultValue : valueStr.charAt(0);
    }

    public static Character toChar(Object value)
    {
        return toChar(value, null);
    }

    public static Byte toByte(Object value, Byte defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Byte)
        {
            return (Byte) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).byteValue();
        }

        String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Byte.parseByte(valueStr.trim());
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static Byte toByte(Object value)
    {
        return toByte(value, null);
    }

    public static Short toShort(Object value, Short defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Short)
        {
            return (Short) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).shortValue();
        }

        String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Short.parseShort(valueStr.trim());
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static Short toShort(Object value)
    {
        return toShort(value, null);
    }

    public static Number toNumber(Object value, Number defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Number)
        {
            return (Number) value;
        }

        String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return NumberFormat.getInstance().parse(valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static Number toNumber(Object value)
    {
        return toNumber(value, null);
    }

    public static Integer toInt(Object value, Integer defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Integer)
        {
            return (Integer) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).intValue();
        }

        String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Integer.parseInt(valueStr.trim());
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static Integer toInt(Object value)
    {
        return toInt(value, null);
    }

    public static Integer[] toIntArray(String str)
    {
        return toIntArray(",", str);
    }

    public static Long[] toLongArray(String str)
    {
        return toLongArray(",", str);
    }

    public static Integer[] toIntArray(String split, String str)
    {
        if (StringUtils.isEmpty(str))
        {
            return new Integer[] {};
        }
        String[] arr = str.split(split);
        Integer[] ints = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            ints[i] = toInt(arr[i], 0);
        }
        return ints;
    }

    public static Long[] toLongArray(String split, String str)
    {
        if (StringUtils.isEmpty(str))
        {
            return new Long[] {};
        }
        String[] arr = str.split(split);
        Long[] longs = new Long[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            longs[i] = toLong(arr[i], null);
        }
        return longs;
    }

    public static String[] toStrArray(String str)
    {
        if (StringUtils.isEmpty(str))
        {
            return new String[] {};
        }
        return toStrArray(",", str);
    }

    public static String[] toStrArray(String split, String str)
    {
        return str.split(split);
    }

    public static Long toLong(Object value, Long defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Long)
        {
            return (Long) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }

        String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return new BigDecimal(valueStr.trim()).longValue();
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static Long toLong(Object value)
    {
        return toLong(value, null);
    }

    public static Double toDouble(Object value, Double defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Double)
        {
            return (Double) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).doubleValue();
        }

        String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return new BigDecimal(valueStr.trim()).doubleValue();
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static Double toDouble(Object value)
    {
        return toDouble(value, null);
    }

    public static Float toFloat(Object value, Float defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Float)
        {
            return (Float) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).floatValue();
        }

        String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Float.parseFloat(valueStr.trim());
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static Float toFloat(Object value)
    {
        return toFloat(value, null);
    }

    public static Boolean toBool(Object value, Boolean defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof Boolean)
        {
            return (Boolean) value;
        }

        String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }

        switch (valueStr.trim().toLowerCase())
        {
            case "true":
            case "yes":
            case "ok":
            case "1":
            case "y":
            case "on":
            case "是":
                return true;
            case "false":
            case "no":
            case "0":
            case "n":
            case "off":
            case "否":
                return false;
            default:
                return defaultValue;
        }
    }

    public static Boolean toBool(Object value)
    {
        return toBool(value, null);
    }

    public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value, E defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (clazz.isAssignableFrom(value.getClass()))
        {
            @SuppressWarnings("unchecked")
            E myE = (E) value;
            return myE;
        }

        String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return Enum.valueOf(clazz, valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value)
    {
        return toEnum(clazz, value, null);
    }

    public static BigInteger toBigInteger(Object value, BigInteger defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof BigInteger)
        {
            return (BigInteger) value;
        }
        if (value instanceof Long)
        {
            return BigInteger.valueOf((Long) value);
        }

        String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return new BigInteger(valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static BigInteger toBigInteger(Object value)
    {
        return toBigInteger(value, null);
    }

    public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        if (value instanceof BigDecimal)
        {
            return (BigDecimal) value;
        }
        if (value instanceof Long)
        {
            return new BigDecimal((Long) value);
        }
        if (value instanceof Double)
        {
            return BigDecimal.valueOf((Double) value);
        }
        if (value instanceof Integer)
        {
            return new BigDecimal((Integer) value);
        }

        String valueStr = toStr(value, null);
        if (StringUtils.isEmpty(valueStr))
        {
            return defaultValue;
        }
        try
        {
            return new BigDecimal(valueStr);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static BigDecimal toBigDecimal(Object value)
    {
        return toBigDecimal(value, null);
    }

    public static String utf8Str(Object obj)
    {
        return str(obj, CharsetKit.CHARSET_UTF_8);
    }

    public static String str(Object obj, String charsetName)
    {
        return str(obj, Charset.forName(charsetName));
    }

    public static String str(Object obj, Charset charset)
    {
        if (obj == null)
        {
            return null;
        }

        if (obj instanceof String)
        {
            return (String) obj;
        }
        if (obj instanceof byte[])
        {
            return str((byte[]) obj, charset);
        }
        if (obj instanceof Byte[])
        {
            Byte[] bytes = (Byte[]) obj;
            byte[] dest = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++)
            {
                dest[i] = bytes[i];
            }
            return str(dest, charset);
        }
        if (obj instanceof ByteBuffer)
        {
            return str((ByteBuffer) obj, charset);
        }
        return obj.toString();
    }

    public static String str(byte[] bytes, String charset)
    {
        return str(bytes, StringUtils.isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    public static String str(byte[] data, Charset charset)
    {
        if (data == null)
        {
            return null;
        }
        if (charset == null)
        {
            return new String(data);
        }
        return new String(data, charset);
    }

    public static String str(ByteBuffer data, String charset)
    {
        if (data == null)
        {
            return null;
        }
        return str(data, Charset.forName(charset));
    }

    public static String str(ByteBuffer data, Charset charset)
    {
        if (charset == null)
        {
            charset = Charset.defaultCharset();
        }
        return charset.decode(data).toString();
    }

    public static String toSBC(String input)
    {
        return toSBC(input, null);
    }

    public static String toSBC(String input, Set<Character> notConvertSet)
    {
        if (input == null)
        {
            return null;
        }
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            if (notConvertSet != null && notConvertSet.contains(chars[i]))
            {
                continue;
            }
            if (chars[i] == ' ')
            {
                chars[i] = '\u3000';
            }
            else if (chars[i] < '\177')
            {
                chars[i] = (char) (chars[i] + 65248);
            }
        }
        return new String(chars);
    }

    public static String toDBC(String input)
    {
        return toDBC(input, null);
    }

    public static String toDBC(String text, Set<Character> notConvertSet)
    {
        if (text == null)
        {
            return null;
        }
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            if (notConvertSet != null && notConvertSet.contains(chars[i]))
            {
                continue;
            }
            if (chars[i] == '\u3000')
            {
                chars[i] = ' ';
            }
            else if (chars[i] > '\uFF00' && chars[i] < '\uFF5F')
            {
                chars[i] = (char) (chars[i] - 65248);
            }
        }
        return new String(chars);
    }

    public static String digitUppercase(double n)
    {
        String[] fraction = { "角", "分" };
        String[] digit = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
        String[][] unit = { { "元", "万", "亿" }, { "", "拾", "佰", "仟" } };

        String head = n < 0 ? "负" : "";
        n = Math.abs(n);

        String s = "";
        for (int i = 0; i < fraction.length; i++)
        {
            s += (digit[(int) (Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
        }
        if (s.length() < 1)
        {
            s = "整";
        }

        int integerPart = (int) Math.floor(n);
        for (int i = 0; i < unit[0].length && integerPart > 0; i++)
        {
            String p = "";
            for (int j = 0; j < unit[1].length && integerPart > 0; j++)
            {
                p = digit[integerPart % 10] + unit[1][j] + p;
                integerPart = integerPart / 10;
            }
            s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
        }
        return head + s.replaceAll("(零.)*零元", "元")
            .replaceFirst("(零.)+", "")
            .replaceAll("(零.)+", "零")
            .replaceAll("^整$", "零元整");
    }
}
