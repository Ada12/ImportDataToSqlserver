package pers.yangchen.IDTS;

import com.sun.tools.jdi.LinkedHashMap;
import javafx.scene.chart.PieChart;
import org.apache.commons.lang.math.NumberUtils;
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

        String settingPath = args[0];
//        String settingPath = "/Users/yangchen/Desktop/exam.txt";
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
                        String database = setting.getString("DATABASE");
                        String databaseName = DatabaseRelated.createDatabase(database);
                        if (databaseName != null) {
                            String tableName = DatabaseRelated.createTables(databaseName, setting, content);
                            if (tableName != null) {
                                DatabaseRelated.importData(databaseName, tableName, content);
                            }
                        }
                        DatabaseRelated.saveToFixedDataSurvey(setting, content);
                    } else {
                        content = ReadLocalData.readTextData(setting);
                        String database = setting.getString("DATABASE");
                        String databaseName = DatabaseRelated.createDatabase(database);
                        if (databaseName != null) {
                            String tableName = DatabaseRelated.createTables(databaseName, setting, content);
                            if (tableName != null) {
                                DatabaseRelated.importData(databaseName, tableName, content);
                            }
                        }
                        DatabaseRelated.saveToFixedDataSurvey(setting, content);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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
