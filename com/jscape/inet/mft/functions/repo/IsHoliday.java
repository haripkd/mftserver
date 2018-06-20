package com.jscape.inet.mft.functions.repo;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import com.jscape.inet.mft.Function;


public class IsHoliday extends Function {
	
	private static final String pattern = "MM/dd/YYYY";
	private Date date;
	private BufferedReader reader;

	@Override
	public String getName() {
		return "IsHoliday";
	}
	
    @Override
    public String toString() {
        return getName();
    }	

	@Override
	public Map<String, String> getTemplates() {
		HashMap <String,String> map = new HashMap<String,String>();
		map.put("IsHoliday(file,date)", "Returns true if specified file contains date, false otherwise. The first argument is path to holiday file and second argument is date in milliseconds");		
		map.put("IsHoliday(file)", "Returns true if file contains current date, false otherwise.");
		return map;
	}

	@Override
	public Object evaluate(Object[] value) {
		File file = new File(value[0].toString());
		if(value.length == 2) {
			date = new Date(Long.parseLong(value[1].toString()));	
		} else {
			date = new Date(System.currentTimeMillis());
		}
						
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		String formattedDate = formatter.format(date);
		boolean match = false;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			for(String line; (line = reader.readLine()) != null; ) {
		       if(line.equals(formattedDate)) {
		    	   match = true;
		    	   break;
		       }
		    }
			
			
		} catch (IOException ioe) {
			ioe.printStackTrace();			
		} finally {
			try {
				reader.close();	
			} catch (IOException ie) {
				ie.printStackTrace();
			} finally {
				reader = null;
			}			
		}
		return match;
	}
	
}
