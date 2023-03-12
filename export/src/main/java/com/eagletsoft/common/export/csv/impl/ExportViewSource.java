package com.eagletsoft.common.export.csv.impl;

import com.eagletsoft.common.export.csv.CsvSource;
import com.eagletsoft.common.export.csv.ViewRender;
import com.eagletsoft.common.export.csv.meta.ExportField;
import com.eagletsoft.common.export.csv.meta.ExportViewRender;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/*
		String fileName = "会员登录记录_" + System.currentTimeMillis() + ".csv";
		resp.setContentType("application/octet-stream;charset=UTF-8");
		resp.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
		CsvSource source = new ExportViewSource(data, AppuserLoginLogViewForExcel.class);
		EasyCsvGenerator.generate(source, resp.getOutputStream(), "GBK");
* */
public class ExportViewSource<T> implements CsvSource<T> {
	private List list;
	private Class clazz;
	private List<Column> cols;
	private ViewRender render;
	
	public ExportViewSource(List list, Class clazz) {
		this.list = list;
		this.clazz = clazz;
		this.init();
	}

	public ExportViewSource(List list, Class clazz, ViewRender render) {
		this(list, clazz);
		this.render = render;
	}
	
	private void init() {
		try {
			ExportViewRender ann = (ExportViewRender)clazz.getAnnotation(ExportViewRender.class);
			if (null != ann && null != ann.value()) {
				render = ann.value().newInstance();
			}
			cols = this.loadDefinitions(clazz);
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public List<T> list() {
		return list;
	}

	@Override
	public String[] getHeaders() {
		return extractHeader(cols);
	}

	@Override
	public Object getData(int row, int col, String header) {
		Object obj = list.get(row);
		Column c = cols.get(col);
		try {
			Object data = PropertyUtils.getProperty(obj, c.fieldName);
			if (null != render) {
				return render.render(obj, c.fieldName, data);
			}
			return data;
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static String[] extractHeader(List<Column> definition) {
		String[] header = new String[definition.size()];
		int i = 0;
		for (Column col : definition) {
			header[i++] = col.getTitle();
		}
		return header;
	}

	private static List<Column> loadDefinitions(Class clazz) {
		List<Column> cols = new ArrayList<>();
		
		Field[] fields = clazz.getDeclaredFields();
		
		for (Field f : fields) {
			ExportField ann = f.getAnnotation(ExportField.class);
			if (null != ann) {
				Column col = new Column();
				col.position = ann.position();
				col.fieldName = f.getName();
				col.title = ann.name();
				cols.add(col);
			}
		}
		
		Collections.sort(cols, new Comparator<Column>() {
			@Override
			public int compare(Column o1, Column o2) {
				return o1.position - o2.position;
			}
		});
		return cols;
	}
	
	private static class Column {
		public int position;
		public String title;
		public String fieldName;
		public int getPosition() {
			return position;
		}
		public void setPosition(int position) {
			this.position = position;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
	}

}
