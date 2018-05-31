/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import sun.security.util.Length;

/**
 *
 * @author Anarghya
 */
public class MajorProjectSearchEngineConjecture  {

    /**
     * @param args the command line arguments
     */
    static int count = 0;
    static String query;
    static File f;
    FileOutputStream fs;
    private static final short windowSize=3;
    public node Root = new node();
    public static Stack<Character> bucket=new Stack<Character>();
    public static int size = 0;
    public static boolean searchFlag =false;
    public static ArrayList<wordVec> wordArray=new ArrayList<wordVec>();
    private int uid=0;
    public static int dictionarySize=0;
    public  HashMap< String,Integer> wordList=new HashMap<String,Integer>();
    public HashMap<String,wordVec> wordVectors=new HashMap<String,wordVec>();
    private contextWord context=new contextWord();
    private mainWord main=new mainWord();
    ObjectOutputStream oos;
    
    
    public static void main(String[] args) throws IOException {

        MajorProjectSearchEngineConjecture m = new MajorProjectSearchEngineConjecture();

        m.buildDictionary();
        
//        m.train();
m.readVectors();
m.test();

    }
    
    public void test(){
    boolean read=false;
        while(true){
    String test;
    Scanner sc=new Scanner(System.in);
            System.out.println("Enter the word:");
    test=sc.next();
    
        wordVec mainword=wordVectors.get(test);
        double distance=0;
        String word[]=new String[10];
        double minDistance=Double.MAX_VALUE;
        
       // System.out.println("size"+m.wordArray.size());
       
       
        for(wordVec w: wordVectors.values()){
            size=size+1;
           // System.out.println("size"+size);
            //wordVec contextWordVector=wordArray.get(size);
          //  System.out.println("wordVec:");w.display();
            distance= euclidianDistance(mainword.get(), w.get());
            
            if(distance<minDistance&&!test.equals(w.keyWord)){
                word[0]=w.keyWord;
            
            minDistance=distance;
            }
        
        }
        minDistance=Double.MAX_VALUE;
        for(wordVec w: wordVectors.values()){
        distance=euclidianDistance(mainword.get(), w.get());
        
        if(!w.keyWord.equals(word[0])&&distance<minDistance&&!test.equals(w.keyWord))
        {word[1]=w.keyWord;
        minDistance=distance;
        }
        }
        
        minDistance=Double.MAX_VALUE;
                for(wordVec w: wordVectors.values()){
        
                    
                distance=euclidianDistance(mainword.get(), w.get());
        
        if(!w.keyWord.equals(word[1])&&!w.keyWord.equals(word[0])&&distance<minDistance&&!test.equals(w.keyWord))
        {word[2]=w.keyWord;
        minDistance=distance;
        }
        }
        
      
        System.out.println("test word:"+test+"\nWORD1:"+word[0]+"\nWORD2:"+word[1]+"\nWORD3:"+word[2]);
        } 
    }
    
    
    public double add(double[] arg2, double[] dotProduct){
    double sum=0;
    for(int i=0;i<wordVec.dimension;i++){
    sum+=dotProduct[i]+arg2[i];
    }
    return sum;
    }
    
    
    public double euclidianDistance(double[] vecA,double[] vecB){
        
        double distance=0;
        double difference,ms=0;
        for(int i=0;i<wordVec.dimension;i++){
    difference=vecA[i]-vecB[i];
    difference=difference*difference;
    ms+=difference;difference=0;
        
        }
        distance=Math.pow(ms, 0.5);
        return distance;
    
    
    }
    
