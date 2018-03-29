package se.util.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import jsonparser.Json;
import jsonparser.JsonException;
import jsonparser.JsonExposed;
import se.dscore.Master;
import se.dscore.RequestHandler;
import se.dscore.Server;
import se.dscore.MasterView;
import se.ipc.ESocket;
import se.ipc.pdu.AckPDU;
import se.ipc.pdu.ConnectPDU;
import se.ipc.pdu.GetPDU;
import se.ipc.pdu.IntroPDU;
import se.util.Logger;

public class HttpServer implements RequestHandler {

    @JsonExposed MasterView mview;
    String docRoot = ".";
    Server httpserver;
    Master master;
    HashMap<String, FileInputStream> openFiles;

    public HttpServer(String docRoot, Master master) throws IOException {
        this.master = master;
        mview = master.getDomainStatus();
        this.docRoot = docRoot;
        httpserver = new Server(this);
        openFiles = new HashMap<>();
    }

    public String getHost() {
        return httpserver.getHost();
    }

    public int getPort() {
        return httpserver.getPort();
    }

    public String getDocumentRoot() {
        return new File(docRoot).getAbsolutePath();
    }

    /* Runs this http server in the current thread */
    public void run() throws IOException {
        httpserver.run();
    }

    private void serve(HttpRequest req) throws IOException {

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
            default:
                sendFile(url, req.getOutputStream());
        }
    }

    @Override
    public void handle(ESocket sock) throws IOException {
        try {
            HttpRequest request = new HttpRequest(sock);
            serve(request);
            sock.close();
        } catch (HttpException ex) {
            Logger.ilog(Logger.LOW, ex.getMessage());
        }
    }

    /* never going to call them */
    @Override
    public void handle_get(ESocket sock, GetPDU gpdu) throws IOException {
    }

    @Override
    public void handle_intro(ESocket sock, IntroPDU ipdu) throws IOException {
    }

    @Override
    public void handle_connect(ESocket sock, ConnectPDU cpdu) throws IOException {
    }

    @Override
    public void handle_ack(ESocket sock, AckPDU apdu) throws IOException {
    }

    private void sendStatus(String url, OutputStream out) throws IOException {
        String dump = mview.getObjectAsJson(url);
        out.write("HTTP/1.0 200 OK\n".getBytes());
        out.write(("Content-Length: " + dump.length() + "\n\n").getBytes());
        out.write(dump.getBytes());
    }

    private void sendFile(String url, OutputStream out) throws IOException {
        FileInputStream fis;
        File file = new File(docRoot + "/" + url);

        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            out.write("HTTP/1.0 404 Not Found\n\n".getBytes());
            return;
        }

        out.write("HTTP/1.0 200 OK\n".getBytes());
        out.write(("Content-Length: " + file.length() + "\n\n").getBytes());
        byte[] buffer = new byte[256];
        int read;
        while ((read = fis.read(buffer)) != -1) {
            out.write(buffer, 0, read);
            if (read < buffer.length) {
                break;
            }
        }
    }

    private void executeProcedure(String url, String data, OutputStream out) throws IOException {
        String dump;
        try {
            dump = mview.execute(url, Json.parse(docRoot));
        } catch (JsonException ex) {
            dump = "{\"error\": \"Couldn't parse the data sent as JSON\"}";
            Logger.elog(Logger.MEDIUM, "Couldn't parse the data sent as JSON");
        }
        out.write("HTTP/1.0 200 OK\n".getBytes());
        out.write(("Content-Length: " + dump.length() + "\n\n").getBytes());
        out.write(dump.getBytes());
    }

}
