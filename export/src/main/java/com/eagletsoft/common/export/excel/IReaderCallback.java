package com.eagletsoft.common.export.excel;

public interface IReaderCallback<T>{
    void onRead(int row, T model);
    void onFailedRead(int row, String message);
    Class<T> getModelClass();
}