    public void buildDictionary() {
        Root.setChar('*');
       String s = "";
       
            String currentDirectory = "";
 
                
                String path = currentDirectory+"ex.txt";
                try {
                    BufferedReader br = new BufferedReader(new FileReader(path));
                    while ((s = br.readLine()) != null) {
                        if (s.matches("[a-zA-Z]+")) {
                             s=s.toLowerCase();
                             wordList.put(s,uid++);
                           
                          // System.out.println(wordList.get(s));
                            buildTree(Root, s);
                        }
                    }
                    // System.out.print("INDEX is:"+wordList.get("how"));
                   // System.out.print(wordList.size());  
                } catch (IOException ex) {
                    System.err.print(ex);
                }
                   
                  dictionarySize=wordList.size();
                try {
                     {
                         serialize(wordList,"Dictionary.ser" );
            
                    }
                  //  traceTree(Root, 0);
                   // System.out.println(wordList.size());
                   
                   // search(Root, word, word.length());
                   //System.out.println(wordList.get("galaxy"));
                    if (searchFlag) {
                       // System.err.println("FOUND:");
                    } else {
                       // System.err.println("NOT FOUND:");
                    }
                    //System.out.println("COUNT:"+count);
                } catch (Exception t) {
                    System.err.println("ERROR:" + t);
                    t.printStackTrace();
                }
                

    }
    
    public void train() throws IOException{
    
     BufferedReader br=null;
         String line=null;
         String []sentence=null;
        String[]words=null;
         String filename="wiki.txt";
         int index;
         int keeper;
        context.init();
         Document doc=new Document(filename,"1220");
         ArrayList<String> sentences=new ArrayList<String>();
         ArrayList<wordVec> nearbyWords=null;nearbyWords=new ArrayList<wordVec>();
        
      int j=0;
      
       try{
        br= new BufferedReader(new FileReader(filename));
       
       do{//for the entire corpus
          
        line=br.readLine();
        
           
           if(line!=null){
               line=line.replace("[_-]", " ");
               System.err.println("LINE:"+line);
           sentence=line.split("[.,!?]");}
           
           for(int numOfsentencesinLIne=0;numOfsentencesinLIne<sentence.length;numOfsentencesinLIne++){
               sentence[numOfsentencesinLIne]=sentence[numOfsentencesinLIne].trim();
               System.err.println("SENTENCE:"+sentence[numOfsentencesinLIne]);
          
         words=sentence[numOfsentencesinLIne].split(" ");
               
           for(short check=0; check<words.length;check++){
                     words[check]=words[check].toLowerCase();
                     words[check]=words[check].trim();
                     words[check]=words[check].replaceAll("[^a-zA-Z]", "");
                   // System.err.println("word is:"+words[check]);
                    searchFlag=false;
                    if(words[check].length()>=1)
                    {
                        search(Root, words[check], words[check].length());
                    if(!searchFlag)
                    {
                        addWord(words[check],sentence[numOfsentencesinLIne]);//System.err.println("ADDED:"+words[check]);
                    }
                    }
           
           }
           
           
         
         for(int numOfwordsinSentence=0;numOfwordsinSentence<words.length&&words[numOfwordsinSentence].length()>=1;numOfwordsinSentence++){
     
             System.out.println("WORD:"+words[numOfwordsinSentence]);
        words[numOfwordsinSentence]=words[numOfwordsinSentence].replaceAll("[.,!-]", ""); words[numOfwordsinSentence]=words[numOfwordsinSentence].toLowerCase();
        //sentences.add(words[numOfwordsinSentence]);// adding the words in the sentence
          for(short nearby=-1;nearby<2;nearby+=2)  {
             for(short multiplier=1;multiplier<=2;multiplier++){
                   if(numOfwordsinSentence+nearby*multiplier>=0&&words.length>numOfwordsinSentence+nearby*multiplier){sentences.add(words[numOfwordsinSentence+nearby*multiplier]);
                   System.err.println("word:"+words[numOfwordsinSentence]+"\tadded:"+words[numOfwordsinSentence+nearby*multiplier]);
                   }
             }
          } 
          
             
             //Search the mainword line[numOfwordsinSentence] in dictionary, if found extract vector from mainword
             search(Root, words[numOfwordsinSentence], words[numOfwordsinSentence].length(),doc);
            
             
             if(!searchFlag){
                 //word not found in dictionary 
                 
                 //do some wornumOfwordsinSentence here to find if word to be added or not
                 //System.err.println("added:"+words[numOfwordsinSentence]);
                 addWord(words[numOfwordsinSentence],sentence[numOfsentencesinLIne]);
             }
             
             if(searchFlag){
                 searchFlag=false;}
            double sum[]=new double[wordVec.dimension];
            main.init();
             //System.err.println("word is:"+words[numOfwordsinSentence]);
             wordVec mainword=null;
             
              
                
                mainword=main.getVec(wordList.get(words[numOfwordsinSentence]));
            
             //Extract the rest of context word vectors
          // System.err.println("====Mainword====:"+words[numOfwordsinSentence]);
             for(String str : sentences){
                
                 
                //System.err.println("size:"+wordList.size());    
                 if(!str.equals(words[numOfwordsinSentence])){
                  //  System.err.println("context word:"+str);
                    
                    try{ int i=wordList.get(str);nearbyWords.add(context.getVec(i));
                    }
                    catch(NullPointerException e)
                    {addWord(str, sentence[numOfsentencesinLIne]);
                    int i=wordList.get(str);
                    nearbyWords.add(context.getVec(i));
                    }
                     
                     for(wordVec contextword: nearbyWords){
              //           System.err.println("contextword:");contextword.display();System.err.println("\tmainword:");mainword.display();
                     double dotProduct=contextword.product(mainword.get());
                     dotProduct=exp(dotProduct);
                    // sum=add(sum,dotProduct);
                     
                     
                     }                  
             //call the backpropagation algorithm
             
           
         
                 }}sentences.clear();
         
         
         
         }}
         words=null;
           
       }while(line!=null);
           
       }
       
    
       
       catch(Exception e){System.err.println(e);e.printStackTrace();}
       
       
       
     //   traceTree(Root, 0);
       
       
        
        
        // TODO code application logic here
    
    
    
    }

