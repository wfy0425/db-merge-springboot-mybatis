/*
 * Name: 吴丰源
 * Data: 2019/8/23
 * 此类负责比对数据库差异
 *
 */
package com.jpa.demo.dbutil;

import com.jpa.demo.pojo.Column;
import com.jpa.demo.pojo.Table;
import com.jpa.demo.util.DbCompareResult;

import java.util.*;

public class DatabaseComparator {
    private List<Table> newTableList = new ArrayList<>();
    private List<Table> deleteTableList = new ArrayList<>();
    private List<Column> newColumnList = new ArrayList<>();
    private List<Column> deleteColumnList = new ArrayList<>();
    private List<Column> updateColumnList = new ArrayList<>();
    private List<Column> conflictAutoIncreaseColumnList = new ArrayList<>();
    private List<Column> conflictFieldColumnList = new ArrayList<>();

    private boolean hasAutoIncrease = false;

    public void compareTables(Map<String, Table> targetMap, Map<String, Table> inputMap) {
        // 遍历源文件库Map
        for (Iterator<String> mapIter = inputMap.keySet().iterator(); mapIter.hasNext(); ) {
            String tableKey = mapIter.next();
            Table inputTable = inputMap.get(tableKey);// 获得源文件库中的表
            Table targetTable = targetMap.get(tableKey);// 尝试从新文件库中获得同名表
            if (targetTable == null) { // 如果获得表为空，说明源文件存在，新文件不存在

                newTableList.add(inputTable);
            } else { // 表相同，判断字段、字段类型、字段长度
                for (Iterator<String> columnIter = inputTable.getColumns().keySet().iterator(); columnIter.hasNext(); ) {
                    String columnKey = columnIter.next();
                    Column inputColumn = inputTable.getColumns().get(columnKey);// 获得源文件库中的列
                    Column targetColumn = targetTable.getColumns().get(columnKey);// 尝试从新文件库中获得同名列
                    if (inputColumn.isAutoIncrement()){
                        hasAutoIncrease = true;
                    }
                    if (targetColumn == null) {// 如果列名为空，说明源文件存在，新文件不存在

                        newColumnList.add(inputColumn);
                    } else {// 说明两者都存在
                        boolean needChange = false;
                        String columnTypeTemp=targetColumn.getDataType();
                        boolean isPrimaryKeyTemp=targetColumn.isPrimaryKey();
                        boolean isAutoIncrementTemp=targetColumn.isAutoIncrement();
                        boolean isNullableTemp=targetColumn.isNullable();
                        List<Long> dataLengthTemp=targetColumn.getDataLength();
                        if (!inputColumn.getDataType().equals(targetColumn.getDataType())) {// 字段类型长度不一致
//
                                conflictFieldColumnList.add(inputColumn);
                        }
                        else if(inputColumn.getDataLength()==null||targetColumn.getDataLength()==null){}
                        else if(inputColumn.getDataLength().equals(targetColumn.getDataLength())){}
                        else if(inputColumn.getDataLength().size()!=targetColumn.getDataLength().size()) {//字段类型一致，长度不一致
                            conflictFieldColumnList.add(inputColumn);
                        }else {
                            for (int i =0;i<inputColumn.getDataLength().size();i++) {
                                if (inputColumn.getDataLength().get(i) > targetColumn.getDataLength().get(i)) {//字段长度不一致
                                    dataLengthTemp = inputColumn.getDataLength();
                                    break;
                                }
                            }
                                dataLengthTemp = targetColumn.getDataLength();
                            needChange = true;
                        }
                            if (inputColumn.isPrimaryKey() != targetColumn.isPrimaryKey()) {// 字段PRI不一致
//                            append(inputTable, inputColumn, targetTable, targetColumn, 6);

                                isPrimaryKeyTemp = true;
                                needChange = true;
                            }
                            if (inputColumn.isAutoIncrement() != targetColumn.isAutoIncrement()) {// 字段Extra不一致
//                            append(inputTable, inputColumn, targetTable, targetColumn, 7);
                                if (hasAutoIncrease) {
                                    conflictAutoIncreaseColumnList.add(inputColumn);
                                } else {
                                    isAutoIncrementTemp = true;
                                    needChange = true;
                                }
                            }
                            if (inputColumn.isNullable() != targetColumn.isNullable()) {// 字段isNullable不一致
//                            append(inputTable, inputColumn, targetTable, targetColumn, 7);

                                isNullableTemp = true;
                                needChange = true;
                            }
                            if (needChange) {
                                Column column = new Column();
                                column.setDataType(columnTypeTemp);
                                column.setDataLength(dataLengthTemp);
                                column.setAutoIncrement(isAutoIncrementTemp);
                                column.setNullable(isNullableTemp);
                                column.setPrimaryKey(isPrimaryKeyTemp);
                                column.setTableName(targetTable.getTableName());
                                column.setColumnName(targetColumn.getColumnName());
                                updateColumnList.add(column);
                            }
                        }
                    }
                }
            }


        // 遍历新文件库Map
        for (Iterator<String> tableIter = targetMap.keySet().iterator(); tableIter
                .hasNext(); ) {
            String tableKey = tableIter.next();
            Table targetTable = targetMap.get(tableKey);// 尝试从新文件库中获得同名表
            Table inputTable = inputMap.get(tableKey);// 获得源文件库中的表
            if (inputTable == null) { // 如果获得表为空，说明源文件存在，新文件不存在
                deleteTableList.add(targetTable);
//                append(targetTable, null, null, null, 1);
            } else { // 表相同，判断字段、字段类型、字段长度
                for (Iterator<String> iter_column = targetTable.getColumns()
                        .keySet().iterator(); iter_column.hasNext(); ) {
                    String key_column = iter_column.next();
                    Column targetColumn = targetTable.getColumns().get(key_column);// 获得新文件库中的列
                    Column inputColumn = inputTable.getColumns().get(key_column);// 尝试从源文件库中获得同名列
                    if (inputColumn == null) {// 如果列名为空，说明新文件存在，源文件不存在
//                        append(targetTable, targetColumn, inputTable, inputColumn, 3);
                        deleteColumnList.add(targetColumn);
                    }
                }
            }
        }
    }

