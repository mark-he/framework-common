package com.eagletsoft.framework.plugin.dataview.validator.impl;

import com.eagletsoft.boot.framework.api.utils.ApiResponse;
import com.eagletsoft.boot.framework.common.i18n.MessageMaker;
import com.eagletsoft.boot.framework.common.utils.BeanUtils;
import com.eagletsoft.boot.framework.common.utils.JsonUtils;
import com.eagletsoft.framework.plugin.dataview.def.types.*;
import com.eagletsoft.framework.plugin.dataview.utils.DataViewUtils;
import com.eagletsoft.framework.plugin.dataview.validator.impl.ValidationContext;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE )
public class ApiMetaAspect {
    private static Logger LOGGER = LoggerFactory.getLogger(ApiMetaAspect.class);

    @Autowired
    private MessageMaker messageMaker;

    @Pointcut("execution(* com.eagletsoft..api..*Api.*(..))")
    private void executer(){}

    @Around(value = "executer()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        boolean isMeta = null != request.getParameter("_meta");
        boolean isDocs = null != request.getParameter("_docs");

        if (isMeta || isDocs) {
            Signature signature = point.getSignature();

            MethodSignature methodSignature = (MethodSignature) signature;
            Annotation[][] parameterAnnotations = methodSignature.getMethod().getParameterAnnotations();
            List<Method> methods = BeanUtils.findAllDeclaredMethods(point.getTarget().getClass());
            Map<TypeVariable<?>, Type> genericTypeMap = null;

            if ((Type)point.getTarget().getClass().getGenericSuperclass() instanceof ParameterizedType) {
                genericTypeMap = TypeUtils.getTypeArguments((ParameterizedType)point.getTarget().getClass().getGenericSuperclass());
            }

            int idx = 0;
            Map pathMap = new HashMap();
            Map requestBodyMap = new HashMap();
            Map responseBodyMap = new HashMap();

            for (Annotation[] annotations : parameterAnnotations) {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof PathVariable) {
                        if (isDocs) {
                            Map map = new HashMap();
                            map.put(methodSignature.getParameterNames()[idx], new Meta(methodSignature.getParameterNames()[idx], methodSignature.getParameterTypes()[idx].getSimpleName()));
                            pathMap.putAll(map);
                        }
                    } else if (annotation instanceof RequestBody) {
                        for (Method method : methods) {
                            if (method.getName().equals(methodSignature.getName())) {
                                Class clazz = method.getParameterTypes()[idx];
                                Type genericType = method.getGenericParameterTypes()[idx];

                                Map map = print(clazz, genericType, genericTypeMap);
                                requestBodyMap.putAll(map);
                                break;
                            }
                        }
                    } else {
                    }
                }
                idx++;
            }
            if (isDocs) {
                Type type = methodSignature.getMethod().getGenericReturnType();
                Map map = print(methodSignature.getMethod().getReturnType(), type, genericTypeMap);
                responseBodyMap.putAll(map);
            }

