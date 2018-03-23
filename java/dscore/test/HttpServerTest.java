
import java.io.IOException;
import se.util.http.HttpServer;

public class HttpServerTest {

    public static void main(String[] args) throws IOException {
        
        HttpServer server = new HttpServer("/home/mmp/", null);
        
        System.out.println("http://localhost:" + server.getPort() + "/t.c");
        
        server.run();
        
    }
    
}
