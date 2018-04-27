package se.dscore;

import java.io.IOException;

public class SlaveProcessConfiguration extends Configuration {

    public static final String
            TICKET = "ticket",
            PID = "pid",
            MASTER_HOST = "masterHost",
            MASTER_PORT = "masterPort";
    public SlaveProcessConfiguration() {
        super();
    }
    
    public SlaveProcessConfiguration(String filePath) throws IOException {
        super(filePath);
    }
    
    public int getMasterPort() {
        return (int) Integer.parseInt((String) get(MASTER_PORT));
    }

    public void setMasterPort(int port) {
        set(MASTER_PORT, port);
    }

    public String getMasterHost() {
        return (String) get(MASTER_HOST);
    }

    public void setMasterHost(String host) {
        set(MASTER_HOST, host);
    }
}
