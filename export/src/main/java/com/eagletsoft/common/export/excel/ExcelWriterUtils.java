package com.eagletsoft.common.export.excel;

import com.eagletsoft.boot.framework.common.i18n.MessageMaker;
import com.eagletsoft.boot.framework.common.utils.ApplicationUtils;
import com.eagletsoft.boot.framework.common.utils.BeanUtils;
import com.eagletsoft.common.export.excel.meta.XlsModel;
import com.eagletsoft.common.export.excel.meta.XlsWriteData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.annotation.AnnotationUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.TreeMap;

public class ExcelWriterUtils {
    public static void writeResponseHeader(String fileName, HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("GBK"), "ISO8859-1"));
            response.setCharacterEncoding("utf-8");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void write(String fileName, String sheetName, OutputStream outputStream, IPageDataProvider dataProvider) {
        MessageMaker messageMaker = ApplicationUtils.getBean(MessageMaker.class);

        XlsModel xlsModel = AnnotationUtils.getAnnotation(dataProvider.getModelClass(), XlsModel.class);

        IDataRenderer dataRenderer = (IDataRenderer) BeanUtils.create(xlsModel.outputRenderer());

        Workbook workbook = getWorkbook(fileName);
        Sheet sheet = workbook.createSheet(sheetName);
        sheet.setDefaultColumnWidth(15);

        int rowIndex = 0;
        //设置表头样式
        CellStyle style = setHeaderStyle(workbook);
        TreeMap<Integer, Column> headerMap = findHeaders(dataProvider.getModelClass());

        CellStyle cellStyle = setCellStyle(workbook);

        Row row = sheet.createRow(rowIndex++);
        for (Column column : headerMap.values()) {
            sheet.setDefaultColumnStyle(column.getIndex(), cellStyle);
            sheet.autoSizeColumn(column.getIndex());

            Cell headerCell = row.createCell(column.getIndex());
            if (null != messageMaker) {
                headerCell.setCellValue(messageMaker.make(column.getName()));
            } else {
                headerCell.setCellValue(column.getName());
            }
            headerCell.setCellStyle(style);
        }

        List data = null;
        while ((data = dataProvider.next()).size() > 0) {
            for (Object obj : data) {
                row = sheet.createRow(rowIndex++);
                for (Column column : headerMap.values()) {
                    Cell cell = row.createCell(column.getIndex());
                    cell.setCellValue(dataRenderer.render(column.getName(), column.getIndex(), obj, column.getProperty()));
                    cell.setCellStyle(cellStyle);
                }
            }
        }

        try {
            workbook.write(outputStream);
            outputStream.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Workbook getWorkbook(String fileName) {
        Workbook workbook = null;
        if (fileName.endsWith(".xls")) {
            workbook = new HSSFWorkbook();
        } else {
            workbook = new XSSFWorkbook();
        }
        return workbook;
    }

    private static CellStyle setHeaderStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        //水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER_SELECTION);
        //垂直居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        //设置字体
        Font cellFont = workbook.createFont();
        cellFont.setBold(true);
        cellStyle.setFont(cellFont);
        return cellStyle;
    }

    /**
     * 设置单元格样式
     */
    private static CellStyle setCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();

        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        //垂直居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        //设置字体
        /*
        Font cellFont = workbook.createFont();
        cellFont.setFontName("仿宋_GB2312");
        cellStyle.setFont(cellFont);
         */
        return cellStyle;
    }

    private static TreeMap findHeaders(Class modelClass) {
        List<Field> fields = BeanUtils.findAllDeclaredFields(modelClass);

        TreeMap<Integer, Column> headers = new TreeMap<>();
        for (Field f : fields) {
            XlsWriteData xlsWriteData = AnnotationUtils.getAnnotation(f, XlsWriteData.class);

            if (null != xlsWriteData) {
                headers.put(xlsWriteData.col(), new Column(xlsWriteData.col(), xlsWriteData.name(), f));
            }
        }
        return headers;
    }

    static class Column {
        private int index;
        private String name;
        private Field property;
        public Column(int index, String name, Field property) {
            this.index = index;
            this.name = name;
            this.property = property;

        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Field getProperty() {
            return property;
        }

        public void setProperty(Field property) {
            this.property = property;
        }

    }
}
