/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package majorproject.search.engine.conjecture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author akjantal
 */
public class Indexing {
    
    File indexer;
    String fileName="index.txt";
    HashMap<String, ArrayList<Document>> index;
    public void index(String docId,String key){
        
    
    }
    
    public void write(){
    
    
    }
    
    public Indexing(){
        try{FileInputStream fs=new FileInputStream(fileName);
        
        }catch(FileNotFoundException fileNotFound){indexer=new File(fileName);
        index=new HashMap<String,ArrayList<Document>>();}
        
    
    }
    
}
