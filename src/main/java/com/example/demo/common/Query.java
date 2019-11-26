package com.example.demo.common;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther: raohr
 * @Title:
 * @Description:
 * @Date: 2019/11/25 10:20
 * @param:
 * @return:
 * @throws:
 */
public class Query extends HashMap<String,Object> {
    public Query() {
        super.put("currentPage", 1);
        super.put("pageSize", 10);
    }

    public static Query pageQuery(Object o) {
        Query q = new Query();

        if (o instanceof Map) {
            q.putAll((Map<String, Object>) o);
        } else {
            Class<?> clazz = o.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = null;
                try {
                    value = field.get(o);
                } catch (IllegalAccessException e) {
                    System.out.println("查询列表 条件不是map和实体类型");
                }
                q.equal(fieldName, value);
            }
        }
        if ((q.get("currentPage") instanceof String)) {
            q.setCurrentPage(Integer.parseInt((String) q.get("currentPage")));
        }
        if ((q.get("pageSize") instanceof String)) {
            q.setPageSize(Integer.parseInt((String) q.get("pageSize")));
        }
        return q;
    }

    public Query setCurrentPage(Integer currentPage) {
        if (currentPage != null && currentPage > 1) {
            super.put("currentPage", currentPage);
        }
        return this;
    }

    public int getCurrentPage() {
        Object currentPage = get("currentPage");
        return currentPage == null ? 1 : Integer.parseInt(currentPage + "");
    }

    public Query setPageSize(Integer pageSize) {
        if (pageSize != null && pageSize > 0) {
            super.put("pageSize", pageSize);
        }
        return this;
    }

    public int getPageSize() {
        Object pageSize = get("pageSize");
        return pageSize == null ? 10 : Integer.parseInt(pageSize + "");
    }

    private static Object getValue(Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof String) {
            if (StringUtils.isBlank((String) object)) {
                return null;
            }
        }
        return object;
    }

    public Query equal(String property, Object o) {
        o = getValue(o);
        if (o != null) {
            super.put(property, o);
        }

        return this;
    }

    public Query notEqual(String property, Object o) {
        o = getValue(o);
        if (o != null) {
            super.put(property + "_notEqual", o);
        }

        return this;
    }

    /**
     * null
     * @param property
     * @return
     */
    public Query isNull(String property) {
        super.put(property + "_isNull", 1);
        return this;
    }

    /**
     * 非null
     * @param property
     * @return
     */
    public Query isNotNull(String property) {
        super.put(property + "_isNotNull", 1);
        return this;
    }

    /**
     * 获取模糊搜索字符串，防止%搜索出全部问题
     *
     * @param value
     * @return 返回处理过后的字符串，若参数为空或空白串，则返回null
     */
    public static String getStrLikeValue(Object value) {
        value = getValue(value);
        if (value == null) {
            return null;
        }
        return value.toString().replaceAll("%", "\\\\%");
    }

    public Query fullLike(String property, Object o) {
        String strLikeValue = getStrLikeValue(o);
        if (strLikeValue != null) {
            super.put(property + "_like", "%" + strLikeValue + "%");
        }
        return this;
    }

    public Query headLike(String property, Object o) {
        String strLikeValue = getStrLikeValue(o);
        if (strLikeValue != null) {
            super.put(property + "_like", strLikeValue + "%");
        }
        return this;
    }

    public Query tailLike(String property, Object o) {
        String strLikeValue = getStrLikeValue(o);
        if (strLikeValue != null) {
            super.put(property + "_like", "%" + strLikeValue);
        }
        return this;
    }

    public Query in(String property, Iterable o) {
        if (o != null) {
            super.put(property + "_in", o);
        }
        return this;
    }

    public Query notIn(String property, Iterable o) {
        if (o != null) {
            super.put(property + "_notIn", o);
        }
        return this;
    }

    public Query orderAsc(String property) {
        if (property != null && property.trim().length() > 0) {
            super.put("orderBy", property + " asc");
        }
        return this;
    }

    public Query orderDesc(String property) {
        super.put("orderBy", property + " desc");
        return this;
    }

    public Query less(String property, Object o) {
        o = getValue(o);
        if (o != null) {
            super.put(property + "_less", o);
        }
        return this;
    }

    public Query greater(String property, Object o) {
        o = getValue(o);
        if (o != null) {
            super.put(property + "_greater", o);
        }
        return this;
    }

    public Query lessOrEqual(String property, Object o) {
        o = getValue(o);
        if (o != null) {
            super.put(property + "_le", o);
        }
        return this;
    }

    public Query greaterOrEqual(String property, Object o) {
        o = getValue(o);
        if (o != null) {
            super.put(property + "_ge", o);
        }
        return this;
    }

    public Query between(String property, Object o1, Object o2) {
        super.put(property + "_between", new Object[]{o1, o2});
        return this;
    }

    /**
     * 是否包含被假删除的记录
     *
     * @param includeDelete
     * @return
     */
    public Query includeDelete(Boolean includeDelete) {
        if (includeDelete != null && includeDelete) {
            super.put("withDel", true);
        }
        return this;
    }

    /**
     * 加上此条件可以对已被假删除的数据操作
     *
     * @return
     */
    public Query withDel() {
        return includeDelete(true);
    }

    public Map emptyPageData() {
        Map map = new HashMap();
        map.put("total", 0);
        map.put("currentPage", 1);
        map.put("pageSize", 10);
        map.put("vos",new ArrayList<>());
        return map;
    }
}
