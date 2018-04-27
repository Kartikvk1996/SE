package se.dscore;

import java.io.IOException;
import jsonparser.JsonException;
import se.ipc.pdu.ConnectPDU;
import se.ipc.pdu.InvalidPDUException;
import se.ipc.pdu.StatusPDU;
import se.util.Logger;

public class SlaveProcess extends Process {

    public String pid;
    protected MasterProxy mproxy;
    protected String ticket;

    public SlaveProcess(SlaveProcessConfiguration config) throws IOException, ArrayIndexOutOfBoundsException {
        super(config);
        mproxy = new MasterProxy(config.getMasterHost(), config.getMasterPort());
        ticket = config.getTicket();
        pid = config.getPid();
        StatusPDU.setProcessDetails(ticket, pid);
    }

    @Override
    public void run() {

        /* report to the master that you are running on port X */
        try {
            ConnectPDU pdu = new ConnectPDU(ticket, pid, getPort());
            pdu.setEOFiles(getErrFile(), getOutFile());
            mproxy.send(pdu, true);
        } catch (IOException | JsonException | InvalidPDUException ex) {
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
}
