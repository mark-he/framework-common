package com.eagletsoft.common.export.excel;

import java.lang.reflect.Field;

public class DataMapping {
    private int col;
    private Field property;

    public Field getProperty() {
        return property;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setProperty(Field property) {
        this.property = property;
    }

}
