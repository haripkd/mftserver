package com.jscape.inet.mft.workflow.actions.repo;

import com.jscape.inet.mft.management.client.api.ManagerSubsystem;
import com.jscape.inet.mft.subsystems.as2.AS2MessageInfo;
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
import java.util.List;

/**
 * Created by hari on 8/14/2018.
 */
public class AS2MessagesReportAction extends AbstractAction {

    protected static final String DESCRIPTION = "Export the AS2 Messages for the Domain";
    protected static final String[] COLUMN_HEADER = {"Message ID", "Processed Date", "Message Type", "Direction", "AS2 From", "AS2 To", "Filename", "MDN", "User", "Trading Partner", "Status"};
    protected static final String SHEET_NAME = "AS2 Messages";
    protected static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected static final PropertyDescriptor[] DESCRIPTORS = {
            new PropertyDescriptor("File", new FileField(), true, false),
            ACTION_PRIORITY_DESCRIPTOR,
            TRIGGER_ERROR_MESSAGE_DESCRIPTOR,
            LOG_ACTION_DESCRIPTOR,
    };

    protected String file;


    public AS2MessagesReportAction() {
        super("com.jscape.inet.mft.workflow.actions.repo.properties.AS2MessagesReportHelp");

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
    protected void execute()
            throws Exception {
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
        for (AS2MessageInfo messageInfo : infos()) {
            XSSFRow row = sheet.createRow(rowNum++);
            createRow(messageInfo, row);
        }
    }

    private void save(XSSFWorkbook workbook)
            throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(this.file)) {
            workbook.write(fileOut);
        }
    }

    private List<AS2MessageInfo> infos()
            throws Exception {
        try (ManagerSubsystem client = new ManagerSubsystem(Paths.get("etc/client.cfg").toFile())) {
            client.connect();
            return client.as2MessageInfosOf(this.domain.getName());
        }
    }

    private void createRow(AS2MessageInfo as2MessageInfo, XSSFRow row) {
        row.createCell(0).setCellValue(as2MessageInfo.getMessageId());
        row.createCell(1).setCellValue(simpleDateFormat.format(as2MessageInfo.getDate()));
        row.createCell(2).setCellValue(as2MessageInfo.getType().name());
        row.createCell(3).setCellValue(as2MessageInfo.getDirection().name());
        row.createCell(4).setCellValue(as2MessageInfo.getSender());
        row.createCell(5).setCellValue(as2MessageInfo.getRecipient());
        row.createCell(6).setCellValue(as2MessageInfo.getFilename());
        row.createCell(7).setCellValue(as2MessageInfo.getReceiptType().toString());
        row.createCell(8).setCellValue(as2MessageInfo.getUsername());
        row.createCell(9).setCellValue(as2MessageInfo.getTradingPartner());
        row.createCell(10).setCellValue(as2MessageInfo.getStatus().toString());
    }


}
