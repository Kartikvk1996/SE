package dmgr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import se.util.Logger;

public class Indexer implements Runnable {

    Trie dict;
    final File file;
    final File cdir;

    public Indexer(Trie dict, File file, File cdir) {
        this.dict = dict;
        this.file = file;
        this.cdir = cdir;
    }

    @Override
    public void run() {
        try {
            index(file);
        } catch (FileNotFoundException ex) {
            Logger.elog(Logger.MEDIUM, "File not found. " + ex.getMessage());
        } catch (IOException ex) {
            Logger.elog(Logger.MEDIUM, "File read exception. " + ex.getMessage());
        }
    }

    private void index(File file) throws FileNotFoundException, IOException {

        Logger.ilog(Logger.LOW, "Indexing " + file.getAbsolutePath());

        /* word: frequency */
        HashMap<Integer, Integer> word2freq = new HashMap<>();
        Long docid = Long.parseLong(file.getName());

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        Pattern pattern = Pattern.compile("[A-Za-z]+");

        try {
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    Integer key = dict.get(matcher.group()).id;
                    if (word2freq.containsKey(key)) {
                        word2freq.put(key, word2freq.get(key) + 1);
                    } else {
                        word2freq.put(key, 1);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.elog(Logger.MEDIUM, "Error reading input file. " + file.getName());
        }

        br.close();

        for (Map.Entry<Integer, Integer> entry : word2freq.entrySet()) {
            appendDoc(entry.getKey(), new IndexWrapper(docid, new Index(entry.getValue())));
        }

        file.delete();
    }

    private void appendDoc(Integer uniqWord, IndexWrapper windex) {

        HashMap<Long, Index> docs;
        File wfile = new File(cdir, "" + uniqWord);

        try {
            if (!wfile.exists()) {
                wfile.createNewFile();
                docs = new HashMap<>();
            } else {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(wfile))) {
                    docs = (HashMap<Long, Index>) ois.readObject();
                }
            }
            if (docs.containsKey(windex.docid)) {
                docs.get(windex.docid).frequency += windex.index.frequency;
            } else {
                docs.put(windex.docid, windex.index);
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(wfile))) {
                oos.writeObject(docs);
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.elog(Logger.MEDIUM, "Couldn't add follow to the file. " + ex.getMessage());
        }
    }
}
