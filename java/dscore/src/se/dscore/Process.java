package se.dscore;

/**
 * Core of the domain. This implements the basic functionality of a component
 * process.
 */
import java.io.IOException;
import java.io.OutputStream;
import jsonparser.Json;
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
import se.util.http.HttpRequest;
import se.util.http.HttpServer;
import se.util.http.ProgressiveProcess;
import se.util.http.RestServlet;

public class Process implements RequestHandler, ProgressiveProcess, RestServlet {

    private final Server ipcServer;
    private final HttpServer restServer;
    public static final int HEARTBEAT_INTERVAL = 2000;
    protected Configuration config;

    /* this is just for master port unavailibility issue */
    protected Process(Configuration config) throws IOException {
        this.config = config;
        ipcServer = new Server(this);
        restServer = new HttpServer(config.getDocRoot());
        PDU.setProcessRole(config.getProcessRole());
        Logger.setLoglevel(config.getDebugLevel());
    }

    public void registerAPI(String url, RestServlet servlet) {
        restServer.registerAPI(url, servlet);
    }

    public int getHttpPort() {
        return restServer.getPort();
    }

    public void run() {

        /* add a shutdown hook */
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    deinit();
                })
        );

        /* register API urls */
        registerAPI("status", this);
        registerAPI("exec", this);

        new Thread(() -> {
            try {
                ipcServer.run();
            } catch (IOException ex) {
                Logger.elog(Logger.HIGH, "Exception in dscore thread. ", ex);
            }
        }, "dscore-thread").start();

        new Thread(() -> {
            try {
                restServer.run();
            } catch (IOException ex) {
                Logger.elog(Logger.HIGH, "Exception in rest-thread. ", ex);
            }
        }, "rest-server").start();
    }

    public String getHost() {
        return ipcServer.getHost();
    }

    public int getIPCPort() {
        return ipcServer.getPort();
    }

    public int getRestPort() {
        return restServer.getPort();
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
                sock.send(new HiPDU(getIPCPort(), getHttpPort()));
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
                socket.send(new HiPDU(getIPCPort(), getHttpPort()));
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

    private void sendStatus(String url, OutputStream out) throws IOException {
        String dump = APIUtil.getObjectAsJson(getProgress(), url);
        out.write("HTTP/1.0 200 OK\n".getBytes());
        out.write(("Content-Length: " + dump.length() + "\n\n").getBytes());
        out.write(dump.getBytes());
    }

    private void executeProcedure(String url, String data, OutputStream out) throws IOException {
        String dump;
        try {
            dump = APIUtil.execute(getProgress(), url, Json.parse(data));
        } catch (JsonException ex) {
            dump = "{\"error\": \"Couldn't parse the data sent as JSON\"}";
            Logger.elog(Logger.MEDIUM, "Couldn't parse the data sent as JSON");
        }
        out.write("HTTP/1.0 200 OK\n".getBytes());
        out.write(("Content-Length: " + dump.length() + "\n\n").getBytes());
        out.write(dump.getBytes());
    }

    @Override
    public void serve(HttpRequest req) throws IOException {
        String url = req.getUrl();
        String service = url;
        if (url.contains("/")) {
            service = url.substring(0, url.indexOf('/'));
        }
        switch (service) {
            case "status":
                sendStatus(url, req.getOutputStream());
                break;
            case "exec":
                executeProcedure(url, req.getData(), req.getOutputStream());
                break;
        }
    }
    
    @Override
    public Object getProgress() {
        return this;
    }
}
