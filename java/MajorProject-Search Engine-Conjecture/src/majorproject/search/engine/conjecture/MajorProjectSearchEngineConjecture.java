/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package majorproject.search.engine.conjecture;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
    private int uid=0;
    public static int dictionarySize=0;
    public  HashMap< String,Integer> wordList=new HashMap<String,Integer>();
    private contextWord context=new contextWord();
    private mainWord main=new mainWord();
    public static void main(String[] args) {

        MajorProjectSearchEngineConjecture m = new MajorProjectSearchEngineConjecture();

        m.buildDictionary();
        
        m.train();
        
    }
    ObjectOutputStream oos;
    
    public float[] add(float [] arg1,float [] arg2){
    float sum[]=new float[wordVec.dimension];
    for(int i=0;i<wordVec.dimension;i++){
    sum[i]=arg1[i]+arg2[i];
    }
    
    
    return sum;
    }
    
    public float add(float arg1,float arg2){
    float sum=0;
    sum=arg1+arg2;
    return sum;
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
    
   /* public float[] div(float [] numerator,float[] denominator)
    {
        float res[]=new float[numerator.length];
        for(int iterator=0;iterator<numerator.length;i++)
            res[iterator]=numerator[iterator]/
    
    }*/
    public void train(){
    
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
              // System.err.println("LINE:"+line);
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
     
           //  System.out.println("WORD:"+words[numOfwordsinSentence]);
        words[numOfwordsinSentence]=words[numOfwordsinSentence].replaceAll("[.,!-]", ""); words[numOfwordsinSentence]=words[numOfwordsinSentence].toLowerCase();
        //sentences.add(words[numOfwordsinSentence]);// adding the words in the sentence
          for(short nearby=-1;nearby<2;nearby+=2)  {
             for(short multiplier=1;multiplier<=2;multiplier++){
                   if(numOfwordsinSentence+nearby*multiplier>=0&&words.length>numOfwordsinSentence+nearby*multiplier){sentences.add(words[numOfwordsinSentence+nearby*multiplier]);
                  // System.err.println("word:"+words[numOfwordsinSentence]+"\tadded:"+words[numOfwordsinSentence+nearby*multiplier]);
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
            float expectency[]=new float[wordVec.dimension];
            main.init();
             //System.err.println("word is:"+words[numOfwordsinSentence]);
             wordVec mainword=null;
             float []sum = null;
             float gradient[]=null;
              
                //get the center word vector
                mainword=main.getVec(wordList.get(words[numOfwordsinSentence]));
            
             //Extract the rest of context word vectors
          // System.err.println("====Mainword====:"+words[numOfwordsinSentence]);
             for(String str : sentences){
                
                 
                //System.err.println("size:"+wordList.size());    
                 if(!str.equals(words[numOfwordsinSentence])){
                   System.err.println("context word:"+str);
                    
                     int indexOfContextwords=wordList.get(str);
                     
                     //add the vectors to be updated
                     nearbyWords.add(context.getVec(indexOfContextwords));
                  
                     
                     for(wordVec contextword: nearbyWords){
                         float[] numerator,denominator;
                        // denominator=denominator(mainword);
                         for(int cindex=0;cindex <nearbyWords.size();cindex++){
              //           System.err.println("contextword:");contextword.display();System.err.println("\tmainword:");mainword.display();
                     float dotProduct=contextword.product(mainword.get());
                    // dotProduct=exp(dotProduct);
                     //numerator=product(dotProduct, contextword.get());
                   //  sum=add(sum,numerator);
                     } 
                   //  gradient=divide(sum, denominator);
                     
                     }                 
            
             
           
         
                 }}sentences.clear();
         
         
         
         }}
         words=null;
           
       }while(line!=null);
           
       }
       
    
       
       catch(Exception e){System.err.println(e);e.printStackTrace();}
       
       
       
     //   traceTree(Root, 0);
       
       
   
    
    
    
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
   
    public float dotproduct(float []arg1, float[] arg2)
    {
    float res=0;
    for(int dim=0;dim<arg1.length;dim++){
    res=arg1[dim]* arg2[dim];
    }
    return res;
    
    
    }
    
    public float dotproduct(float[]arg1,wordVec word)
    
    {
    float arg2[]=word.get();
    float res=0;
    for(int dim=0;dim<arg1.length;dim++)
    {
    res+=arg1[dim]*arg2[2];
    
    }
    return res;
    }
    
    public float[] divide(float[]arg1,float[]arg2){
    
    float[]res=null;
    for(int size=0; size<arg1.length;size++){
    res[size]=arg1[size]/arg2[size];
    }
    return res;
    }
    
    
    public float[] difference(float[]arg1, float[] arg2){
    
    float res[]=null;
    for(int size=0; size<arg1.length;size++){
    
     res[size]=arg1[size]-arg2[size];
    }
    return res;
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

    public int keyHash(String str) {
        //This will result to a hash value based onthe charaacters preesnrt in the string

        return 1;
    }
  
    float  exp(float arg){
  
  
  arg=(float) Math.exp(arg);
  
  
  return arg;
  }
    
    
    public boolean addWord(String word,String sentence){
    String path="";
    File file=new File("newWords.txt");
    try{
    FileWriter fw=new FileWriter(file, true);
    //new added words output to a new 
    fw.write(word+"\n");
    fw.close();
        buildTree(Root, word);
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
    
   public float denominator(wordVec mainword){
       
       
float dotProduct=0;
float  sum=0;

for(int i=0;i<MajorProjectSearchEngineConjecture.dictionarySize;i++){
      dotProduct=mainword.product(context.getVec(i).get());
      dotProduct=exp(dotProduct);
      sum=add(sum,dotProduct);
}

return sum;}
    
}

class node {

    private node pointers[] = new node[26];
    private char character;
    public ArrayList<Document> docs=null;
    static int index;
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
    
    private float []word;
   static int dimension=50;
   
    public void init()
    {
        word =new float[dimension];
        for(int i=0;i<dimension;i++){word[i]=(float) (1/(1+Math.exp((Math.random()))));}
    }
    
    public void display(){for(int i=0;i<dimension;i++)System.err.print(word[i]+",");System.err.print("\n");}
    
    public float[] get(){return word;}
    
    public float product(float[] vecB){
    float c=0;
    for(int i=0;i<dimension;i++){c+=word[i]*vecB[i];}
    return c;
    }
    
    
    
    
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
        
        
    //initialize noOfwords
        noOfwords=MajorProjectSearchEngineConjecture.dictionarySize;
        //Initialize the matrix
         for(int i=0;i<noOfwords;i++)
     {
         temp=new wordVec();
         
         temp.init();
        
        // for(int j=0;j<;j++)//System.out.print(t[j]);
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

class Document{
private String url=null;
public String docid=null;
private String date;
boolean isCrawled=false;
static int rank;
public Document(String url,String docid){this.url=url;this.docid=docid;}

public Document(String url,String docid,String date){this.url=url;this.docid=docid;this.date=date;isCrawled=true;}


}