package com.jscape.inet.mft.workflow.actions.repo;

import com.jscape.inet.mft.TriggerState;
import com.jscape.inet.mft.management.client.api.ManagerSubsystem;
import com.jscape.inet.mft.workflow.AbstractAction;
import com.jscape.util.Assert;
import com.jscape.util.reflection.PropertyDescriptor;
import com.jscape.util.reflection.types.FileField;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

public class FailedTriggerReportAction extends AbstractAction {

    protected static final String DESCRIPTION = "Export failed triggers";
    protected static final String[] COLUMN_HEADER = {"Trigger ID", "Trigger Name", "Start Date & Time", "End Date & Time", "Status"};
    protected static final String SHEET_NAME = "Failed Triggers";
    protected static final String FAILED_STATUS = "FAILED";
    protected static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected String file;

    protected static final PropertyDescriptor[] DESCRIPTORS = {
            new PropertyDescriptor("File", new FileField(), true, false),
            ACTION_PRIORITY_DESCRIPTOR,
            TRIGGER_ERROR_MESSAGE_DESCRIPTOR,
            LOG_ACTION_DESCRIPTOR,
    };

    public FailedTriggerReportAction() {
        super("com.jscape.inet.mft.workflow.actions.repo.properties.FailedTriggerReportHelp");
    }


    public void setFile(String value) {
        Assert.isValidString(value);
        this.file = value;
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
            XSSFSheet sheet = workbook.createSheet(SHEET_NAME);
            fillHeaderOf(sheet);
            fillDataOf(sheet);
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

    private void fillDataOf(XSSFSheet sheet)
            throws Exception {
        int rowNum = 1;
        for (TriggerState state : triggerStates()) {
            if (state.getStatus().name().equalsIgnoreCase(FAILED_STATUS)) {
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
        row.createCell(2).setCellValue(simpleDateFormat.format(triggerState.getStartTime()));
        row.createCell(3).setCellValue(simpleDateFormat.format(triggerState.getEndTime()));
        row.createCell(4).setCellValue(triggerState.getStatus().name());

    }


}
