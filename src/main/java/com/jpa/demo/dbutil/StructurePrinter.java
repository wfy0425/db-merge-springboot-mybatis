package com.jpa.demo.dbutil;

import com.jpa.demo.pojo.Table;

import java.util.Map;

public interface StructurePrinter {
    Map<String, Table> printStructure(String printerName);
}
