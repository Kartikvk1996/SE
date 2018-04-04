package se.ipc.pdu;

import se.dscore.SysInfo;
import jsonparser.DictObject;
import jsonparser.JsonExposed;

public class ConnectPDU extends PDU {

    private static final String ATTR_HTTP_PORT = "logport";
    private static final String ATTR_ERR_FILE = "errfile";
    private static final String ATTR_OUT_FILE = "outfile";
    
    /* port on which this server is listening. can be zero for a client */
    @JsonExposed public int port;
    
    /* process id attached to this process. can be 0xbaba for the fireup */
    @JsonExposed public String pid;
    
    /* System's resources */
    @JsonExposed public SysInfo sysInfo;
    
    /* ticket got by the master. Is empty string initially */
    @JsonExposed public String ticket;
    
    public ConnectPDU(DictObject jObject) throws InvalidPDUException {
        super(jObject);
    }
    
    public SysInfo getSysInfo() {
        return sysInfo;
    }

    private void init(String ticket, String pid, int port) {
        this.sysInfo = new SysInfo(true);
        this.port = port;
        this.pid = pid;
        this.ticket = ticket;
    }
    
    public ConnectPDU(String ticket, String pid, int port) {
        super(PDUConsts.METHOD_CONNECT);
        init(ticket, pid, port);
    }
    
    public int getLogPort() {
        return (Integer)((DictObject)data).get(ATTR_HTTP_PORT).getValue();
    }
    
    public void setLogPort(int port) {
        ((DictObject)data).set(ATTR_HTTP_PORT, port);
    }
    
    public void setEOFiles(String errFile, String outFile) {
        ((DictObject)data).set(ATTR_OUT_FILE, outFile);
        ((DictObject)data).set(ATTR_ERR_FILE, errFile);
    }
    
    public String getOutFile() {
        return (String)((DictObject)data).get(ATTR_OUT_FILE).getValue();
    }
    
    public String getErrFile() {
        return (String)((DictObject)data).get(ATTR_ERR_FILE).getValue();
    }
    
    public int getConnectPort() {
        return port;
    }

    public String getPid() {
        return pid;
    }
    
    public String getTicket() {
        return ticket;
    }
}
