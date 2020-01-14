package com.jscape.inet.mft.functions.repo;

import com.jscape.inet.mft.Function;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        File file = new File(value[0].toString());
        Date date = null;
        try {
            String regex = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(value[1].toString());
            if (matcher.matches()) {
                dateFormat.setLenient(false);
                date = dateFormat.parse(value[1].toString());
            } else {
                InvalidDate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return FileUtils.isFileNewer(file, date);


    }

    private void InvalidDate() {
        try {
            throw new InvalidDateFormatException("Invalid Date");
        } catch (InvalidDateFormatException ex) {
            ex.printStackTrace();
        }
    }

    private class InvalidDateFormatException extends Exception {
        private InvalidDateFormatException(String invalid_date) {
            super(invalid_date);
        }
    }

}
