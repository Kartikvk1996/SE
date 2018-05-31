package se.util.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import se.dscore.RequestHandler;
import se.dscore.Server;
import se.ipc.ESocket;
import se.ipc.pdu.PDU;
import se.util.Logger;

public class HttpServer implements RequestHandler {

    String docRoot = ".";
    Server httpserver;
    HashMap<String, FileInputStream> openFiles;
    HashMap<String, RestServlet> mappings;

    public HttpServer(String docRoot) throws IOException {
        this.docRoot = docRoot;
        httpserver = new Server(this);
        openFiles = new HashMap<>();
        mappings = new HashMap<>();
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

    protected void serveReq(HttpRequest req) throws IOException {
        String url = req.getUrl();
        String service = url;
        if (url.contains("/")) {
            service = url.substring(0, url.indexOf('/'));
        }
        if (mappings.containsKey(service)) {
            mappings.get(service).serve(req);
        } else {
            sendFile(req.getUrl(), req.getOutputStream());
        }
    }

    public final void registerAPI(String url, RestServlet servelet) {
        mappings.put(url, servelet);
    }

    @Override
    public void handle(ESocket sock) throws IOException {
        try {
            HttpRequest request = new HttpRequest(sock);
            serveReq(request);
            sock.close();
        } catch (HttpException ex) {
            Logger.ilog(Logger.LOW, ex.getMessage());
        }
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

    @Override
    public void handler(ESocket sock, PDU pdu) throws IOException {
    }

}
