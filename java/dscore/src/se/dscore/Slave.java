package se.dscore;

import java.io.IOException;
import se.ipc.ESocket;
import se.ipc.pdu.ConnectPDU;

public class Slave extends Probable {

    public Slave(MasterProxy mproxy) throws IOException {
        super(mproxy);
        /* report to the master that you are running on port X */
        ESocket sock = new ESocket(mproxy.getHost(), mproxy.getPort());
        sock.send(new ConnectPDU());
        sock.close();
    }
    
}