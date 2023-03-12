package com.eagletsoft.common.export.csv.impl;

import com.eagletsoft.common.export.csv.CsvSource;
import com.eagletsoft.common.export.csv.ViewRender;

import java.util.List;

/*

		CsvSource source = new ExportDataSource(
				exportList,
				new String[] {"会员名称(呢称)","手机号","最后一次活动时间","最后一次消费时间","账户余额","总消费","总充值","标签"}
				);
		String fileName = "会员分析_" + System.currentTimeMillis() + ".csv";
		resp.setContentType("application/octet-stream;charset=UTF-8");
		resp.setHeader("Content-Disposition", "attachment;fileName=" + fileName);

		EasyCsvGenerator.generate(source, resp.getOutputStream(), "GBK");

* */
public class ExportDataSource implements CsvSource<Object[]> {

	private List<Object[]> list;
	private String[] headers;
	private ViewRender render;

	public ExportDataSource(List<Object[]> list, String[] headers) {
		super();
		this.list = list;
		this.headers = headers;
	}
	
	public ExportDataSource(List<Object[]> data, String[] headers, ViewRender render) {
		this(data, headers);
		this.render = render;
	}
	
	@Override
	public List<Object[]> list() {
		return list;
	}

	@Override
	public String[] getHeaders() {
		return headers;
	}
	
	@Override
	public Object getData(int row, int col, String header) {
		Object[] obj = list.get(row);
		Object data = obj[col];
		if (null != render) {
			return render.render(obj, header, data);
		}
		return data;
	}
}
