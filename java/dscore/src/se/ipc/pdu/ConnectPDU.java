package se.ipc.pdu;

import se.dscore.SysInfo;
import jsonparser.DictObject;
import jsonparser.JsonExposed;

public class ConnectPDU extends PDU {

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

    public ConnectPDU(String ticket, int port, String pid) {
        super(PDUConsts.METHOD_CONNECT);
        this.sysInfo = new SysInfo(true);
        this.port = port;
        this.pid = pid;
        this.ticket = ticket;
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
