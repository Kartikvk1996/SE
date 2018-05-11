
import java.io.IOException;
import se.dscore.MasterProcessConfiguration;

public class DMasterConfiguration extends MasterProcessConfiguration {

    public static final String 
            DMGR_CMDLINE = "dmgr-cmdline",
            DMGR_EXEC = "dmgr-executable",
            TCRAWLER_EXEC = "tcrawler-executable",
            TCRAWLER_CMDLINE = "tcrawler-cmdline",
            WS_EXEC = "ws-executable",
            WS_CMDLINE = "ws-cmdline",
            PRXY_EXEC = "prxy-executable",
            PRXY_CMDLINE = "prxy-cmdline";

    public DMasterConfiguration() throws IOException {
        super();
    }
    
    DMasterConfiguration(String filePath) throws IOException {
        super(filePath);
    }
    
    String getDmgrCommandLine() {
        return (String) get(DMGR_CMDLINE);
    }

    void setDmgrCommandLine(String cmdline) {
        set(DMGR_CMDLINE, cmdline);
    }
    
    String getDmgrExecutable() {
        return (String) get(DMGR_EXEC);
    }
    
    void setDmgrExecutable(String cmd) {
        set(DMGR_EXEC, cmd);
    }
    
    String getTcrawlerExecutable() {
        return (String) get(TCRAWLER_EXEC);
    }
    
    String getTcrawlerCmdline() {
        return (String) get(TCRAWLER_CMDLINE);
    }
    
    void setTcrawlerExecutable(String cmd) {
        set(TCRAWLER_EXEC, cmd);
    }
    
    void setTcrawlerCmdline(String cmdline) {
        set(TCRAWLER_CMDLINE, cmdline);
    }
    
    String getWsExecutable() {
        return (String) get(WS_EXEC);
    }
    
    String getWsCmdline() {
        return (String) get(WS_CMDLINE);
    }
    
    void setWsExecutable(String cmd) {
        set(WS_EXEC, cmd);
    }
    
    void setWsCmdline(String cmdline) {
        set(WS_CMDLINE, cmdline);
    }
    
    
    String getPrxyExecutable() {
        return (String) get(PRXY_EXEC);
    }
    
    String getPrxyCmdline() {
        return (String) get(PRXY_CMDLINE);
    }
    
    void setPrxyExecutable(String cmd) {
        set(PRXY_EXEC, cmd);
    }
    
    void setPrxyCmdline(String cmdline) {
        set(PRXY_CMDLINE, cmdline);
    }
}
