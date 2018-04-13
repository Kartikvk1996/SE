/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wordp;

/**
 *
 * @author akjantal
 */
public class wordVec {
    
    private double []word;
    private int dimension=5;
    private boolean initialied=false;
    public void init()
    {
        word =new double[dimension];
        for(int i=0;i<dimension;i++)word[i]=(float) (1+i*0.1);
        initialied=true;
    }
    
    public void display(){for(int i=0;i<dimension;i++)System.err.print(word[i]+",");}
    
    public double[] get(){return word;}
   
    public double []product(double[] vecB){
    double c[]=new double[dimension];
    for(int i=0;i<dimension;i++){c[i]=word[i]*vecB[i];}
    return c;
    }
    
    
    
    
}
