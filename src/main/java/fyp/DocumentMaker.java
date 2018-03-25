package fyp;

import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.HashMap;


public class DocumentMaker {

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