package se.ipc.pdu;

import java.io.File;
import jsonparser.DictObject;
import jsonparser.JsonExposed;

public class AckPDU extends PDU {

    /* this will serve as a check for assignement */
    @JsonExposed public int restPort;
    @JsonExposed public static String jarRevision;
    @JsonExposed public String ticket;
    
    public AckPDU(String ticket, int restPort) {
        super(PDUConsts.METHOD_ACK);
        this.ticket = ticket;
        this.restPort = restPort;
    }

    public String getJarVersion() {
        return jarRevision;
    }
    
    public AckPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
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

    public void setHttpPort(int httpPort) {
        restPort = httpPort;
    } 
    
    public int getHttpPort() {
        return restPort;
    }

    public String getTicket() {
        return ticket;
    }

}
