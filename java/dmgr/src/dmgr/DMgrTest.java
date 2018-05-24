//package dmgr;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayDeque;
//import java.util.Arrays;
//import java.util.Queue;
//import java.util.logging.Level;
//import se.util.Logger;
//
//public class DMgrTest {
//
//    static Trie dict;
//    static File tdir, cdir;
//    static SearchServer sserver;
//
//    public static void main(String[] args) throws IOException, InterruptedException {
//
//        tdir = new File("C:\\Users\\mpataki\\Documents\\se\\java\\dmgr\\tmp");
//        cdir = new File("C:\\Users\\mpataki\\Documents\\se\\java\\dmgr\\content");
//
//        String www = "C:\\Users\\mpataki\\Documents\\se\\java\\dmgr\\www";
//        String trieFile = "C:\\Users\\mpataki\\Documents\\se\\java\\dmgr\\dist\\dict.obj";
//        String dictFile = "C:\\Users\\mpataki\\Documents\\se\\java\\dmgr\\dict.txt";
//
//        try {
//            dict = Trie.restore(trieFile);
//        } catch (Exception ex) {
//            Logger.ilog(Logger.MEDIUM, "Trie not found");
//            dict = Trie.fromFile(dictFile);
//        }
//
//        int ts = 5;
//        Thread[] threads = new Thread[ts];
//        Queue<File> fque = new ArrayDeque<>();
//        sserver = new SearchServer(www, dict, cdir);
//        Logger.ilog(Logger.HIGH, "Search server process running on " + sserver.getPort());
//
//        new Thread(() -> {
//            try {
//                sserver.run();
//            } catch (IOException ex) {
//                java.util.logging.Logger.getLogger(DMgrTest.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }).start();
//
//        while (true) {
//            fque.addAll(Arrays.asList(tdir.listFiles()));
//            Logger.ilog(Logger.LOW, "Found [" + fque.size() + "] files");
//            if (fque.isEmpty()) {
//                try {
//                    Thread.sleep(20000);
//                } catch (InterruptedException ex) {
//                }
//            } else {
//                while (!fque.isEmpty()) {
//                    for (int i = 0; i < ts && !fque.isEmpty(); i++) {
//                        threads[i] = new Thread(new Indexer(dict, fque.poll(), cdir));
//                        threads[i].start();
//                    }
//                    for (int i = 0; i < ts; i++) {
//                        threads[i].join();
//                    }
//                }
//            }
//        }
//    }
//}
