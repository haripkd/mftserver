package com.jscape.inet.mft.functions.repo;

import com.jscape.inet.mft.Function;
import com.jscape.util.io.primitive.CommonLineReader;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by hari on 8/28/2018.
 */
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
        map.put("FileContains(filepath,regex)", "Returns true if regular expression matches any content in the file on default character set and default max lines");
        map.put("FileContains(filepath,regex,maxlines)", "Returns true if regular expression matches any content in the file on specified maximum lines");
        map.put("FileContains(filepath,regex,maxlines,charset)", "Returns true if regular expression matches any content in the file on specified maximum lines and charset");
        return map;
    }

    @Override
    public Object evaluate(Object[] value) {
        try {
            Path file = Paths.get(value[0].toString());
            Pattern pattern = Pattern.compile(value[1].toString(),Pattern.CASE_INSENSITIVE);
            int maxLineLength = value.length > 2 ? Integer.parseInt(value[2].toString()) : MAX_LINE_LENGTH;
            Charset charset = value.length > 3 ? Charset.forName(value[3].toString()) : CHARSET;

            try (InputStream in = Files.newInputStream(file)) {
                CommonLineReader reader = new CommonLineReader(in, maxLineLength);
                String line;
                int lineNo =0;
                while ((line = reader.readLine(charset)) != null) {
                    lineNo++;
                    if (lineNo <=maxLineLength && pattern.matcher(line).find()) {
                        return true;
                    }
                }
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
