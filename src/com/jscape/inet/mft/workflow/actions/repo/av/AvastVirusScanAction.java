package com.jscape.inet.mft.workflow.actions.repo.av;

import com.jscape.inet.mft.workflow.AbstractAction;
import com.jscape.inet.mft.workflow.actions.repo.av.util.AvUtil;
import com.jscape.util.Assert;
import com.jscape.util.reflection.PropertyDescriptor;
import com.jscape.util.reflection.StringField;
import com.jscape.util.reflection.types.FileField;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * Avast Virus Scan
 *
 * @author Hari Prasad.
 */
public class AvastVirusScanAction extends AbstractAction {

    protected static final String DESCRIPTION = "Scan the directory or file using Avast";

    protected static final PropertyDescriptor[] DESCRIPTORS = {
            new PropertyDescriptor("Location", new FileField(), true, false),
            new PropertyDescriptor("Arguments", new StringField(), true, false),
            new PropertyDescriptor("OutputLog", new FileField(), true, false),
            ACTION_PRIORITY_DESCRIPTOR,
            TRIGGER_ERROR_MESSAGE_DESCRIPTOR,
            LOG_ACTION_DESCRIPTOR,
    };
    protected String location;
    protected String arguments;
    private List<String> commandElements;
    private Process process;
    private String scanResponse;
    private String outputLog;
    private File outputFile;


    public AvastVirusScanAction() {
        super("com.jscape.inet.mft.workflow.actions.repo.av.properties.AvastVirusScanHelp");
    }

    /**
     * Sets the program which is ran. It is requiered.
     *
     * @param value the path to Avast Scan
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

    public void setOutputLog(String outputLog) {
        Assert.notNull(outputLog);
        this.outputLog = outputLog;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
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
        this.commandElements = AvUtil.formatArgument(this.arguments, this.location,false);
    }

    private void startScan() throws Exception {

        this.process = new ProcessBuilder(this.commandElements).start();
    }

    private void pipeStream() throws IOException {
        if (this.outputLog == null || this.outputLog.isEmpty()) {
            return;
        }
        this.scanResponse = AvUtil.processStream(this.process.getInputStream());
        this.outputFile = new File(this.outputLog);
        StreamPipe pipe = new StreamPipe(this.scanResponse, this.outputFile);
        pipe.start();
    }

    private void closeStream() throws Exception {
        this.process.getInputStream().close();
    }

    private void setupResultMessage() {
        this.resultMessage = String.format("the scan process has %s been executed", this.location);
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





    private static class StreamPipe
            extends Thread {

        private final String outLog;
        private final File outFile;
        private final String dateOfScan="Date of Scan --> ";

        private StreamPipe(String response, File file) {
            Assert.notNull(response);
            this.outLog = response;

            Assert.notNull(file);
            this.outFile = file;

            setDaemon(true);
        }

        public void run() {
            try {
                FileWriter fileWriter = new FileWriter(this.outFile,true);
                fileWriter.write(dateOfScan + new Date() + "\n");
                fileWriter.write(this.outLog);
                fileWriter.flush();
                fileWriter.close();
            } catch(Throwable e) {
                // ignore
            }
        }
    }
}
