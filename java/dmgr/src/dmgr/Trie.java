package dmgr;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Trie implements Serializable {

    public class NodeData implements Serializable {

        int id;
        long freq;

        public NodeData(int id, long freq) {
            this.id = id;
            this.freq = freq;
        }

        @Override
        public String toString() {
            return "[ id : " + id + ", freq: " + freq + " ]";
        }
    }

    public class Node implements Serializable {

        Node[] ptrs;
        NodeData data;

        Node(NodeData data) {
            this.data = data;
            ptrs = new Node[26];
        }
    }

    int lastid;
    Node root;

    Trie() {
        lastid = 1;
        root = new Node(null);
    }

    public static Trie fromFile(String fileName)
            throws FileNotFoundException, IOException {
        Trie trie = new Trie();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        Pattern pattern = Pattern.compile("[A-Za-z]+");

        while ((line = br.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                trie.insert(matcher.group());
            }
        }
        return trie;
    }

    public NodeData insert(String word) {
        word = word.toLowerCase();
        Node cur = root;
        int len = word.length();
        for (int i = 0; i < len; i++) {
            int index = word.charAt(i) - 'a';
            if (cur.ptrs[index] == null) {
                cur.ptrs[index] = new Node(null);
            }
            cur = cur.ptrs[index];
        }
        if (cur.data == null) {
            cur.data = new NodeData(lastid++, 1);
        }
        return cur.data;
    }

    public NodeData get(String word) {
        word = word.toLowerCase();
        Node cur = root;
        int len = word.length();
        for (int i = 0; cur != null && i < len; i++) {
            cur = cur.ptrs[word.charAt(i) - 'a'];
        }
        NodeData data = cur == null ?
                insert(word) :
                cur.data;
        if(data == null) {
            data = cur.data = new NodeData(lastid++, 0); //frequency increment next line
        }
        data.freq++;
        return data;
    }

    public void print() {
        printx(root);
    }

    private int depth;

    private void printx(Node node) {
        depth++;

        if (node.data != null) {
            System.out.print(node.data);
        }
        System.out.println();

        for (int i = 0; i < 26; i++) {
            if (node.ptrs[i] != null) {
                for (int j = 0; j < depth; j++) {
                    System.out.print("-");
                }
                System.out.print((char) (i + 'a') + " ");
                printx(node.ptrs[i]);
            }
        }
        depth--;
    }

    public void exportDict() {
        exportDictx(root);
    }
    int ind = 0;
    char buf[] = new char[256];

    public void exportDictx(Node node) {
        ind++;
        if (node.data != null) {
            for (int i = 1; i < ind; i++) {
                System.out.print(buf[i]);
            }
            //System.out.print(node.data);
            System.out.println();
        }
        for (int i = 0; i < 26; i++) {
            if (node.ptrs[i] != null) {
                buf[ind] = (char) (i + 'a');
                exportDictx(node.ptrs[i]);
            }
        }

        ind--;
    }

    /**
     * Serializes this trie and saves this instance
     *
     * @param fileName : This is where the tried is saved
     * @throws IOException
     */
    public void save(String fileName) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(fileName));
        oos.writeObject(this);
    }

    /**
     * Returns a saved instance of trie
     *
     * @param fileName : The file where the trie is stored
     * @return : the store instance
     * @throws FileNotFoundException : If file is not present
     * @throws IOException
     * @throws ClassNotFoundException : If you don't have this Trie class in
     * classpath
     */
    public static Trie restore(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
        return (Trie) ois.readObject();
    }
}
