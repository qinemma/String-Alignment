import java.io.*;
import java.util.*;

/**
*
* This Parser class helps to read sequence and scoring matrix 
* in the same directory
* @author Emma Qin
*
*/
public class Parser{
    
    /**
    * This function reads DNA sequence stores in fasta file 
    * @param String fileName - file need to be read
    * @return String - DNA sequence
    */
    public String sequenceReader(String fileName){
        String sequence = "";
        try{
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                if(line.contains(">")){
                    continue;
                }
                stringBuffer.append(line);
            }
            fileReader.close();
            sequence = stringBuffer.toString().toUpperCase();
            //System.out.println(sequence);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return sequence;      
    }
    
    /**
    * This function reads scoring matrix with 4 parameters
    * @param String fileName - file need to be read
    * @return int[] - score matrix
    */
    public int[] scoreMatrixReader(String fileName){
        int[] scoreMatrix = new int[4];
        try{
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while(scanner.hasNext()){
                if(scanner.hasNextInt()){
                    for(int i = 0; i < 4; i++){
                        scoreMatrix[i] = scanner.nextInt();
                    }
                }
                else{
                    scanner.next();
                }
            }
            scanner.close();
        }
        catch(FileNotFoundException e){
            System.err.println("File not exist");
            System.exit(0);
        }
        return scoreMatrix;  
        
    }
    
}