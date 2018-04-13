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
public class mainWord {
    
    private int noOfwords;
    
    ArrayList<wordVec> words=new ArrayList<wordVec>();
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
    
}
