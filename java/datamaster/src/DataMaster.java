import se.dscore.Master;
import java.io.IOException;
import se.dscore.Scheduler;
import se.ipc.pdu.PDU;
import se.util.Logger;

public class DataMaster {
    
    public static void main(String argv[]) throws IOException {

        
        PDU.setProcessRole("master");
        Logger.setLoglevel(Logger.LOW);
        
        Master master = new Master("configFile", new Scheduler());
	System.out.printf("master running on port  [%d]\n", master.getPort());
        System.out.printf("Http server running on port [%d]\n", master.getHttpServer().getPort());
        System.out.printf("Http server document root : [%s]\n", master.getHttpServer().getDocumentRoot());
        master.run();
        
    }
}
