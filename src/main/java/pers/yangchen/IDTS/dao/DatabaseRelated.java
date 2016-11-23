package pers.yangchen.IDTS.dao;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

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
            // SQL DDL, use executeUpdate, and it's return value always equals 0
            int databaseState = stmt.executeUpdate("create database " + databaseName);
            if (databaseState == 0) {
                System.out.println("已创建名称为"+ databaseName +"的数据库");
                stmt.close();
                conn.close();
                return databaseName;
            } else {
                System.out.println("网络异常，数据库创建失败");
                stmt.close();
                conn.close();
                return null;
            }
        } else {
            System.out.println("系统中存在名称为"+ databaseName +"的数据库，将建立数据表");
            return databaseName;
        }
    }

    public static String createTables(String databaseName, JSONObject setting, Map<String, Object> content) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException, JSONException {
        String tableName = setting.getString("TABLE");
        List<Integer> everySize = (List<Integer>) content.get("sizes");
        List<String> names = (List<String>) content.get("names");
        List<String> types = (List<String>) content.get("types");
        for (int e = 0; e < everySize.size(); e ++) {
            int s = everySize.get(e);
            s = s + 20;
            everySize.set(e, s);
        }
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
//            for (int i = 0; i < headers.length; i ++) {
//                sb.append(headers[i] + " varchar(" + (everySize[i] + 10) + "),");
//            }
            for (int i = 0; i< names.size(); i ++) {
                sb.append(names.get(i));
                sb.append(" ");
                sb.append(types.get(i));
                if (types.get(i).equals("VARCHAR")) {
                    sb.append("(" + everySize.get(i) + ")");
                }
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(");");
            // SQL DDL, use executeUpdate, and it's return value always equals 0
            int tableState = stmt.executeUpdate(String.valueOf(sb));
            if (tableState == 0) {
                System.out.println("已创建名称为"+ tableName +"的数据表，数据将进行导入");
                stmt.close();
                conn.close();
                return tableName;
            } else {
                System.out.println("网络异常，数据表创建失败");
                stmt.close();
                conn.close();
                return null;
            }
        } else {
            System.out.println("数据库"+ databaseName +"中存在名称为"+ tableName +"的数据表，数据将继续导入");
            return tableName;
        }
    }

    public static void importData(String databaseName, String tableName, Map<String, Object> data) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        List<String[]> content = (List<String[]>) data.get("content");
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
            try {
                boolean affectedRows = stmt.execute(String.valueOf(sb));
                if (i != 0) {
                    int printFlag = content.size() /10 ;
                    if (printFlag > 1000) {
                        printFlag = 1000;
                    }
                    if (i % printFlag == 0) {
                        System.out.println("正在导入第" + i + "条数据...");
                    }
                }
                if (affectedRows == true) {
                    System.out.println("failed save "+ tableName +" data at line" + i);
                }

            } catch (SQLServerException e){
                String thisData = "";
                for (int t = 0; t < content.get(i).length; t ++) {
                    thisData = thisData + content.get(i)[t] + ",";
                }
                System.out.println("数据"+ thisData +"不符合规范");
            }
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("成功将数据存入数据表"+ tableName +"，时间为："+ df.format(new java.util.Date()));
    }

    public static void saveToFixedDataSurvey(JSONObject setting, Map<String, Object> content) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, JSONException {
        String tableName = setting.getString("TABLE");
        String databaseName = setting.getString("DATABASE");
        String indexName = setting.getString("INDEX");
        String desc = setting.getString("DESCRIBE");
        Date beginTime = (Date) content.get("beginTime");
        Date endTime = (Date) content.get("endTime");
        List<String[]> realData = (List<String[]>) content.get("content");

        Connection conn = null;
        String sql;
        String url = "jdbc:sqlserver://10.10.240.120:1433;DatabaseName=DatabaseStatus";
        String user = "sa";
        String password = "Sa1234";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement();
        String surveySql = "SELECT * FROM dbo.fixed_data_survey WHERE data_name='"+ tableName +"' AND database_name='"+ databaseName +"'";
        ResultSet rs = stmt.executeQuery(surveySql);
        long currentNumber = realData.size();
        int flag = 0; // no such type data in database: 0, otherwise: 1
        while (rs.next()) {
            currentNumber = currentNumber + rs.getLong(6);
            Date dbBeginTime = rs.getTimestamp(7);
            if (dbBeginTime != null) {
                if (dbBeginTime.getTime() < beginTime.getTime()) {
                    beginTime = dbBeginTime;
                }
            }
            Date dbEndTime = rs.getTimestamp(8);
            if (dbEndTime != null) {
                if (dbEndTime.getTime() > endTime.getTime()) {
                    endTime  = dbEndTime;
                }
            }
            flag = 1;
        }
        Calendar beginCalendar=Calendar.getInstance();
        beginCalendar.setTime(beginTime);
        String beginTimeStr = beginCalendar.get(Calendar.YEAR) + "-" + (beginCalendar.get(Calendar.MONTH) + 1) + "-" + beginCalendar.get(Calendar.DAY_OF_MONTH) + " "
                + beginCalendar.get(Calendar.HOUR) + ":" + beginCalendar.get(Calendar.MINUTE) + ":" + beginCalendar.get(Calendar.SECOND);
        Calendar endCalendar=Calendar.getInstance();
        endCalendar.setTime(endTime);
        String endTimeStr = endCalendar.get(Calendar.YEAR) + "-" + (endCalendar.get(Calendar.MONTH) + 1) + "-" + endCalendar.get(Calendar.DAY_OF_MONTH) + " "
                + endCalendar.get(Calendar.HOUR) + ":" + endCalendar.get(Calendar.MINUTE) + ":" + endCalendar.get(Calendar.SECOND);
        if (flag == 0) {
            System.out.println("数据表中不存在名为"+ tableName +"的信息，将进行插入");
            String insertSql = "INSERT INTO dbo.fixed_data_survey values ('"+ tableName +"', '"+ databaseName +"', '"+ indexName +"', '"+ desc +"', " +
                    "'"+ currentNumber +"', '"+ beginTimeStr +"', '"+ endTimeStr +"')";
            boolean affectedRows = stmt.execute(insertSql);
            if (affectedRows == true) {
                System.out.println("网络异常，数据插入失败");
            } else {
                System.out.println("数据"+ tableName +"总概况插入成功\n");
            }
        } else {
            System.out.println("数据表中存在名为"+ tableName +"的信息，将进行更新");
            String updateSql = "UPDATE dbo.fixed_data_survey SET current_number='"+ currentNumber +"', begin_time='"+ beginTimeStr +"', end_time='"+ endTimeStr +"' WHERE data_name = '"+ tableName +"' AND database_name = '"+ databaseName +"'";
            boolean affectedRowsUpdate = stmt.execute(updateSql);
            if (affectedRowsUpdate == true) {
                System.out.println("网络异常，数据更新失败");
            } else {
                System.out.println("数据"+ tableName +"总概况更新成功\n");
            }
        }
    }
}
