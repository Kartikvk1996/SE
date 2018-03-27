package se.util.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import jsonparser.Json;
import jsonparser.JsonException;
import se.dscore.Master;
import se.dscore.RequestHandler;
import se.dscore.Server;
import se.dscore.Status;
import se.ipc.ESocket;
import se.ipc.pdu.AckPDU;
import se.ipc.pdu.ConnectPDU;
import se.ipc.pdu.GetPDU;
import se.ipc.pdu.IntroPDU;
import se.util.Logger;

public class HttpServer implements RequestHandler {

    String docRoot = ".";
    Server httpserver;
    Master master;
    HashMap<String, FileInputStream> openFiles;

    public HttpServer(String docRoot, Master master) throws IOException {
        this.master = master;
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

        if (req.getUrl().split("/")[0].equals("status")) {
            Status stat = master.getDomainStatus();
            String dump = stat.getObject(req.getUrl());
            OutputStream out = req.getOutputStream();
            out.write("HTTP/1.0 200 OK\n".getBytes());
            out.write(("Content-Length: " + dump.length() + "\n\n").getBytes());
            out.write(dump.getBytes());
        } else {

            FileInputStream fis;
            File file = new File(docRoot + "/" + req.getUrl());

            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException ex) {
                fis = openFiles.get(docRoot + "/404.html");
            }

            OutputStream out = req.getOutputStream();

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

}
