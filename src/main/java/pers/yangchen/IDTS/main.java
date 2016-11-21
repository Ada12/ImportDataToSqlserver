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

        String settingPath = "/Users/yangchen/Desktop/exam.txt";
        try {
            JSONArray settings = ReadSetting.getSettingContent(settingPath);
            for (int i = 0; i < settings.length(); i ++) {
                JSONObject setting = settings.getJSONObject(i);
                String filePath = setting.getString("FILEPATH");
                Map<String, Object> content = new HashMap<String, Object>();
                if (filePath != null) {
                    String[] filePathArray = filePath.split("\\.");
                    if (filePathArray[filePathArray.length - 1].equals("csv")) {
                        content = ReadLocalData.readCsvData(setting);
                        List<Integer> everySize = (List<Integer>) content.get("sizes");
                        List<String> names = (List<String>) content.get("names");
                        List<String> types = (List<String>) content.get("types");
                        List<String[]> c = (List<String[]>) content.get("content");
                        Date beginTime = (Date) content.get("beginTime");
                        Date endTime = (Date) content.get("endTime");
                        System.out.println(beginTime.toString());
                        System.out.println(endTime.toString());
                        for (int e = 0; e < everySize.size(); e ++) {
                            System.out.println("every size: " + everySize.get(e));
                        }
                        for (int n = 0; n < names.size(); n ++) {
                            System.out.println("name: " + names.get(n));
                        }
                        for (int t = 0; t < types.size(); t ++) {
                            System.out.println("types: " + types.get(t));
                        }
                        for (int a = 0; a < c.size(); a ++) {
                            String[] cs = c.get(a);
                            for (int b = 0; b < cs.length; b ++) {
                                System.out.println(cs[b]);
                            }
                        }
                    } else {
                        content = ReadLocalData.readTextData(setting);
                        List<Integer> everySize = (List<Integer>) content.get("sizes");
                        List<String> names = (List<String>) content.get("names");
                        List<String> types = (List<String>) content.get("types");
                        List<String[]> c = (List<String[]>) content.get("content");
                        Date beginTime = (Date) content.get("beginTime");
                        Date endTime = (Date) content.get("endTime");
                        System.out.println(beginTime.toString());
                        System.out.println(endTime.toString());
                        for (int e = 0; e < everySize.size(); e ++) {
                            System.out.println("every size: " + everySize.get(e));
                        }
                        for (int n = 0; n < names.size(); n ++) {
                            System.out.println("name: " + names.get(n));
                        }
                        for (int t = 0; t < types.size(); t ++) {
                            System.out.println("types: " + types.get(t));
                        }
                        for (int a = 0; a < c.size(); a ++) {
                            String[] cs = c.get(a);
                            for (int b = 0; b < cs.length; b ++) {
                                System.out.println(cs[b]);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        int[] everySize = {1, 2, 3, 4};
//        int index = 2;
//        int[] sizes = new int[everySize.length-1];
//        for (int i = 0; i < index; i ++) {
//            sizes[i] = everySize[i];
//        }
//        for (int j = index + 1; j < everySize.length; j ++) {
//            sizes[j-1] = everySize[j];
//        }
//        for (int k = 0; k < sizes.length; k ++) {
//            System.out.println(sizes[k]);
//        }

    }
}
