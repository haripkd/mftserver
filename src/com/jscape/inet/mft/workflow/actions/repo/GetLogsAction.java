package com.jscape.inet.mft.workflow.actions.repo;

import com.jscape.inet.mft.workflow.AbstractAction;
import com.jscape.util.Assert;
import com.jscape.util.reflection.PropertyDescriptor;
import com.jscape.util.reflection.StringField;
import com.jscape.util.reflection.types.FileField;

import java.io.*;
import java.util.Arrays;

/**
 * Created by hari on 8/18/2018.
 */
public class GetLogsAction extends AbstractAction {

    protected static final String DESCRIPTION = "Export the logs by keywords";
    protected static final String USER_DIRECTORY = "user.dir";
    protected static final String FILE_SEPARATOR = "file.separator";
    protected static final String FILE_FOLDER_1="var";
    protected static final String FILE_FOLDER_2="log";
    protected static final String FILE_NAME="server0.log";

    protected static final PropertyDescriptor[] DESCRIPTORS = {
            new PropertyDescriptor("File", new FileField(), true, false),
            new PropertyDescriptor("Exceptions", new StringField(), true, false),
            ACTION_PRIORITY_DESCRIPTOR,
            TRIGGER_ERROR_MESSAGE_DESCRIPTOR,
            LOG_ACTION_DESCRIPTOR,
    };

    protected String file;
    protected String exceptions;

    public GetLogsAction(){
        super("com.jscape.inet.mft.workflow.actions.repo.properties.GetLogsHelp");
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return DESCRIPTORS;
    }

    public void setFile(String value) {
        Assert.isValidString(value);
        this.file = value;
    }

    public void setExceptions(String value) {
        Assert.isValidString(value);
        this.exceptions = value;
    }

    @Override
    protected void execute() throws Exception {

        String logPath = System.getProperty(USER_DIRECTORY)
                + System.getProperty(FILE_SEPARATOR)
                + FILE_FOLDER_1
                + System.getProperty(FILE_SEPARATOR)
                + FILE_FOLDER_2
                + System.getProperty(FILE_SEPARATOR) + FILE_NAME;

        FileInputStream fileInputStream = new FileInputStream(logPath);

        DataInputStream in = new DataInputStream(fileInputStream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        // output file
        BufferedWriter out = new BufferedWriter(new FileWriter(file));

        String strLine;
        Boolean broken = false;
        int line = 0;

        while ((strLine = br.readLine()) != null) {
            if (Arrays.asList(exceptions.split(",")).contains(strLine)){		// keyword List
                broken = true;
            }
            if (broken) {
                out.write(strLine);
                out.newLine();
                line++;
            }
            if (line == 10) {	// print next 10 lines after exception
                broken = false;
                line = 0;
            }
        }

        in.close();
        out.close();
    }


}
