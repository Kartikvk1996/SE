package dmgr;

import java.io.IOException;
import se.dscore.SlaveProcessConfiguration;

public class DMgrConfiguration extends SlaveProcessConfiguration {

    public static final String SCAN_INTERVAL = "scan-interval",
            SCANNERS_COUNT = "scan-thread-count",
            CONTENT_DIRECTORY = "content-dir",
            TRIE_FILE = "trie-file",
            DICTIONARY_FILE = "dictionary-file",
            TEMP_DIRECTORY = "temp-dir";

    public DMgrConfiguration() {
        
    }
    
    public DMgrConfiguration(String filePath) throws IOException {
        super(filePath);
    }
    
    public long getScanInterval() {
        return (long) Long.parseLong((String) get(SCAN_INTERVAL));
    }

    public void setScanInterval(long scaninterval) {
        set(SCAN_INTERVAL, scaninterval);
    }

    public int getScannersCount() {
        return (int) Integer.parseInt((String) get(SCANNERS_COUNT));
    }

    public void setScannersCount(int scannerCount) {
        set(SCANNERS_COUNT, scannerCount);
    }

    public String getContentDirectory() {
        return (String) get(CONTENT_DIRECTORY);
    }
    
    public void setContentDirectory(String cdir) {
        set(CONTENT_DIRECTORY, cdir);
    }
    
    public String getTempDirectory() {
        return (String) get(TEMP_DIRECTORY);
    }
    
    public void setTempDirectory(String cdir) {
        set(TEMP_DIRECTORY, cdir);
    }
    
    public String getTrieFile() {
        return (String) get(TRIE_FILE);
    }
    
    public void setTrieFile(String trieFile) {
        set(TRIE_FILE, trieFile);
    }

    public String getDictionaryFile() {
        return (String) get(DICTIONARY_FILE);
    }
    
    public void setDictionaryFile(String file) {
        set(DICTIONARY_FILE, file);
    }
}

