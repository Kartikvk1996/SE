package dmgr;

import java.io.IOException;
import java.util.Arrays;
import se.dscore.Slave;
import se.ipc.pdu.PDU;
import se.util.Logger;

public class Dmgr extends Slave {

    Dmgr(String args[]) throws IOException {
        super(args);
    }
    
    public static void main(String[] args) throws IOException {

        System.out.println(Arrays.toString(args));
        Logger.setLoglevel(Logger.LOW);
        PDU.setProcessRole("dmgr");
        new Dmgr(args).run();
    }
    
}
