/*
 * Name: 吴丰源
 * Data: 2019/8/23
 * 此类负责将PostgreSQL数据库结构查询结果放入封装结构中
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

@Component("PostgreSQLStructure")
public class PostgreSQLStructurePrinter implements StructurePrinter {
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
            tableName = (String) map.get("relname");
            List<Map> columnMapList = databaseService.listTableColumn(printerName, tableName);
            columnsMap = new HashMap<>();

            for (Map map1 : columnMapList) {
                column = new Column();
                String columnName = (String) map1.get("column_name");
                column.setColumnName(columnName);
                if(((Integer) (map1.get("is_pk")))==1){
                    column.setPrimaryKey(true);
                }
                column.setDataType((String)map1.get("udt_name"));
                if("numeric".equals(column.getDataType())||"decimal".equals(column.getDataType())) {
                    List<Long> columnLength = new ArrayList<>();
                    columnLength.add(((Integer) map1.get("numeric_precision")).longValue());
                    columnLength.add(((Integer) map1.get("numeric_scale")).longValue());
                    column.setDataLength(columnLength);
                }
                else if("char".equals(column.getDataType())||"character".equals(column.getDataType())||"character varying".equals(column.getDataType())||"varchar".equals(column.getDataType())) {
                    List<Long> columnLength = new ArrayList<>();
                    columnLength.add(((Integer) map1.get("character_maximum_length")).longValue());
                    column.setDataLength(columnLength);
                }
                if(map1.get("column_default")!=null&&((String) map1.get("column_default")).contains("nextval")){
                    column.setAutoIncrement(true);
                }
                if(map1.get("IS_NULLABLE")==null||((String) map1.get("IS_NULLABLE")).contains("YES")){
                    column.setNullable(true);
                }
                column.setTableName((String) map1.get("table_name"));
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
