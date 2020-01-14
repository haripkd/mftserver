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
            Date dateTime = null;
            File file = new File(value[0].toString());
            String dateAndTime = value[1].toString();
            if (validateDateAndTime(dateAndTime)) {
                dateTimeFormat.setLenient(false);
                dateTime = dateTimeFormat.parse(dateAndTime);
            }
            return FileUtils.isFileNewer(file, dateTime.getTime());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateDateAndTime(String dateAndTime) {
        String dateRegex = "^(3[01]|[12][0-9]|0[1-9])-(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)-[0-9]{4} (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]";
        Pattern datePattern = Pattern.compile(dateRegex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = datePattern.matcher(dateAndTime);
        if (matcher.matches()) return true;
        else InvalidDate(dateAndTime);
        return false;
    }

    private void InvalidDate(String dateAndTime) {
        try {
            throw new IsFileNewerByTimeStamp.InvalidDateFormatException("Unparseable date - dateAndTime");
        } catch (IsFileNewerByTimeStamp.InvalidDateFormatException ex) {
            ex.printStackTrace();
        }
    }


    private class InvalidDateFormatException extends Exception {
        private InvalidDateFormatException(String invalid_date) {
            super(invalid_date);
        }
    }
}
