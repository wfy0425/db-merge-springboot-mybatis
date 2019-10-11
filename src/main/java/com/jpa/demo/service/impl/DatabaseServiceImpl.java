/*
 * Name: 吴丰源
 * Data: 2019/8/23
 * 此类为service层，复制事务控制
 *
 */
package com.jpa.demo.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.jpa.demo.dao.DatabaseDao;
import com.jpa.demo.dbutil.DatabaseComparator;
import com.jpa.demo.dbutil.SQLGenerator;
import com.jpa.demo.dbutil.StructurePrinter;
import com.jpa.demo.pojo.Column;
import com.jpa.demo.pojo.Table;
import com.jpa.demo.service.DatabaseService;
import com.jpa.demo.util.DbCompareResult;
import com.jpa.demo.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Service
@Transactional(rollbackFor = Exception.class)
public class DatabaseServiceImpl implements DatabaseService {
    @Autowired
    private DatabaseDao databaseDao =null;

    @Value("${database.JSON.path}")
    String JSONPath = null;//输入JSON文件路径
    @Value("${database.generatedSQL.path}")
    String SQLPath = null;//输出SQL文件路径

    String generatorString = "Generator";//SQL生成器对应的javabean后缀
    String structurePrinterString = "Structure";//数据库结构打印对应的javabean后缀

    @Override
    public List<Map> listTable(String dbType) {
        SQLGenerator sqlGenerator = (SQLGenerator)SpringUtil.getBean(dbType+ generatorString);
        return databaseDao.listStructureBySQL(sqlGenerator.listTable());
    }

    @Override
    public List<Map> listTableColumn(String dbType, String tableName) {
        SQLGenerator sqlGenerator = (SQLGenerator)SpringUtil.getBean(dbType+ generatorString);

        return databaseDao.listStructureBySQL(sqlGenerator.listTableColumn(tableName));
    }

    @Override
    public Map<String, Table> printStructure(String dbType) {
        StructurePrinter structurePrinter = (StructurePrinter)SpringUtil.getBean(dbType+structurePrinterString);
        return structurePrinter.printStructure(dbType);
    }

    @Override
    public boolean createTable(String dbType, List<Table> tableList) {
        if (tableList.size()==0){
            return false;
        }
        SQLGenerator sqlGenerator = (SQLGenerator)SpringUtil.getBean(dbType+ generatorString);
        return databaseDao.operateBySQL(sqlGenerator.createTable(tableList));
    }

    @Override
    public boolean addColumn(String dbType, List<Column> columnList) {
        if (columnList.size()==0){
            return false;
        }
        SQLGenerator sqlGenerator = (SQLGenerator)SpringUtil.getBean(dbType+ generatorString);
        return databaseDao.operateBySQL(sqlGenerator.addColumn(columnList));
    }

    @Override
    public boolean modifyColumn(String dbType, List<Column> columnList) {
        if (columnList.size()==0){
            return false;
        }
        SQLGenerator sqlGenerator = (SQLGenerator)SpringUtil.getBean(dbType+ generatorString);
        return databaseDao.operateBySQL(sqlGenerator.modifyColumn(columnList));
    }

    @Override
    public DbCompareResult mergeDb(String dbType) {
        SQLGenerator sqlGenerator = (SQLGenerator)SpringUtil.getBean(dbType+ generatorString);
        BufferedReader read = null;
        StringBuilder sb = new StringBuilder();
        try {//读取文件
            read = new BufferedReader(new FileReader(JSONPath));
            String s;

            while((s=read.readLine()) != null){
                sb.append(s);
            }
            read.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String inputDbJson = sb.toString();
        inputDbJson = inputDbJson.replaceAll("\\r\\n", "\\n").replaceAll("\\s*", "");//去除换行
        Map<String,Table> userListInput = JSONArray.parseObject(inputDbJson, new TypeReference<HashMap<String,Table>>() {});

        Map<String,Table> userListDb = printStructure(dbType);//取得连接数据库结构
        DatabaseComparator databaseComparator = new DatabaseComparator();
        databaseComparator.compareTables(userListDb,userListInput);


        if(databaseComparator.getCompareResult(SQLPath)!=null) {//如果比对后有不同
            Scanner scanner = new Scanner(System.in);
            //获取生成SQL命令所需的比对结果
            List<Table> newTableList= databaseComparator.getNewTableList();
            List<Column> newColumnList = databaseComparator.getNewColumnList();
            List<Column> updateColumnList = databaseComparator.getUpdateColumnList();
            List<Table> deleteTableList = databaseComparator.getDeleteTableList();
            List<Column> deleteColumnList = databaseComparator.getDeleteColumnList();

            try {//写入SQL文件
                File file = new File(SQLPath);
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(sqlGenerator.createTable(newTableList));
                bw.write("\r\n");
                bw.write(sqlGenerator.addColumn(newColumnList));
                bw.write("\r\n");
                bw.write(sqlGenerator.modifyColumn(updateColumnList));
                bw.write("\r\n");
                bw.write(sqlGenerator.deleteTable(deleteTableList));
                bw.write("\r\n");
                bw.write(sqlGenerator.deleteColumn(deleteColumnList));
                bw.write("\r\n");
                bw.close();

                System.out.println("sql文件写入完成");

            } catch (IOException e) {
                e.printStackTrace();
            }

            return databaseComparator.getCompareResult(SQLPath);//返回所有比对结果，包括冲突结果
        }else{
            return null;
        }
    }
}
