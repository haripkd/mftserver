package com.jscape.inet.mft.workflow.actions.repo.av;

import com.jscape.inet.mft.management.client.api.ManagerSubsystem;
import com.jscape.inet.mft.resources.VirtualRemoteFileDescriptor;
import com.jscape.inet.mft.workflow.AbstractAction;
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
public class ExportUsersAction extends AbstractAction {

    protected static final String DESCRIPTION = "Export the users from the domain";
    protected static final String[] COLUMN_HEADER = {"Login Name", "Company", "Status", "Expiry", "Email", "Phone", "Groups"
            , "Upload Quota", "Download Quota", "Transfer Quota", "Secured Connection", "Password Change"
            , "Email File Transfer", "Phone Auth", "Password Date", "Password Aging", "Owned By", "Last Login", "Domain Administration", "Virtual Paths", "Bound Keys"};
    protected static final String SHEET_NAME = "User report";
    protected static final String MEGA_BYTES = " Mb";
    protected static final String ENABLED = "Enabled";
    protected static final String DISABLED = "Disabled";
    protected static final String ALLOWED = "Allowed";
    protected static final String NOT_ALLOWED = "Not Allowed";
    protected static final String FOLLOWED = "Followed";
    protected static final String NOT_FOLLOWED = "Not Followed";
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
    protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    protected String file;
    protected String domain;

    public ExportUsersAction() {
        super("com.jscape.inet.mft.workflow.actions.repo.av.properties.ExportUsersHelp");
    }

    private static long getQuoataInMb(Account account) {
        StringBuffer sb = new StringBuffer();
        Quota uploadQuota = account.getUploadsQuota();
        long MB = uploadQuota.getBytes() / (1024 * 1024);
        return MB;
    }

    private static String getValue(Account account) {
        String value = null;
        if (account.isEnabled()) value = ENABLED;
        else value = DISABLED;
        return value;
    }

    private static String getPaths(Account account) {
        VirtualFileDescriptor[] virtualFileDescriptor1 = account.getResources();
        StringBuilder sb = new StringBuilder();
        VirtualFileDescriptor[] virtualFileDescriptor2 = virtualFileDescriptor1;
        int length = virtualFileDescriptor1.length;

        for (int i = 0; i < length; i++) {
            VirtualFileDescriptor virtualFileDescriptor = virtualFileDescriptor2[i];
            if (sb.length() != 0) {
                sb.append('|');
            }

            String path = virtualFileDescriptor.getPath();
            if (virtualFileDescriptor instanceof VirtualLocalFileDescriptor) {
                sb.append((path.isEmpty() ? "/" : path) + "@" + ((VirtualLocalFileDescriptor) virtualFileDescriptor).getRealPath() + "@" + formatAccessPermissions(virtualFileDescriptor.getAccessPermissions()));
            } else if (virtualFileDescriptor instanceof VirtualRemoteFileDescriptor) {
                sb.append((path.isEmpty() ? "/" : path) + "@" + ((VirtualRemoteFileDescriptor) virtualFileDescriptor).getName() + "@" + formatAccessPermissions(virtualFileDescriptor.getAccessPermissions()));
            }
        }

        return "\"" + sb.toString() + "\"";
    }

    private static String getPhone(Account account) {
        Phone value = account.getPhone();
        return (value.getCode() == null ? "" : value.getCode() + "-") + value.getNumber() + (value.getExtension() == null ? "" : " ext " + value.getExtension());
    }

