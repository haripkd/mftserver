package com.jscape.inet.mft.workflow.actions.repo;

import com.jscape.inet.mft.management.client.api.ManagerException;
import com.jscape.inet.mft.management.client.api.ManagerSubsystem;
import com.jscape.inet.mft.workflow.AbstractAction;
import com.jscape.inet.vfs.Account;
import com.jscape.inet.vfs.VirtualFileDescriptor;
import com.jscape.inet.vfs.VirtualLocalFileDescriptor;
import com.jscape.util.Assert;
import com.jscape.util.reflection.PropertyDescriptor;
import com.jscape.util.reflection.StringField;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ReplaceUsersPathAction extends AbstractAction {

    protected static final String DESCRIPTION = "Replace the path for the users";
    protected static final String ALL_USERS = "ALL";

    protected static final PropertyDescriptor[] DESCRIPTORS = {
            new PropertyDescriptor("UserNames", new StringField(), true, false),
            new PropertyDescriptor("CurrentRootPath", new StringField(), true, false),
            new PropertyDescriptor("PathToReplace", new StringField(), true, false),
            ACTION_PRIORITY_DESCRIPTOR,
            TRIGGER_ERROR_MESSAGE_DESCRIPTOR,
            LOG_ACTION_DESCRIPTOR,

    };

    protected String userNames;
    protected String currentRootPath;
    protected String pathToReplace;

    public ReplaceUsersPathAction() {
        super("com.jscape.inet.mft.workflow.actions.repo.properties.ReplaceUsersPathHelp");
    }

    public void setUserNames(String value) {
        Assert.isValidString(value);
        this.userNames = value;
    }

    public void setCurrentRootPath(String value) {
        Assert.isValidString(value);
        this.currentRootPath = value;
    }

    public void setPathToReplace(String value) {
        Assert.isValidString(value);
        this.pathToReplace = value;
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
        try (ManagerSubsystem client = new ManagerSubsystem(Paths.get("etc/client.cfg").toFile())) {
            client.connect();
            List<String> userNameList = Arrays.asList(userNames.split(","));
            Account[] accounts = client.accountsOf(this.domain.getName());
            if (null != userNameList && null != accounts) {
                for (Account account : accounts) {
                    if (userNameList.contains(account.getUsername())) ReplacePathForAccount(client, account);
                    if (userNameList.contains(ALL_USERS)) ReplacePathForAccount(client, account);
                }
            }
        }
    }

    private void ReplacePathForAccount(ManagerSubsystem client, Account account) throws ManagerException {
        VirtualFileDescriptor[] virtualFileDescriptors = account.getResources();
        for (VirtualFileDescriptor virtualFileDescriptor : virtualFileDescriptors) {
            if (currentRootPath.equalsIgnoreCase(((VirtualLocalFileDescriptor) virtualFileDescriptor).getRealPath().replace("%", ""))) {
                ((VirtualLocalFileDescriptor) virtualFileDescriptor).setRealPath(pathToReplace);
                client.updateAccount(this.domain.getName(), account);
            }
        }
    }
}
