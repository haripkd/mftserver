package com.jscape.inet.mft.workflow.actions.repo.av;

import com.jscape.inet.mft.workflow.AbstractAction;
import com.jscape.inet.mft.workflow.actions.repo.av.util.ClamAvUtil;
import com.jscape.util.Assert;
import com.jscape.util.reflection.PropertyDescriptor;
import com.jscape.util.reflection.StringField;
import com.jscape.util.reflection.types.FileField;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


/**
 * Clam AV Virus Scan
 *
 * @author Hari Prasad.
 */
public class ClamAvVirusScanAction extends AbstractAction {

    protected static final String DESCRIPTION = "Scan the directory or file using Clam AV";

    protected static final PropertyDescriptor[] DESCRIPTORS = {
            new PropertyDescriptor("Location", new FileField(), true, false),
            new PropertyDescriptor("Arguments", new StringField(), true, false),
            ACTION_PRIORITY_DESCRIPTOR,
            TRIGGER_ERROR_MESSAGE_DESCRIPTOR,
            LOG_ACTION_DESCRIPTOR,
    };
    protected String location;
    protected String arguments;
    private List<String> commandElements;
    private Process process;
    private String scanResponse;

    public ClamAvVirusScanAction() {
        super("com.jscape.inet.mft.workflow.actions.repo.av.properties.ClamAvVirusScanHelp");
    }

    /**
     * Sets the program which is ran. It is requiered.
     *
     * @param value the path to Clam AV Scan
     */

    public void setLocation(String value) {
        Assert.notNull(value);
        this.location = value;
    }


    /**
     * Sets the arguments used by the process.
     *
     * @param value the process arguments.
     */
    public void setArguments(String value) {
        Assert.notNull(value);
        this.arguments = value;
    }

    @Override
    protected void execute() throws Exception {
        startScanProcess();
        setupResultMessage();
    }

    private void startScanProcess() throws Exception {
        initCommandElements();
        startScan();
        pipeStream();
        closeStream();

    }

    private void initCommandElements() throws Exception {
        this.commandElements = new LinkedList<>();
        addArguments();

    }

    private void addArguments() {
        if (this.arguments == null && this.arguments.isEmpty()) {
            return;
        }
        this.commandElements = ClamAvUtil.formatArgument(this.arguments, this.location);
    }

    private void startScan() throws Exception {
        this.process = new ProcessBuilder(this.commandElements).start();
    }

    private void pipeStream() throws IOException {
        this.scanResponse = ClamAvUtil.processStream(this.process.getInputStream());
    }

    private void closeStream() throws Exception {
        this.process.getInputStream().close();
    }

    private void setupResultMessage() {
        this.resultMessage = String.format("the scan process has been executed and the response is %s", this.scanResponse);
    }

    public String getScanResponse() {
        return scanResponse;
    }

    public void setScanResponse(String scanResponse) {
        this.scanResponse = scanResponse;
    }


    @Override
    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return DESCRIPTORS;
    }


}
