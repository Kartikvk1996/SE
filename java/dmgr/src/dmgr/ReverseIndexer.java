package dmgr;

import dmgr.Trie.NodeData;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import se.util.Logger;

public class ReverseIndexer extends Thread {

    File cdir;
    Trie trie;
    String words[];
    HashMap<Long, Index> resultSet;

    public ReverseIndexer(Trie trie, String words, File cdir) {
        this.trie = trie;
        this.words = words.split("[ \t\n\r]+");
        this.cdir = cdir;
    }

    @Override
    public void run() {
        Logger.ilog(Logger.DEBUG, "Reverse indexing " + Arrays.toString(words));
        resultSet = new HashMap<>();
        for (String word : words) {
            NodeData nd = trie.get(word);
            int wid = nd == null ? -1 : nd.id;
            Logger.ilog(Logger.DEBUG, word + " found in dictionary? " + (wid != -1));
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(cdir, wid + "")))) {
                resultSet.putAll((HashMap<Long, Index>) ois.readObject());
            } catch (IOException | ClassNotFoundException ex) {
                Logger.elog(Logger.MEDIUM, "Error reading " + wid + " " + ex.getMessage());
            }
        }
    }

    public HashMap<Long, Index> getDocs() {
        return resultSet;
    }
}
