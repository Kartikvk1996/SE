package se.dscore;

/**
 * Core of the domain. This implements the basic functionality of a component
 * process.
 */
import java.io.IOException;
import jsonparser.JsonException;
import se.ipc.pdu.PDU;
import se.ipc.ESocket;
import se.ipc.pdu.InvalidPDUException;
import static se.ipc.pdu.PDUConsts.METHOD_DIE;
import se.ipc.pdu.StatusPDU;
import se.util.Logger;

public class Process implements RequestHandler {

    
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
        Logger.setLoglevel(Integer.parseInt((String) config.get(Configuration.DEBUG_LEVEL)));
    }

    public String getErrFile() {
        return err;
    }
    
    public String getOutFile() {
        return out;
    }
    
    public void run() {
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
    }

    @Override
    public void handler(ESocket socket, PDU pdu) throws IOException {
        switch(pdu.getMethod()) {
            case METHOD_DIE:
                deinit();
                System.exit(0);
        }
    }

    public PDU getStatus() {
        return new StatusPDU();
    }
    
    public void deinit() {
        
    }
}
