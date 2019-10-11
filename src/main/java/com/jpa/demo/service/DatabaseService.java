
package com.jpa.demo.service;

import com.jpa.demo.pojo.Column;
import com.jpa.demo.pojo.Table;
import com.jpa.demo.util.DbCompareResult;

import java.util.List;
import java.util.Map;

public interface DatabaseService {
    List<Map> listTable(String dbType);
    List<Map> listTableColumn(String dbType, String tableName);
    Map<String, Table> printStructure(String dbType);
    boolean createTable(String dbType, List<Table> tableList);
    boolean addColumn(String dbType, List<Column> columnList);
    boolean modifyColumn(String dbType, List<Column> columnList);
    DbCompareResult mergeDb(String dbType);
}