    public boolean search(node root, String str, int length) {
        str=str.toLowerCase();
        int position = str.charAt(0) - 'a';
      
        node child = null;
        //System.err.println(str);
        try {
            child = root.getChild(position);//System.err.println("child char:"+child.getChar());
        } catch (NullPointerException ex) {
            searchFlag=false;
            bucket.pop();
            return false;
            //Handling some memory related exceptions
        }
        catch(Exception r){r.printStackTrace();}

        if (length > 0 && child != null) {
            
            //String still up for comparison
            if (child.getChar() != str.charAt(0)) {
                
                //some weird behaviour pre-emptive handling
                System.err.println("mismatch");
                searchFlag = false;
                return false;
            }
            //normal comparsion continues char by char
           // System.err.println("STRING:"+str+" LENGTH:"+length);
            length = length - 1;
            bucket.push(child.getChar());
            if (length == 0) {
                if (child.end) {
                    searchFlag=true;if(!bucket.empty())bucket.pop();
                   // traceTree(child, 0);
                    return true;
                    //String found case
                }
            }
            //Continue searching incase of String not compared completely
            else if (length>0){ if(search(child, str.substring(1), length)) {
                
                return true;
            }
            else{
            //Return false on search unsuccessful
            if(!bucket.empty())bucket.pop();
     //       traceTree(child, 0);
            return false;}}
        }
        //This traces the string from point of mismatch, so we get 
        
        if(!bucket.empty())bucket.pop();
        //return just to keep compiler quiet
        return false;

    }
    
