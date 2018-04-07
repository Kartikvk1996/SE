package se.dscore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import jsonparser.DictObject;
import jsonparser.Json;
import jsonparser.JsonException;
import se.util.Logger;

public class Configuration {

    public static final String HTTP_ROOT = "http-root";
    public static final String DEBUG_LEVEL = "debug-level";

    private DictObject conf;

    public Configuration() {
        conf = new DictObject();
    }

    public void generateSample(String filePath) throws FileNotFoundException {

        Field[] fields = Configuration.class.getDeclaredFields();
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
            pw.append(conf.toString());
        }
    }

    public static Configuration readFromFile(String filePath)
            throws FileNotFoundException, IOException {
        Configuration nobj = new Configuration();
        try {
            nobj.conf = (DictObject) Json.parse(new FileInputStream(filePath));
        } catch (JsonException ex) {
            Logger.elog(Logger.MEDIUM, "Error reading the configuration file " + filePath);
        }
        return nobj;
    }

    public String get(String key) {
        return (String) conf.get(key).getValue();
    }

}
