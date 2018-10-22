package com.jscape.inet.mft.functions.repo;

import com.jscape.inet.mft.Function;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hari on 10/6/2018.
 */
public class GetUsedHeapInMb extends Function {

    @Override
    public String getName() {
        return "GetUsedHeapInMb";
    }

    @Override
    public Map<String, String> getTemplates() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("GetUsedHeapInMb()", "Returns the used heap value in mb");
        return map;
    }

    @Override
    public Object evaluate(Object[] objects) {
        int MB = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() / MB;
    }
}
