package pers.yangchen.IDTS.file;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.util.Map;

/**
 * Created by yangchen on 16/11/15.
 */
public class ReadSetting {

    public static JSONArray getSettingContent (String path) throws IOException {
        InputStream is = new FileInputStream(path);
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine();
        String content = "";
        while (line != null){
            content = content + line;
            line = reader.readLine();
        }
        try {
            JSONArray settings = new JSONArray(content);
            return settings;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
