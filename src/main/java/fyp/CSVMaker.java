package fyp;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

public class CSVMaker {

    public static HashMap<String, List<String>> uniqueRowsFromCSV(String csvFile){
        HashMap<String, List<String>> rows = new HashMap();
        HashSet<String> users = new HashSet<>();
        Iterable<CSVRecord> records = getRowsFromCSV(csvFile);
        int index = 0;
        for (CSVRecord record : records) {
            if(!users.contains(record.get("#AUTHID"))){
                String user = record.get("#AUTHID");
                users.add(user);
                Iterator it = record.iterator();

                //skipping user field
                it.next();
                if(record.isMapped("STATUS")){
                    it.next();
                }

                List<String> fields = new ArrayList<String>();
                while(it.hasNext()){
                    fields.add((String) it.next());
                }
                rows.put(user, fields);
            }

        }
        return rows;
    }


    public static Iterable<CSVRecord> getRowsFromCSV(String csvFile){
        Iterable<CSVRecord> records = null;
        Reader in = null;
        try {
            in = new FileReader(csvFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }


    public static String getHeader(String inputCSV){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(inputCSV));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String header = null;
        try {
            header = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        header = header.replaceAll(",STATUS", "");
        return header;
    }


    public static void writeToCSV(String inputCSV, String outputCSV, double[][] newData, String newHeaderCols) {

        try {
            BufferedWriter out = null;

            String header = getHeader(inputCSV)+newHeaderCols;
            System.out.println(header);

            HashMap<String, List<String>> rows = uniqueRowsFromCSV(inputCSV);

            FileWriter fstream = new FileWriter(outputCSV, true); //true tells to append data.
            out = new BufferedWriter(fstream);

            out.write(header);
            out.newLine();



            File dir = new File("user_statuses");
            File[] directoryListing = dir.listFiles();
            int pos = 0;
            String user = "";
            int userNum = 0;
            for (File file : directoryListing) {
                pos = file.getName().lastIndexOf(".");
                user = file.getName().substring(0, pos);
                String str = "";
                str += user;
               // System.out.println(str);

                List<String> values = rows.get(user);
                for (int i = 0; i < values.size(); i++) {
                    str += "," + values.get(i);
                }

                for (int j = 0; j < newData[userNum].length; j++) {
                    str += "," + newData[userNum][j];
                }
                out.write(str);
                out.newLine();
                userNum++;
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
