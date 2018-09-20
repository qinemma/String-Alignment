import java.io.*;
import java.util.*;

/**
*
* This class calculates optimal global alignment with affine gaps between two 
* sequences by giving maximum alignment score and final alignment
* @author Emma Qin
* @Collaborator Rosa Zhou
* 
*/
public class GlobalAlignment{
    //Instance variable
    private int[][] tableG;
    private int[][] tableE;
    private int[][] tableF;
    private int[][] tableS;
    private String v;
    private String w;
    private int match;
    private int mismatch;
    private int gapExtend;
    private int gapOpen;
    
    
    /**
    *
    * Constructor of GlobalAlignment class
    * @param String v - first sequence to align
    * @param String w - seconde sequence to align
    * @param int[] scoreMatrix 
    *
    */
    public GlobalAlignment(String v, String w, int[] scoreMatrix){
        this.v = v;
        this.w = w;
        tableG = new int[v.length() + 1][w.length() + 1];
        tableE = new int[v.length() + 1][w.length() + 1];
        tableF = new int[v.length() + 1][w.length() + 1];
        tableS = new int[v.length() + 1][w.length() + 1];
        match = scoreMatrix[0];
        mismatch = scoreMatrix[1];
        gapExtend = scoreMatrix[2];
        gapOpen = scoreMatrix[3];
        
        
    }
    
    /**
    *
    * This method aligns two sequences and provide alignment by traceback
    *
    */
    public void alignment(){
        int m = v.length();
        int n = w.length();
       
        //Initiation
        tableS[0][0] = 0;
        for(int i = 1; i < m + 1; i++){
            tableS[i][0] = gapOpen + i * gapExtend;
            tableE[i][0] = gapOpen + i * gapExtend;
            tableF[i][0] = -99999;
        }
        
        for(int j = 1; j < n + 1; j++){
            tableS[0][j] = gapOpen + j * gapExtend;
            tableF[0][j] = gapOpen + j * gapExtend;
            tableE[0][j] = -99999;
        }
        
        //Recurrence
        for(int i = 1; i < m + 1; i++){
            for(int j = 1; j < n + 1; j++){
                //Recurrence for table G - diagnal path with no gaps
                if(v.charAt(i-1) == w.charAt(j-1)){
                    tableG[i][j] = tableS[i-1][j-1] + match;
                }else{
                    tableG[i][j] = tableS[i-1][j-1] + mismatch;
                }
                //Recurrence for table E - horizontal path with two choices
                tableE[i][j] = Math.max((tableE[i][j-1] + gapExtend), 
                                        (tableS[i][j-1] + gapExtend + gapOpen));
                //Recurrene for table F - vertical path with two choices
                tableF[i][j] = Math.max((tableF[i-1][j] + gapExtend), 
                                        (tableS[i-1][j] + gapExtend + gapOpen));
                //Recurrence for table S - each cell stores max score among 
                //three tables
                int s = Math.max(tableG[i][j], tableE[i][j]);
                tableS[i][j] = Math.max(s, tableF[i][j]);
    
            }
        }

        String[] alignment = new String[2];
        //Traceback
        String[] finalAlignment = globalTraceBack(m, n, alignment, "S");
        System.out.println("Alignment score is " + tableS[m][n]);
        System.out.println("Final alignment is: " + "\n" + finalAlignment[0] + 
                           "\n" + finalAlignment[1]);
    }

    /**
    *
    * This is a helper function to do traceback using recurssion
    * @param int i - coordinate of where to start
    * @param int j - coordinate of where to start
    * @param String[] alignment - alignment it returns
    * @param String table - table where score comes from
    * @return String[] - final optimal global alignment
    *
    */
    private String[] globalTraceBack(int i, int j, String[] alignment, 
                                   String table){
        //base case
        if(i == 0){
            char[] repeat = new char[j];
            Arrays.fill(repeat, '_');
            alignment[0] = new String(repeat) + alignment[0];
            alignment[1] = w.substring(0,j) + alignment[1];
            return alignment;
        }
        //another base case
        else if(j == 0){
            char[] repeat = new char[i];
            Arrays.fill(repeat, '_');
            alignment[1] = new String(repeat) + alignment[1];
            alignment[0] = v.substring(0,i) + alignment[0];
            
            return alignment;
        }
        else{
            //every score starts from table S
            if(table == "S"){
                if(tableS[i][j] == tableE[i][j]){
                    globalTraceBack(i, j, alignment, "E");
                }
                else if(tableS[i][j] == tableF[i][j]){
                    globalTraceBack(i, j, alignment, "F");
                }
                else if(tableS[i][j] == tableG[i][j]){
                    globalTraceBack(i, j, alignment, "G");
                }
            }
            else if(table == "E"){
                if(tableE[i][j] == tableE[i][j-1] + gapExtend){
                    alignment[0] = "_" + alignment[0];
                    alignment[1] = w.charAt(j-1) + alignment[1];
                    globalTraceBack(i, j-1, alignment, "E");
                }
                else if(tableE[i][j] == tableS[i][j-1] + gapOpen + gapExtend){
                    alignment[0] = "_" + alignment[0];
                    alignment[1] = w.charAt(j-1) + alignment[1];
                    globalTraceBack(i, j-1, alignment, "S");
                }
            }
            else if(table == "F"){
                if(tableF[i][j] == tableF[i-1][j] + gapExtend){
                    alignment[0] = v.charAt(i-1) + alignment[0];
                    alignment[1] = "_" + alignment[1];
                    globalTraceBack(i-1, j, alignment, "F");
                }
                else if(tableF[i][j] == tableS[i-1][j] + gapOpen + gapExtend){
                    alignment[0] = v.charAt(i-1) + alignment[0];
                    alignment[1] = "_" + alignment[1];
                    globalTraceBack(i-1, j, alignment, "S");
                }
            }
            else if(table == "G"){
                alignment[0] = v.charAt(i-1) + alignment[0];
                alignment[1] = w.charAt(i-1) + alignment[1];
                globalTraceBack(i-1, j-1, alignment, "S");
            }     
            
        }
        return alignment;
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
            int[] scoreMatrix = parser.scoreMatrixReader(scoreFile);
            GlobalAlignment alignment = new GlobalAlignment(sequence1, 
                                                            sequence2, scoreMatrix);
            System.out.println("Global alignment between " + file1 + " and " + 
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