package com.jscape.inet.mft.functions.repo;

import com.jscape.inet.mft.Function;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hari on 10/6/2018.
 */
public class GetHeapUsageInPercentile extends Function {

    @Override
    public String getName() {
        return "GetHeapUsageInPercentile";
    }

    @Override
    public Map<String, String> getTemplates() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("GetHeapUsageInPercentile()", "Returns heap usage in percentile");
        return map;
    }

    @Override
    public Object evaluate(Object[] objects) {
        int MB = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        int allocatedHeap = (int) (runtime.totalMemory() / MB);
        int maxHeap = (int) (runtime.maxMemory() / MB);
        int percentileUsedHeap = 100 - (int) ((Float.valueOf(maxHeap-allocatedHeap)/maxHeap) * 100);
        return percentileUsedHeap;
    }
}
