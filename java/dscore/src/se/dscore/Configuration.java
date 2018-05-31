package se.dscore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import jsonparser.DictObject;
import jsonparser.Formatter;
import jsonparser.Json;
import jsonparser.JsonException;
import static se.dscore.SlaveProcessConfiguration.PID;
import static se.dscore.SlaveProcessConfiguration.TICKET;
import se.util.Logger;

public class Configuration {

    public static final String DEBUG_LEVEL = "debug-level",
            PROCESS_ROLE = "processrole",
            DOC_ROOT = "doc-root";

    private DictObject conf;

    public Configuration() {
        conf = new DictObject();
    }

    public void putDefaults() {
        
    }
    
    public void generateSample(String filePath) throws FileNotFoundException {

        Field[] fields = this.getClass().getFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                try {
                    conf.set((String) field.get(null), "");
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.elog(Logger.MEDIUM, "Error creating the file " + filePath);
                }
            }
        }
        dump(filePath);
    }

    public void dump(String filePath) throws FileNotFoundException {
        try (PrintWriter pw = new PrintWriter(filePath)) {
            pw.append(Formatter.format(conf.toString()));
        } catch (IOException | JsonException ex) {
            java.util.logging.Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Configuration(String filePath)
            throws FileNotFoundException, IOException {
        try {
            conf = (DictObject) Json.parse(new FileInputStream(filePath));
        } catch (JsonException ex) {
            Logger.elog(Logger.MEDIUM, "Error reading the configuration file " + filePath);
        }
    }

    public Object get(String key) {
        return conf.get(key).getValue();
    }

    public void set(String key, Object value) {
        conf.set(key, String.valueOf(value));
    }

    public String getPid() {
        return (String) get(PID);
    }

    public void setPid(String pid) {
        set(PID, pid);
    }

    public String getTicket() {
        return (String) get(TICKET);
    }

    public void setTicket(String ticket) {
        set(TICKET, ticket);
    }

    public String getProcessRole() {
        return (String) get(PROCESS_ROLE);
    }

    public void setProcessRole(String role) {
        set(PROCESS_ROLE, role);
    }

    public void setDebugLevel(int level) {
        set(DEBUG_LEVEL, level);
    }
    
    public int getDebugLevel() {
        return Integer.parseInt((String) get(DEBUG_LEVEL));
    }

    public void setDocRoot(String docRoot) {
        set(DOC_ROOT, docRoot);
    }
    
    public String getDocRoot() {
        return (String) get(DOC_ROOT);
    }
}
