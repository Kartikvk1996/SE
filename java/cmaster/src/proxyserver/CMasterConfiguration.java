package proxyserver;

import java.io.IOException;
import se.dscore.SlaveProcessConfiguration;

public class CMasterConfiguration extends SlaveProcessConfiguration {

    public static final String MAP_FILE = "map-file";

    public CMasterConfiguration() {
        super();
    }

    public CMasterConfiguration(String filePath) throws IOException {
        super(filePath);
    }

    public String getMapFile() {
        return (String) get(MAP_FILE);
    }

    public void setMapFile(String mapFile) {
        set(MAP_FILE, mapFile);
    }
}
