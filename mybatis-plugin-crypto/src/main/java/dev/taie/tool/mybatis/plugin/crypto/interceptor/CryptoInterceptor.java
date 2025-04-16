package dev.taie.tool.mybatis.plugin.crypto.interceptor;

import dev.taie.tool.mybatis.plugin.crypto.annotation.CryptoField;
import dev.taie.tool.mybatis.plugin.crypto.config.CryptoProperties;
import dev.taie.tool.mybatis.plugin.crypto.service.MybatisAutoCryptoService;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @date 2024/11/9
 */
@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}), @Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class})})
public class CryptoInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(CryptoInterceptor.class);

    private static final Map<String, Set<String>> MAPPER_METHOD_CACHE = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Boolean> CLASS_CACHE = new ConcurrentHashMap<>();

    private static final Pattern PATTERN = Pattern.compile("^param\\d+$");

    private final MybatisAutoCryptoService cryptoService;

    private final CryptoProperties cryptoProperties;

    public CryptoInterceptor(MybatisAutoCryptoService cryptoService, CryptoProperties cryptoProperties) {
        this.cryptoService = cryptoService;
        this.cryptoProperties = cryptoProperties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof ResultSetHandler) {
            return handleResultSet(invocation);
        } else if (invocation.getTarget() instanceof ParameterHandler) {
            handleParameter(invocation);
            return invocation.proceed();
        }
        return invocation.proceed();
    }

    private Object handleResultSet(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();
        if (result instanceof List) {
            List<?> list = (List<?>) result;
            for (Object obj : list) {
                handleCryptoFields(obj, false);
            }
        } else {
            handleCryptoFields(result, false);
        }
        return result;
    }

    private void handleParameter(Invocation invocation) throws Throwable {
        ParameterHandler parameterHandler = (ParameterHandler) invocation.getTarget();

        MetaObject metaObject = SystemMetaObject.forObject(parameterHandler);

        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("mappedStatement");
        BoundSql boundSql = (BoundSql) metaObject.getValue("boundSql");
        Object parameterObject = boundSql.getParameterObject();

        String mappedId = mappedStatement.getId();
        if (MAPPER_METHOD_CACHE.get(mappedId) == null) {
            synchronized (MAPPER_METHOD_CACHE) {
                updateCache(mappedId);
            }
        }

        // 处理参数对象
        if (parameterObject instanceof MapperMethod.ParamMap) {
            @SuppressWarnings("unchecked")
            MapperMethod.ParamMap<Object> paramMap = (MapperMethod.ParamMap<Object>) parameterObject;
            Set<String> paramNameSet = MAPPER_METHOD_CACHE.get(mappedId);
            if (paramNameSet != null) {
                for (String key : paramNameSet) {
                    handleCryptoFields(paramMap.get(key), true);
                }
            }
        } else {
            // 处理单个参数
            if (parameterObject != null) {
                handleCryptoFields(parameterObject, true);
            }
        }
    }

    private void updateCache(String mappedId) throws Throwable {

        if (MAPPER_METHOD_CACHE.get(mappedId) == null) {
            String className = mappedId.substring(0, mappedId.lastIndexOf("."));
            String methodName = mappedId.substring(mappedId.lastIndexOf(".") + 1);
            Class<?> mapperClass = Class.forName(className);

            Method[] methods = mapperClass.getMethods();

            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    Parameter[] parameters = method.getParameters();
                    Set<String> paramNameSet = new HashSet<>();
                    for (int i = 0, l = parameters.length; i < l; i++) {
                        Parameter parameter = parameters[i];
                        paramNameSet.add("param" + (i + 1));
                        Param paramAnnotation = parameter.getAnnotation(Param.class);
                        if (paramAnnotation != null) {
                            Matcher matcher = PATTERN.matcher(paramAnnotation.value());
                            if (matcher.matches()) {
                                log.error("The value of the @Param annotation in parameter {} is not recommended in this format \"{}\"", mappedId, PATTERN.pattern());
                            }
                        }
                    }
                    MAPPER_METHOD_CACHE.put(mappedId, paramNameSet);
                    break;
                }
            }
        }

    }

    private Boolean handleCryptoFields(Object obj, boolean isEncrypt) throws Exception {
        if (obj == null) return null;

        // 处理集合类型
        if (obj instanceof Collection) {
            for (Object item : (Collection<?>) obj) {
                handleCryptoFields(item, isEncrypt);
            }
            return null;
        } else if (obj instanceof Map) {
            for (Object value : ((Map<?, ?>) obj).values()) {
                handleCryptoFields(value, isEncrypt);
            }
            return null;
        } else if (obj.getClass().isArray()) {
            for (Object item : (Object[]) obj) {
                handleCryptoFields(item, isEncrypt);
            }
            return null;
        }

        Class<?> clazz = obj.getClass();
        if (Boolean.FALSE.equals(CLASS_CACHE.get(clazz))) {
            return Boolean.FALSE;
        }
        // 如果是基本类型或包装类型，直接返回
        if (!isProjectClass(clazz)) {
            return Boolean.FALSE;
        }
        // 获取所有字段（包括父类的字段）
        List<Field> fields = getAllFieldsList(clazz);
        Boolean flag = false;
        for (Field field : fields) {
            field.setAccessible(true);
            // 处理带有@CryptoField注解的字段
            CryptoField cryptoField = field.getAnnotation(CryptoField.class);
            Object value = field.get(obj);
            if (cryptoField != null) {
                flag = true;
                if (value instanceof String) {
                    String strValue = (String) value;
                    if (isEncrypt) {
                        field.set(obj, cryptoService.encrypt(strValue, cryptoField));
                    } else {
                        field.set(obj, cryptoService.decrypt(strValue, cryptoField));
                    }
                }
            } else {
                // 递归处理复杂对象
                if (value != null && isProjectClass(field.getType())) {
                    flag = handleCryptoFields(value, isEncrypt);
                }
            }
        }
        if (flag != null) {
            CLASS_CACHE.putIfAbsent(clazz, flag);
        }
        return flag;
    }

    private List<Field> getAllFieldsList(final Class<?> cls) {
        final List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(allFields, declaredFields);
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }

    private boolean isProjectClass(Class<?> clazz) {
        String canonicalName = clazz.getCanonicalName();
        String packageName = cryptoProperties.getPackageName();
        if (packageName == null) {
            log.error("请配置mybatis.plugin.crypto.packageName");
            System.exit(1);
        }
        return canonicalName.startsWith(packageName);
    }
}
