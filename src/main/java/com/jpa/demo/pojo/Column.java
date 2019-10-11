/*
 * Name: 吴丰源
 * Data: 2019/8/23
 * 此类为数据库column结构封装类
 *
 */
package com.jpa.demo.pojo;

import java.util.List;

public class Column {
    private String columnName="";
    private String dataType ="";
    private List<Long> dataLength;
    private boolean isPrimaryKey=false;
    private boolean isAutoIncrement=false;
    private boolean isNullable=false;
    private String tableName="";

    public List<Long> getDataLength() {
        return dataLength;
    }

    public void setDataLength(List<Long> dataLength) {
        this.dataLength = dataLength;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    public String getDataType() {
        return dataType;
    }
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

}
