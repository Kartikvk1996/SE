package dmgr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import se.util.Logger;

public class ReverseIndexer extends Thread {

    File cdir;
    Trie trie;
    String words[];
    HashSet<Index> resultSet;

    public ReverseIndexer(Trie trie, String words, File cdir) {
        this.trie = trie;
        this.words = words.split("[ \t\n\r]+");
        this.cdir = cdir;
    }
    
    @Override
    public void run() {
        resultSet = new HashSet<>();
        for (String word : words) {
            int wid = trie.get(word).id;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(cdir, wid + "")))) {
                if(resultSet.isEmpty())
                    resultSet.addAll((HashSet<Index>)ois.readObject());
                else
                    resultSet.retainAll((HashSet<Long>)ois.readObject());
            } catch (IOException | ClassNotFoundException ex) {
                Logger.elog(Logger.MEDIUM, "Error reading " + wid + " " + ex.getMessage());
            }
        }
    }
    
    public HashSet<Index> getDocs() {
        return resultSet;
    }
}