    public boolean search(node root, String str, int length,Document d) {
        str=str.toLowerCase();
        int position = str.charAt(0) - 'a';
      
        node child = null;
        //System.err.println(str);
        try {
            child = root.getChild(position);//System.err.println("child char:"+child.getChar());
        } catch (NullPointerException ex) {
            searchFlag=false;
            bucket.pop();
            return false;
            //Handling some memory related exceptions
        }
        catch(Exception r){r.printStackTrace();}

        if (length > 0 && child != null) {
            
            //String still up for comparison
            if (child.getChar() != str.charAt(0)) {
                
                //some weird behaviour pre-emptive handling
                System.err.println("mismatch");
                searchFlag = false;
                return false;
            }
            //normal comparsion continues char by char
           // System.err.println("STRING:"+str+" LENGTH:"+length);
            length = length - 1;
            bucket.push(child.getChar());
            if (length == 0) {
                if (child.end) {
                    searchFlag=true;if(!bucket.empty())bucket.pop();
                    child.addDoc(d);
                   // traceTree(child, 0);
                    return true;
                    //String found case
                }
            }
            //Continue searching incase of String not compared completely
            else if (length>0){ if(search(child, str.substring(1), length)) {
                
                return true;
            }
            else{
            //Return false on search unsuccessful
            if(!bucket.empty())bucket.pop();
     //       traceTree(child, 0);
            return false;}}
        }
        //This traces the string from point of mismatch, so we get 
        
        if(!bucket.empty())bucket.pop();
        //return just to keep compiler quiet
        return false;

    }

    public void traceTree(node root, int height) {
        if (root == null) {
            
            return;
        }
       
        String st = " ";
        if(root!=Root){
        bucket.push(root.getChar());}
        
        if(root.end){System.out.println(bucket.toString());//root.docs.toString();
        }for (int i = 0; i < 26; i++) {

            traceTree(root.getChild(i), height + 1);

        }
        if(!bucket.empty())bucket.pop();
    }

    private void buildTree(node root, String str) {
        if (str.length() == 0) {
            root.end = true;
            return;
        }
       // System.err.println(str);
        int pos = 0;
        
        try {
            if (str.length() >= 1) {
                str = str.toLowerCase();
                pos = str.charAt(0) - 'a';
                if (root.getChild(pos) == null) {
                    node child = new node();
                    if (root.addNode(str.charAt(0) - 'a', str.charAt(0), child)) {
                        count++;
                    }
                    String temp = new String(str.substring(1));
                    buildTree(child, temp);
                } else {

                    String temp = new String(str.substring(1));
                    buildTree(root.getChild(pos), temp);
                }
            } else {
                if (root.setChar(str.charAt(0))) {
                    str.charAt(0);
                }
                root.flagEnd();
                count++;
                root.flagEnd();
            }
            
           
        } catch (ArrayIndexOutOfBoundsException ex) {
        } catch (Exception er) {
            System.err.println("ERROR:" + er + "Character is" + str.charAt(0));
        }

    }

    
    public void readVectors() throws FileNotFoundException, IOException{
    
    String fileName="vectors.txt";
        BufferedReader br =new BufferedReader(new FileReader(fileName));
        String line=null;
        int read=0;
        wordVec w;int count=0;
        int s=0;
        try{
        while ((line = br.readLine()) != null&&read<1000000) {

                read++;
               // use comma as separator
                String[] country = line.split(" ");
double [] temp=new double[wordVec.dimension];
                for(int i=1;i<301;i++){
                   // System.err.print(country[i]+",");
                    temp[i-1]=Double.valueOf(country[i]);
                }++count;
               // System.err.println(count);
                w=new wordVec();w.init(country[0],temp);
                wordList.put(country[0], count);
                wordVectors.put(country[0], w);
                wordArray.add(w);
               
               // System.out.println("added");wordArray.get(count-1).display();

            }
        
        System.out.println("VECTORS LOADED");
        
    }catch(OutOfMemoryError e){System.out.println("Number of vectors read="+read);
}}
   
    public int keyHash(String str) {
        //This will result to a hash value based onthe charaacters preesnrt in the string

        return 1;
    }
  
