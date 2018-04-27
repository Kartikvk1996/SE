import se.dscore.MasterProcess;
import java.io.IOException;

public class DataMaster {
    
    public static void main(String argv[]) throws IOException {
        
        /**
        new DMasterConfiguration().generateSample("masterconfig.conf");
        System.exit(0);
        /**/
        
        DMasterConfiguration config = new DMasterConfiguration("masterconfig.conf");
        MasterProcess master = new MasterProcess(config);
        master.setScheduler(new RBScheduler(config, master.getHost(), master.getPort()));
	System.out.printf("master running on port  [%d]\n", master.getPort());
        System.out.printf("Http server running on port [%d]\n", master.getHttpServer().getPort());
        System.out.printf("Http server document root : [%s]\n", master.getHttpServer().getDocumentRoot());
        master.run();
        
    }
}
