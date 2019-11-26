package com.example.demo.service.impl;

import com.example.demo.common.MyMapper;
import com.example.demo.common.PageData;
import com.example.demo.common.Query;
import com.example.demo.common.annontation.ValidAnnontation;
import com.example.demo.common.exception.AppException;
import com.example.demo.common.uuid.UUIDUtil;
import com.example.demo.constant.EntityConst;
import com.example.demo.service.BaseService;
import com.example.demo.util.AssertUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @auther: raohr
 * @Title:
 * @Description:
 * @Date: 2019/11/25 10:37
 * @param:
 * @return:
 * @throws:
 */
public abstract class BaseServiceImpl<T> implements BaseService<T> {
    @Autowired
    protected MyMapper<T> mapper;

    /**
     * 当前泛型真实类型的Class
     */
    private Class<T> modelClass;

    public BaseServiceImpl() {
        // 获得具体model，通过反射来根据属性条件查找数据
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        modelClass = (Class<T>) pt.getActualTypeArguments()[0];
    }

    @Override
    public T save(T model) throws Exception {
        setInit(model, EntityConst.ENTITY_ADD);
        // 检查参数，是否符合要求
        validData(model, getFieldList(), false);
        mapper.insertSelective(model);
        return model;
    }

    @Override
    public void updateSelective(T model) throws Exception {
        setInit(model, EntityConst.ENTITY_UPDATE);
        // 检查参数，是否符合要求
        validData(model, getFieldList(), true);
        mapper.updateByPrimaryKeySelective(model);
    }

    @Override
    public void update(T model) throws Exception {
        setInit(model, EntityConst.ENTITY_UPDATE);
        // 检查参数，是否符合要求
        validData(model, getFieldList(), false);
        mapper.updateByPrimaryKey(model);
    }

    @Override
    public int updateByQuery(T model, Query query) throws Exception {
        setInit(model, EntityConst.ENTITY_UPDATE);
        Example example = getExample(query);
        return mapper.updateByExampleSelective(model, example);
    }

    @Override
    public void directUpdateSelective(T model) throws Exception {
        // 校验参数格式
        List<Field> fieldList = getFieldList();
        validData(model, fieldList, true);
        mapper.updateByPrimaryKeySelective(model);
    }

    @Override
    public T directSave(T model) throws Exception {
        List<Field> fieldList = getFieldList();
        // 检查参数，是否符合要求
        validData(model, fieldList, false);
        mapper.insertSelective(model);
        return model;
    }

    @Override
    public void directUpdate(T model) throws Exception {
        // 检查参数，是否符合要求
        List<Field> fieldList = getFieldList();
        validData(model, fieldList, true);
        mapper.updateByPrimaryKey(model);
    }

    @Override
    public <E> E findOnePropertyValueById(String id, String property) throws Exception {
        T t = findSomePropertiesById(id, property);
        if (t == null) {
            return null;
        }
        Method method = modelClass.getMethod("get" + property.substring(0, 1).toUpperCase() + property.substring(1));
        return (E) method.invoke(t);
    }