            if (isMeta) {
                response.getWriter().write(JsonUtils.writeValue(ApiResponse.make(requestBodyMap)));
            } else {
                Map output = new HashMap();
                output.put("path", pathMap);
                output.put("request", requestBodyMap);
                output.put("response", responseBodyMap);
                response.getWriter().write(JsonUtils.writeValue(ApiResponse.make(output)));
            }
        }
        else {
            Object ret = point.proceed();
            return ret;
        }
        return null;
    }

    protected Type findFromMap(String name, Map<TypeVariable<?>, Type> map) {
        if (null == map) {
            return null;
        }

        for(TypeVariable tv : map.keySet()) {
            if (tv.getName().equals(name)) {
                return map.get(tv);
            }
        }
        return null;
    }

    /*
     * 虽然有优化空间，但我已经不想再动这个代码了。JAVA 的泛型真的是糟糕的设计
     * */
    protected Map print(Class clazz, Type genericType, Map<TypeVariable<?>, Type> genericTypeMap) {
        if (null == genericTypeMap) {
            genericTypeMap = new HashMap<>();
        }
        if (null != genericType) {

            Type rawType = this.findFromMap(genericType.getTypeName(), genericTypeMap);
            if (rawType instanceof ParameterizedType) {
                clazz = (Class) ((ParameterizedType) rawType).getRawType();
            } else if (rawType instanceof Class) {
                clazz = (Class) rawType;
            }
        }
        Map ret = new HashMap();
        ValidationContext context = DataViewUtils.loadContext(clazz);

        Map<TypeVariable<?>, Type> currentGenericTypeMap = null;
        if (genericType instanceof ParameterizedType) {
            currentGenericTypeMap = TypeUtils.getTypeArguments((ParameterizedType)genericType);
        }
        List<Field> fs = BeanUtils.findAllDeclaredFields(clazz);
        for (Field f : fs) {
            Class subClazz = f.getType();
            Type subGenericType = f.getGenericType();
            Type rawType = this.findFromMap(f.getGenericType().getTypeName(), currentGenericTypeMap);
            if (null == rawType) {
                rawType = f.getGenericType();
            }

            if (rawType instanceof TypeVariable) {
                rawType = this.findFromMap(rawType.getTypeName(), genericTypeMap);
            }
            if (null != rawType) {
                if (rawType instanceof ParameterizedType) {
                    subClazz = (Class) ((ParameterizedType) rawType).getRawType();
                } else if (rawType instanceof Class) {
                    subClazz = (Class) rawType;
                } else {

                }
            } else {
                rawType = f.getGenericType();
            }

            if (subClazz.getTypeName().startsWith("com.eagletsoft.")) {
                Map map = print(subClazz, rawType, genericTypeMap);
                ret.put(f.getName(), map);
            } else if (Collection.class.isAssignableFrom(subClazz)) {
                List list = new ArrayList();
                if (rawType instanceof ParameterizedType) {
                    Type[] actualTypeArguments = ((ParameterizedType) rawType).getActualTypeArguments();
                    Type listItemRawType = actualTypeArguments[0];

                    if (listItemRawType instanceof TypeVariable) {
                        rawType = this.findFromMap(listItemRawType.getTypeName(), currentGenericTypeMap);
                    }
                    if (null == rawType) {
                        rawType = listItemRawType;
                    }
                    if (rawType instanceof TypeVariable) {
                        rawType = this.findFromMap(rawType.getTypeName(), genericTypeMap);
                    }
                    if (rawType instanceof ParameterizedType) {
                        subClazz = (Class) ((ParameterizedType) rawType).getRawType();
                    } else if (rawType instanceof Class) {
                        subClazz = (Class) rawType;
                    }
                    Map map = print(subClazz, rawType, currentGenericTypeMap);
                    list.add(map);
                }
                else {

                }
                ret.put(f.getName(), list);
            } else if (Map.class.isAssignableFrom(subClazz)) {
                ret.put(f.getName(), new HashMap());
            } else {
                Meta meta = getMeta(context, clazz, f);
                ret.put(f.getName(), meta);
            }
        }

        return ret;
    }

    protected Meta getMeta(String name, PathVariable v) {
        Meta m = new Meta(v.value() != null ? v.value() : name, "");
        m.setRequired(v.required());
        m.setDescription(v.name());
        return m;
    }

    protected Meta getMeta(ValidationContext context, Class t, Field f) {

        Meta m = new Meta(f.getName(), f.getType().getSimpleName());
        ValidationContext.DataFieldSource s = context.findDataField(f.getName());
        Class root = t;
        if (null != s) {
            root = s.root;
            m.setRequired(s.dataField.required());
            IType type = TypeRegister.getInstance().find(s.dataField.value());
            if (type instanceof StringType) {
                if (null != ((StringType) type).getMax()) {
                    m.setMax(((StringType) type).getMax().longValue());
                }
                if (null != ((StringType) type).getMin()) {
                    m.setMin(((StringType) type).getMin().longValue());
                }
            } else  if (type instanceof DecimalType) {
                if (null != ((DecimalType) type).getMax()) {
                    m.setMax(((DecimalType) type).getMax());
                }
                if (null != ((DecimalType) type).getMin()) {
                    m.setMin(((DecimalType) type).getMin());
                }
            } else  if (type instanceof IntegerType) {
                if (null != ((IntegerType) type).getMax()) {
                    m.setMax(((IntegerType) type).getMax());
                }
                if (null != ((IntegerType) type).getMin()) {
                    m.setMin(((IntegerType) type).getMin());
                }
            } else if (type instanceof DateType) {
                if (null != ((DateType) type).getMax()) {
                    m.setMax(((DateType) type).getMax().getTime());
                }
                if (null != ((DateType) type).getMin()) {
                    m.setMin(((DateType) type).getMin().getTime());
                }
            }
        }

        String key = root.getSimpleName() + "@" + f.getName();
        String title = messageMaker.make(key);
        if (!key.equals(title)) {
            m.setTitle(title);
        }

        if (null == m.getTitle()) {
            title = messageMaker.make(f.getName());
            if (!title.equals(f.getName())) {
                m.setTitle(title);
            } else {
                m.setTitle(key);
            }
        }
        return m;
    }


    public static class Meta {
        private String name;
        private String type;
        private boolean required;
        private String description;
        private String title;
        private Long max;
        private Long min;

        public Meta(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public Long getMax() {
            return max;
        }

        public void setMax(Long max) {
            this.max = max;
        }

        public Long getMin() {
            return min;
        }

        public void setMin(Long min) {
            this.min = min;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}