    double  exp(double arg){
  
  
  return Math.exp(arg);
  }
    
    
    public boolean addWord(String word,String sentence){
    String path="";
    File file=new File("newWords.txt");
    try{
    FileWriter fw=new FileWriter(file, true);
    //new added words output to a new 
    fw.write(word+"\n");
    fw.close();
      //  System.out.println("word:"+word+"\tsentence"+sentence);
   try{
       int index=wordList.get(word);
   }catch(NullPointerException e){wordList.put(word, wordList.size());
   
   main.addWord();
    context.addWord();
    }
    
        //System.err.println(wordList.get(word));
    }catch(Exception t){t.printStackTrace();return false;}
    
   return true; }

        
    public void serialize (Object obj,String filename){
    FileOutputStream fs;
         ObjectOutputStream oos;
         try{
          fs=new FileOutputStream(filename);
          oos=new ObjectOutputStream(fs);
          oos.writeObject(obj);
          }catch(Exception e){e.printStackTrace();}
    
    }
    
    public void deSerialize(Object obj,String filename,Class objClassName){
    FileInputStream fs;
         ObjectInputStream oos;
         try{
          fs=new FileInputStream(filename);
        oos=new ObjectInputStream(fs);
        obj=oos.readObject();
         obj=objClassName.cast(obj);}catch(Exception e){e.printStackTrace();}
    
    }
   
    
}

class node {

    private node pointers[] = new node[26];
    private char character;
    public ArrayList<Document> docs=null;
    public boolean end;

    public boolean addNode(int position, char c, node child) {

        pointers[position] = child;
        pointers[position].setChar(c);
        return true;
    }

    public void flagEnd() {
        this.end = true;
    }

    public char getChar() {
        return this.character;
    }

    public boolean setChar(char c) {
        this.character = c;
        return true;
    }

    public node getChild(int position) {
        return this.pointers[position];
    }
    
    public void addDoc(Document d){
        
        
       try{ docs.add(d);}catch(Exception e){System.err.println("Could not add doc to the word");}
    
    
    }

}

 class wordVec {
    
    private double []word;
   static int dimension=300;
   String keyWord=null;
    public void init()
    {
        word =new double[dimension];
        for(int i=0;i<dimension;i++)word[i]=(float) (1);
    }
    
    public void display(){for(int i=0;i<dimension;i++)System.err.print(word[i]+",");System.err.print("\n");}
    
    public double[] get(){return word;}
    
    public double product(double[] vecB){
    double c=0;
    for(int i=0;i<dimension;i++){c+=word[i]*vecB[i];}
    return c;
    }
    
    public void init(String keyword,double[] fromFile){this.word=fromFile;this.keyWord=keyword;}
    
    
}

 class mainWord {
    
    private int noOfwords;
    private static ArrayList<wordVec> words=new ArrayList<wordVec>();
    //this matrix contains vectors of all words when they are mainword.
    //This is referenced by dictionary
    public void init()
    
    { wordVec temp;
    //initialize no of words
     noOfwords=MajorProjectSearchEngineConjecture.dictionarySize;
     
     
     //Initialize the matrix
     for(int i=0;i<noOfwords;i++)
     {
         temp=new wordVec();
         temp.init();
         words.add(temp);
     }
        
        
        
    }
    
        public wordVec getVec(int index){
    return words.get(index);
    
    }
        public void addWord(){
            wordVec word=new wordVec();
            word.init();
        
        this.words.add(word);
        }
        
      
    
}


 class contextWord {
    private int noOfwords;
    private static ArrayList<wordVec> words=new ArrayList<wordVec>();
    //this matrix contains vectors of all words when they are contextword.
    //creation of 
    public void init()
    
    {
        wordVec temp;
        double [] t={1,1,1,1,1};
        
    //initialize noOfwords
        noOfwords=MajorProjectSearchEngineConjecture.dictionarySize;
        //Initialize the matrix
         for(int i=0;i<noOfwords;i++)
     {
         temp=new wordVec();
         
         temp.init();
//        t=temp.product(t);
         for(int j=0;j<5;j++)//System.out.print(t[j]);
         words.add(temp);
     }
        
    }
    
    public void addWord(){
            wordVec word=new wordVec();
            word.init();
        
        this.words.add(word);
        }
    public wordVec getVec(int index){
    return words.get(index);
    
    }
    
   

 }



