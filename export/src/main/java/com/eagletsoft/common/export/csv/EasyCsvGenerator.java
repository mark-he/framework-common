package com.eagletsoft.common.export.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Author: Mark HE
 * To replace EasyExcelOp.java as its low performance.
 * 
 * */
public class EasyCsvGenerator {

	public static void generateInBatch(int batchId, CsvSource source, OutputStream os, String encoding) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
			String[] headers = source.getHeaders();
			CSVPrinter csvPrinter = null;
			CSVFormat csvFormat = CSVFormat.DEFAULT;
			if (batchId == 0) {
				csvFormat = CSVFormat.DEFAULT.withHeader(headers);
			}
			csvPrinter = new CSVPrinter(osw, csvFormat);
			List list = source.list();
			if (null != list) {
				for (int i = 0; i < list.size(); i++) {
					csvPrinter.printRecord(extractRow(source, i, headers));
				}
			}
			csvPrinter.flush();
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void generate(CsvSource source, OutputStream os, String encoding) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
			String[] headers = source.getHeaders();
	        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers);
	        CSVPrinter csvPrinter = new CSVPrinter(osw, csvFormat);
	        
	        List list = source.list();
			if (null != list) {
				for (int i = 0; i < list.size(); i++) {
					csvPrinter.printRecord(extractRow(source, i, headers));
				}
			}
			csvPrinter.close(true);
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static List<Object> extractRow(CsvSource source, int row, String[] headers) {
		List<Object> ret = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		int col = 0;
		for (String h : headers) {
			Object data = source.getData(row, col++, h);
			if (data instanceof Date) {
				data = sdf.format((Date)data);
			}
			ret.add(data);
		}
		return ret;
	}
}
