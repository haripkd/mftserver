package com.jscape.inet.mft.functions.repo;

import com.jscape.inet.mft.Function;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hari on 7/11/2018.
 */
public class AppendDateOrTimeToFileName extends Function {

    private Date date;

    @Override
    public String getName() {
        return "AppendDateOrTimeToFileName";
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Map<String, String> getTemplates() {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("AppendDateOrTimeToFileName(file,datePattern)","Returns filename appended with the specified pattern. Pattern examples: MM/dd/YYYY,hh_mm_ss");
        return map;
    }

    @Override
    public Object evaluate(Object[] value) {
        String filePath = value[0].toString();
        String datePattern = value[1].toString();
        date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        String formattedDate = formatter.format(date);
        int lastPointer = filePath.lastIndexOf(".");
        filePath = filePath.substring(0,lastPointer) + formattedDate + filePath.substring(lastPointer);
        return filePath;
    }
}
