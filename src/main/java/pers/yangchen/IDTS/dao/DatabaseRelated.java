package pers.yangchen.IDTS.dao;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yangchen on 16/11/8.
 */
public class DatabaseRelated {
    public static String createDatabase(String databaseName) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Connection conn = null;
        String sql;
        String url = "jdbc:sqlserver://10.10.240.120:1433;DatabaseName=master";
        String user = "sa";
        String password = "Sa1234";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement();
        sql = "SELECT name FROM master.dbo.sysdatabases WHERE name = '"+ databaseName +"'";
        ResultSet rs = stmt.executeQuery(sql);
        if (!rs.next()) {
            System.out.println("系统中不存在以该名称命名的数据库，可以进行创建");
            int b = stmt.executeUpdate("create database " + databaseName);
            System.out.println("已创建名称为"+ databaseName +"的数据库");
            stmt.close();
            conn.close();
            return databaseName;
        } else {
            System.out.println("系统中存在以该名称命名的数据库，将建立数据表");
            return databaseName;
        }
    }

    public static String createTables(String databaseName, String[] headers, int[] everySize, String tableName) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Connection conn = null;
        String sql;
        String url = "jdbc:sqlserver://10.10.240.120:1433;DatabaseName="+ databaseName;
        String user = "sa";
        String password = "Sa1234";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement();
        sql = "select * from sysobjects where name='"+ tableName +"'";
        ResultSet rs = stmt.executeQuery(sql);
        if (!rs.next()) {
            System.out.println(databaseName +"中不存在以"+ tableName +"命名的数据表，可以进行创建");
            StringBuffer sb = new StringBuffer("");
            sb.append("CREATE TABLE "+ tableName +"(");
            sb.append("id int identity(1,1) primary key,");
            for (int i = 0; i < headers.length; i ++) {
                sb.append(headers[i] + " varchar(" + (everySize[i] + 10) + "),");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(");");
            int tableState = stmt.executeUpdate(String.valueOf(sb));
            if (tableState >= 0) {
                System.out.println("已创建名称为"+ tableName +"的数据表，数据将进行导入");
                stmt.close();
                conn.close();
                return tableName;
            } else {
                System.out.println("未知原因，数据表创建失败");
                stmt.close();
                conn.close();
                return null;
            }
        } else {
            System.out.println("数据库"+ databaseName +"中存在以该名称命名的数据表，数据将继续导入");
            return tableName;
        }
    }

    public static void importData(List<String[]> content, String databaseName, String tableName) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Connection conn = null;
        String sql;
        String url = "jdbc:sqlserver://10.10.240.120:1433;DatabaseName="+ databaseName;
        String user = "sa";
        String password = "Sa1234";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement();
        for (int i = 0; i < content.size(); i ++) {
            StringBuffer sb = new StringBuffer("");
            sb.append("INSERT INTO dbo."+ tableName + " values (");
            for (int j = 0; j < content.get(i).length; j ++) {
                sb.append("'"+ content.get(i)[j] + "',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(");");
            boolean affectedRows = stmt.execute(String.valueOf(sb));
            if (affectedRows == true) {
                System.out.println("failed save "+ tableName +" data at line" + i);
            }
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("成功将数据存入 "+ tableName +"，时间为："+ df.format(new java.util.Date()));
    }
}
