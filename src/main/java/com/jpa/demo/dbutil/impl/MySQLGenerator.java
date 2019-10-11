/*
 * Name: 吴丰源
 * Data: 2019/8/23
 * 此类负责生成MySQL数据库对应的数据库修改SQL语句
 *
 */
package com.jpa.demo.dbutil.impl;

import com.jpa.demo.pojo.Column;
import com.jpa.demo.pojo.Table;
import com.jpa.demo.dbutil.SQLGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component("MySQLGenerator")
@Configuration
public class MySQLGenerator implements SQLGenerator {
    @Override
    public String listTable() {
        return "select * from information_schema.TABLES where TABLE_SCHEMA=(select database())";
    }

    @Override
    public String listTableColumn(String tableName) {
        String sql = "select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database()) and TABLE_NAME=" +
                "'"+tableName+"'";

        return sql;
    }

    @Override
    public String deleteTable(List<Table> tableList) {
        StringBuilder sql = new StringBuilder();
        for (Table table : tableList) {
            String tableName = table.getTableName();
            String primaryKey = "";
            HashMap<String, Column> tableColumn = table.getColumns();
            sql.append("DROP TABLE ");
            sql.append(tableName);
            sql.append(" ;");
        }
        return sql.toString();
    }

    @Override
    public String deleteColumn(List<Column> columnList) {
        StringBuilder sql = new StringBuilder();
        for (Column column : columnList) {
            sql.append("alter table ");
            sql.append(column.getTableName());
            sql.append(" drop column ");
            sql.append(column.getColumnName());
            sql.append(";");
        }
        return sql.toString();
    }

    @Override
    public String createTable(List<Table> tableList) {
        StringBuilder sql = new StringBuilder();
        for (Table table : tableList) {
            String tableName = table.getTableName();
            String primaryKey = "";
            HashMap<String, Column> tableColumn = table.getColumns();
            sql.append("CREATE TABLE ");
            sql.append(tableName);
            sql.append(" (");
            for (String key : tableColumn.keySet()) {
                sql.append(tableColumn.get(key).getColumnName());
                sql.append(" ");
                sql.append(tableColumn.get(key).getDataType());
                if (tableColumn.get(key).isAutoIncrement())
                sql.append(" auto_increment");
                if (!tableColumn.get(key).isNullable()) {
                    sql.append(" NOT NULL");
                }
                sql.append(",");
                if (tableColumn.get(key).isPrimaryKey())
                    primaryKey = key;
            }
            if (primaryKey.length() > 0) {
                sql.append("PRIMARY KEY ( ");
                sql.append(primaryKey);
                sql.append(" )");
            } else {
                sql.deleteCharAt(sql.length() - 1);
            }
            sql.append(")ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }
        return sql.toString();
    }
    @Override
    public String addColumn(List<Column> columnList) {
        StringBuilder sql = new StringBuilder();
        for (Column column : columnList) {
            sql.append("alter table ");
            sql.append(column.getTableName());
            sql.append(" ADD ");
            sql.append(column.getColumnName());
            sql.append(" ");
            sql.append(column.getDataType());
            sql.append(" ");
            if(!column.isNullable()){
                sql.append("NOT NULL");
            }
            if (column.isAutoIncrement())
                sql.append(" auto_increment");

            if(column.isPrimaryKey()){
                sql.append(" ,add primary key (");
                sql.append(column.getColumnName());
                sql.append(")");
            }
            sql.append(";");
        }
        return sql.toString();
    }

    @Override
    public String modifyColumn(List<Column> columnList) {
        StringBuilder sql = new StringBuilder();
        for (Column column : columnList) {
            sql.append("alter table ");
            sql.append(column.getTableName());
            sql.append(" modify column ");
            sql.append(column.getColumnName());
            sql.append(" ");
            sql.append(column.getDataType());
            sql.append(" ");
            if(!column.isNullable()){
                sql.append("NOT NULL");
            }
            if (column.isAutoIncrement())
                sql.append(" auto_increment");

            if(column.isPrimaryKey()){
                sql.append(" ,add primary key (");
                sql.append(column.getColumnName());
                sql.append(")");
            }
            sql.append(";");
        }
        return sql.toString();
    }
}
