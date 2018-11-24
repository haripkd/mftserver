package com.jscape.inet.mft.functions.repo;

import com.jscape.inet.mft.Function;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hari on 9/15/2018.
 */
public class GetDiskUsageInPercentile extends Function {

    @Override
    public String getName() {
        return "GetDiskUsageInPercentile";
    }

    @Override
    public Map<String, String> getTemplates() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("GetDiskUsageInPercentile(String)", "Returns usage in percentile");
        return map;
    }

    @Override
    public Object evaluate(Object[] value) {
        File file = new File(value[0].toString());
        Double GB = 1024.0 * 1024 * 1024;
        Double usableSpace = file.getUsableSpace() / GB;
        Double totalSpace = file.getTotalSpace() / GB;
        Double usedSpace = 100.00 - ((usableSpace / totalSpace) * 100);
        return (usedSpace.intValue());
    }
}
