package dmgr;

import java.io.IOException;
import se.dscore.Slave;
import se.dscore.MasterProxy;
import se.ipc.pdu.PDU;

public class Dmgr extends Slave {

    Dmgr(MasterProxy mproxy, String ticket, int pid) throws IOException {
        super(mproxy, ticket, pid);
    }
    
    public static void main(String[] args) throws IOException {
        PDU.setProcessRole("dmgr");
        //Integer.parseInt(args[1])
        new Dmgr(
                new MasterProxy(
                        args[1],
                        Integer.parseInt(args[2])
                ),
                args[0],
                Integer.parseInt(args[1])
        ).run();
    }
    
}
