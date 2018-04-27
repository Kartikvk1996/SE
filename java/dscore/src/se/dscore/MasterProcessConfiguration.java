package se.dscore;

import java.io.IOException;


public class MasterProcessConfiguration extends Configuration {
    
    public static final String HTTP_ROOT = "http-root";    
    
    public MasterProcessConfiguration(String filePath) throws IOException {
        super(filePath);
    }
    
    public MasterProcessConfiguration() {
        super();
    }
    
}