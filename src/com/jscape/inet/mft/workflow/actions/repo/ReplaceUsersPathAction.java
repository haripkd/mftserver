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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ReplaceUsersPathAction extends AbstractAction {

    protected static final String DESCRIPTION = "Replace the path for the users";
    private static final Logger LOGGER = Logger.getLogger(ReplaceUsersPathAction.class.getName());
    private static final String ALL_USERS = "ALL";
    private static final String DEFAULT_PATH = "%installdir%/users/%domain%/%username%";


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
            ArrayList<String> userNameList = new ArrayList<>(Arrays.asList(userNames.split(",")));
            ArrayList<String> accountList = new ArrayList<>();
            Account[] accounts = client.accountsOf(this.event.getDomainName());
            if (null != userNameList && null != accounts) {
                if (userNameList.contains(ALL_USERS))
                    for (Account account : accounts) ReplacePathForAccount(client, account);
                else {
                    for (Account account : accounts) {
                        for (String userName : userNameList) {
                            if ((userName.equalsIgnoreCase(account.getUsername())) || (userName.equalsIgnoreCase(account.getLogin()))) {
                                ReplacePathForAccount(client, account);
                                accountList.add(userName);
                            }
                        }
                    }
                }

                // Print invalid accounts found any
                userNameList.removeAll(accountList);

                //LOGGER.info("ReplaceUsersPathAction -- > Cannot change the path for the user(s) / User(s) not found--> " + userNameList);

                if (userNameList.size() > 0 || !userNameList.isEmpty()) setResultMessage(userNameList);

            }
        }
    }

    private void setResultMessage(List<String> userNameList) {
        this.resultMessage = String.format("ReplaceUsersPathAction -- > Cannot change the path for the user(s) / User(s) not found--> %s", userNameList.toString());
    }

    private void ReplacePathForAccount(ManagerSubsystem client, Account account) throws ManagerException {
        VirtualFileDescriptor[] virtualFileDescriptors = account.getResources();
        for (VirtualFileDescriptor virtualFileDescriptor : virtualFileDescriptors) {
            if (currentRootPath.equalsIgnoreCase(((VirtualLocalFileDescriptor) virtualFileDescriptor).getRealPath().replace("%", ""))) {
                ((VirtualLocalFileDescriptor) virtualFileDescriptor).setRealPath(pathToReplace);
                client.updateAccount(this.event.getDomainName(), account);
            }
            if (pathToReplace.equalsIgnoreCase(DEFAULT_PATH.replace("%", ""))) {
                ((VirtualLocalFileDescriptor) virtualFileDescriptor).setRealPath(DEFAULT_PATH);
            }
        }
    }
}
