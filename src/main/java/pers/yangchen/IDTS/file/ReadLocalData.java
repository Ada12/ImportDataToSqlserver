package pers.yangchen.IDTS.file;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.io.*;
import org.supercsv.prefs.CsvPreference;


/**
 * Created by yangchen on 16/11/5.
 */
public class ReadLocalData {
    // get csv content via super-csv
    public static Map<String, Object> readCsvData(String path) throws IOException {
        Map<String, Object> result = new HashMap<String, Object>();
        List<String[]> content = new ArrayList<String[]>();
        CsvListReader reader = new CsvListReader(new FileReader(path), CsvPreference.EXCEL_PREFERENCE);
        String[] header = reader.getHeader(true); // if remove the header
        List<String> line = new ArrayList<String>();
        int[] everySize = new int[header.length];
        for (int j = 0; j < everySize.length; j ++) {
            everySize[j] = 0;
        }
        while ((line = reader.read()) != null) {
            List<String> newLine = new ArrayList<String>();
            for (int i = 0; i < line.size(); i ++) {
                if ((line.get(i) != null)&&(line.get(i) != "null")) {
                    if(line.get(i).length() > everySize[i]) {
                        everySize[i] = line.get(i).length();
                    }
                    char a = line.get(i).charAt(0);
                    if(line.get(i).charAt(0) == '\"') {
                        newLine.add(line.get(i).substring(1, line.get(i).length()-1));
                    } else {
                        newLine.add(i, line.get(i));
                    }
                } else {
                    newLine.add(i, "");
                }
            }
            content.add(newLine.toArray(new String[] {}));
        }
        result.put("everySize", everySize);
        result.put("header", header);
        result.put("content", content);
        return result;
    }

    public static Map<String, Object> readTextData(String path) throws IOException {
        Map<String, Object> result = new HashMap<String, Object>();
        List<String[]> content = new ArrayList<String[]>();
        InputStream is = new FileInputStream(path);
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine();
        String[] header = line.split("\\\t");
        line = reader.readLine();
        int[] everySize = new int[header.length];
        for (int j = 0; j < everySize.length; j ++) {
            everySize[j] = 0;
        }
        while (line != null){
            String[] single = line.split("\\\t");
            String[] newSingle = new String[single.length];
            for (int i = 0; i < single.length; i ++) {
                if ((single[i] != "")&&(single[i] != "null")&&(single[i] != null)) {
                    if (single[i].length() > everySize[i]) {
                        everySize[i] = single[i].length();
                    }
                    if((single[i].charAt(0) == '\"')&&(single[i].charAt(single[i].length()-1) == '\"')) {
                        newSingle[i] = single[i].substring(1, single[i].length()-1);
                    } else {
                        newSingle[i] = single[i];
                    }
                } else {
                    newSingle[i] = "";
                }
            }
            content.add(newSingle);
            line = reader.readLine();
        }
        result.put("everySize", everySize);
        result.put("header", header);
        result.put("content", content);
        return result;
    }
}
