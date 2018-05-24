package wserver;

import java.io.IOException;
import se.dscore.SlaveProcessConfiguration;

public class WSConfiguration extends SlaveProcessConfiguration {

    public static final String DOC_ROOT = "doc-root";

    public WSConfiguration() {
        super();
    }

    public WSConfiguration(String filePath) throws IOException {
        super(filePath);
    }

    public String getDocRoot() {
        return (String) get(DOC_ROOT);
    }
    
    public void setDocRoot(String docroot) {
        set(DOC_ROOT, docroot);
    }
}
