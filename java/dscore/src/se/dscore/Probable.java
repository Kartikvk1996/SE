package se.dscore;

import java.io.IOException;
import se.ipc.pdu.PDU;
import se.ipc.ESocket;
import se.ipc.pdu.PDUConsts;
import se.ipc.pdu.StatusPDU;

public class Probable extends Node {

    public Probable(MasterProxy mproxy, String ticket, String pid) throws IOException {
        super(mproxy, ticket, pid);
    }

    protected Probable() throws IOException {
        /* just for the master port unavailibity */
    }
    
    @Override
    public void def_handler(ESocket sock, PDU pdu) throws IOException {
        switch (pdu.getMethod()) {
            case PDUConsts.METHOD_STATUS:
                handle_status(sock, pdu);
        }
    }

    private void handle_status(ESocket sock, PDU pdu) throws IOException {
        sock.send(new StatusPDU());
    }

}
