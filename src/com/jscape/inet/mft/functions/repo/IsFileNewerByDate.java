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
public class IsFileNewerByDate extends Function {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public String getName() {
        return "IsFileNewerByDate";
    }

    @Override
    public Map<String, String> getTemplates() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("IsFileNewerByDate(File,dd/MM/yyyy)", "Return true if the specified File is newer than the specified Date.");
        return map;
    }

    @Override
    public Object evaluate(Object[] value) {
        try {
            File file = new File(value[0].toString());
            Date date = dateFormat.parse(value[1].toString());
            return FileUtils.isFileNewer(file, date);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
