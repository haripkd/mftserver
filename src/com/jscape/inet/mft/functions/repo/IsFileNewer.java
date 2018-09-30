package com.jscape.inet.mft.functions.repo;

import com.jscape.inet.mft.Function;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.texen.util.FileUtil;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hari on 9/21/2018.
 */
public class IsFileNewer extends Function {

    @Override
    public String getName() {
        return "IsFileNewer";
    }

    @Override
    public Map<String, String> getTemplates() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("IsFileNewer(File1,File2)", "Return true if file1 is newer than file2");
        return map;
    }

    @Override
    public Object evaluate(Object[] value) {
        File file1 = new File(value[0].toString());
        File file2 = new File(value[1].toString());
        return FileUtils.isFileNewer(file1, file2);
    }
}
