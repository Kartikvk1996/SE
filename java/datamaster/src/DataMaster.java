import java.io.FileNotFoundException;
import se.dscore.MasterProcess;
import java.io.IOException;
import se.dscore.MasterProcessConfiguration;
import se.dscore.Scheduler;

public class DataMaster extends MasterProcess {
    
    public DataMaster(MasterProcessConfiguration config, Scheduler scheduler) throws IOException, FileNotFoundException {
        super(config);
        this.scheduler = scheduler;
    }

    public static void main(String argv[]) throws IOException {
        
        /**
        new DMasterConfiguration().generateSample("masterconfig.conf");
        System.exit(0);
        /**/
        
        DMasterConfiguration config = new DMasterConfiguration("masterconfig.conf");
        DataMaster master = new DataMaster(config, new RBScheduler(config));
	System.out.printf("master running on port  [%d]\n", master.getPort());
        System.out.printf("Http server running on port [%d]\n", master.getHttpServer().getPort());
        System.out.printf("Http server document root : [%s]\n", master.getHttpServer().getDocumentRoot());
        master.run();
        
    }
}
