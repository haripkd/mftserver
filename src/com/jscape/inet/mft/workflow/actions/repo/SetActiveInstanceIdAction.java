package com.jscape.inet.mft.workflow.actions.repo;

import com.jscape.inet.mft.TriggerServiceConfiguration;
import com.jscape.inet.mft.management.client.api.ManagerException;
import com.jscape.inet.mft.management.client.api.ManagerSubsystem;
import com.jscape.inet.mft.workflow.AbstractAction;
import com.jscape.util.Assert;
import com.jscape.util.reflection.PropertyDescriptor;
import com.jscape.util.reflection.StringField;
import com.jscape.util.reflection.types.IntegerField;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Paths;

public class SetActiveInstanceIdAction extends AbstractAction {

    protected static final String DESCRIPTION = "Set the active instance id to the global variable";

    protected static final PropertyDescriptor[] DESCRIPTORS = {
            new PropertyDescriptor("Host1AppInstanceId", new StringField(), true, false),
            new PropertyDescriptor("Host1Ip", new StringField(), true, false),
            new PropertyDescriptor("Host1Port", new IntegerField(), true, false),
            new PropertyDescriptor("Host2AppInstanceId", new StringField(), true, false),
            new PropertyDescriptor("Host2Ip", new StringField(), true, false),
            new PropertyDescriptor("Host2Port", new IntegerField(), true, false),
            ACTION_PRIORITY_DESCRIPTOR,
            TRIGGER_ERROR_MESSAGE_DESCRIPTOR,
            LOG_ACTION_DESCRIPTOR,
    };

    protected String host1AppInstanceId;
    protected String host1Ip;
    protected int host1Port;
    protected String host2AppInstanceId;
    protected String host2Ip;
    protected int host2Port;
    protected boolean isHost1Alive;
    protected boolean isHost2Alive;

    protected final String ACTIVE_INSTANCE_ID = "activeInstanceId";
    protected final String HOST_DOWN_MESSAGE = "Unable to connect any host";

    public SetActiveInstanceIdAction() {
        super("com.jscape.inet.mft.workflow.actions.repo.properties.SetActiveInstanceIdHelp");
    }

    public void setHost1AppInstanceId(String host1AppInstanceId) {
        Assert.isValidString(host1AppInstanceId);
        this.host1AppInstanceId = host1AppInstanceId;
    }

    public void setHost1Ip(String host1Ip) {
        Assert.isValidString(host1Ip);
        this.host1Ip = host1Ip;
    }

    public void setHost1Port(int host1Port) {
        Assert.isValidPort(host1Port);
        this.host1Port = host1Port;
    }

    public void setHost2AppInstanceId(String host2AppInstanceId) {
        Assert.isValidString(host2AppInstanceId);
        this.host2AppInstanceId = host2AppInstanceId;
    }

    public void setHost2Ip(String host2Ip) {
        Assert.isValidString(host2Ip);
        this.host2Ip = host2Ip;
    }

    public void setHost2Port(int host2Port) {
        Assert.isValidPort(host2Port);
        this.host2Port = host2Port;
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
        isHost1Alive = getHostStatus(host1Ip, host1Port);
        isHost2Alive = getHostStatus(host2Ip, host2Port);
        if (isHost1Alive && !isHost2Alive) {
            setGlobalVariableForActiveInstanceId(host1AppInstanceId);
        }
        if (isHost2Alive && !isHost1Alive) {
            setGlobalVariableForActiveInstanceId(host2AppInstanceId);
        }

        if (!isHost2Alive && !isHost1Alive) {
            throw new UnableToConnectHostException(HOST_DOWN_MESSAGE);
        }

    }

    private void setGlobalVariableForActiveInstanceId(String host1applicationInstanceId) throws ManagerException {
        try (ManagerSubsystem client = new ManagerSubsystem(Paths.get("etc/client.cfg").toFile())) {
            client.connect();
            TriggerServiceConfiguration serviceConfiguration = client.triggerServiceConfiguration(this.event.getDomainName());
            serviceConfiguration.getGlobalVariables().put(ACTIVE_INSTANCE_ID, host1applicationInstanceId);
            client.updateTriggerServiceConfiguration(this.event.getDomainName(), serviceConfiguration);
            client.disconnect();
        }
    }


    private boolean getHostStatus(String ip, int port) {
        try {
            SocketAddress socketAddressHost = new InetSocketAddress(ip, port);
            Socket serverHostSocket = new Socket();
            serverHostSocket.connect(socketAddressHost);
            if (serverHostSocket.isConnected()) {
                serverHostSocket.close();
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    class UnableToConnectHostException extends Exception {
        public UnableToConnectHostException(String message) {
            super(message);
        }
    }
}
