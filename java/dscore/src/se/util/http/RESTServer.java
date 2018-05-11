package se.util.http;

import java.io.IOException;
import java.io.OutputStream;
import jsonparser.Json;
import jsonparser.JsonException;
import se.dscore.APIUtil;
import se.util.Logger;

public class RESTServer extends HttpServer {

    Object status;
    ProgressiveProcess process;

    public RESTServer(String docRoot, ProgressiveProcess process) throws IOException {
        super(docRoot);
        this.process = process;
        status = process.getProgress();
    }

    @Override
    protected void serve(HttpRequest req) throws IOException {

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
                super.serve(req);
        }
    }

    private void sendStatus(String url, OutputStream out) throws IOException {
        String dump = APIUtil.getObjectAsJson(status, url);
        out.write("HTTP/1.0 200 OK\n".getBytes());
        out.write(("Content-Length: " + dump.length() + "\n\n").getBytes());
        out.write(dump.getBytes());
    }

    private void executeProcedure(String url, String data, OutputStream out) throws IOException {
        String dump;
        try {
            dump = APIUtil.execute(status, url, Json.parse(data));
        } catch (JsonException ex) {
            dump = "{\"error\": \"Couldn't parse the data sent as JSON\"}";
            Logger.elog(Logger.MEDIUM, "Couldn't parse the data sent as JSON");
        }
        out.write("HTTP/1.0 200 OK\n".getBytes());
        out.write(("Content-Length: " + dump.length() + "\n\n").getBytes());
        out.write(dump.getBytes());
    }

}
