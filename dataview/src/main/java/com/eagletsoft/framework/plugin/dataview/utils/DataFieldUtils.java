package com.eagletsoft.framework.plugin.dataview.utils;

import com.eagletsoft.boot.framework.common.utils.BeanUtils;
import com.eagletsoft.framework.plugin.dataview.def.functions.ExpressionPresenter;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataField;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataView;
import com.eagletsoft.framework.plugin.dataview.def.types.IType;
import com.eagletsoft.framework.plugin.dataview.def.types.TypeRegister;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

public class DataFieldUtils {
    public static void normalizeBean(Collection col) {
        for (Object sub : col) {
            DataView dv = sub.getClass().getAnnotation(DataView.class);
            if (null != dv) {
                normalizeBean(sub);
            }
        }
    }

    public static void normalizeBean(Object bean) {
        try {
            if (null == bean) {
                return;
            }

            if (bean instanceof Collection) {
                normalizeBean((Collection)bean);
            }
            else {
                DataView dv = bean.getClass().getAnnotation(DataView.class);
                if (null != dv) {
                    List<Field> fields = BeanUtils.findAllDeclaredFields(bean.getClass());
                    for (Field f : fields) {
                        Object current =normalize(bean, f);
                        if (null != current) {
                            if (current instanceof Collection) {
                                normalizeBean((Collection)current);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Object normalize(Object bean, Field field) {
        return normalize(bean, field, false, false);
    }

    public static Object normalize(Object bean, Field field, boolean isOnCreated, boolean isOnUpdated) {
        try {
            Object current = PropertyUtils.getProperty(bean, field.getName());
            Object newValue = current;
            DataField dataField = DataViewUtils.findDataField(field);
            if (null != dataField && StringUtils.isNotEmpty(dataField.value())) {
                String typeName = dataField.value();

                IType type = TypeRegister.getInstance().find(typeName);
                if (null != type) {
                    newValue = type.format(current);
                }

                if (isOnCreated && !StringUtils.isEmpty(dataField.onCreated())) {
                    newValue = ExpressionPresenter.apply(dataField.onCreated(), bean);
                }

                if (isOnUpdated && !StringUtils.isEmpty(dataField.onUpdated())) {
                    newValue = ExpressionPresenter.apply(dataField.onUpdated(), bean);
                }

                if (newValue != current) {
                    PropertyUtils.setProperty(bean, field.getName(), newValue);
                }
            }
            return newValue;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
