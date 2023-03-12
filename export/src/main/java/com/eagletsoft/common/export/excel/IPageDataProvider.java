package com.eagletsoft.common.export.excel;
import java.util.List;

public interface IPageDataProvider<T> {
    List next();
    Class<T> getModelClass();
}
