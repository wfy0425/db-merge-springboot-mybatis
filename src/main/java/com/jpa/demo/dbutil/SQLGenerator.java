package com.jpa.demo.dbutil;

import com.jpa.demo.pojo.Column;
import com.jpa.demo.pojo.Table;

import java.util.List;

public interface SQLGenerator {
    String listTable();
    String listTableColumn(String tableName);
    String createTable(List<Table> tableList);
    String addColumn(List<Column> columnList);
    String modifyColumn(List<Column> columnList);
    String deleteTable(List<Table> tableList);
    String deleteColumn(List<Column> columnList);

}
