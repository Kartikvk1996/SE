package se.dscore;

/**
 * Core of the domain. This implements the basic functionality of a
 * component process. 
 */

import java.io.IOException;
import java.util.logging.Level;
import jsonparser.JsonException;
import se.ipc.pdu.IntroPDU;
import se.ipc.pdu.PDU;
import se.ipc.ESocket;
import se.ipc.pdu.AckPDU;
import se.ipc.pdu.InvalidPDUException;
import se.ipc.pdu.PDUConsts;
import se.ipc.pdu.StatusPDU;

public class Node implements RequestHandler {

    public String pid = "*****";
    private boolean running = false;
    private Server server;
    public static final int HEARTBEAT_INTERVAL = 2000;
    protected MasterProxy mproxy;
    protected String ticket = "*****";
    
    /* just a hack to avoid mutiple assignement statements */
    final void commonInit() throws IOException {
         server = new Server(this);     
         StatusPDU.setProcessDetails(ticket, pid);
    }
    
    /* this is just for master port unavailibility issue */
    protected Node() throws IOException {
        commonInit();
    }
    
    public Node(MasterProxy mproxy, String ticket, String pid) throws IOException {
        this.mproxy = mproxy;
        this.ticket = ticket;
        this.pid = pid;
        commonInit();
    }

    public void run() throws IOException {
        running = true;
        server.run();
    }
    
    public String getHost() {
        return server.getHost();
    }
    
    public int getPort() {
        return server.getPort();
    }
    
    @Override
    public void handle(ESocket socket) throws IOException {
        PDU pdu = null;
        try {
            pdu = socket.recvPDU();
        } catch (JsonException | InvalidPDUException ex) {
            java.util.logging.Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (pdu == null) {
            return;
        }
        handler(socket, pdu);
    }
    
    @Override
    public void handler(ESocket socket, PDU pdu) throws IOException {
        switch(pdu.getMethod()) {
            case PDUConsts.METHOD_INTRO:
                handle_intro(socket, (IntroPDU) pdu);
                break;
        }
    }

    public void handle_intro(ESocket sock, IntroPDU ipdu) throws IOException {
       AckPDU apdu = new AckPDU(ticket);
       ESocket gsock = new ESocket(ipdu.getGuestHost(), ipdu.getGuestPort());
       gsock.send(apdu);
    }

    public PDU getStatus() {
        return new StatusPDU();
    }
    
    public void setProxy(MasterProxy mproxy) {
        this.mproxy = mproxy;
    }

}
