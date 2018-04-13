/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wordp;

import java.util.ArrayList;
import majorproject.search.engine.conjecture.MajorProjectSearchEngineConjecture;

/**
 *
 * @author akjantal
 */
public class contextWord {
    private int noOfwords;
    ArrayList<wordVec> words=new ArrayList<wordVec>();
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
        t=temp.product(t);
         for(int j=0;j<5;j++)System.out.println(t[j]);
         words.add(temp);
     }
        
    }
    
    public wordVec getVec(int index){
    return words.get(index);
    
    }
    
}
