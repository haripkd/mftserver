package com.jscape.inet.mft.functions.repo;


import com.jscape.inet.mft.Function;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FileContains extends Function {

    private static final int MAX_LINE_LENGTH = 65536;

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public String getName() {
        return "FileContains";
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Map<String, String> getTemplates() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("FileContainsCopy(filepath,regex)", "Returns true if regular expression matches any content in the file");
        map.put("FileContainsCopy(filepath,regex,maxlines)", "Returns true if regular expression matches any content in the file on specified maximum lines");
        return map;
    }

    @Override
    public Object evaluate(Object[] value) {

        File file = new File(value[0].toString());
        String phrase = value[1].toString();
        int maxLineLength = value.length > 2 ? Integer.parseInt(value[2].toString()) : MAX_LINE_LENGTH;
        int lineNo = 1;
        try {
            Scanner fileScanner = new Scanner(file);
            Pattern pattern = Pattern.compile(phrase,Pattern.CASE_INSENSITIVE);

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (pattern.matcher(line).matches()){
                    fileScanner.close();
                    return true;
                }
                lineNo++;
                if (lineNo > maxLineLength) break;
            }
            fileScanner.close();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
