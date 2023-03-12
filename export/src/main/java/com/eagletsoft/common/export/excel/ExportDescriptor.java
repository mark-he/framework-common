package com.eagletsoft.common.export.excel;

import java.io.OutputStream;

public class ExportDescriptor {
    private String fileName;
    private String sheetName;
    private OutputStream outputStream;

    public ExportDescriptor(String fileName, String sheetName, OutputStream outputStream) {
        this.fileName = fileName;
        this.sheetName = sheetName;
        this.outputStream = outputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
