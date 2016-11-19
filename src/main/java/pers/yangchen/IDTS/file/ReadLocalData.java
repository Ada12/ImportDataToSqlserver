package pers.yangchen.IDTS.file;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.supercsv.io.*;
import org.supercsv.prefs.CsvPreference;


/**
 * Created by yangchen on 16/11/5.
 */
public class ReadLocalData {

    // get csv content via super-csv
    public static Map<String, Object> readCsvData(JSONObject setting) throws IOException, JSONException {
        String path = setting.getString("FILEPATH");
        JSONArray fields = setting.getJSONArray("FIELDS");
        List<String> typeArray = new ArrayList<String>();
        List<String> nameArray = new ArrayList<String>();
        for (int f = 0; f < fields.length(); f ++) {
            typeArray.add(fields.getJSONObject(f).getString("TYPE"));
            nameArray.add(fields.getJSONObject(f).getString("NAME"));
        }
        String dateFormat = setting.getString("DATE_FORMAT");
        Map<String, Object> result = new HashMap<String, Object>();
        List<String[]> content = new ArrayList<String[]>();
        CsvListReader reader = new CsvListReader(new FileReader(path), CsvPreference.EXCEL_PREFERENCE);
        String[] header = reader.getHeader(true); // if remove the header
        List<String> line = new ArrayList<String>();
        int[] everySize = new int[fields.length()];
        for (int j = 0; j < everySize.length; j ++) {
            everySize[j] = 0;
        }
        while ((line = reader.read()) != null) {
            List<String> newLine = new ArrayList<String>();
            for (int i = 0; i < line.size(); i ++) {
                String newSingle = getSingle(typeArray.get(i), line.get(i), dateFormat);
                if ((newSingle != null)&&(newSingle != "null")&&(newSingle != "NULL")) {
                    int singleLength = newSingle.length();
                    if (isContainChinese(newSingle)) {
                        singleLength = singleLength * 2;
                    }
                    if (singleLength > everySize[i]) {
                        everySize[i] = singleLength;
                    }
                    char a = newSingle.charAt(0);
                    if (newSingle.charAt(0) == '\"') {
                        newLine.add(newSingle.substring(1, newSingle.length()-1));
                    } else {
                        newLine.add(i, newSingle);
                    }
                } else {
                    newLine.add(i, "");
                }
            }
            content.add(newLine.toArray(new String[] {}));
        }
        result.put("everySize", everySize);
//        result.put("header", header);
        result.put("content", content);
        return result;
    }

    public static Map<String, Object> readTextData(JSONObject setting) throws IOException, JSONException {
        String path = setting.getString("FILEPATH");
        JSONArray fields = setting.getJSONArray("FIELDS");
        List<String> fieldsArray = new ArrayList<String>(); // type的array
        List<String> nameArray = new ArrayList<String>();
        for (int f = 0; f < fields.length(); f ++) {
            fieldsArray.add(fields.getJSONObject(f).getString("TYPE"));
            nameArray.add(fields.getJSONObject(f).getString("NAME"));
        }
        String dateFormat = setting.getString("DATE_FORMAT"); // date format
        Map<String, Object> result = new HashMap<String, Object>();
        List<String[]> content = new ArrayList<String[]>();
        InputStream is = new FileInputStream(path);
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine();
        line = reader.readLine();
        String[] header = line.split("\\\t");
        line = reader.readLine();
        int[] everySize = new int[fields.length()];
        for (int j = 0; j < everySize.length; j ++) {
            everySize[j] = 0;
        }
        while (line != null){
            String[] single = line.split("\\\t");
            String[] newLine = new String[single.length];
            for (int i = 0; i < single.length; i ++) {
                String newSingle = getSingle(fieldsArray.get(i), single[i], dateFormat);
                if ((single[i] != "")&&(single[i] != "null")&&(single[i] != null)&&(single[i] != "NULL")) {
                    int singleLength = single[i].length();
                    if (isContainChinese(single[i])) {
                        singleLength = singleLength * 2;
                    }
                    if (singleLength > everySize[i]) {
                        everySize[i] = singleLength;
                    }
                    if((single[i].charAt(0) == '\"')&&(single[i].charAt(single[i].length()-1) == '\"')) {
                        newLine[i] = single[i].substring(1, single[i].length()-1);
                    } else {
                        newLine[i] = single[i];
                    }
                } else {
                    newLine[i] = "";
                }
            }
            content.add(newLine);
            line = reader.readLine();
        }
        result.put("everySize", everySize);
//        result.put("header", header);
        result.put("content", content);
        return result;
    }

    public static int isId(List<String> nameList) {
        int idIndex = -1;
        for (int i = 0; i < nameList.size(); i ++) {
            if (nameList.get(i).equals("ID")) {
                idIndex = i;
            }
        }
        return idIndex;
    }

    public static boolean isPos(List<String> nameList) {
        if (nameList.equals("LON")&&nameList.equals("LAT")) {
            return true;
        } else {
            return false;
        }
    }

    public static List<Integer> getPosIndex(List<String> nameList) {
        List<Integer> positionIndex = new ArrayList<Integer>();
        positionIndex.add(0, -1);
        positionIndex.add(1, -1);
        for (int i = 0; i < nameList.size(); i ++) {
            if (nameList.get(i).equals("LON")) {
                positionIndex.add(0, i);
            } else if (nameList.get(i).equals("LAT")) {
                positionIndex.add(1, i);
            }
        }
        return positionIndex;
    }

    public static List<Double> LL2Mercator(String lon, String lat) {
        try {
            List<Double> mercatorPos = new ArrayList<Double>();
            double lonD = Double.parseDouble(lon);
            double latD = Double.parseDouble(lat);
            if (latD > 85.05112) {
                latD = 85.05112;
            } else if (latD < -85.05112) {
                latD = -85.05112;
            }
            // x为经度，longitude
            double x = lonD * 20037508.34 / 180;
            // y为纬度，latitude
            double y = Math.log(Math.tan((90 + latD) * Math.PI / 360)) / (Math.PI / 180);
            y = y * 20037508.34 / 180;
            mercatorPos.add(0, x);
            mercatorPos.add(1, y);
            return mercatorPos;
        } catch (NumberFormatException e) {
            List<Double> errorPos = new ArrayList<Double>();
            errorPos.add(0, null);
            errorPos.add(0, null);
            return errorPos;
        }
    }

    public static String getSingle(String type, String content, String dateFormat) {
        if (type.equals("INT")) {
            return toInt(content);
        } else if (type.equals("FLOAT")) {
            return toFloat(content);
        } else if (type.equals("DATETIME")) {
            return toDateTime(dateFormat, content);
        } else {
            // type is varchar
            return content;
        }
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static String toInt(String str) {
        if (NumberUtils.isDigits(str)) {
            return str;
        } else {
            return "";
        }
    }

    public static String toFloat(String str) {
        if (NumberUtils.isNumber(str)) {
            return str;
        } else {
            return "";
        }
    }

    public static String toDateTime(String formatStr, String dateStr) {
        SimpleDateFormat format=new SimpleDateFormat(formatStr);
        String thisTime = "";
        try {
            Date date = format.parse(dateStr);
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            thisTime = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
        } catch (ParseException e) {
            System.out.println("时间" + dateStr + "出错，错误信息为:" + e.getMessage());
        }
        return thisTime;
    }
}
