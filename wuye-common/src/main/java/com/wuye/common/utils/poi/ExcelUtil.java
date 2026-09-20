package com.wuye.common.utils.poi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.wuye.common.annotation.Excel;
import com.wuye.common.annotation.Excels;
import com.wuye.common.core.domain.AjaxResult;
import com.wuye.common.core.text.Convert;
import com.wuye.common.exception.UtilException;
import com.wuye.common.utils.DateUtils;
import com.wuye.common.utils.StringUtils;

/**
 * Simplified Excel utility used by controllers and imports.
 *
 * @author wuye
 */
public class ExcelUtil<T>
{
    private final Class<T> clazz;
    private final DataFormatter dataFormatter = new DataFormatter();

    public ExcelUtil(Class<T> clazz)
    {
        this.clazz = clazz;
    }

    public List<T> importExcel(InputStream is) throws Exception
    {
        return importExcel(is, 0);
    }

    public List<T> importExcel(InputStream is, int headerRowNum) throws Exception
    {
        try (Workbook workbook = WorkbookFactory.create(is))
        {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(headerRowNum);
            if (headerRow == null)
            {
                throw new UtilException("Excel表头不能为空");
            }

            Map<Integer, FieldBinding> bindings = buildImportBindings(headerRow);
            List<T> result = new ArrayList<T>();
            for (int rowIndex = headerRowNum + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++)
            {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isRowEmpty(row))
                {
                    continue;
                }

                T entity = clazz.getDeclaredConstructor().newInstance();
                setImportRowNum(entity, rowIndex + 1);
                for (Map.Entry<Integer, FieldBinding> entry : bindings.entrySet())
                {
                    Cell cell = row.getCell(entry.getKey());
                    String cellValue = cell == null ? StringUtils.EMPTY : dataFormatter.formatCellValue(cell).trim();
                    if (StringUtils.isEmpty(cellValue))
                    {
                        continue;
                    }
                    FieldBinding binding = entry.getValue();
                    Object value = convertImportValue(cell, cellValue, binding.excel, binding.field.getType());
                    binding.field.setAccessible(true);
                    binding.field.set(entity, value);
                }
                result.add(entity);
            }
            return result;
        }
    }

    public void exportExcel(HttpServletResponse response, List<T> list, String sheetName)
    {
        try (Workbook workbook = new XSSFWorkbook())
        {
            writeSheet(workbook, sheetName, sheetName, list, clazz);
            writeResponse(response, workbook, sheetName);
        }
        catch (Exception e)
        {
            throw new UtilException("导出Excel文件失败", e);
        }
    }

    public void importTemplateExcel(HttpServletResponse response, String sheetName)
    {
        try (Workbook workbook = new XSSFWorkbook())
        {
            writeSheet(workbook, sheetName, StringUtils.EMPTY, new ArrayList<T>(), clazz);
            writeResponse(response, workbook, sheetName + "_template");
        }
        catch (Exception e)
        {
            throw new UtilException("导出导入模板失败", e);
        }
    }

    public static Workbook exportMultiSheet(List<ExcelSheet<?>> sheets)
    {
        try
        {
            Workbook workbook = new XSSFWorkbook();
            for (ExcelSheet<?> sheet : sheets)
            {
                writeSheet(workbook, sheet);
            }
            return workbook;
        }
        catch (Exception e)
        {
            throw new UtilException("导出多工作表文件失败", e);
        }
    }

    private void writeSheet(Workbook workbook, String sheetName, String title, List<T> list, Class<T> targetClass)
        throws IllegalAccessException
    {
        Sheet sheet = workbook.createSheet(StringUtils.isEmpty(sheetName) ? "Sheet1" : sheetName);
        List<ExportColumn> columns = getExportColumns(targetClass);
        int rowIndex = 0;
        if (StringUtils.isNotEmpty(title))
        {
            Row titleRow = sheet.createRow(rowIndex++);
            titleRow.createCell(0).setCellValue(title);
        }
        Row headerRow = sheet.createRow(rowIndex++);
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        CreationHelper creationHelper = workbook.getCreationHelper();
        Font requiredMarkerFont = workbook.createFont();
        requiredMarkerFont.setColor(IndexedColors.RED.getIndex());
        for (int i = 0; i < columns.size(); i++)
        {
            ExportColumn column = columns.get(i);
            Cell headerCell = headerRow.createCell(i);
            String headerText = column.header + (column.excel.required() ? "*" : "");
            RichTextString richHeader = creationHelper.createRichTextString(headerText);
            if (column.excel.required())
            {
                richHeader.applyFont(column.header.length(), headerText.length(), requiredMarkerFont);
            }
            headerCell.setCellValue(richHeader);
            if (StringUtils.isNotEmpty(column.excel.prompt()))
            {
                ClientAnchor anchor = creationHelper.createClientAnchor();
                anchor.setCol1(i);
                anchor.setCol2(i + 2);
                anchor.setRow1(headerRow.getRowNum());
                anchor.setRow2(headerRow.getRowNum() + 3);
                Comment comment = drawing.createCellComment(anchor);
                comment.setString(creationHelper.createRichTextString(column.excel.prompt()));
                comment.setAuthor("物业报修系统");
                headerCell.setCellComment(comment);
            }
        }
        int firstDataRow = rowIndex;

        for (T item : list)
        {
            Row row = sheet.createRow(rowIndex++);
            for (int i = 0; i < columns.size(); i++)
            {
                ExportColumn column = columns.get(i);
                Object value = getExportValue(item, column);
                row.createCell(i).setCellValue(value == null ? StringUtils.EMPTY : String.valueOf(value));
            }
        }

        for (int i = 0; i < columns.size(); i++)
        {
            sheet.autoSizeColumn(i);
            ExportColumn column = columns.get(i);
            int configuredWidth = (int) Math.min(255 * 256, column.excel.width() * 256);
            if (sheet.getColumnWidth(i) < configuredWidth)
            {
                sheet.setColumnWidth(i, configuredWidth);
            }
            if (column.excel.combo().length > 0)
            {
                DataValidationHelper helper = sheet.getDataValidationHelper();
                DataValidationConstraint constraint = helper.createExplicitListConstraint(column.excel.combo());
                CellRangeAddressList range = new CellRangeAddressList(firstDataRow, firstDataRow + 999, i, i);
                DataValidation validation = helper.createValidation(constraint, range);
                validation.setShowErrorBox(true);
                validation.setSuppressDropDownArrow(true);
                sheet.addValidationData(validation);
            }
        }
    }

    private void writeResponse(HttpServletResponse response, Workbook workbook, String fileName) throws IOException
    {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + sanitizeFileName(fileName) + ".xlsx\"");
        workbook.write(response.getOutputStream());
    }

    private String sanitizeFileName(String fileName)
    {
        if (StringUtils.isEmpty(fileName))
        {
            return "export";
        }
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private Map<Integer, FieldBinding> buildImportBindings(Row headerRow)
    {
        Map<String, FieldBinding> byHeader = new LinkedHashMap<String, FieldBinding>();
        for (Field field : getAllFields(clazz))
        {
            Excel excel = field.getAnnotation(Excel.class);
            if (excel != null && excel.type() != Excel.Type.EXPORT && StringUtils.isNotEmpty(excel.name()))
            {
                byHeader.put(excel.name(), new FieldBinding(field, excel));
            }
        }

        Map<Integer, FieldBinding> bindings = new LinkedHashMap<Integer, FieldBinding>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++)
        {
            String header = dataFormatter.formatCellValue(headerRow.getCell(i)).trim();
            if (header.endsWith("*") || header.endsWith("＊"))
            {
                header = header.substring(0, header.length() - 1).trim();
            }
            FieldBinding binding = byHeader.get(header);
            if (binding != null)
            {
                bindings.put(i, binding);
            }
        }
        return bindings;
    }

    private Object convertImportValue(Cell cell, String text, Excel excel, Class<?> targetType)
    {
        String actualText = reverseReadConverter(text, excel.readConverterExp());
        if (String.class == targetType)
        {
            return actualText;
        }
        if (Integer.class == targetType || Integer.TYPE == targetType)
        {
            return Integer.parseInt(actualText);
        }
        if (Long.class == targetType || Long.TYPE == targetType)
        {
            return Long.parseLong(actualText);
        }
        if (Double.class == targetType || Double.TYPE == targetType)
        {
            return Double.parseDouble(actualText);
        }
        if (Float.class == targetType || Float.TYPE == targetType)
        {
            return Float.parseFloat(actualText);
        }
        if (BigDecimal.class == targetType)
        {
            return new BigDecimal(actualText);
        }
        if (Boolean.class == targetType || Boolean.TYPE == targetType)
        {
            return Convert.toBool(actualText, false);
        }
        if (Date.class == targetType)
        {
            if (cell != null && cell.getCellType() == CellType.NUMERIC)
            {
                return cell.getDateCellValue();
            }
            return DateUtils.parseDate(actualText);
        }
        return actualText;
    }

    private String reverseReadConverter(String value, String converterExp)
    {
        if (StringUtils.isEmpty(converterExp))
        {
            return value;
        }
        String[] items = converterExp.split(",");
        for (String item : items)
        {
            String[] pair = item.split("=");
            if (pair.length == 2 && StringUtils.equals(pair[1], value))
            {
                return pair[0];
            }
        }
        return value;
    }

    private List<ExportColumn> getExportColumns(Class<?> type)
    {
        List<ExportColumn> columns = new ArrayList<ExportColumn>();
        for (Field field : getAllFields(type))
        {
            Excel excel = field.getAnnotation(Excel.class);
            if (excel != null && excel.isExport() && excel.type() != Excel.Type.IMPORT)
            {
                columns.add(new ExportColumn(field, excel.name(), excel, excel.targetAttr()));
            }

            Excels excels = field.getAnnotation(Excels.class);
            if (excels != null)
            {
                for (Excel item : excels.value())
                {
                    columns.add(new ExportColumn(field, item.name(), item, item.targetAttr()));
                }
            }
        }
        return columns;
    }

    private Object getExportValue(Object bean, ExportColumn column) throws IllegalAccessException
    {
        column.field.setAccessible(true);
        Object value = column.field.get(bean);
        if (value != null && StringUtils.isNotEmpty(column.targetAttr))
        {
            value = getNestedValue(value, column.targetAttr);
        }

        if (value == null)
        {
            return column.excel.defaultValue();
        }
        if (StringUtils.isNotEmpty(column.excel.readConverterExp()))
        {
            value = applyReadConverter(String.valueOf(value), column.excel.readConverterExp());
        }
        if (value instanceof Date && StringUtils.isNotEmpty(column.excel.dateFormat()))
        {
            return DateUtils.parseDateToStr(column.excel.dateFormat(), (Date) value);
        }
        if (value instanceof LocalDateTime && StringUtils.isNotEmpty(column.excel.dateFormat()))
        {
            return DateUtils.parseDateToStr(column.excel.dateFormat(), DateUtils.toDate((LocalDateTime) value));
        }
        if (value instanceof LocalDate && StringUtils.isNotEmpty(column.excel.dateFormat()))
        {
            return DateUtils.parseDateToStr(column.excel.dateFormat(), DateUtils.toDate((LocalDate) value));
        }
        if (StringUtils.isNotEmpty(column.excel.suffix()))
        {
            return value + column.excel.suffix();
        }
        return value;
    }

    private Object getNestedValue(Object bean, String targetAttr) throws IllegalAccessException
    {
        Object current = bean;
        for (String part : targetAttr.split("\\."))
        {
            if (current == null)
            {
                return null;
            }
            Field nestedField = findField(current.getClass(), part);
            if (nestedField == null)
            {
                return null;
            }
            nestedField.setAccessible(true);
            current = nestedField.get(current);
        }
        return current;
    }

    private String applyReadConverter(String value, String converterExp)
    {
        String[] items = converterExp.split(",");
        for (String item : items)
        {
            String[] pair = item.split("=");
            if (pair.length == 2 && StringUtils.equals(pair[0], value))
            {
                return pair[1];
            }
        }
        return value;
    }

    private List<Field> getAllFields(Class<?> type)
    {
        List<Field> fields = new ArrayList<Field>();
        Class<?> current = type;
        while (current != null && current != Object.class)
        {
            for (Field field : current.getDeclaredFields())
            {
                fields.add(field);
            }
            current = current.getSuperclass();
        }
        return fields;
    }

    private Field findField(Class<?> type, String name)
    {
        Class<?> current = type;
        while (current != null && current != Object.class)
        {
            try
            {
                return current.getDeclaredField(name);
            }
            catch (NoSuchFieldException e)
            {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private void setImportRowNum(T entity, Integer rowNum) throws IllegalAccessException
    {
        Field field = findField(clazz, "importRowNum");
        if (field != null && (Integer.class == field.getType() || Integer.TYPE == field.getType()))
        {
            field.setAccessible(true);
            field.set(entity, rowNum);
        }
    }

    private boolean isRowEmpty(Row row)
    {
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++)
        {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK
                    && StringUtils.isNotEmpty(dataFormatter.formatCellValue(cell)))
            {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void writeSheet(Workbook workbook, ExcelSheet<?> sheet) throws IllegalAccessException
    {
        ExcelUtil util = new ExcelUtil(sheet.getClazz());
        util.writeSheet(workbook, sheet.getSheetName(), sheet.getTitle(), (List) sheet.getList(), sheet.getClazz());
    }

    private static final class FieldBinding
    {
        private final Field field;
        private final Excel excel;

        private FieldBinding(Field field, Excel excel)
        {
            this.field = field;
            this.excel = excel;
        }
    }

    private static final class ExportColumn
    {
        private final Field field;
        private final String header;
        private final Excel excel;
        private final String targetAttr;

        private ExportColumn(Field field, String header, Excel excel, String targetAttr)
        {
            this.field = field;
            this.header = header;
            this.excel = excel;
            this.targetAttr = targetAttr;
        }
    }
}