    @Override
    public T findSomePropertiesById(String id, String... properties) {
        String idProperty = null;
        List<Field> fieldList = getFieldList();
        for (Field f : fieldList) {
            if (f.getAnnotation(Id.class) != null) {
                idProperty = f.getName();
                break;
            }
        }
        if (idProperty == null) {
            throw new IllegalStateException(modelClass.getName() + " has no id");
        }
        Example example = getExample(properties);
        example.createCriteria().andEqualTo(idProperty, id);
        List<T> list = mapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public T findById(String id) throws Exception {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public T selectOne(T model) {
        return mapper.selectOne(model);
    }

    @Override
    public T selectOne(Query query) {
        List<T> list = findAll(query);
        int listSize = list.size();
        if (listSize > 1) {
            throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        }
        return listSize == 0 ? null : list.get(0);
    }

    @Override
    public int count(Query query) {
        Example example = getExample(query);
        return mapper.selectCountByExample(example);
    }

    @Override
    public <E> List<E> findOnePropertyValueList(Query query, String property) throws Exception {
        List<T> list = findSomePropertiesList(query, property);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }

        Method method = modelClass.getMethod("get" + property.substring(0, 1).toUpperCase() + property.substring(1));

        List<E> valueList = new ArrayList<>();
        for (T t : list) {
            Object value = method.invoke(t);
            valueList.add((E) value);
        }
        return valueList;
    }

    @Override
    public List<T> findSomePropertiesList(Query query, String... fieldNames) {
        Example example = getExample(query, fieldNames);
        List<T> list = mapper.selectByExample(example);

        if (list != null) {
            list.remove(null);
        }

        return list;
    }

    @Override
    public List<T> findAll(Query query) {
        Example example = getExample(query);
        return mapper.selectByExample(example);
    }

    @Override
    public PageData selectPage(Query query, String... fieldNames) {
        Example example = getExample(query, fieldNames);
        PageHelper.startPage(query.getCurrentPage(), query.getPageSize());
        PageInfo<T> pageInfo = new PageInfo<>(mapper.selectByExample(example));
        return PageData.data(pageInfo);
    }

    @Override
    public PageData selectPage(Query query) {
        Example example = getExample(query);
        PageHelper.startPage(query.getCurrentPage(), query.getPageSize());
        PageInfo<T> pageInfo = new PageInfo<>(mapper.selectByExample(example));
        return PageData.data(pageInfo);
    }

    @Override
    public void deleteFalse(String id) throws Exception {
        List<Field> fieldList = getFieldList();
        // 检测是否有逻辑删字段、 找到id字段
        boolean hasIsDelField = false;
        String idFieldName = null;
        for (Field field : fieldList) {
            if (hasIsDelField && idFieldName != null) {
                break;
            }
            if (field.getName().equals("isDel") || field.getName().equals("del")) {
                hasIsDelField = true;
                continue;
            }
            if (field.getAnnotation(Id.class) != null) {
                idFieldName = field.getName();
                continue;
            }
        }
        AssertUtil.check(!hasIsDelField, "当前数据没有假删除条件，请核对业务进行操作！");
        AssertUtil.isNull(idFieldName, "未找到主键属性");
        T model = modelClass.newInstance();
        setBeanValue(modelClass, idFieldName, model, id);
        try {
            setBeanValue(modelClass, "isDel", model, "1");
        } catch (NoSuchMethodException e) {
            setBeanValue(modelClass, "del", model, true);
        }
        updateSelective(model);
    }

    @Override
    public void deleteById(String id) throws Exception {
        mapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteByIds(String ids) throws Exception {
        for (String id : ids.split(";")) {
            deleteById(id);
        }
    }

    @Override
    public int deleteFalseByQuery(Query q) {
        try {
            Object model = modelClass.newInstance();

            try {
                setBeanValue(modelClass, "isDel", model, "1");
            } catch (NoSuchMethodException e) {
                setBeanValue(modelClass, "del", model, true);
            }

            return updateByQuery((T) model, q);
        } catch (Exception e) {
            throw new AppException("当前数据没有假删除条件，请核对业务进行操作！");
        }
    }

    /**
     * 设置目标属性
     *
     * @param fieldNames
     * @return
     */
    private Example getExample(String... fieldNames) {
        if (fieldNames == null || fieldNames.length == 0) {
            throw new IllegalArgumentException("fieldNames is must not empty");
        }
        Example example = new Example(modelClass);
        example.selectProperties(fieldNames);
        return example;
    }

    /**
     * 设置查询条件，用于查询全部字段
     *
     * @param query
     * @return
     */
    private Example getExample(Query query) {
        Example example = new Example(modelClass);
        setQuery(example, query);
        return example;
    }

    /**
     * 设置属性
     *
     * @param query
     * @param fieldNames
     * @return
     */
    private Example getExample(Query query, String... fieldNames) {
        if (fieldNames == null || fieldNames.length == 0) {
            throw new IllegalArgumentException("fieldNames is must not empty");
        }

        Example example = new Example(modelClass);
        example.selectProperties(fieldNames);
        setQuery(example, query);
        return example;
    }

    /**
     * 设置查询条件
     * 只能设置实体类中存在的属性相关条件
     *
     * @param example
     * @param query
     */
    private void setQuery(Example example, Query query) {
        Example.Criteria criteria = example.createCriteria();

        // 排序
        String orderBy = (String) query.get("orderBy");
        if (orderBy != null) {
            example.setOrderByClause(orderBy + "");
        }

        // 取实体类中的属性列表
        List<String> fieldNameList = getFieldNameList();

        // 默认不包含已删除的
        Boolean withDel = (Boolean) query.get("withDel");
        if (withDel == null || !withDel) {
            if (fieldNameList.contains("isDel")) {
                criteria.andEqualTo("isDel", "0");
            } else if (fieldNameList.contains("del")) {
                criteria.andEqualTo("del", false);
            }
        }

        Iterator iterator = query.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            Object val = entry.getValue();

            // 若包含该属性，则作为条件
            if (fieldNameList.contains(key)) {
                criteria.andEqualTo(key, val);
                continue;
            }

            // 跳过不符合的其他条件
            String[] keys = key.split("_");
            if (keys.length != 2 || !fieldNameList.contains(keys[0])) {
                continue;
            }

            // 设置逻辑条件
            String fieldName = keys[0];
            String operaStr = keys[1];
            if ("notEqual".equalsIgnoreCase(operaStr)) {
                criteria.andNotEqualTo(fieldName, val);
            } else if ("like".equalsIgnoreCase(operaStr)) {
                if ((val + "").contains("%")) {
                    criteria.andLike(fieldName, val + "");
                } else {
                    criteria.andLike(fieldName, "%" + val + "%");
                }
            } else if ("less".equalsIgnoreCase(operaStr)) {
                criteria.andLessThan(fieldName, val);

            } else if ("greater".equalsIgnoreCase(operaStr)) {
                criteria.andGreaterThanOrEqualTo(fieldName, val);

            } else if ("le".equalsIgnoreCase(operaStr)) {
                criteria.andLessThanOrEqualTo(fieldName, val);
            } else if ("ge".equalsIgnoreCase(operaStr)) {
                criteria.andGreaterThanOrEqualTo(fieldName, val);
            } else if ("in".equalsIgnoreCase(operaStr)) {
                if (val instanceof Iterable) {
                    criteria.andIn(fieldName, (Iterable) val);
                }
            } else if ("notIn".equalsIgnoreCase(operaStr)) {
                if (val instanceof Iterable) {
                    criteria.andNotIn(fieldName, (Iterable) val);
                }
            } else if ("between".equalsIgnoreCase(operaStr)) {
                if (val instanceof Object[]) {
                    Object[] arr = (Object[]) val;
                    if (arr.length >= 2) {
                        criteria.andBetween(fieldName, arr[0], arr[1]);
                    }
                }
            }

        }
    }

    /**
     * 检查数据
     *
     * @param model
     * @param fieldList
     * @param ignoreNullField
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws AppException
     */
    private void validData(T model, List<Field> fieldList, boolean ignoreNullField) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, AppException {
        for (Field field : fieldList) {
            if (field.isAnnotationPresent(ValidAnnontation.class)) {
                ValidAnnontation validAnnontation = field.getAnnotation(ValidAnnontation.class);
                String fieldName = field.getName();
                Method methodGet = getMethodGet(modelClass, fieldName);
                Object data = methodGet.invoke(model);
                if (ignoreNullField && data == null) {
                    continue;
                }
                if (validAnnontation.allowEmpty() && (data == null || "".equals(data + ""))) {
                    continue;
                }
                String message = StringUtils.isBlank(validAnnontation.message()) ? fieldName + "格式不正确" : validAnnontation.message();
                if (data == null || StringUtils.isBlank(data + "")) {
                    throw new AppException(message);
                }
                if (StringUtils.isNotBlank(validAnnontation.regexp())) {
                    if (!Pattern.matches(validAnnontation.regexp(), data + "")) {
                        throw new AppException(message);
                    }
                }
            }
        }
    }

    /**
     * @auther: caoqiuliang
     * @Title: getAllFieldName
     * @Description: 获取所有属性名称
     * @Date: 2019/5/30 15:18
     * @param:
     * @return: java.util.List<java.lang.String>
     * @throws:
     */
    private List<String> getFieldNameList() {
        List<Field> fields = getFieldList();

        List<String> fieldNames = new ArrayList<>();
        fields.stream().forEach(field -> fieldNames.add(field.getName()));

        return fieldNames;
    }

    /**
     * @auther: tuweihua
     * @Title:
     * @Description:获取对象所有属性
     * @Date: 2018/11/19 13:44
     * @param:
     * @return:
     * @throws:
     */
    private List<Field> getFieldList() {
        List<Field> fieldList = new ArrayList<>(Arrays.asList(modelClass.getDeclaredFields()));
        for (Field f : modelClass.getSuperclass().getDeclaredFields()) {
            fieldList.add(f);
        }
        for (int size = fieldList.size(), i = size - 1; i >= 0; i--) {
            Field field = fieldList.get(i);
            if (field.getName().equalsIgnoreCase("serialVersionUID")) {
                fieldList.remove(field);
                break;
            }
        }
        return fieldList;
    }

    /**
     * 设置数据
     *
     * @param clazz
     * @param fieldName
     * @param model
     * @param value
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void setBeanValue(Class clazz, String fieldName, Object model, Object value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName = getMethodName(fieldName);
        Method methodSet = clazz.getMethod("set" + methodName, value.getClass());
        methodSet.invoke(model, value);
    }

    /**
     * 获取get方法
     *
     * @param clazz
     * @param fieldName
     * @return
     * @throws NoSuchMethodException
     */
    private Method getMethodGet(Class clazz, String fieldName) throws NoSuchMethodException {
        String methodName = getMethodName(fieldName);
        return clazz.getMethod("get" + methodName);
    }

    /**
     * 获取属性名对应的方法名后缀
     *
     * @param fieldName
     * @return
     */
    private String getMethodName(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }


    private void setInit(Object obj, int type) throws Exception {
        Class clazz = obj.getClass();
        List<Field> fieldList = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        for (Field f : clazz.getSuperclass().getDeclaredFields()) {
            fieldList.add(f);
        }
        for (Field field : fieldList) {
            String fieldName = field.getName();
            if (fieldName.equalsIgnoreCase("serialVersionUID")) {
                continue;
            }
            // 跳过已设置值得属性
            Object fieldValue = getFieldValue(clazz, fieldName, obj);
            if (fieldValue != null) {
                continue;
            }
            if (field.isAnnotationPresent(Id.class)) {
                if (String.class.getName().equals(field.getGenericType().getTypeName())) {
                    setBeanValue(clazz, fieldName, obj, UUIDUtil.get32UUID());
                }
                continue;
            }
            if (type == EntityConst.ENTITY_ADD) {
                switch (fieldName) {
                    case EntityConst.ENTITY_CREATED_BY:
                        setBeanValue(clazz, fieldName, obj, getUserId());
                        break;
                    case EntityConst.ENTITY_CREATED_BY_NAME:
                        setBeanValue(clazz, fieldName, obj, getUserName());
                        break;
                    case EntityConst.ENTITY_CREATED_TIME:
                        setBeanValue(clazz, fieldName, obj, new Date());
                        break;
                    default:
                        break;
                }
            } else if (type == EntityConst.ENTITY_UPDATE) {
                switch (fieldName) {
                    case EntityConst.ENTITY_UPDATE_BY:
                        setBeanValue(clazz, fieldName, obj, getUserId());
                        break;
                    case EntityConst.ENTITY_UPDATED_BY_NAME:
                        setBeanValue(clazz, fieldName, obj, getUserName());
                        break;
                    case EntityConst.ENTITY_UPDATE_TIME:
                        setBeanValue(clazz, fieldName, obj, new Date());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private Object getFieldValue(Class clazz, String fieldName, Object model) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getMethod("get" + getMethodName(fieldName));
        return method.invoke(model);
    }

    private String getUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            userId = "";
        }
        return userId;
    }

    private String getUserName() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String name = (String) request.getAttribute("userName");
        if (name == null) {
            name = "";
        }
        return name;
    }
}
