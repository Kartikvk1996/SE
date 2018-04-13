/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wordp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import majorproject.search.engine.conjecture.MajorProjectSearchEngineConjecture;
/**
 *
 * @author akjantal
 */
public class WordP {
private static int windowSize=4;
    /**
     * @param args the command line arguments
     */
   static MajorProjectSearchEngineConjecture m=new MajorProjectSearchEngineConjecture();
    public static void main(String[] args) {
         BufferedReader br=null;
         String temp=null;
         String[]line=null;
         int index;
         int keeper;
         HashMap<String,Integer> wordList;
         
         ArrayList<String> sentences=new ArrayList<String>();
         
        
      int j=0;
       try{
        br= new BufferedReader(new FileReader("sample.txt"));
       
        //System.out.println(wordList.size());
           //System.err.println(wordList.get("galactic"));
       {//for the entire corpus
          
        temp=br.readLine();
           
           try{
         line=temp.split(" ");}catch(NullPointerException e){}
           catch(Exception et){et.printStackTrace();}
           
         
         for(int k=0;k<line.length;k++){
     line[k].replaceAll("[^a-zA-Z]", "");
             
         sentences.add(line[k]);
             //System.out.println(line[k]);
         m.search(m.Root,line[k],line[k].length());
         
         //word exists in dictionary
         //    System.err.println("Found:"+line[k]);
            
         
         
         //all the calculation here
           
         //get the vector for the center word from mainword vector
          
         
         
         }
       }while(temp!=null);
           
       }
       
       catch(Exception e){System.err.println(e);e.printStackTrace();}
       
       
       
       
       
       
        
        
        // TODO code application logic here
    }
    
    
    
}
