/*
 * Name: 吴丰源
 * Data: 2019/8/23
 * 此类负责返回数据库对比结果以及生成的sql文件路径
 *
 */
package com.jpa.demo.util;

import java.util.List;
import java.util.Map;

public class DbCompareResult {
    private Map<String, List> result;
    private String SQLPath;

    public Map<String, List> getResult() {
        return result;
    }

    public void setResult(Map<String, List> result) {
        this.result = result;
    }

    public String getSQLPath() {
        return SQLPath;
    }

    public void setSQLPath(String SQLPath) {
        this.SQLPath = SQLPath;
    }
}
