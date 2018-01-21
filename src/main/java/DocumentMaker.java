import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class DocumentMaker {


    public static ArrayList<String> users;


    public static HashMap<String, Integer> getUserStatusCount() {
        HashMap<String, Integer> userStatusCount = new HashMap<>();
        Iterable<CSVRecord> records = CSVMaker.getRowsFromCSV("mypersonality_final.csv");
        for (CSVRecord record : records) {
            String user = record.get("#AUTHID");

            if(!userStatusCount.containsKey(user)){
                userStatusCount.put(user, 1);
            }
            else {
                userStatusCount.put(user, userStatusCount.get(user)+1);
            }
        }
        return userStatusCount;
    }





    public static void writeToFiles(){
        String csvFile = "mypersonality_final.csv";
        try {
            BufferedWriter out = null;

            Iterable<CSVRecord> records = CSVMaker.getRowsFromCSV(csvFile);
            for (CSVRecord record : records) {

                try
                {
                    FileWriter fstream = new FileWriter("user_statuses/"+record.get("#AUTHID")+".txt", true); //true tells to append data.
                    out = new BufferedWriter(fstream);
                    String line = record.get("STATUS");
//                    if(line.charAt(line.length()-1) !='.' && line.charAt(line.length()-1) !='!' && line.charAt(line.length()-1) !='?'){
//                        line = record.get("STATUS")+".";
//                    }
                    out.write(line);
                }
                catch (IOException e)
                {
                    System.err.println("Error: " + e.getMessage());
                }
                finally
                {
                    if(out != null) {
                        out.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        writeToFiles();
    }
}