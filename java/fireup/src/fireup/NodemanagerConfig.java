package fireup;

import java.io.IOException;
import se.dscore.SlaveProcessConfiguration;

public class NodemanagerConfig extends SlaveProcessConfiguration {

    public static final String LOGSFOLDER = "logs-dir";
    public static final String NM_HOME = "nodemanager-home";

    public NodemanagerConfig(String filePath) throws IOException {
        super(filePath);
    }

    public NodemanagerConfig() {
    }
    
    public void setLogsDir(String logsDir) {
        set(LOGSFOLDER, logsDir);
    }
    
    public String getLogsDir() {
        return (String) get(LOGSFOLDER);
    }
    
    public void setNMHome(String nmhome) {
        set(NM_HOME, nmhome);
    }
    
    public String getNMHome() {
        return (String) get(NM_HOME);
    }
}
