package com.d2d.util;

import com.d2d.constant.Constant;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommonUtil {

    public String CapitalizeClassName(String str) {
        String[] words = str.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }
    public String data(String str){
        return str.replace('_', ' ').replaceFirst("^[Tt]", "m").replaceAll("\\s", "");
    }
    public String removeUnderScore(String str) {
        return WordUtils.capitalizeFully(str, '_').replace("_", "");
    }
    public String toCamelCase(String str) {
        String[] words = str.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                sb.append(Character.toLowerCase(word.charAt(0)));
            } else {
                sb.append(Character.toUpperCase(word.charAt(0)));
            }
            sb.append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    public String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public Map<Integer, Map<String, String>> getJavaType() {

        Map<Integer, Map<String, String>> dbEngines = new HashMap<>();

        Map<String, String> mysqlDatatypes = new HashMap<>();
        mysqlDatatypes.put("INT", Constant.INTEGER);
        mysqlDatatypes.put("SMALLINT", Constant.INTEGER);
        mysqlDatatypes.put("BIGINT", "Long");
        mysqlDatatypes.put("MEDIUMINT", "Long");
        mysqlDatatypes.put("VARCHAR", Constant.STRING);
        mysqlDatatypes.put("TINYTEXT", Constant.STRING);
        mysqlDatatypes.put("LONGTEXT", Constant.STRING);
        mysqlDatatypes.put("MEDIUMTEXT", Constant.STRING);
        mysqlDatatypes.put("TIMESTAMP", Constant.TIMESTAMP_PASCAL);
        mysqlDatatypes.put("DATE", Constant.TIMESTAMP_PASCAL);
        mysqlDatatypes.put("TIME", Constant.TIMESTAMP_PASCAL);
        mysqlDatatypes.put("YEAR", Constant.TIMESTAMP_PASCAL);
        mysqlDatatypes.put("DATETIME", Constant.TIMESTAMP_PASCAL);
        mysqlDatatypes.put("BOOLEAN", Constant.BOOLEAN);
        mysqlDatatypes.put("BIT", Constant.BOOLEAN);
        mysqlDatatypes.put("TINYINT", Constant.BOOLEAN);
        mysqlDatatypes.put("DOUBLE", "double");
        mysqlDatatypes.put("BINARY", "byte");
        mysqlDatatypes.put("VARBINARY", "byte");
        mysqlDatatypes.put("BLOB", "Blob");
        mysqlDatatypes.put("TINYBLOB", "Blob");
        mysqlDatatypes.put("LONGBLOB", "Blob");
        mysqlDatatypes.put("MEDIUMBLOB", "Blob");
        mysqlDatatypes.put("FLOAT", Constant.FLOAT);
        mysqlDatatypes.put("DECIMAL", Constant.FLOAT);
        mysqlDatatypes.put("JSON", Constant.STRING);
        dbEngines.put(1, mysqlDatatypes);

        Map<String, String> postgresqlDatatypes = new HashMap<>();
        postgresqlDatatypes.put("bool", Constant.BOOLEAN);
        postgresqlDatatypes.put("bit", Constant.BOOLEAN);
        postgresqlDatatypes.put("int8", "Long");
        postgresqlDatatypes.put("bigserial", "Long");
        postgresqlDatatypes.put("oid", "Long");
        postgresqlDatatypes.put("bytea", "byte");
        postgresqlDatatypes.put("char", Constant.STRING);
        postgresqlDatatypes.put("bpchar", Constant.STRING);
        postgresqlDatatypes.put(Constant.NUMERIC, Constant.DOUBLE);
        postgresqlDatatypes.put("int4", Constant.INTEGER);
        postgresqlDatatypes.put("serial", Constant.INTEGER);
        postgresqlDatatypes.put("int2", Constant.SHORT);
        postgresqlDatatypes.put("smallserial", Constant.SHORT);
        postgresqlDatatypes.put("float4", Constant.FLOAT);
        postgresqlDatatypes.put("float8", Constant.DOUBLE);
        postgresqlDatatypes.put("money", Constant.DOUBLE);
        postgresqlDatatypes.put("name", Constant.STRING);
        postgresqlDatatypes.put("text", Constant.STRING);
        postgresqlDatatypes.put(Constant.VARCHAR_SMALL, Constant.STRING);
        postgresqlDatatypes.put("date", Constant.TIMESTAMP_PASCAL);
        postgresqlDatatypes.put("time", Constant.TIMESTAMP_PASCAL);
        postgresqlDatatypes.put("timetz", Constant.TIMESTAMP_PASCAL);
        postgresqlDatatypes.put(Constant.TIMESTAMP_SMALL, Constant.TIMESTAMP_PASCAL);
        postgresqlDatatypes.put("timestamptz", Constant.TIMESTAMP_PASCAL);

        dbEngines.put(4, postgresqlDatatypes);


        Map<String, String> oracleDatatype = new HashMap<>();
        oracleDatatype.put("NUMBER", Constant.INTEGER);
        oracleDatatype.put("CLOB", "Clob");
        oracleDatatype.put("BLOB", "Blob");
        oracleDatatype.put("VARCHAR2", Constant.STRING);
        oracleDatatype.put("VARCHAR", Constant.STRING);
        oracleDatatype.put("CHAR", Constant.STRING);
        oracleDatatype.put("CHAR VARYING", Constant.STRING);
        oracleDatatype.put("CHARACTER", Constant.STRING);
        oracleDatatype.put("CHARACTER VARYING", Constant.STRING);
        oracleDatatype.put("DEC", Constant.FLOAT);
        oracleDatatype.put("DECIMAL", Constant.DOUBLE);
        oracleDatatype.put("DOUBLE PRECISION", Constant.DOUBLE);
        oracleDatatype.put("FLOAT", Constant.FLOAT);
        oracleDatatype.put("INT", Constant.INTEGER);
        oracleDatatype.put("INTEGER", Constant.INTEGER);
        oracleDatatype.put("ROWID", Constant.INTEGER);
        oracleDatatype.put("UROWID", Constant.INTEGER);
        oracleDatatype.put("SMALLINT", Constant.INTEGER);
        oracleDatatype.put("DATE", Constant.TIMESTAMP_PASCAL);
        oracleDatatype.put("TIMESTAMP", Constant.TIMESTAMP_PASCAL);
        oracleDatatype.put("LONG", "Clob");
        oracleDatatype.put("LONG RAW", "Blob");
        oracleDatatype.put("NATIONAL CHAR", Constant.STRING);
        oracleDatatype.put("NATIONAL CHAR VARYING", Constant.STRING);
        oracleDatatype.put("NATIONAL CHARACTER", Constant.STRING);
        oracleDatatype.put("NCHAR", Constant.STRING);
        oracleDatatype.put("NCHAR VARYING", Constant.STRING);
        oracleDatatype.put("NCLOB", "Clob");
        oracleDatatype.put("NUMERIC", Constant.DOUBLE);
        oracleDatatype.put("NVARCHAR2", Constant.STRING);
        oracleDatatype.put("RAW", "byte");
        oracleDatatype.put("REAL", Constant.FLOAT);
        oracleDatatype.put("BINARY_DOUBLE", "byte");
        oracleDatatype.put("BINARY_FLOAT", "byte");
        dbEngines.put(3, oracleDatatype);

        Map<String, String> msSqlDatatype = new HashMap<>();
        msSqlDatatype.put("bigint", "Long");
        msSqlDatatype.put("binary", "byte");
        msSqlDatatype.put("bit", Constant.BOOLEAN);
        msSqlDatatype.put("char", Constant.STRING);
        msSqlDatatype.put("date", Constant.TIMESTAMP_PASCAL);
        msSqlDatatype.put("datetime", Constant.TIMESTAMP_PASCAL);
        msSqlDatatype.put("datetime2", Constant.TIMESTAMP_PASCAL);
        msSqlDatatype.put("datetimeoffset", Constant.TIMESTAMP_PASCAL);
        msSqlDatatype.put("decimal", Constant.DOUBLE);
        msSqlDatatype.put(Constant.FLOAT, Constant.FLOAT);
        msSqlDatatype.put("geography", "byte");
        msSqlDatatype.put("geometry", "byte");
        msSqlDatatype.put("hierarchyid", "byte");
        msSqlDatatype.put("image", "byte");
        msSqlDatatype.put("int", Constant.INTEGER);
        msSqlDatatype.put("money", Constant.DOUBLE);
        msSqlDatatype.put("nchar", Constant.STRING);
        msSqlDatatype.put("ntext", Constant.STRING);
        msSqlDatatype.put(Constant.NUMERIC, Constant.DOUBLE);
        msSqlDatatype.put("nvarchar", Constant.STRING);
        msSqlDatatype.put("real", Constant.FLOAT);
        msSqlDatatype.put("smalldatetime", Constant.TIMESTAMP_PASCAL);
        msSqlDatatype.put("smallint", Constant.SHORT);
        msSqlDatatype.put("smallmoney", Constant.DOUBLE);
        msSqlDatatype.put("sql_variant", "Object");
        msSqlDatatype.put("text", Constant.STRING);
        msSqlDatatype.put("time", Constant.TIMESTAMP_PASCAL);
        msSqlDatatype.put(Constant.TIMESTAMP_SMALL, Constant.TIMESTAMP_PASCAL);
        msSqlDatatype.put("tinyint", Constant.SHORT);
        msSqlDatatype.put("uniqueidentifier", Constant.STRING);
        msSqlDatatype.put("varbinary", "byte");
        msSqlDatatype.put(Constant.VARCHAR_SMALL, Constant.STRING);
        msSqlDatatype.put("xml", Constant.STRING);
        dbEngines.put(5, msSqlDatatype);

        return dbEngines;
    }


    public static boolean isColumnTypeValid(String columnType) {
        return columnType.toLowerCase().startsWith("bit") ||
                columnType.toLowerCase().startsWith("bit varying") ||
                columnType.toLowerCase().startsWith("bit varying[]") ||
                columnType.toLowerCase().startsWith("bit[]") ||
                columnType.toLowerCase().startsWith("character") ||
                columnType.toLowerCase().startsWith("character varying") ||
                columnType.toLowerCase().startsWith("character varying[]") ||
                columnType.toLowerCase().startsWith("character[]") ||
                columnType.toLowerCase().startsWith("interval") ||
                columnType.toLowerCase().startsWith("interval[]") ||
                columnType.toLowerCase().startsWith(Constant.NUMERIC) ||
                columnType.toLowerCase().startsWith("numeric[]") ||
                columnType.toLowerCase().startsWith("time with time zone") ||
                columnType.toLowerCase().startsWith("time with time zone[]") ||
                columnType.toLowerCase().startsWith("time without time zone") ||
                columnType.toLowerCase().startsWith("time without time zone[]") ||
                columnType.toLowerCase().startsWith("timestamp with time zone") ||
                columnType.toLowerCase().startsWith("timestamp with time zone[]") ||
                columnType.toLowerCase().startsWith("timestamp without time zone") ||
                columnType.toLowerCase().startsWith("time") ||
                columnType.toLowerCase().startsWith("timetz") ||
                columnType.toLowerCase().startsWith(Constant.TIMESTAMP_SMALL) ||
                columnType.toLowerCase().startsWith("timestamptz") ||
                columnType.toLowerCase().startsWith("timestamp without time zone[]");


    }

    public static boolean isColumnTypeValidDefaultValue(String columnType) {
        return columnType.toLowerCase().startsWith("bit") ||
                columnType.toLowerCase().startsWith("bit varying") ||
                columnType.toLowerCase().startsWith("bit varying[]") ||
                columnType.toLowerCase().startsWith("bit[]") ||
                columnType.toLowerCase().startsWith("character") ||
                columnType.toLowerCase().startsWith("character varying") ||
                columnType.toLowerCase().startsWith("character varying[]") ||
                columnType.toLowerCase().startsWith("character[]") ||
                columnType.toLowerCase().startsWith("interval") ||
                columnType.toLowerCase().startsWith("interval[]") ||
                columnType.toLowerCase().startsWith(Constant.NUMERIC) ||
                columnType.toLowerCase().startsWith("numeric[]") ||
                columnType.toLowerCase().startsWith("time with time zone") ||
                columnType.toLowerCase().startsWith("time with time zone[]") ||
                columnType.toLowerCase().startsWith("time without time zone") ||
                columnType.toLowerCase().startsWith("time without time zone[]") ||
                columnType.toLowerCase().startsWith("timestamp with time zone") ||
                columnType.toLowerCase().startsWith("timestamp with time zone[]") ||
                columnType.toLowerCase().startsWith("timestamp without time zone") ||
                columnType.toLowerCase().startsWith("timestamp without time zone[]") ||
                columnType.toLowerCase().startsWith("char") ||
                columnType.toLowerCase().startsWith("bool") ||
                columnType.toLowerCase().startsWith(Constant.VARCHAR_SMALL);
    }

}
