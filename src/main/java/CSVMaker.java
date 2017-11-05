import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

public class CSVMaker<T> {

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
        header = header.replaceAll(",\"STATUS\"", "");
        return header;
    }


    public static void writeToCSV(String inputCSV, String outputCSV, HashMap<String, Object> newData, String newHeaderCols) {

        try {
            BufferedWriter out = null;

            String header = getHeader(inputCSV)+newHeaderCols;
            Iterable<CSVRecord> records = getRowsFromCSV(inputCSV);
            System.out.println(header);

            //HashMap<String, Ob> userValences = anewUserValence();

            FileWriter fstream = new FileWriter(outputCSV, true); //true tells to append data.
            out = new BufferedWriter(fstream);

            out.write(header);
            out.newLine();

            HashMap<String, List<String>> rows = uniqueRowsFromCSV(inputCSV);

            Iterator it = rows.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                String str = "";
                str += pair.getKey();

                List<String> values = (List) pair.getValue();
                System.out.println(values.size());
                for(int i=0; i<values.size(); i++){
                    str+= ","+values.get(i);
                }

                if(newData.get(pair.getKey()) instanceof LinkedHashMap){
                    Iterator it2 = ((LinkedHashMap) newData.get(pair.getKey())).entrySet().iterator();
                    while(it2.hasNext()){
                        Map.Entry pair2 = (Map.Entry)it2.next();
                        str+= ","+pair2.getValue();
                        it2.remove();
                    }
                }
                else {
                    str += ","+newData.get(pair.getKey());
                }

                out.write(str);
                out.newLine();
                it.remove(); // avoids a ConcurrentModificationException
            }
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
