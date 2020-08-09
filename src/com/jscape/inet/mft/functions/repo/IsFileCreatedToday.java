package com.jscape.inet.mft.functions.repo;

import com.jscape.inet.mft.Function;
import org.apache.commons.lang3.time.DateUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IsFileCreatedToday extends Function {
    @Override
    public String getName() {
        return "IsFileCreatedToday";
    }

    @Override
    public Map<String, String> getTemplates() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("IsFileCreatedToday(filepath)", "Return true if file creation date matches current day");
        return map;
    }

    @Override
    public Object evaluate(Object[] value) {
        String filePath = value[0].toString();
        Path path = Paths.get(filePath);
        try {
            FileTime creationTime = (FileTime) Files.getAttribute(path, "creationTime");
            return DateUtils.isSameDay(new Date(creationTime.toMillis()), new Date());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
