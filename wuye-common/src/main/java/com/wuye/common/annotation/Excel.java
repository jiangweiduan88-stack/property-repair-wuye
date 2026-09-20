package com.wuye.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import com.wuye.common.utils.poi.ExcelHandlerAdapter;

/**
 * Custom Excel export/import annotation.
 *
 * @author wuye
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Excel
{
    /** Column order in the exported sheet. */
    int sort() default Integer.MAX_VALUE;

    /** Column name in the exported sheet. */
    String name() default "";

    /** Date format, for example `yyyy-MM-dd`. */
    String dateFormat() default "";

    /** Dictionary type, for example `sys_user_sex`. */
    String dictType() default "";

    /** Read conversion expression, for example `0=Male,1=Female`. */
    String readConverterExp() default "";

    /** Separator used when reading joined values. */
    String separator() default ",";

    /** BigDecimal scale, `-1` means disabled. */
    int scale() default -1;

    /** BigDecimal rounding mode. */
    @SuppressWarnings("deprecation")
    int roundingMode() default BigDecimal.ROUND_HALF_EVEN;

    /** Row height in the exported sheet. */
    double height() default 14;

    /** Column width in the exported sheet. */
    double width() default 16;

    /** Text suffix, for example turning `90` into `90%`. */
    String suffix() default "";

    /** Default value used when the field is empty. */
    String defaultValue() default "";

    /** Prompt message. */
    String prompt() default "";

    /** Whether the column is required during import. */
    boolean required() default false;

    /** Whether wrapping text is enabled. */
    boolean wrapText() default false;

    /** Optional drop-down values. */
    String[] combo() default {};

    /** Whether combo values should be loaded from a dictionary. */
    boolean comboReadDict() default false;

    /** Whether vertical cell merging is needed. */
    boolean needMerge() default false;

    /** Whether the field should be exported. */
    boolean isExport() default true;

    /** Nested attribute name, supports multi-level access. */
    String targetAttr() default "";

    /** Whether totals should be calculated automatically. */
    boolean isStatistics() default false;

    /** Cell data type. */
    ColumnType cellType() default ColumnType.STRING;

    /** Header background color. */
    IndexedColors headerBackgroundColor() default IndexedColors.GREY_50_PERCENT;

    /** Header font color. */
    IndexedColors headerColor() default IndexedColors.WHITE;

    /** Cell background color. */
    IndexedColors backgroundColor() default IndexedColors.WHITE;

    /** Cell font color. */
    IndexedColors color() default IndexedColors.BLACK;

    /** Horizontal alignment. */
    HorizontalAlignment align() default HorizontalAlignment.CENTER;

    /** Custom handler. */
    Class<?> handler() default ExcelHandlerAdapter.class;

    /** Custom handler arguments. */
    String[] args() default {};

    /** Import/export direction. */
    Type type() default Type.ALL;

    enum Type
    {
        ALL(0), EXPORT(1), IMPORT(2);

        private final int value;

        Type(int value)
        {
            this.value = value;
        }

        public int value()
        {
            return this.value;
        }
    }

    enum ColumnType
    {
        NUMERIC(0), STRING(1), IMAGE(2), TEXT(3);

        private final int value;

        ColumnType(int value)
        {
            this.value = value;
        }

        public int value()
        {
            return this.value;
        }
    }
}
