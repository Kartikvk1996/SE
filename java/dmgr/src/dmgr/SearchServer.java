package dmgr;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashSet;
import jsonparser.Json;
import jsonparser.JsonException;
import se.util.Logger;
import se.util.http.HttpRequest;
import se.util.http.HttpServer;

public class SearchServer extends HttpServer {

    Trie dictionary;
    File cdir;

    public SearchServer(String docRoot, Trie dictionary, File cdir) throws IOException {
        super(docRoot);
        this.dictionary = dictionary;
        this.cdir = cdir;
    }

    @Override
    protected void serve(HttpRequest req) throws IOException {

        String url = req.getUrl();
        System.out.println(url);
        switch (url) {
            case "search":
                ReverseIndexer rin = new ReverseIndexer(
                        dictionary,
                        req.getData(),
                        cdir
                );
                long start = new Date().getTime();
                rin.start();
                try {
                    rin.join();
                } catch (InterruptedException ex) {
                }
                HashSet set = rin.getDocs();
                long end = new Date().getTime();
                
                try {
                    String str = Json.dump(new SearchResult(end - start, set));
                    OutputStream out = req.getOutputStream();
                    out.write("HTTP/1.0 200 OK\n".getBytes());
                    out.write(("Content-Length: " + str.length() + "\n\n").getBytes());
                    req.getOutputStream().write(str.getBytes());
                } catch (JsonException ex) {
                    Logger.elog(Logger.MEDIUM, "Error responding back to client " + ex.getMessage());
                }
                break;
            case "dump":
                synchronized(dictionary) {
                    dictionary.save("dict.obj");
                }
                break;
            default:
                super.serve(req); //To change body of generated methods, choose Tools | Templates.

        }
    }
}
