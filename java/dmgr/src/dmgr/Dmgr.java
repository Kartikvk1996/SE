package dmgr;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import se.dscore.SlaveProcess;
import se.dscore.SlaveProcessConfiguration;
import se.ipc.ESocket;
import se.ipc.pdu.PDU;
import se.ipc.pdu.PDUConsts;
import se.ipc.pdu.SearchPDU;
import se.util.Logger;

public class Dmgr extends SlaveProcess {

    static DMgrConfiguration conf;
    static Trie dictionary, rdictionary;
    static File tdir, cdir;

    Dmgr(SlaveProcessConfiguration spc) throws IOException {
        super(spc);
    }

    @Override
    public void handler(ESocket socket, PDU pdu) throws IOException {
        switch (pdu.getMethod()) {
            case PDUConsts.METHOD_SEARCH:
                SearchPDU spdu = (SearchPDU) pdu;
                Logger.ilog(Logger.LOW, "Search request for [" + spdu.getQuery() + "]");
                ReverseIndexer rin = new ReverseIndexer(
                        rdictionary,
                        spdu.getQuery(),
                        cdir
                );
                rin.start();
                try {
                    rin.join();
                } catch (InterruptedException ex) {
                }
                SearchResult res = new SearchResult(rin.getDocs());
                new ObjectOutputStream(socket.getOutputStream()).writeObject(res);
                break;
            default:
                super.handler(socket, pdu);
        }
    }

    public static void main(String[] args) throws IOException {

        /**
         * 127.0.0.1 60834 192.168.56.1 60834 dmgr.conf new
         * DMgrConfiguration().generateSample("dmgr.conf"); System.exit(0); /*
         */
        Logger.ilog(Logger.HIGH, "The arguments must be <Ticket> <PID> <ERRFILE> <OUTFILE> <MHOST> <MPORT> [configFile]");
        System.out.println(Arrays.toString(args));

        conf = (DMgrConfiguration) ((args.length > 6)
                ? new DMgrConfiguration(args[6])
                : new DMgrConfiguration());

        conf.setTicket(args[0]);
        conf.setPid(args[1]);
        conf.setErrFile(args[2]);
        conf.setOutFile(args[3]);
        conf.setMasterHost(args[4]);
        conf.setMasterPort(Integer.parseInt(args[5]));
        conf.setProcessRole("dmgr");

        Dmgr dmgr = new Dmgr(conf);
        dmgr.run();
        Logger.ilog(Logger.HIGH, "search server [not http] process running on " + dmgr.getPort());

        tdir = new File(conf.getTempDirectory());
        cdir = new File(conf.getContentDirectory());
        try {
            dictionary = Trie.restore(conf.getTrieFile());
        } catch (Exception ex) {
            Logger.ilog(Logger.MEDIUM, "Trie not found");
            dictionary = Trie.fromFile(conf.getDictionaryFile());
        }

        rdictionary = new Trie(dictionary, false);
        
        Queue<File> fque = new ArrayDeque<>();

        while (true) {
            try {
                Thread.sleep(conf.getScanInterval());
            } catch (InterruptedException ex) {
            }

            Logger.ilog(Logger.LOW, "Scanning started");
            fque.addAll(Arrays.asList(tdir.listFiles()));
            Logger.ilog(Logger.LOW, "Found [" + fque.size() + "] new files");

            for (int i = 0; i < conf.getScannersCount() && !fque.isEmpty(); i++) {
                new Thread(new Indexer(dictionary, fque.poll(), cdir)).start();
            }
        }
    }

    @Override
    public void deinit() {
        try {
            dictionary.save(conf.getTrieFile());
        } catch (IOException ex) {
            Logger.elog(Logger.HIGH, "Error saving the trie file");
        }
    }

}
