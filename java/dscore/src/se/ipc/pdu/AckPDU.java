package se.ipc.pdu;

import java.io.File;
import java.lang.reflect.Field;
import jsonparser.DictObject;

public class AckPDU extends PDU {

    /* this will serve as a check for assignement */
    public static Integer httpPort = null;
    public static String jarRevision;
    
    public AckPDU() {
        super(PDUConsts.METHOD_ACK);
    }

    public String getJarVersion() {
        return jarRevision;
    }
    
    public AckPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
        for (Field field : getClass().getDeclaredFields()) {
            try {
                field.set(this, jObject.get(field.getName()).getValue());
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new InvalidPDUException();
            }
        }
    }

    static {
        long curdate = Long.MAX_VALUE;
        File folder = new File(".");
        File[] jars = folder.listFiles();

        for (File jar : jars) {
            if (!jar.getName().contains(".jar")) {
                continue;
            }
            long fdate = jar.lastModified();
            if (fdate < curdate) {
                curdate = fdate;
            }
        }
        jarRevision = curdate + "";
    }

    public int getHttpPort() {
        return httpPort;
    }

}
