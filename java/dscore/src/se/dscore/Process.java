package se.dscore;

/**
 * Core of the domain. This implements the basic functionality of a component
 * process.
 */
import java.io.IOException;
import jsonparser.JsonException;
import se.ipc.pdu.PDU;
import se.ipc.ESocket;
import se.ipc.pdu.HiPDU;
import se.ipc.pdu.IntroPDU;
import se.ipc.pdu.InvalidPDUException;
import se.ipc.pdu.PDUConsts;
import static se.ipc.pdu.PDUConsts.METHOD_DIE;
import se.ipc.pdu.StatusPDU;
import se.util.Logger;

public class Process implements RequestHandler {

    private int httpPort;
    private final String err;
    private final String out;
    private final Server server;
    public static final int HEARTBEAT_INTERVAL = 2000;
    protected Configuration config;

    /* this is just for master port unavailibility issue */
    protected Process(Configuration config) throws IOException {
        this.config = config;
        server = new Server(this);
        err = config.getErrFile();
        out = config.getOutFile();
        PDU.setProcessRole(config.getProcessRole());
        Logger.setLoglevel(config.getDebugLevel());
    }

    public String getErrFile() {
        return err;
    }

    public String getOutFile() {
        return out;
    }

    public void setHttpPort(int port) {
        this.httpPort = port;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void run() {

        /* add a shutdown hook */
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    deinit();
                })
        );

        new Thread(() -> {
            try {
                server.run();
            } catch (IOException ex) {
                Logger.elog(Logger.HIGH, "Exception in dscore thread. " + ex.getMessage());
            }
        }, "dscore-thread").start();
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
            Logger.elog(Logger.HIGH, "Error handling the client. " + ex.getMessage());
        }
        if (pdu == null) {
            return;
        }
        handler(socket, pdu);
        socket.close();
    }

    @Override
    public void handler(ESocket socket, PDU pdu) throws IOException {
        switch (pdu.getMethod()) {
            case METHOD_DIE:
                deinit();
                System.exit(0);
            case PDUConsts.METHOD_INTRO:
                IntroPDU ipdu = (IntroPDU) pdu;
                ESocket sock = new ESocket(ipdu.getGuestHost(), ipdu.getGuestPort());
                sock.send(new HiPDU(getPort(), getHttpPort()));
                try {
                    HiPDU hello = (HiPDU) sock.recvPDU();
                    handle_hello(sock, hello);
                } catch (JsonException | InvalidPDUException ex) {
                    Logger.elog(Logger.HIGH, "Couldn't say hello to " + sock);
                }
                break;
            case PDUConsts.METHOD_HI:
                HiPDU hpdu = (HiPDU) pdu;
                handle_hello(socket, hpdu);
                /* Say hello back on same socket */
                socket.send(new HiPDU(getPort(), getHttpPort()));
                break;
        }
    }

    public PDU getStatus() {
        return new StatusPDU();
    }

    public void deinit() {
    }

    protected void handle_hello(ESocket sock, HiPDU hpdu) {
    }
}