    public DbCompareResult getCompareResult(String SQLPath){
        if(newTableList.size()>0||deleteTableList.size()>0||newColumnList.size()>0
                ||deleteColumnList.size()>0||updateColumnList.size()>0||conflictAutoIncreaseColumnList.size()>0||
                conflictFieldColumnList.size()>0)
        {

        Map<String,List> result = new HashMap<>();
        DbCompareResult dbCompareResult = new DbCompareResult();
        result.put("newTableList",newTableList);
        result.put("deleteTableList",deleteTableList);
        result.put("newColumnList",newColumnList);
        result.put("deleteColumnList",deleteColumnList);
        result.put("updateColumnList",updateColumnList);
        result.put("conflictAutoIncreaseColumnList",conflictAutoIncreaseColumnList);
        result.put("conflictFieldColumnList",conflictFieldColumnList);
        dbCompareResult.setResult(result);
        dbCompareResult.setSQLPath(SQLPath);
        return dbCompareResult;
        }
        else {
            return null;
        }
    }
    public List<Table> getNewTableList() {
        return newTableList;
    }

    public List<Column> getNewColumnList() {
        return newColumnList;
    }

    public List<Column> getUpdateColumnList() {
        return updateColumnList;
    }

    public List<Table> getDeleteTableList() {
        return deleteTableList;
    }

    public List<Column> getDeleteColumnList() {
        return deleteColumnList;
    }

    public List<Column> getConflictAutoIncreaseColumnList() {
        return conflictAutoIncreaseColumnList;
    }

    public List<Column> getConflictFieldColumnList() {
        return conflictFieldColumnList;
    }
}
