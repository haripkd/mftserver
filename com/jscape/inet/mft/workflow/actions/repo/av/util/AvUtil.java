package com.jscape.inet.mft.workflow.actions.repo.av.util;

import org.eclipse.jetty.util.QuotedStringTokenizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 *  AV Util
 *
 * @author Hari Prasad.
 */
public class AvUtil {
    private static List<String> commandList;

    public static List<String> formatArgument(String arguments, String location,boolean format) {
        commandList = new ArrayList<>();
        commandList.add(location);
        for (StringTokenizer t = new QuotedStringTokenizer(arguments, ",", false, true); t.hasMoreTokens(); ) {
            String arg = t.nextToken();

            if (arg.charAt(0) == '"' && arg.charAt(arg.length() - 1) == '"') {
                arg = arg.substring(1, arg.length() - 1);
            }

            if (arg.trim().length() > 0) {
                arg = (arg.contains("/") || arg.contains("\\")) && format ? arg.replace("/", "\\") : arg;
                commandList.add(arg);
            }
        }

        return commandList;
    }

    public static String processStream(InputStream inputStream) throws IOException {
        String response = null;
        if (null != inputStream) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int c = -1;
            while ((c = inputStream.read()) != -1) {
                byteArrayOutputStream.write(c);
            }
            response = new String(byteArrayOutputStream.toByteArray());
            inputStream.close();
            byteArrayOutputStream.close();
        }
        System.out.println("Response after processing the stream-->" +response);
        return response;

    }

}