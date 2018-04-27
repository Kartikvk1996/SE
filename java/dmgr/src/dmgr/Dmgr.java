package dmgr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import se.dscore.SlaveProcess;
import se.dscore.SlaveProcessConfiguration;
import se.util.Logger;

public class Dmgr extends SlaveProcess {

    static DMgrConfiguration conf;
    static Trie dict;
    static File tdir, cdir;
    SearchServer sserver;
    

    Dmgr(SlaveProcessConfiguration spc) throws IOException {
        super(spc);
        sserver = new SearchServer(config.getTicket(), dict, cdir);
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
        conf.setMasterHost(args[4]);
        conf.setMasterPort(Integer.parseInt(args[5]));
        conf.setErrFile(args[2]);
        conf.setOutFile(args[3]);
        conf.setProcessRole("dmgr");

        Dmgr dmgr = new Dmgr(conf);
        dmgr.run();
        Logger.ilog(Logger.HIGH, "Search server process running on " + dmgr.getPort());
        
        tdir = new File(conf.getTempDirectory());
        cdir = new File(conf.getContentDirectory());
        try {
            dict = Trie.restore(conf.getTrieFile());
        } catch (Exception ex) {
            Logger.ilog(Logger.MEDIUM, "Trie not found");
            dict = Trie.fromFile(conf.getDictionaryFile());
        }

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
                new Thread(new Indexer(dict, fque.poll(), cdir)).start();
            }
        }
    }

    @Override
    public void deinit() {
        try {
            dict.save(conf.getTrieFile());
        } catch (IOException ex) {
            Logger.elog(Logger.HIGH, "Error saving the trie file");
        }
    }

}
