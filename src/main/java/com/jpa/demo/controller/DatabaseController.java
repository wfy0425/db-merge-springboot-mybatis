/*
 * Name: 吴丰源
 * Data: 2019/8/23
 * 此类负责请求转发，接受页面过来的参数，传给Service处理，接到返回值，再传给页面
 *
 */
package com.jpa.demo.controller;

import com.jpa.demo.configuration.TargetDataSource;
import com.jpa.demo.service.DatabaseService;
import com.jpa.demo.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dbComparator")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService = null;

    @Value("${database.type}")
    String dbType = null;

    @TargetDataSource("master")//该注释用以标记所使用的数据库
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="/MySQL/list",method= RequestMethod.GET)
    public JsonResult list() {
        JsonResult jr = new JsonResult();
        jr.setObj(databaseService.listTable("MySQL"));
        jr.setMsg("OK");
        jr.setCode(200L);
        return jr;
    }

    @TargetDataSource("slave")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="/Postgre/list",method= RequestMethod.GET)
    public JsonResult listSlave() {
        JsonResult jr = new JsonResult();
        jr.setObj(databaseService.listTable("PostgreSQL"));
        jr.setMsg("OK");
        jr.setCode(200L);
        return jr;
    }

    @TargetDataSource("master")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="/MySQL/columns/{tableName}",method= RequestMethod.GET)
    public JsonResult ServiceRowsResult(@PathVariable("tableName")String tableName) {
        JsonResult jr = new JsonResult();
        jr.setObj(databaseService.listTableColumn("MySQL",tableName));
        jr.setMsg("OK");
        jr.setCode(200L);
        return jr;
    }

    @TargetDataSource("slave")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="/Postgre/columns/{tableName}",method= RequestMethod.GET)
    public JsonResult ServiceRowsResultPostgres(@PathVariable("tableName")String tableName) {
        JsonResult jr = new JsonResult();
        jr.setObj(databaseService.listTableColumn("PostgreSQL",tableName));
        jr.setMsg("OK");
        jr.setCode(200L);
        return jr;
    }

    @TargetDataSource("master")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="/MySQL/print",method= RequestMethod.GET)
    public JsonResult printStructure() {
        JsonResult jr = new JsonResult();
        jr.setObj(databaseService.printStructure("MySQL"));
        jr.setMsg("OK");
        jr.setCode(200L);
        return jr;
    }

    @TargetDataSource("slave")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="/Postgre/print",method= RequestMethod.GET)
    public JsonResult printStructurePostgres() {
        JsonResult jr = new JsonResult();
        jr.setObj(databaseService.printStructure("PostgreSQL"));
        jr.setMsg("OK");
        jr.setCode(200L);
        return jr;
    }

//    @TargetDataSource("master")
//    @ResponseStatus(HttpStatus.OK)
//    @RequestMapping(value="/comparedb",method= RequestMethod.POST)
//    public JsonResult CompareDb(@RequestBody JSONObject response) {
////        JSONArray jsonArray = response.getJSONArray("student");
//        String jsonStr1 = JSONArray.toJSONString(response.getJSONObject("student"));
//        Map<String,Table> userListInput = JSONArray.parseObject(jsonStr1, new TypeReference<HashMap<String,Table>>() {});
//        JsonResult jr = new JsonResult();
//        Map<String,Table> userListDb = databaseService.printStructure(this.dbType);
//        DatabaseComparator databaseComparator = new DatabaseComparator();
//        databaseComparator.compareTables(userListDb,userListInput);
//
//        jr.setObj(databaseComparator.getResult());
//        jr.setMsg("OK");
//        jr.setCode(200L);
//        return jr;
//    }

//    @ResponseStatus(HttpStatus.CREATED)
//    @RequestMapping(value="/createTable",method= RequestMethod.POST)
//    public JsonResult createTable(){
//        Table table=new Table();
//        table.setTableName("stu");
//        Column column = new Column();
//        column.setColumnName("s");
//        column.setDataType("int(12)");
//        column.setColumnKey("PRI");
//        column.setExtra("auto_increment");
//        HashMap<String,Column> hashMap = new HashMap<>();
//        hashMap.put("s",column);
//        table.setColumns(hashMap);
//        JsonResult jr = new JsonResult();
//        jr.setObj(databaseService.createTable(table));
//        jr.setMsg("success");
//        jr.setCode(200L);
//        return jr;
//    }
//    @ResponseStatus(HttpStatus.CREATED)
//    @RequestMapping(value="/addColumn",method= RequestMethod.POST)
//    public JsonResult addColumn(){
//
//        Column column = new Column();
//        column.setColumnName("a");
//        column.setDataType("int(12)");
//        column.setColumnKey("");
//        column.setExtra("");
//        JsonResult jr = new JsonResult();
//        jr.setObj(databaseService.addColumn(column));
//        jr.setMsg("success");
//        jr.setCode(200L);
//        return jr;
//    }

    @TargetDataSource("master")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value="/MySQL/mergeDb",method= RequestMethod.POST)
    public JsonResult mergeDbMySQL(){
        JsonResult jr = new JsonResult();
        jr.setObj(databaseService.mergeDb("MySQL"));
        jr.setMsg("success");
        jr.setCode(200L);
        return jr;
    }

    @TargetDataSource("slave")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value="/PostgresSQL/mergeDb",method= RequestMethod.POST)
    public JsonResult mergeDbPostgresSQL(){
        JsonResult jr = new JsonResult();
        jr.setObj(databaseService.mergeDb("PostgreSQL"));
        jr.setMsg("success");
        jr.setCode(200L);
        return jr;
    }

}
