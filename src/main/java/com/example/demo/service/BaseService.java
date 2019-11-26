package com.example.demo.service;

import com.example.demo.common.PageData;
import com.example.demo.common.Query;

import java.util.List;

/**
 * 表单操作的接口
 */
public interface BaseService<T> {

    //---------------------------------------------增------------------------------------------------
    /**
     * 新增
     *     自动添加uuid和创建人、时间
     * @param model
     * @return
     * @throws Exception
     */
    T save(T model) throws Exception;

    /**
     * 新增
     *      不会给id,createdBy,createdTime等属性赋值
     *
     * @param model
     * @throws Exception
     */
    T directSave(T model) throws Exception;

    //---------------------------------------------删------------------------------------------------
    /**
     *  逻辑删
     *      根据主键
     *
     *   前提是： 有id字段，有逻辑删除字段
     *
     * @param id
     * @throws Exception
     */
    void deleteFalse(String id) throws Exception;

    /**
     * 根据主键真删除
     *
     * @param id
     * @throws Exception
     */
    void deleteById(String id) throws Exception;

    /**
     * 根据主键ids真删除
     *
     * @param ids eg：ids -> “1;2;3;4”
     * @throws Exception
     */
    void deleteByIds(String ids) throws Exception;

    /**
     * @Title: deleteFalseByQuery
     * @Description:根据条件做假删除
     * @param: [q]
     * @return: int
     * @throws:
     */
    int deleteFalseByQuery(Query query)throws Exception;

    //---------------------------------------------改------------------------------------------------
    /**
     *  修改有值的字段
     *       根据主键
     *
     * @param model
     * @throws Exception
     */
    void updateSelective(T model) throws Exception;

    /**
     *  修改实体类的所有属性值
     *      根据主键
     *
     * @param model
     * @throws Exception
     */
    void update(T model) throws Exception;

    /**
     *  直接修改有值的字段
     *      即：不设置updatedBy,updatedTime等属性值
     *      根据主键
     *
     * @param model
     * @throws Exception
     */
    void directUpdateSelective(T model) throws Exception;

    /**
     *  直接修改实体类的全部字段
     * @param model
     * @throws Exception
     */
    void directUpdate(T model) throws Exception;

    /**
     * @Title: updateByQuery
     * @Description:根据条件更新对象
     * @param: [model, query]
     * @return: int
     * @throws:
     */
    int updateByQuery(T model, Query query)throws Exception;

    //---------------------------------------------查------------------------------------------------

    /**
     *
     * @Title: findOnePropertyValueById
     * @Description: 获取指定属性的值，根据ID
     * @param: id
     * @param: property 属性名称
     * @return: java.lang.Object
     * @throws:
     */
    <E> E findOnePropertyValueById(String id, String property) throws Exception;

    /**
     * 获取部分字段
     *      根据ID
     * @param id
     * @param properties 目标字段
     * @return
     * @throws Exception
     */
    T findSomePropertiesById(String id, String... properties) throws Exception;

    /**
     * 根据主键查找
     *
     * @param id
     * @return
     * @throws Exception
     */
    T findById(String id) throws Exception;

    /**
     * @Title: 查询一个符合条件的记录
     * @Description:
     * @param: [model]
     * @return: 若查询结果为空，则返回null
     * @throws:
     */
    T selectOne(T model)throws Exception;

    /**
     * 只查询一个
     *
     * @param query
     * @return 若查询结果为空，则返回null
     * @throws Exception
     */
    T selectOne(Query query)throws Exception;

    /**
     * @Title: count
     * @Description: 统计满足条件的记录数
     * @param: [query]
     * @return: int
     * @throws:
     */
    int count(Query query)throws Exception;

    /**
     * 获取单个属性的值
     *      根据查询条件
     * @param query
     * @param fieldName
     * @return 若找不到则返回空集合
     * @throws Exception
     */
    <E> List<E> findOnePropertyValueList(Query query, String fieldName) throws Exception;

    /**
     * 获取指定的部分属性的值
     *      根据查询条件
     * @param query
     * @param fieldNames 目标属性，和条件属性
     * @return 若找不到则返回空集合
     * @throws Exception
     */
    List<T> findSomePropertiesList(Query query, String... fieldNames) throws Exception;

    /**
     * 查询所有列表
     *
     * @param query
     * @return 若找不到则返回空集合
     * @throws Exception
     */
    List<T> findAll(Query query) throws Exception;

    /**
     * 分页查询部分属性
     *
     * @param query
     * @return
     * @throws Exception
     */
    PageData selectPage(Query query, String... fieldNames) throws Exception;

    /**
     * 分页查询所有属性
     *
     * @param query
     * @return
     * @throws Exception
     */
    PageData selectPage(Query query) throws Exception;
}
