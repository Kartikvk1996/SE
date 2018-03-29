package se.dscore;

import java.io.IOException;
import se.ipc.ESocket;
import se.ipc.pdu.ConnectPDU;

public class Slave extends Probable {

    private String pid;
    
    public Slave(MasterProxy mproxy, String ticket, String pid) throws IOException {
        super(mproxy, ticket);
        this.pid = pid;
    }
    
    public void run() throws IOException {
        /* report to the master that you are running on port X */
        ESocket sock = new ESocket(mproxy.getHost(), mproxy.getPort());
        sock.send(new ConnectPDU(ticket, getPort(), pid));
        sock.close();
        super.run();
    }    
}