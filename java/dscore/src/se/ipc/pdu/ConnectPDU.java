package se.ipc.pdu;

import se.dscore.SysInfo;
import jsonparser.DictObject;
import jsonparser.JsonExposed;

public class ConnectPDU extends PDU {

    private static final String ATTR_HTTP_PORT = "restPort";
    
    /* port for http thread */
    @JsonExposed public int restPort;
    
    /* port on which this server is listening. can be zero for a client */
    @JsonExposed public int ipcPort;
    
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

    private void init(String ticket, String pid, int ipcPort, int restPort) {
        this.sysInfo = new SysInfo(true);
        this.ipcPort = ipcPort;
        this.pid = pid;
        this.ticket = ticket;
        this.restPort = restPort;
    }
    
    public ConnectPDU(String ticket, String pid, int ipcPort, int restPort) {
        super(PDUConsts.METHOD_CONNECT);
        init(ticket, pid, ipcPort, restPort);
    }
    
    public int getHttpPort() {
        return restPort;
    }
    
    public void setHttpPort(int port) {
        restPort = port;
    }
    
    public int getConnectPort() {
        return ipcPort;
    }

    public String getPid() {
        return pid;
    }
    
    public String getTicket() {
        return ticket;
    }
}
