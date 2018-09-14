package com.jscape.inet.mft.functions.repo;

import com.jscape.inet.mft.Function;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hari on 9/9/2018.
 */
public class GetDirectorySize extends Function {

    @Override
    public String getName() {
        return "GetDirectorySize";
    }

    @Override
    public Map<String, String> getTemplates() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("GetDirectorySize(directory)", "Returns size of a directory including its contents in MB");
        return map;
    }


    @Override
    public Object evaluate(Object[] value) {
        Float directorySize = 0.00f;
        File directory = new File(value[0].toString());
        directorySize = Float.valueOf(FileUtils.sizeOfDirectory(directory)/(1024.00f*1024.00f));
        return directorySize;
    }
}
