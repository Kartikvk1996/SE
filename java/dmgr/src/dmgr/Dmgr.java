package dmgr;

import java.io.IOException;
import se.dscore.Slave;
import se.dscore.MasterProxy;
import se.ipc.pdu.PDU;

public class Dmgr extends Slave {

    Dmgr(MasterProxy mproxy) throws IOException {
        super(mproxy);
    }
    
    public static void main(String[] args) throws IOException {
        PDU.setProcessRole("dmgr");
        //Integer.parseInt(args[1])
        new Dmgr(new MasterProxy(args[0], 62721)).run();
    }
    
}
