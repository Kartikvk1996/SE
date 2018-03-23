import se.dscore.Master;
import java.io.IOException;
import se.ipc.pdu.PDU;

public class DataMaster {
    
    /*
     * args:
     *	host: IP on which to bind the server. This actually initialises
     *  	   the master-config.
     *	port: port on which the server should be listening.
     */
    public static void main(String argv[]) throws IOException {

        
        /*
        HttpServer server = new HttpServer("/home/mmp", null);
        System.out.println("http://localhost:" + server.getPort() + "/t.c");
        server.run();
        */
        
        PDU.setProcessRole("master");
        
        Master master = new Master("configFile");
	System.out.printf("master running on port  [%d]\n", master.getPort());
        master.run();
        
    }

    
}
