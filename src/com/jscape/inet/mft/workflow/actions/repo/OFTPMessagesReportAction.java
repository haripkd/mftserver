package com.jscape.inet.mft.workflow.actions.repo;

import com.jscape.inet.mft.management.client.api.ManagerSubsystem;
import com.jscape.inet.mft.subsystems.OftpMessageInfo;
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
import java.text.SimpleDateFormat;
import java.util.List;

public class OFTPMessagesReportAction extends AbstractAction {

    protected static final String DESCRIPTION = "Export the OFTP Messages for the Domain";
    protected static final String[] COLUMN_HEADER = {"Message ID", "Processed Date", "Message Type", "Direction", "Originator", "Destination", "Filename", "User", "Trading Partner", "Status"};
    protected static final String SHEET_NAME = "OFTP Messages";

    protected static final String USER_DIRECTORY = "user.dir";
    protected static final String FILE_SEPARATOR = "file.separator";
    protected static final String ETC_FOLDER = "etc";
    protected static final String CLIENT_CONFG = "client.cfg";


    protected static final PropertyDescriptor[] DESCRIPTORS = {
            new PropertyDescriptor("File", new FileField(), true, false),
            new PropertyDescriptor("Domain", new StringField(), true, false),
            ACTION_PRIORITY_DESCRIPTOR,
            TRIGGER_ERROR_MESSAGE_DESCRIPTOR,
            LOG_ACTION_DESCRIPTOR,
    };

    protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected String file;
    protected String domain;

    public OFTPMessagesReportAction() {
        super("com.jscape.inet.mft.workflow.actions.repo.properties.OFTPMessagesReportHelp");
    }

    public void setFile(String value) {
        Assert.isValidString(value);
        this.file = value;
    }

    public void setDomain(String value) {
        Assert.isValidString(value);
        this.domain = value;
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

        String clientConfigPath = System.getProperty(USER_DIRECTORY)
                + System.getProperty(FILE_SEPARATOR)
                + ETC_FOLDER
                + System.getProperty(FILE_SEPARATOR) + CLIENT_CONFG;


        ManagerSubsystem managerSubsystem = new ManagerSubsystem(clientConfigPath);
        managerSubsystem.connect();


        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = xssfWorkbook.createSheet(SHEET_NAME);

        XSSFRow headerRow = sheet.createRow(0);

        // Create header cells

        for (int i = 0; i < COLUMN_HEADER.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(COLUMN_HEADER[i]);
        }

        // Create Other rows and with data

        int rowNum = 1;
        List<OftpMessageInfo> oftpMessageInfoList = managerSubsystem.oftpMessageInfosOf(this.domain);
        if (null != oftpMessageInfoList) {
            for (OftpMessageInfo oftpMessageInfo : oftpMessageInfoList) {
                XSSFRow row = sheet.createRow(rowNum++);
                createRow(oftpMessageInfo, row);
            }
        }

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream(this.file);
        xssfWorkbook.write(fileOut);
        fileOut.close();
        xssfWorkbook.close();
        managerSubsystem.disconnect();
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
