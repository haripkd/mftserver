package com.jscape.inet.mft.workflow.actions.repo;

import com.jscape.inet.mft.workflow.AbstractAction;
import com.jscape.util.Assert;
import com.jscape.util.reflection.PropertyDescriptor;
import com.jscape.util.reflection.StringField;
import com.jscape.util.reflection.types.DirectoryField;
import com.jscape.util.reflection.types.FileField;

import java.io.*;

/**
 * Created by hari on 7/19/2018.
 */
public class SplitLargeTextFileAction extends AbstractAction {

    protected static final String DESCRIPTION = "Split a larger text file into multiple";
    protected static final PropertyDescriptor[] DESCRIPTORS = {
            new PropertyDescriptor("File", new FileField(), true, false),
            new PropertyDescriptor("Destination", new DirectoryField(), true, false),
            new PropertyDescriptor("Size", new StringField(), true, false),
            ACTION_PRIORITY_DESCRIPTOR,
            TRIGGER_ERROR_MESSAGE_DESCRIPTOR,
            LOG_ACTION_DESCRIPTOR,
    };
    private static final String FILE_SEPARATOR = "file.separator";
    private static final String FILE_EXTENSION = ".txt";
    private static long spiltMB = 1024 * 1024;
    protected String file;
    protected String destinationDirectory;
    protected String splitSize;

    public SplitLargeTextFileAction() {
        super("com.jscape.inet.mft.workflow.actions.repo.properties.SplitLargeTextFileHelp");
    }



    /**
     * {@inheritDoc}
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return DESCRIPTORS;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Sets the file to rename.
     *
     * @param value the file to rename.
     */
    public void setFile(String value) {
        Assert.isValidString(value);
        this.file = value;
    }

    /**
     * Sets the output directory.
     *
     * @param value the output directory.
     */
    public void setDestination(String value) {
        Assert.isValidString(value);
        this.destinationDirectory = value;
    }

    public void setSize(String value) {
        Assert.isValidString(value);
        this.splitSize = value;
    }

    @Override
    protected void execute() throws Exception {
        splitFile(file, destinationDirectory, splitSize);
    }

    public void splitFile(String file, String destinationDirectory, String splitSize) throws Exception {
        int count = 1, data = 0;
        long len = 0;
        long byteFileSize = Long.parseLong(splitSize);
        long splitFileSize = byteFileSize * spiltMB;
        File fileName = new File(file);
        InputStream infile = new BufferedInputStream(new FileInputStream(fileName));
        while (data != -1) {
            File splitFileName = new File(destinationDirectory + System.getProperty(FILE_SEPARATOR) + fileName.getName().substring(0,fileName.getName().indexOf(".")) + count + FILE_EXTENSION);
            OutputStream outfile = new BufferedOutputStream(new FileOutputStream(splitFileName));
            while (data != -1 && len < splitFileSize) {
                outfile.write(data);
                len++;
                data = infile.read();
            }
            len = 0;
            outfile.close();
            count++;
        }
        infile.close();
    }
}
