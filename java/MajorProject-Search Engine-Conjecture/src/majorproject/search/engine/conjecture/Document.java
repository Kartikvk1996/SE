/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package majorproject.search.engine.conjecture;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Occurs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Document{
private String url=null;
public String filename;
public String docid=null;
private String date;
boolean isCrawled=false;
HashMap<String, Integer>occurences=new HashMap<String, Integer>();
ArrayList<String> title = new ArrayList<String>();
ArrayList<String>  link = new ArrayList<String>();
ArrayList<String> header = new ArrayList<String>();
ArrayList<String> paragraph = new ArrayList<String>();
static final int titleCoefficient=10000;
static final int headCoefficient=1000;
static final int paraCoefficient=100;
static final int linkCoefficient=10;

private String dumpDirectory="C:\\Users\\akjantal\\Desktop\\Dump";
public Document(String url,String docid) throws FileNotFoundException, IOException{this.url=url;this.docid=docid;analyze();}

public Document(String url,String docid,String date) throws FileNotFoundException, IOException{this.url=url;this.docid=docid;this.date=date;isCrawled=true;analyze();}

public Document(String filename) throws FileNotFoundException, IOException{this.filename=dumpDirectory+"\\"+filename;isCrawled=true;Integer temp=(int) (Math.random()*10000);docid=temp.toString();analyze();}

public void analyze() throws FileNotFoundException, IOException{
    short segment=1;
String line=null;
Pattern p;
Matcher m;
String t[]=null;
String pg[]=null;
String h[]=null;
String l[]=null;

String titlePattern="(?i)(<\\s*title.*?>)(.+?)(<\\s*/title>)";
String headerPattern="(?i)(<\\s*h\\d.*?>)(.+?)(</h\\d>)";
String linkPattern="(?i)(<\\s*a\\s+href=\".*\"\\s*>)(.+?)(<\\s*/a>)";
String paraPattern="(?i)(<\\s*p.*?\\s*>)(.+?)(<\\s*/p>)";

    BufferedReader br = new BufferedReader(new FileReader(filename));
    
        
   line= new String(Files.readAllBytes(Paths.get(filename)));
    if(line==null)throw new ArrayIndexOutOfBoundsException();
    
   
      
    
    try{
    
        p=Pattern.compile(titlePattern);
        m=p.matcher(line);
        while(m.find())
        { 
            t=m.group(2).replaceAll("<.*>", "").split("\\s");
          for(int i=0;i<t.length;i++){title.add(t[i]);
          }
          
        
        }
       
        
        p=Pattern.compile(headerPattern);
        m=p.matcher(line);
        while(m.find()){h=m.group(2).replaceAll("<.*>", "").split("\\s");//System.out.println(m.group(2));
        for(int i=0;i<h.length;i++)header.add(h[i]);
        }
        
        p=Pattern.compile(linkPattern);
        m=p.matcher(line);
        while(m.find()){l=m.group(2).replaceAll("<.*>", "").split("\\s");
//System.out.println(m.group(2));}
for(int i=0;i<l.length;i++)link.add(l[i]);
        }            
             
        
        p=Pattern.compile(paraPattern);
        m=p.matcher(line);
        
        while(m.find()){pg=m.group(2).replaceAll("(<.*?>)|(</.*?>)", "").split("\\s");
            for(int i=0;i<pg.length;i++)paragraph.add(pg[i]);}
    
    }catch(ArrayIndexOutOfBoundsException sizeOut){
    //Do something else don't
    }
   // display();
 
   //Build the occurences matrix for the document
   buildOccurenceMatrix();
   
   //add Document ID to the list of key-occurence table

}
public void display(){
   
    System.out.println("===============TITLE============\n");
    for (Iterator<String> it = title.iterator(); it.hasNext();) {
        String tr = it.next();
        System.out.println(tr);
    }
    
    System.out.println("\n\n===============HEAD============\n");
     for (Iterator<String> it = header.iterator(); it.hasNext();) {
        String tr = it.next();
        System.out.println(tr);
    }
    
    System.out.println("\n\n===============LINKS============\n");
     for (Iterator<String> it = link.iterator(); it.hasNext();) {
        String tr = it.next();
       System.out.println(tr);
    }
    
    System.out.println("\n\n===============PARAGRAPH============\n");
     for (Iterator<String> it = paragraph.iterator(); it.hasNext();) {
        String tr = it.next();
        System.out.println(tr);
    }

}

public void buildOccurenceMatrix(){



for(int i=0;i<title.size();i++){
       
       if(occurences.get(title.get(i))==null){
       
   occurences.put(title.get(i), titleCoefficient);}
       else{
       occurences.put(title.get(i),titleCoefficient+occurences.get(title.get(i)));
       }
   
   }
    for(int i=0;i<header.size();i++){
       
       if(occurences.get(header.get(i))==null){
       
   occurences.put(header.get(i), headCoefficient);}
       else{
       occurences.put(header.get(i),headCoefficient+occurences.get(header.get(i)));
       }
   }
     for(int i=0;i<link.size();i++){
       
       if(occurences.get(link.get(i))==null){
       
   occurences.put(link.get(i), linkCoefficient);}
       else{
       occurences.put(link.get(i),linkCoefficient+occurences.get(link.get(i)));
       }
   } for(int i=0;i<paragraph.size();i++){
       
       if(occurences.get(paragraph.get(i))==null){
       
   occurences.put(paragraph.get(i), paraCoefficient);}
       else{
       occurences.put(paragraph.get(i),paraCoefficient+occurences.get(paragraph.get(i)));
       }
   }
   
    System.out.println(occurences.values());
}

public int getWeight(String word){try{return (occurences.get(word));

}catch(Exception wordNotFound){wordNotFound.printStackTrace();return -1;}
}

}