package com.jscape.inet.mft.workflow.actions.repo;

import com.jscape.inet.mft.workflow.AbstractAction;
import com.jscape.util.Assert;
import com.jscape.util.reflection.PropertyDescriptor;
import com.jscape.util.reflection.StringField;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

/**
 * Created by hari on 9/14/2018.
 */
public class CopyUrlToFileAction extends AbstractAction {

    protected static final String DESCRIPTION = "Download the file from URL";

    protected static final PropertyDescriptor[] DESCRIPTORS = {
            new PropertyDescriptor("Url", new StringField(), true, false),
            new PropertyDescriptor("File", new StringField(), true, false),
            ACTION_PRIORITY_DESCRIPTOR,
            TRIGGER_ERROR_MESSAGE_DESCRIPTOR,
            LOG_ACTION_DESCRIPTOR,
    };


    protected String url;
    protected String file;

    public CopyUrlToFileAction() {
        super("com.jscape.inet.mft.workflow.actions.repo.properties.CopyUrlToFileHelp");
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

    public void setUrl(String value) {
        Assert.isValidString(value);
        this.url = value;
    }

    @Override
    protected void execute() throws Exception {
        URL urlStream = new URL(url);
        FileUtils.copyURLToFile(urlStream, new File(file));
    }
}