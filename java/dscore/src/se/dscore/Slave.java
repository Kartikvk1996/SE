package se.dscore;

import java.io.IOException;
import jsonparser.JsonException;
import se.ipc.pdu.ConnectPDU;
import se.ipc.pdu.InvalidPDUException;
import se.util.Logger;

public class Slave extends Node {

    private final String err;
    private final String out;

    public Slave(String args[]) throws IOException, ArrayIndexOutOfBoundsException {

        super(
                new MasterProxy(
                        args[4], //master hostname
                        Integer.parseInt(args[5]) //master port
                ),
                args[0], //ticket
                args[1] //pid
        );
        this.err = args[2];                             //errfile
        this.out = args[3];                             //outfile
        
        usage();
    }

    @Override
    public void run() throws IOException {

        /* report to the master that you are running on port X */
        try {
            ConnectPDU pdu = new ConnectPDU(ticket, pid, getPort());
            pdu.setEOFiles(err, out);
            mproxy.send(pdu, true);
        } catch (JsonException | InvalidPDUException ex) {
            Logger.elog(Logger.HIGH, "unable to report to master. deinitialising");
            System.exit(0);
        }

        /* Create a heartbeat sending thread */
        new Thread(
                new Heartbeat(mproxy, this, HEARTBEAT_INTERVAL),
                "Heartbeat"
        ).start();

        super.run();
    }

    private void usage() {
        Logger.elog(Logger.HIGH, "The arguments must be <Ticket> <PID> <MHOST> <MPORT> <ERRFILE> <OUTFILE>");
    }
}
