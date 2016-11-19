package pers.yangchen.IDTS;

import com.sun.tools.jdi.LinkedHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import pers.yangchen.IDTS.dao.DatabaseRelated;
import pers.yangchen.IDTS.file.ReadFolder;
import pers.yangchen.IDTS.file.ReadLocalData;
import pers.yangchen.IDTS.file.ReadSetting;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yangchen on 16/11/5.
 */
public class main {
    public static void main(String[] args) {

//        String settingPath = "/Users/yangchen/Desktop/example.txt";
//        try {
//            JSONArray settings = ReadSetting.getSettingContent(settingPath);
//            for (int i = 0; i < settings.length(); i ++) {
//                JSONObject setting = settings.getJSONObject(i);
//                String filePath = setting.getString("FILEPATH");
//                Map<String, Object> content = new HashMap<String, Object>();
//                if (filePath != null) {
//                    String[] filePathArray = filePath.split("\\.");
//                    if (filePathArray[filePathArray.length - 1].equals("csv")) {
//                        content = ReadLocalData.readCsvData(setting);
//                    } else {
//                        content = ReadLocalData.readTextData(setting);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        String date_string = "04/30/16"; MM/dd/yy
        String date_string = "1960/04/30";
        SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd");
        boolean dateflag = true;
        String thisTime = "";
        try
        {
            Date date = format.parse(date_string);
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            thisTime = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
        } catch (ParseException e)
        {
            dateflag=false;
        }finally{
            //	成功：true ;失败:false
            System.out.println(thisTime);
        }
    }
}
