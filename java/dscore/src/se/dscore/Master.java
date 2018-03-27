package se.dscore;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import jsonparser.JsonException;
import se.ipc.Consts;
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

    private final LinkedHashMap<String, SlaveProxy> slaves;
    private final HttpServer hserver;
    private final Status status;

    public Master(String configFile) throws IOException {
        super();
        slaves = new LinkedHashMap<>();
        status = new Status(slaves);
        hserver = new HttpServer(".", this);
        AckPDU.httpPort = hserver.getPort();
    }

    void introduce(ESocket sock, PDU pdu) throws IOException {

        for (String slaveHost : slaves.keySet()) {
            SlaveProxy slave = slaves.get(slaveHost);
            for (Process process : slave.getProcesses()) {
                process.sendPDU(new IntroPDU(sock.getHost(), sock.getPort()), false);
            }
        }
    }

    @Override
    public void handle_connect(ESocket sock, ConnectPDU pdu) throws IOException {

        /* If he is a guest then just introduce it to everyone. */
        if (pdu.getWho().equals(PDUConsts.PN_GUEST)) {
            introduce(sock, pdu);
            return;
        }

        if (pdu.getWho().equals(PDUConsts.PN_FIREUP)) {

            slaves.put(sock.getHost(), new SlaveProxy(
                            this, sock.getHost(),
                            pdu.getConnectPort(), pdu.getSysInfo()));
            AckPDU apdu = new AckPDU();
            sock.send(apdu);
            schedule(sock.getHost());
            return;
        }

        /*************** THIS CODE IS FOR TESTING **************/
        Logger.elog(Logger.HIGH, "Testing code is being run. system intrusion can be done");
        String host = sock.getHost();
        if (!slaves.containsKey(host)) {
            slaves.put(host, new SlaveProxy(
                            this, sock.getHost(),
                            pdu.getConnectPort(), pdu.getSysInfo()));
        }
        /*******************************************************/

        slaves.get(sock.getHost()).addProcessEntry(
                new Process(
                        sock.getHost(),
                        pdu.getConnectPort(),
                        pdu.getWho()
                )
        );

        AckPDU apdu = new AckPDU();
        sock.send(apdu);
    }

    @Override
    public String toString() {

        int i = 0;
        StringBuffer sbuf = new StringBuffer();

        for (String slaveHost : slaves.keySet()) {
            sbuf.append(i++ > 0 ? "," : " ");
            sbuf.append(slaves.get(slaveHost).toString());
        }

        return "{\"ip\": \"" + getHost() + "\",\"port\": \"" + getPort() + "\",\"slaves\": [" + sbuf + "]}";
    }

    int getSlaveCount() {
        return slaves.size();
    }

    void schedule(String host) throws IOException {

        /* we need some load-balancing kind of algorithm here. 
	 * For the time being we will use a simple algorithm a fixed
	 * set of processes per machine.
         */
        Logger.ilog(Logger.LOW, "trying to schedule jobs");

        SlaveProxy slv = slaves.get(host);
        if (slv.getProcessCount() < 2) {
            //slv.createProcess("crawler");
            slv.createProcess(Consts.DMGR_BIN);
        }
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

    public Status getDomainStatus() {
        return status;
    }

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

    private void handle_status(ESocket socket, StatusPDU pdu) {
        try {
            slaves.get(socket.getHost()).rcvHeartBeat(pdu);
        } catch(Exception ex) {}
    }

}
