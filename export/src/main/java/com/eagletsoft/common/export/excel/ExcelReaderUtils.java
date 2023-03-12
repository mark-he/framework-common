package com.eagletsoft.common.export.excel;

import com.eagletsoft.boot.framework.common.utils.BeanUtils;
import com.eagletsoft.common.export.excel.meta.XlsModel;
import com.eagletsoft.common.export.excel.meta.XlsReadData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

public class ExcelReaderUtils {

    private static Workbook getWorkbook(String fileName, InputStream is) throws Exception {
        Workbook workbook = null;
        if (fileName.endsWith(".xls")) {
            workbook = new HSSFWorkbook(is);
        } else {
            workbook = new XSSFWorkbook(is);
        }
        return workbook;
    }
    public static <T> void read(String name, InputStream is, int sheetIndex, IReaderCallback<T> callback,  IDataMapper mapper) throws Exception {
        Workbook workbook = getWorkbook(name, is);
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        int rows = sheet.getPhysicalNumberOfRows();
        List<DataMapping> mappings = findDataMappings(callback.getModelClass());

        T bean = callback.getModelClass().newInstance();

        XlsModel xlsModel = AnnotationUtils.getAnnotation(callback.getModelClass(), XlsModel.class);
        for (int i = xlsModel.startRow(); i < rows; i++) {
            Row row = sheet.getRow(i);

            try {
                for (DataMapping mapping : mappings) {
                    Cell cell = row.getCell(mapping.getCol(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String value = getValue(cell);

                    Object convertedValue = mapper.map(mapping.getProperty(), value);
                    BeanUtils.setProperty(bean, mapping.getProperty().getName(), convertedValue);
                }
                callback.onRead(i, bean);
            }
            catch(Exception ex) {
                callback.onFailedRead(i, ex.getMessage());
                ex.printStackTrace();
                continue;
            }
        }
    }

    public static <T> void read(String name, InputStream is, int sheetIndex, IReaderCallback<T> callback) throws Exception {
        XlsModel xlsModel = AnnotationUtils.getAnnotation(callback.getModelClass(), XlsModel.class);
        IDataMapper mapper =(IDataMapper) BeanUtils.create(xlsModel.inputMapper());
        read(name, is, sheetIndex, callback, mapper);
    }

    private static String getValue(Cell hssfCell) {
        if (hssfCell.getCellType() == BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == NUMERIC) {
            // 返回数值类型的值
            Object inputValue = null;// 单元格值
            Long longVal = Math.round(hssfCell.getNumericCellValue());
            Double doubleVal = hssfCell.getNumericCellValue();
            if(Double.parseDouble(longVal + ".0") == doubleVal){   //判断是否含有小数位.0
                inputValue = longVal;
            }
            else{
                inputValue = doubleVal;
            }
            DecimalFormat df = new DecimalFormat("#.####");    //格式化为四位小数，按自己需求选择；
            return String.valueOf(df.format(inputValue));      //返回String类型
        } else {
            // 返回字符串类型的值
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }

    private static List<DataMapping> findDataMappings(Class modelClass) {
        List<DataMapping> mappings = new ArrayList<>();

        List<Field> fields = BeanUtils.findAllDeclaredFields(modelClass);
        for (Field f : fields) {
            XlsReadData xlsData = f.getAnnotation(XlsReadData.class);

            if (null != xlsData) {
                DataMapping mapping = new DataMapping();
                mapping.setProperty(f);
                mapping.setCol(xlsData.col());
                mappings.add(mapping);
            }
        }
        return mappings;
    }

}
