package com.jscape.inet.mft.functions.repo;

import com.jscape.inet.mft.Function;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hari on 9/29/2018.
 */
public class IsFileNewerByTimeStamp extends Function {

    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

    @Override
    public String getName() {
        return "IsFileNewerByTimeStamp";
    }

    @Override
    public Map<String, String> getTemplates() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("IsFileNewerByTimeStamp(File,dd-MMM-yyyy HH:mm:ss)", "Return true if the specified File is newer than the specified date and time.");
        return map;
    }

    @Override
    public Object evaluate(Object[] value) {
        try {
            File file = new File(value[0].toString());
            Date dateAndTime = dateTimeFormat.parse(value[1].toString());
            return FileUtils.isFileNewer(file, dateAndTime.getTime());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
