package proxyserver;

import java.io.IOException;
import se.dscore.SlaveProcessConfiguration;

public class ProxyConfiguration extends SlaveProcessConfiguration {

    public static final String MAP_FILE = "map-file";

    public ProxyConfiguration() {
        super();
    }

    public ProxyConfiguration(String filePath) throws IOException {
        super(filePath);
    }

    public String getMapFile() {
        return (String) get(MAP_FILE);
    }

    public void setMapFile(String mapFile) {
        set(MAP_FILE, mapFile);
    }
}
