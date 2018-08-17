package com.jscape.inet.mft.workflow.actions.repo.av;

import com.jscape.inet.mft.management.client.api.ManagerSubsystem;
import com.jscape.inet.mft.resources.VirtualRemoteFileDescriptor;
import com.jscape.inet.mft.workflow.AbstractAction;
import com.jscape.inet.mftserver.operation.key.CertificateSummary;
import com.jscape.inet.mftserver.operation.key.ServerKeySummary;
import com.jscape.inet.vfs.*;
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

/**
 * Created by hari on 8/14/2018.
 */
public class ExportKeysAction extends AbstractAction {

    protected static final String DESCRIPTION = "Export the report for the keys";
    protected static final String[] COLUMN_HEADER = {"Alias", "Algorithm", "Size (bits)", "Serial #", "Issuer", "Subject", "Valid not before", "Valid not after"};
    protected static final String SHEET_NAME = "Key Report";

    protected static final String USER_DIRECTORY = "user.dir";
    protected static final String FILE_SEPARATOR = "file.separator";
    protected static final String ETC_FOLDER = "etc";
    protected static final String CLIENT_CONFG = "client.cfg";
    protected static final String SERVER_KEYS_HEADER = "SERVER KEYS";
    protected static final String HOST_CERTIFICATES_HEADER = "HOST CERTIFICATES";


    protected static final PropertyDescriptor[] DESCRIPTORS = {
            new PropertyDescriptor("File", new FileField(), true, false),
            ACTION_PRIORITY_DESCRIPTOR,
            TRIGGER_ERROR_MESSAGE_DESCRIPTOR,
            LOG_ACTION_DESCRIPTOR,
    };

    protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    protected String file;

    public ExportKeysAction() {
        super("com.jscape.inet.mft.workflow.actions.repo.av.properties.ExportKeysHelp");
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
        ServerKeySummary[] serverKeys = managerSubsystem.serverKeySummaries();
        if (null != serverKeys) {
            XSSFRow titleRow = sheet.createRow(rowNum++);
            titleRow.createCell(0).setCellValue(SERVER_KEYS_HEADER);
            for (ServerKeySummary serverKey : serverKeys) {
                XSSFRow row = sheet.createRow(rowNum++);
                createRow(row, serverKey.getName(), serverKey.getKeyAlgorithm(), serverKey.getKeySize(),
                        serverKey.getCertificateSerialNumber(), serverKey.getCertificateIssuer(), serverKey.getCertificateSubject(),
                        simpleDateFormat.format(serverKey.getCertificateBeginDate()),
                        simpleDateFormat.format(serverKey.getCertificateEndDate()));
            }
        }


        CertificateSummary[] certificates = managerSubsystem.clientCertificateSummaries();
        if (null != certificates) {
            XSSFRow titleRow = sheet.createRow(rowNum++);
            titleRow.createCell(0).setCellValue(HOST_CERTIFICATES_HEADER);
            for (CertificateSummary certificate : certificates) {
                XSSFRow row = sheet.createRow(rowNum++);
                createRow(row, certificate.getName(), certificate.getKeyAlgorithm(), certificate.getKeySize(),
                        certificate.getCertificateSerialNumber(), certificate.getCertificateIssuer(), certificate.getCertificateSubject(),
                        simpleDateFormat.format(certificate.getCertificateBeginDate()),
                        simpleDateFormat.format(certificate.getCertificateEndDate()));
            }
        }


        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream(this.file);
        xssfWorkbook.write(fileOut);
        fileOut.close();
    }

    private void createRow(XSSFRow row, String name, String keyAlgorithm, int keySize, String certificateSerialNumber, String certificateIssuer, String certificateSubject, String beginDate, String endDate) {
        row.createCell(0).setCellValue(name);
        row.createCell(1).setCellValue(keyAlgorithm);
        row.createCell(2).setCellValue(keySize);
        row.createCell(3).setCellValue(certificateSerialNumber);
        row.createCell(4).setCellValue(certificateIssuer);
        row.createCell(5).setCellValue(certificateSubject);
        row.createCell(6).setCellValue(beginDate);
        row.createCell(7).setCellValue(endDate);
    }
}
