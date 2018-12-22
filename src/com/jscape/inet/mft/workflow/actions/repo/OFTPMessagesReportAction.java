package com.jscape.inet.mft.workflow.actions.repo;

import com.jscape.inet.mft.management.client.api.ManagerSubsystem;
import com.jscape.inet.mft.subsystems.OftpMessageInfo;
import com.jscape.inet.mft.subsystems.as2.AS2MessageInfo;
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
import java.util.List;

public class OFTPMessagesReportAction extends AbstractAction {

    protected static final String DESCRIPTION = "Export the OFTP Messages for the Domain";
    protected static final String[] COLUMN_HEADER = {"Message ID", "Processed Date", "Message Type", "Direction", "Originator", "Destination", "Filename", "User", "Trading Partner", "Status"};
    protected static final String SHEET_NAME = "OFTP Messages";
    protected static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected static final PropertyDescriptor[] DESCRIPTORS = {
            new PropertyDescriptor("File", new FileField(), true, false),
            ACTION_PRIORITY_DESCRIPTOR,
            TRIGGER_ERROR_MESSAGE_DESCRIPTOR,
            LOG_ACTION_DESCRIPTOR,
    };


    protected String file;



    public OFTPMessagesReportAction() {
        super("com.jscape.inet.mft.workflow.actions.repo.properties.OFTPMessagesReportHelp");
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
        for (OftpMessageInfo messageInfo : infos()) {
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

    private List<OftpMessageInfo> infos()
            throws Exception {
        try (ManagerSubsystem client = new ManagerSubsystem(Paths.get("etc/client.cfg").toFile())) {
            client.connect();
            return client.oftpMessageInfosOf(this.domain.getName());
        }
    }

    private void createRow(OftpMessageInfo oftpMessageInfo, XSSFRow row) {
        row.createCell(0).setCellValue(oftpMessageInfo.getId());
        row.createCell(1).setCellValue(simpleDateFormat.format(oftpMessageInfo.getDate()));
        row.createCell(2).setCellValue(oftpMessageInfo.getType().name());
        row.createCell(3).setCellValue(oftpMessageInfo.getDirection().name());
        row.createCell(4).setCellValue(oftpMessageInfo.getOriginator());
        row.createCell(5).setCellValue(oftpMessageInfo.getDestination());
        row.createCell(6).setCellValue(oftpMessageInfo.getFilename());
        row.createCell(7).setCellValue(oftpMessageInfo.getUsername());
        row.createCell(8).setCellValue(oftpMessageInfo.getTradingPartner());
        row.createCell(9).setCellValue(oftpMessageInfo.getStatus().toString());
    }

}
