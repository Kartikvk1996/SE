package se.dscore;

/**
 * This is a abstract implementation of the Master which can be extended to the
 * implement the required functionality. It manages the status of the domain, 
 * a http-server for serving the insights. It also uses a scheduler plug-in to
 * schedule the processes on the slaves.
 * 
 * The functionalities already implemented by this class are
 * 
 *  1. Handling the connect requests, registering the slave machines.
 *  2. Scheduling the processes on the nodes.
 *  3. Handling heartbeat signals sent by the nodes/
 *  4. Managing the HTTP-server for providing the insights.
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import jsonparser.DictObject;
import jsonparser.JsonException;
import se.ipc.ESocket;
import se.ipc.pdu.AckPDU;
import se.ipc.pdu.ConnectPDU;
import se.ipc.pdu.IntroPDU;
import se.ipc.pdu.InvalidPDUException;
import se.ipc.pdu.PDU;
import se.ipc.pdu.PDUConsts;
import se.ipc.pdu.StatusPDU;
import se.util.Logger;
import se.util.http.HttpServer;

public class Master extends Probable {
    
    private final MasterView status;
    private final LinkedHashMap<String, SlaveProxy> slaves;
    private final HttpServer hserver;
    private Scheduler scheduler;

    /**
     * @param configFile : configuration file of the master may be used in future.
     * @param scheduler
     * @throws IOException 
     */
    public Master(String configFile, Scheduler scheduler) throws IOException {
        super();
        slaves = new LinkedHashMap<>();
        status = new MasterView(slaves);
        hserver = new HttpServer(".", this);
        AckPDU.httpPort = hserver.getPort();
        this.scheduler = scheduler;
    }

    /* Planning to remove this */
    void introduce(ESocket sock, PDU pdu) throws IOException {

        for (String slaveHost : slaves.keySet()) {
            SlaveProxy slave = slaves.get(slaveHost);
            HashMap<String, Process> map = slave.getProcesses();
            for (String key : map.keySet()) {
                map.get(key).sendPDU(new IntroPDU(sock.getHost(), sock.getPort()), false);
            }
        }
    }
    /**/
    
    /**
     * Registers the Node, Process in the domain.
     * 
     *  1. Fireup is treated as a slave agent and will be added as slave
     *  2. Processes on nodes get added to respective SlaveProxies.
     *  3. Any guests will be introduced to the processes.
     * @param sock 
     * @param pdu
     * @throws IOException 
     */
    @Override
    public void handle_connect(ESocket sock, ConnectPDU pdu) throws IOException {

        /* If he is a guest then just introduce it to everyone. */
        if (pdu.getWho().equals(PDUConsts.PN_GUEST)) {
            introduce(sock, pdu);
            return;
        }

        String sender = sock.getHost();
        String ticket = pdu.getTicket();
        
        if (pdu.getWho().equals(PDUConsts.PN_FIREUP)) {

            /* check a ticket is already assigned */
            if(slaves.get(ticket) != null)
                return;
            
            /* We will generate a token for the fireup and associate it with it */
            ticket = "Node-" + slaves.size();
            slaves.put(ticket,
                    new SlaveProxy(
                        this, sender,
                        pdu.getConnectPort(), pdu.getSysInfo()
                    )
            );

            AckPDU apdu = new AckPDU(ticket);
            sock.send(apdu);
            schedule(ticket);
            return;
        }
        
        String pid = pdu.getPid();
        slaves.get(ticket).addProcessEntry(
                pid,
                new Process(
                    sender,
                    pdu.getConnectPort(),
                    pdu.getWho(),
                    pid
                )
        );

        AckPDU apdu = new AckPDU(ticket);
        sock.send(apdu);
    }

    int getSlaveCount() {
        return slaves.size();
    }

    void schedule(String host) throws IOException {
        scheduler.schedule(host, slaves);
    }

    void handle_update(ESocket s, PDU pdu) {
        /* not yet defined */
    }

    void handle_ack(ESocket s, PDU pdu) {
        /* not yet defined */
    }

    /* just leaving for backward compatibility */
    void handle_get(ESocket s, PDU pdu) {
    }

    /**
     * Handles the connect and status PDUs
     * @param socket Socket on which PDUs are been sent.
     * @throws IOException 
     */
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
        switch (pdu.getMethod()) {
            case PDUConsts.METHOD_CONNECT:
                handle_connect(socket, (ConnectPDU) pdu);
                break;
            case PDUConsts.METHOD_STATUS:
                handle_status(socket, (StatusPDU)pdu);
                break;
            default:
                super.def_handler(socket, pdu);
        }
    }

    /**
     * Provides a global level status object which can be queried
     */
    public MasterView getDomainStatus() {
        return status;
    }

    /**
     * Creates a HTTP-server and starts node protocol handler
     * @throws IOException 
     */
    @Override
    public void run() throws IOException {
        setProxy(new MasterProxy("localhost", getPort()));
        new Thread(() -> {
            try {
                hserver.run();
            } catch (IOException ex) {
                Logger.elog(Logger.MEDIUM, "HttpServer encountered an errror");
            }
        }, "HTTP-SERVER").start();
        super.run();
    }

    public HttpServer getHttpServer() {
        return hserver;
    }

    /**
     * Heartbeats are handled here
     * @param socket
     * @param pdu 
     */
    private void handle_status(ESocket socket, StatusPDU pdu) {
        try {
            slaves.get(socket.getHost()).rcvHeartBeat(pdu);
        } catch(Exception ex) {}
    }

}
