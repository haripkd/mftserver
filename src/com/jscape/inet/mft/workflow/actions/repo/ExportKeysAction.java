package com.jscape.inet.mft.workflow.actions.repo;

import com.jscape.inet.mft.management.client.api.ManagerSubsystem;
import com.jscape.inet.mft.workflow.AbstractAction;
import com.jscape.inet.mftserver.operation.key.CertificateSummary;
import com.jscape.inet.mftserver.operation.key.PgpKeySummary;
import com.jscape.inet.mftserver.operation.key.PublicKeySummary;
import com.jscape.inet.mftserver.operation.key.ServerKeySummary;
import com.jscape.util.Assert;
import com.jscape.util.reflection.PropertyDescriptor;
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
    protected static final String[] PGP_COLUMN_HEADER = {"Alias", "Key Algorithm", "Can Encrypt", "Can Decrypt", "Can Sign", "Can Verify", "Fingerprint"};
    protected static final String[] PUBLIC_KEY_COLUMN_HEADER = {"Alias", "Algorithm", "Size(bits)", "Fingerprint"};


    protected static final String SHEET_NAME = "Key Report";

    protected static final String USER_DIRECTORY = "user.dir";
    protected static final String FILE_SEPARATOR = "file.separator";
    protected static final String ETC_FOLDER = "etc";
    protected static final String CLIENT_CONFG = "client.cfg";
    protected static final String SERVER_KEYS_HEADER = "SERVER KEYS";
    protected static final String HOST_CERTIFICATES_HEADER = "HOST CERTIFICATES";
    protected static final String HOST_PUB_KEYS_HEADER = "HOST PUBLIC KEYS";
    protected static final String CLIENT_CERTIFICATES_HEADER = "CLIENT CERTIFICATES";
    protected static final String CLIENT_PUB_KEYS_HEADER = "CLIENT PUBLIC KEYS";
    protected static final String PGP_KEYS_HEADER = "PGP PUBLIC KEYS";


    protected static final PropertyDescriptor[] DESCRIPTORS = {
            new PropertyDescriptor("File", new FileField(), true, false),
            ACTION_PRIORITY_DESCRIPTOR,
            TRIGGER_ERROR_MESSAGE_DESCRIPTOR,
            LOG_ACTION_DESCRIPTOR,
    };

    protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    protected String file;

    public ExportKeysAction() {
        super("com.jscape.inet.mft.workflow.actions.repo.properties.ExportKeysHelp");
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


        // Create Other rows and with data

        int rowNum = 0;
        ServerKeySummary[] serverKeys = managerSubsystem.serverKeySummaries();
        if (null != serverKeys && serverKeys.length > 0) {
            rowNum = CreateHeaderAndRows(sheet, rowNum, SERVER_KEYS_HEADER, COLUMN_HEADER);

            for (ServerKeySummary serverKey : serverKeys) {
                XSSFRow row = sheet.createRow(rowNum++);
                createRow(row, serverKey.getName(), serverKey.getKeyAlgorithm(), serverKey.getKeySize(),
                        serverKey.getCertificateSerialNumber(), serverKey.getCertificateIssuer(), serverKey.getCertificateSubject(),
                        simpleDateFormat.format(serverKey.getCertificateBeginDate()),
                        simpleDateFormat.format(serverKey.getCertificateEndDate()));
            }
        }


        // Client Certificates

        CertificateSummary[] clientCertificateSummaries = managerSubsystem.clientCertificateSummaries();


        if (null != clientCertificateSummaries && clientCertificateSummaries.length > 0) {

            rowNum = CreateHeaderAndRows(sheet, rowNum, CLIENT_CERTIFICATES_HEADER, COLUMN_HEADER);

            for (CertificateSummary certificate : clientCertificateSummaries) {
                rowNum = CreateCertificateRow(sheet, rowNum, certificate);
            }


        }

        // Client Public Keys

        PublicKeySummary[] clientPublicKeySummaries = managerSubsystem.clientPublicKeySummaries();
        if (null != clientPublicKeySummaries && clientPublicKeySummaries.length > 0) {
            rowNum = CreateHeaderAndRows(sheet, rowNum, CLIENT_PUB_KEYS_HEADER, PUBLIC_KEY_COLUMN_HEADER);

            for (PublicKeySummary publicKeySummary : clientPublicKeySummaries) {
                rowNum = CreatePublicKeyRow(sheet, rowNum, publicKeySummary);
            }
        }

        // Host Certificates

        CertificateSummary[] hostCertificateSummaries = managerSubsystem.hostCertificateSummaries();
        if (null != hostCertificateSummaries && hostCertificateSummaries.length > 0) {
            rowNum = CreateHeaderAndRows(sheet, rowNum, HOST_CERTIFICATES_HEADER, COLUMN_HEADER);

            for (CertificateSummary certificate : hostCertificateSummaries) {
                rowNum = CreateCertificateRow(sheet, rowNum, certificate);
            }
        }

        // Host Public keys

        PublicKeySummary[] hostPublicKeySummaries = managerSubsystem.hostPublicKeySummaries();
        if (null != hostPublicKeySummaries && hostPublicKeySummaries.length > 0) {
            rowNum = CreateHeaderAndRows(sheet, rowNum, HOST_PUB_KEYS_HEADER, PUBLIC_KEY_COLUMN_HEADER);

            for (PublicKeySummary publicKeySummary : hostPublicKeySummaries) {
                rowNum = CreatePublicKeyRow(sheet, rowNum, publicKeySummary);
            }
        }


        // PGP Public Keys

        PgpKeySummary[] pgpSecretKeySummaries = managerSubsystem.pgpSecretKeySummaries();

        if (null != pgpSecretKeySummaries && pgpSecretKeySummaries.length > 0) {
            rowNum = CreateHeaderAndRows(sheet, rowNum, PGP_KEYS_HEADER, PGP_COLUMN_HEADER);

            for (PgpKeySummary pgpKeySummary : pgpSecretKeySummaries) {
                XSSFRow row = sheet.createRow(rowNum++);
                createPGPRow(pgpKeySummary, row);
            }
        }

        // Write the output to a file

        FileOutputStream fileOut = new FileOutputStream(this.file);
        xssfWorkbook.write(fileOut);
        fileOut.close();
    }

    private int CreateCertificateRow(XSSFSheet sheet, int rowNum, CertificateSummary certificate) {
        XSSFRow row = sheet.createRow(rowNum++);
        createRow(row, certificate.getName(), certificate.getKeyAlgorithm(), certificate.getKeySize(),
                certificate.getCertificateSerialNumber(), certificate.getCertificateIssuer(), certificate.getCertificateSubject(),
                simpleDateFormat.format(certificate.getCertificateBeginDate()),
                simpleDateFormat.format(certificate.getCertificateEndDate()));
        return rowNum;
    }

    private int CreatePublicKeyRow(XSSFSheet sheet, int rowNum, PublicKeySummary publicKeySummary) {
        XSSFRow row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(publicKeySummary.getName());
        row.createCell(1).setCellValue(publicKeySummary.getKeyAlgorithm());
        row.createCell(2).setCellValue(publicKeySummary.getKeySize());
        row.createCell(3).setCellValue(publicKeySummary.getFingerprint());
        return rowNum;
    }

    private int CreateHeaderAndRows(XSSFSheet sheet, int rowNum, String serverKeysHeader, String[] columnHeader) {
        XSSFRow titleRow = sheet.createRow(rowNum++);
        titleRow.createCell(0).setCellValue(serverKeysHeader);

        XSSFRow headerRow = sheet.createRow(rowNum++);

        // Create header cells

        for (int i = 0; i < columnHeader.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeader[i]);
        }
        return rowNum;
    }

    private void createPGPRow(PgpKeySummary pgpKeySummary, XSSFRow row) {
        row.createCell(0).setCellValue(pgpKeySummary.getName());
        row.createCell(1).setCellValue(pgpKeySummary.getKeyAlgorithm());
        row.createCell(2).setCellValue(pgpKeySummary.isEncryptionAllowed());
        row.createCell(3).setCellValue(pgpKeySummary.isDecryptionAllowed());
        row.createCell(4).setCellValue(pgpKeySummary.isSigningAllowed());
        row.createCell(5).setCellValue(pgpKeySummary.isSignatureVerificationAllowed());
        row.createCell(6).setCellValue(pgpKeySummary.getFingerprint());
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
