
import java.io.IOException;
import se.dscore.MasterProcessConfiguration;

public class DMasterConfiguration extends MasterProcessConfiguration {

    public static final String 
            DMGR_CMDLINE = "dmgr-cmdline",
            DMGR_EXEC = "dmgr-executable";

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
}
