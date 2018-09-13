package com.jscape.inet.mft.functions.repo;

import com.jscape.inet.mft.Function;
import org.apache.commons.io.FileUtils;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hari on 9/9/2018.
 */
public class FileContentCompare extends Function {

    @Override
    public String getName() {
        return "FileContentCompare";
    }

    @Override
    public Map<String, String> getTemplates() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("FileContentCompare(file1,file2)", "Returns true if both file contents are same");
        return map;
    }


    @Override
    public Object evaluate(Object[] value) {
        boolean flag;
        try {
            File file1 = new File(value[0].toString());
            File file2 = new File(value[1].toString());
            flag = FileUtils.contentEquals(file1, file2);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return flag;
    }
}
