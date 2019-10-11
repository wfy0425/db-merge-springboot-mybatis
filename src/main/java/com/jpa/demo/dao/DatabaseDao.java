/*
 * Name: 吴丰源
 * Data: 2019/8/23
 * 此类负责执行执行SQL语句
 *
 */
package com.jpa.demo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

//@Repository
@Mapper
public interface DatabaseDao {

//    @Select("select * from information_schema.TABLES where TABLE_SCHEMA=(select database())")
//    List<Map> listTable();
//
//    @Select("select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database()) and TABLE_NAME=#{tableName}")
//    List<Map> listTableColumn(String tableName);

    @Select("${sql}")
    List<Map> listStructureBySQL(String sql);

    @Update("${sql}")
    boolean operateBySQL(String sql);
}
