/*
 * Name: 吴丰源
 * Data: 2019/8/23
 * 此类负责生成PostgreSQL数据库对应的数据库修改SQL语句
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

@Component("PostgreSQLGenerator")
@Configuration
public class PostgreSQLGenerator implements SQLGenerator {

    @Override
    public String listTable() {

        return "select n.nspname, relname from pg_class c, pg_namespace n where c.relnamespace = n.oid and nspname = 'public' and relkind = 'r' order by relname;";
    }

    @Override
    public String listTableColumn(String tableName) {

        String sql = "SELECT *, CASE WHEN length(B.attname) > 0 THEN 1 ELSE 0 END AS is_pk FROM information_schema.columns A LEFT JOIN ( SELECT pg_attribute.attname FROM pg_index,pg_class,pg_attribute WHERE pg_class.oid = " +
                "'"+tableName +"'"+
                " :: regclass AND pg_index.indrelid = pg_class.oid AND pg_attribute.attrelid = pg_class.oid AND pg_attribute.attnum = ANY (pg_index.indkey)) B ON A.column_name = b.attname WHERE A.table_schema = 'public' AND A.table_name = " +
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

        }
        return sql.toString();
    }
    //TODO notnull
    @Override
    public String addColumn(List<Column> columnList) {
        StringBuilder sql = new StringBuilder();
        for (Column column : columnList) {
            sql.append("alter table ");
            sql.append(column.getTableName());
            sql.append(" ADD ");
            sql.append(column.getColumnName());
            sql.append(" ");
            if (column.isAutoIncrement()) {
                if("smallint".equals(column.getDataType()))
                    sql.append("SMALLSERIAL");
                else if("integer".equals(column.getDataType()))
                    sql.append("SERIAL");
                else if("bigint".equals(column.getDataType()))
                    sql.append("BIGSERIAL");
                else
                    sql.append(column.getDataType());
            }

            sql.append(" ");
            if(column.getDataLength()!=null&&column.getDataLength().size()==1){
                sql.append("(");
                sql.append(column.getDataLength().get(0));
                sql.append(")");
            }else if (column.getDataLength()!=null&&column.getDataLength().size()==2){
                sql.append("(");
                sql.append(column.getDataLength().get(0));
                sql.append(",");
                sql.append(column.getDataLength().get(1));
                sql.append(")");
            }
            if(!column.isNullable()){
                sql.append("NOT NULL");
            }

            if(column.isPrimaryKey()){
                sql.append("add constraint ").append(column.getTableName()).append("_pk ")
                        .append("primary key (").append(column.getColumnName()).append(";");
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
            if (column.isAutoIncrement()) {
                sql.append("create sequence ").
                        append(column.getTableName()).
                        append("_")
                        .append(column.getColumnName()).append("_seq;")
                        .append("alter table ").append(column.getTableName())
                        .append(" alter column ").append(column.getColumnName())
                        .append(" set default nextval('public.").append(column.getTableName()).append("_")
                        .append(column.getColumnName()).append("_seq');")
                        .append("alter sequence ").append(column.getTableName()).append("_")
                        .append(column.getColumnName()).append("_seq").append(" owned by ")
                        .append(column.getTableName()).append(".")
                        .append(column.getColumnName()).append(";");
            }
            if(column.isPrimaryKey()){
                sql.append("add constraint ").append(column.getTableName()).append("_pk ")
                .append("primary key (").append(column.getColumnName()).append(";");
                sql.append(column.getColumnName());
                sql.append(")");
            }
            sql.append(";");
        }
        return sql.toString();
    }
}