    private static String formatAccessPermissions(AccessPermissions accessPermissions) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((char) (accessPermissions.isFileDownloadingAllowed() ? 'R' : '-'));
        stringBuilder.append((char) (accessPermissions.isFileUploadingAllowed() ? 'W' : '-'));
        stringBuilder.append((char) (accessPermissions.isFileAppendingAllowed() ? 'A' : '-'));
        stringBuilder.append((char) (accessPermissions.isFileDeletionAllowed() ? 'D' : '-'));
        stringBuilder.append((char) (accessPermissions.isFileRenamingAllowed() ? 'R' : '-'));
        stringBuilder.append((char) (accessPermissions.isFileListingAllowed() ? 'L' : '-'));
        stringBuilder.append((char) (accessPermissions.isDirectoryMakingAllowed() ? 'C' : '-'));
        stringBuilder.append((char) (accessPermissions.isDirectoryDeletionAllowed() ? 'D' : '-'));
        stringBuilder.append((char) (accessPermissions.isDirectoriesListingAllowed() ? 'L' : '-'));
        stringBuilder.append((char) (accessPermissions.isSubdirectoriesBrowsingAllowed() ? 'B' : '-'));
        return stringBuilder.toString();
    }

    private static String getKeys(Account account) {
        String[] acctKeys = account.getBindedKeys();
        StringBuilder sb = new StringBuilder();
        String[] acctKeys1 = acctKeys;
        int length = acctKeys.length;

        for (int i = 0; i < length; i++) {
            String value = acctKeys1[i];
            if (sb.length() != 0) {
                sb.append('|');
            }

            sb.append(value);
        }

        return "\"" + sb.toString() + "\"";
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

        Account[] accounts = managerSubsystem.accountsOf(this.domain);

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = xssfWorkbook.createSheet(SHEET_NAME);

        XSSFRow headerRow = sheet.createRow(0);

        // Create header cells

        for (int i = 0; i < COLUMN_HEADER.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(COLUMN_HEADER[i]);
        }

        // Create Other rows and cells with user data

        int rowNum = 1;
        for (Account account : accounts) {
            XSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(null != account.getLogin() ? account.getLogin() : "");
            row.createCell(1).setCellValue(null != account.getCompany() ? account.getCompany().getName() : "");
            row.createCell(2).setCellValue(getValue(account));
            row.createCell(3).setCellValue(null != account.getExpirationDate() ? simpleDateFormat.format(account.getExpirationDate()) : "never");
            row.createCell(4).setCellValue(null != account.getEmailAddress() ? account.getEmailAddress() : "");
            row.createCell(5).setCellValue(null != account.getPhone() ? getPhone(account) : "");
            row.createCell(6).setCellValue(account.getGroups().isEmpty() ? "" : account.getGroups().toString());
            row.createCell(7).setCellValue(null != account.getUploadsQuota() ? getQuoataInMb(account) + MEGA_BYTES : "");
            row.createCell(8).setCellValue(null != account.getDownloadsQuota() ? getQuoataInMb(account) + MEGA_BYTES : "");
            row.createCell(9).setCellValue(null != account.getTransfersQuota() ? getQuoataInMb(account) + MEGA_BYTES : "");
            row.createCell(10).setCellValue(account.isSecured() ? ENABLED : DISABLED);
            row.createCell(11).setCellValue(account.isPasswordChangingAllowed() ? ENABLED : DISABLED);
            row.createCell(12).setCellValue(account.isEmailFileTransferAllowed() ? ALLOWED : NOT_ALLOWED);
            row.createCell(13).setCellValue(account.isUsePhoneAuthentication() ? ENABLED : DISABLED);
            row.createCell(14).setCellValue(simpleDateFormat.format(account.getPasswordDate()));
            row.createCell(15).setCellValue(account.isIgnorePasswordAgingRules() ? NOT_FOLLOWED : FOLLOWED);
            row.createCell(16).setCellValue(null != account.getOwner() ? account.getOwner() : "");
            row.createCell(17).setCellValue(null != account.getLastLoginDate() ? simpleDateFormat.format(account.getLastLoginDate()) : "");
            row.createCell(18).setCellValue(null != account.getAdministration() ? account.getAdministration().toString() : "");
            row.createCell(19).setCellValue(null != getPaths(account) ? getPaths(account) : "");
            row.createCell(20).setCellValue(null != getKeys(account) ? getKeys(account) : "");
        }
        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream(this.file);
        xssfWorkbook.write(fileOut);
        fileOut.close();
    }
}
