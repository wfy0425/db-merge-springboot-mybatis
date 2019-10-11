/*
 * Name: 吴丰源
 * Data: 2019/8/23
 * 此类负责将MySQL数据库结构查询结果放入封装结构中
 *
 */
package com.jpa.demo.dbutil.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jpa.demo.dbutil.StructurePrinter;
import com.jpa.demo.pojo.Column;
import com.jpa.demo.pojo.Table;
import com.jpa.demo.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("MySQLStructure")
public class MySQLStructurePrinter implements StructurePrinter {
    @Autowired
    private DatabaseService databaseService = null;
    @Override
    public Map<String, Table> printStructure(String printerName) {
        List<Map> tableMapList = databaseService.listTable(printerName);
//        String[] tableArray = new String[tableMapList.size()];
        Table table;
        Column column;
        String tableName;
        HashMap<String, Column> columnsMap;
        Map<String, Table> tablesMap = new HashMap<>();
//        List<Table> tableList = new ArrayList<>();
        for (Map map : tableMapList) {
//            tableArray[i]= (String) tableMapList.get(i).get("TABLE_NAME");
            tableName = (String) map.get("TABLE_NAME");
            List<Map> columnMapList = databaseService.listTableColumn(printerName, tableName);
            columnsMap = new HashMap<>();

            for (Map map1 : columnMapList) {
                column = new Column();
                String columnName = (String) map1.get("COLUMN_NAME");
                column.setColumnName(columnName);
                if(((String)map1.get("COLUMN_KEY")).contains("PRI")){
                    column.setPrimaryKey(true);
                }
                String[] columnType = ((String) map1.get("COLUMN_TYPE")).split("\\(");//分离字段类型和长度
                String[] columnLength =columnType[1].substring(0,columnType[1].length() - 1).split(",");//得到字段长度
                List<Long> columnLengthList = new ArrayList<>();
                for(int i=0;i<columnLength.length;i++){
                    columnLengthList.add(Long.parseLong(columnLength[i]));
                }
                column.setDataType(columnType[0]);
                column.setDataLength(columnLengthList);
                if(((String) map1.get("EXTRA")).contains("auto_increment")){
                    column.setAutoIncrement(true);
                }
                if(((String) map1.get("IS_NULLABLE")).contains("YES")){
                    column.setNullable(true);
                }
                column.setTableName((String) map1.get("TABLE_NAME"));
                columnsMap.put(columnName, column);
            }
            table = new Table();
            table.setColumns(columnsMap);
            table.setTableName(tableName);
            tablesMap.put(tableName, table);
//            tableList.add(table);

        }
        try {
            //写入JSON文件方便调试
            JSONObject json = new JSONObject();
            json.put("db", tablesMap);
            File file = new File("./src/main/resources/static/data/oldDb.json");
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(JSON.toJSONString(tablesMap));
            bw.close();

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return tablesMap;
    }
}
