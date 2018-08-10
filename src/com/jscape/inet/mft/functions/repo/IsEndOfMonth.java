package com.jscape.inet.mft.functions.repo;

import java.util.*;
import java.util.Map;

import com.jscape.inet.mft.Function;

/**
 * Created by hari on 7/31/2018.
 */
public class IsEndOfMonth extends Function {

    @Override
    public String getName() {
        return "IsEndOfMonth";
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Map<String, String> getTemplates() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("IsEndOfMonth(date)", "Returns true if date parameter is the end of month, false otherwise.");
        map.put("IsEndOfMonth(yyyy,mm,dd)", "Returns true if passed parameters in yyyy/mm/dd format falls by end of the month,false otherwise.");
        return map;
    }

    @Override
    public Object evaluate(Object[] value) {
        boolean isEOM = false;
        if (value.length > 1) {
            int year = Integer.parseInt(value[0].toString());
            int month = Integer.parseInt(value[1].toString());
            int date = Integer.parseInt(value[2].toString());
            Calendar cal = Calendar.getInstance();
            // Month is between 0-11
            cal.set(year, month - 1, date);
            isEOM = (cal.getActualMaximum(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH));
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(value[0].toString()));
            isEOM = (cal.getActualMaximum(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH));
        }
        return isEOM;
    }
}
