package se.dscore;

import java.io.IOException;
import java.util.HashMap;
import se.dscore.Probable;
import se.ipc.Consts;
import se.ipc.ESocket;
import se.ipc.pdu.AckPDU;
import se.ipc.pdu.ConnectPDU;
import se.ipc.pdu.IntroPDU;
import se.ipc.pdu.JsonPathNotExistsException;
import se.ipc.pdu.PDU;
import se.util.Logger;

public class Master extends Probable {

    HashMap<String, SlaveProxy> slaves;
    private Status status;
    
    public Master(String configFile) throws IOException {
        super();
        slaves = new HashMap<>();
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

        System.out.println(pdu);
        
        /* If he is a guest then just introduce it to everyone. */
        if (pdu.getWho().equals(PDU.PN_GUEST)) {
            introduce(sock, pdu);
            return;
        }

        if (pdu.getWho().equals(PDU.PN_FIREUP)) {

            slaves.put(sock.getHost(),
                    new SlaveProxy(this, sock.getHost(), pdu.getConnectPort()));
            schedule();
            return;
        }

        String host = sock.getHost();
        if(!slaves.containsKey(host))
            slaves.put(host, new SlaveProxy(this, host, 100));
        
        /* This is some process we created send back the ACK with PID */
        slaves.get(sock.getHost()).addProcessEntry(
                new Process(
                        sock.getHost(),
                        pdu.getConnectPort(),
                        pdu.getWho()
                )
        );

        AckPDU apdu = new AckPDU();
        try {
            apdu.setValue(Consts.PID, "" + getSlaveCount());
        } catch(JsonPathNotExistsException ex) {}
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

    void schedule() throws IOException {

        /* we need some load-balancing kind of algorithm here. 
	 * For the time being we will use a simple algorithm a fixed
	 * set of processes per machine.
         */
        Logger.ilog(Logger.LOW, "trying to schedule jobs");
        for (String slaveHost : slaves.keySet()) {
            SlaveProxy slv = slaves.get(slaveHost);
            if (slv.getProcessCount() < 2) {
                //slv.createProcess("crawler");
                slv.createProcess(Consts.DMGR_BIN);
            }
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
        PDU pdu = PDU.fromStream(socket.getInputStream());
        switch(pdu.getMethod()) {
            case PDU.METHOD_CONNECT:
                handle_connect(socket, pdu.toConnectPDU());
                break;
            default:
                super.def_handler(socket, pdu);
        }
    }

    public PDU getStatus() {
        return new AckPDU();
    }

    @Override
    public void run() throws IOException {
        setProxy(new MasterProxy("localhost", getPort()));
        super.run();
    }
    
}
