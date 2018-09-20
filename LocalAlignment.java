import java.io.*;
import java.util.*;
import java.lang.*;

/**
*
* This class calculates optimal local alignment between two sequences by 
* giving maximum alignment score and final alignment
* @author Emma Qin
* @Collaborator Rosa Zhou
* 
*/
public class LocalAlignment{
    // Instance variable for LocalAlignment class
    private int[][] table;
    private String v;
    private String w;
    private int match;
    private int mismatch;
    private int indel;
    
    /**
    *
    * Constructor of LocalAlignment class
    * @param String v - first sequence to align
    * @param String w - seconde sequence to align
    * @param int[] scoreMatrix 
    *
    */
    public LocalAlignment(String v, String w, int[] scoreMatrix){
        this.v = v;
        this.w = w;
        table = new int[v.length()+1][w.length()+1];
        match = scoreMatrix[0];
        mismatch = scoreMatrix[1];
        indel = scoreMatrix[2];
    }
    
    /**
    *
    * This method aligns two sequences and provide alignment by traceback
    *
    */
    public void alignment(){
        int max = -9999;
        int maxi = 0;
        int maxj = 0;
        
        //Initiation
        for(int i = 0; i < v.length()+1; i++){
            table[i][0] = 0;
        }
        for(int j = 0; j < w.length()+1; j++){
            table[0][j] = 0;
        }
        //Recurrence
        for(int i = 1; i < v.length()+1; i++){
            for(int j = 1; j < w.length()+1; j++){
                int horizontal = table[i][j-1] + indel; //horizontal path
                int vertical = table[i-1][j] + indel; //vertical path
                int diagnal = 0; //diagnal path
                if(v.charAt(i-1) == w.charAt(j-1)){
                    diagnal = table[i-1][j-1] + match;
                }else{
                    diagnal = table[i-1][j-1] + mismatch;
                }
                int score = Math.max(horizontal, vertical);
                score = Math.max(score, diagnal);
                score = Math.max(score, 0);
                table[i][j] = score;
                if(score > max){
                    max = score;
                    maxi = i;
                    maxj = j;
                }
            }
        }
        
        
        System.out.println("Maximum score of Local alignment: " + max);
        String finalAlignment = localTraceBack(maxi, maxj);
        System.out.println(finalAlignment);
    }

    /**
    *
    * This is a helper function to do traceback
    * @param int i - coordinate of where to start
    * @param int j - coordinate of where to start
    * @return String - final optimal local alignment
    *
    */
    private String localTraceBack(int i, int j){
        if(table[i][j] == 0){
            System.out.println("No optimal local alignment.");
        }
        String vAlignment = "";
        String wAlignment = "";
        Stack alignv = new Stack();
        Stack alignw = new Stack();
        //loop starts from max score, ends at 0
        while(table[i][j] != 0){
            //score comes from diagnal path
            if(table[i][j] == table[i-1][j-1] + match || table[i][j] == 
               table[i-1][j-1] + mismatch){
                //vAlignment = v.charAt(i-1) + vAlignment;
                //wAlignment = w.charAt(j-1) + wAlignment;
                alignv.push(v.charAt(i-1));
                alignw.push(w.charAt(j-1));
                i = i - 1;
                j = j - 1;
            }
            //score comes from horizontal path
            else if(table[i][j] == table[i][j-1] + indel){
                //vAlignment = "_" + vAlignment;
                //wAlignment = w.charAt(j-1) + wAlignment;
                alignv.push("_");
                alignw.push(w.charAt(j-1));
                j = j - 1;
            }
            //score comes from vertical path
            else if(table[i][j] == table[i-1][j] + indel){
                //vAlignment = v.charAt(i-1) + vAlignment;
                //wAlignment = "_" + wAlignment;
                alignv.push(v.charAt(i-1));
                alignw.push("_");
                i = i - 1;
            }
             
        }
        
        while(!alignv.empty()){
            vAlignment = vAlignment + alignv.pop();
            wAlignment = wAlignment + alignw.pop();
        }
        
        String finalAlignment = vAlignment + "\n" + wAlignment;
        return finalAlignment;
    }
    
    /**
    * Main function of LocalAlignment class
    */
    public static void main(String args[]){
        if(args.length == 3){
            String file1 = args[0];
            String file2 = args[1];
            String scoreFile = args[2];
            Parser parser = new Parser();
            String sequence1 = parser.sequenceReader(file1);
            String sequence2 = parser.sequenceReader(file2);
            //System.out.println(sequence1);
            //System.out.println(sequence2);
            int[] scoreMatrix = parser.scoreMatrixReader(scoreFile);
            LocalAlignment alignment = new LocalAlignment(sequence1, sequence2, 
                                                          scoreMatrix);
            System.out.println("Local alignment between " + file1 + " and " + 
                               file2);
            alignment.alignment();
        }
        
        /*
        //test
        int[] scoreMatrix = {1,-2,-2,-3};
        String[][] testPairs = {
                //{"",""},
                //{"jfwoknda","helloworld"},
                //{"aac","aba"},
                //{"","a"},
                //{"ababa","abcba"},
                //{"ijbcfghj","cgdef"},
                //{"abcd","efhij"},
                //{"CAGGCTGGAGTGGAGTGGCGTGATCTTGGCT","ACAGTGTTGCCCAG"}
        };
        for (String[] pair : testPairs){
            System.out.println("\nStrings:");
            System.out.println(pair[0]);
            System.out.println(pair[1]);
            LocalAlignment alignment = new LocalAlignment(pair[0],pair[1],scoreMatrix);
            alignment.alignment();
        }
        
        */
        
    }
    
}