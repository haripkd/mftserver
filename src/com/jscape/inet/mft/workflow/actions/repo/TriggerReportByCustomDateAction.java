package com.jscape.inet.mft.workflow.actions.repo;

import com.jscape.inet.mft.TriggerState;
import com.jscape.inet.mft.management.client.api.ManagerSubsystem;
import com.jscape.inet.mft.workflow.AbstractAction;
import com.jscape.util.Assert;
import com.jscape.util.reflection.PropertyDescriptor;
import com.jscape.util.reflection.StringField;
import com.jscape.util.reflection.types.FileField;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TriggerReportByCustomDateAction extends AbstractAction {

    protected static final String DESCRIPTION = "Export the trigger report by custom date";
    protected static final String[] COLUMN_HEADER = {"Trigger ID", "Trigger Name", "Start Date & Time", "End Date & Time", "Status"};
    protected static final String SHEET_NAME = "Trigger_report";
    protected static final SimpleDateFormat reportFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    protected String file;
    protected String triggerName;
    protected String fromDate;
    protected String toDate;

    public TriggerReportByCustomDateAction() {
        super("com.jscape.inet.mft.workflow.actions.repo.properties.TriggerReportByCustomDateHelp");
    }

    protected static final PropertyDescriptor[] DESCRIPTORS = {
            new PropertyDescriptor("File", new FileField(), true, false),
            new PropertyDescriptor("TriggerName", new StringField(), true, false),
            new PropertyDescriptor("FromDate", new StringField(), true, false),
            new PropertyDescriptor("ToDate", new StringField(), true, false),
            ACTION_PRIORITY_DESCRIPTOR,
            TRIGGER_ERROR_MESSAGE_DESCRIPTOR,
            LOG_ACTION_DESCRIPTOR,
    };

    public void setFromDate(String value) {
        Assert.isValidString(value);
        this.fromDate = value;
    }

    public void setToDate(String value) {
        Assert.isValidString(value);
        this.toDate = value;
    }

    public void setFile(String value) {
        Assert.isValidString(value);
        this.file = value;
    }

    public void setTriggerName(String value) {
        Assert.isValidString(value);
        this.triggerName = value;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    protected void execute() throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet workbookSheet = workbook.createSheet(SHEET_NAME);
            fillHeaderOf(workbookSheet);
            fillDataOf(workbookSheet, fromDate, toDate);
            save(workbook);
        }

    }

    private void fillHeaderOf(XSSFSheet sheet) {
        XSSFRow headerRow = sheet.createRow(0);

        for (int i = 0; i < COLUMN_HEADER.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(COLUMN_HEADER[i]);
        }
    }

    private void fillDataOf(XSSFSheet sheet, String fromDate, String toDate)
            throws Exception {
        int rowNum = 1;
        Date fDate = dateFormat.parse(fromDate);
        Date tDate = dateFormat.parse(toDate);
        tDate = new Date(tDate.getTime() + TimeUnit.DAYS.toMillis(1));
        for (TriggerState state : triggerStates()) {
            Date triggerDate = new Date(state.getStartTime());
            if (Arrays.asList(this.triggerName.split(",")).contains(state.getTriggerName()) && (triggerDate.after(fDate) && triggerDate.before(tDate))) {
                XSSFRow row = sheet.createRow(rowNum++);
                createRow(state, row);
            }
        }
    }

    private void save(XSSFWorkbook workbook)
            throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(this.file)) {
            workbook.write(fileOut);
        }
    }

    private TriggerState[] triggerStates() throws Exception {
        try (ManagerSubsystem client = new ManagerSubsystem(Paths.get("etc/client.cfg").toFile())) {
            client.connect();
            return client.triggerStatesOf(this.domain.getName());
        }
    }

    private void createRow(TriggerState triggerState, XSSFRow row) {
        row.createCell(0).setCellValue(triggerState.getId());
        row.createCell(1).setCellValue(triggerState.getTriggerName());
        row.createCell(2).setCellValue(reportFormat.format(triggerState.getStartTime()));
        row.createCell(3).setCellValue(reportFormat.format(triggerState.getEndTime()));
        row.createCell(4).setCellValue(triggerState.getStatus().name());

    }

}
