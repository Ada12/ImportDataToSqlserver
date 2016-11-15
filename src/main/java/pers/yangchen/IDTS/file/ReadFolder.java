package pers.yangchen.IDTS.file;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangchen on 16/11/5.
 */
public class ReadFolder {

    private static List<String> filePaths;

    private static boolean flag = false;

    private static int index = -1;

    private static List<List<String>> partedFilePaths;

    private static List<String> directory;

    public static Map<String, Object> main(String path) {
        File[] files = new File(path).listFiles();
        List<String> paths = new ArrayList<String>();
        ReadFolder.setFilePaths(paths);
        directory = new ArrayList<String>();
        partedFilePaths = new ArrayList<List<String>>();
        showFiles(files);
        Map<String, Object> returnInfo = new HashMap<String, Object>();
        returnInfo.put("directory", directory);
        returnInfo.put("paths", partedFilePaths);
        return returnInfo;
    }

    public static List<String> getFilePaths() {
        return filePaths;
    }

    public static void setFilePaths(List<String> filePaths) {
        ReadFolder.filePaths = filePaths;
    }

    //get all files
    private static void showFiles(File[] files) {
        for(File file : files){
            if(file.isDirectory()){
                directory.add(file.getName().toString());
                flag = true;
                showFiles(file.listFiles());
            }else{
                if (!file.isHidden()) {
                    if (flag == true) {
                        partedFilePaths.add(filePaths);
                        filePaths = new ArrayList<String>();
                        filePaths.add(file.getAbsolutePath());
                    } else {
                        filePaths.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }
}
