import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Anew {

    public static HashMap<String, Double> anewWordValenceVals(){
        HashMap<String, Double> wordValence = new HashMap<String, Double>();
        try {

            // open input stream test.txt for reading purpose.
            BufferedReader br = new BufferedReader(new FileReader("ANEW2010All.txt"));
            String line  = br.readLine();
            line  = br.readLine();
            while ((line != null)) {
                String[] values = line.split("\\s+");
                wordValence.put(values[0], Double.parseDouble(values[2]));
                line = br.readLine();

            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return wordValence;
    }


    public static HashMap<String, Double> anewUserValence(){
        File dir = new File("user_statuses");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            HashMap<String, Double> wordValenceVals = anewWordValenceVals();
            HashMap<String, Double> userValence = new HashMap<>();
            for (File file : directoryListing) {
                String statusText = "";
                try {
                    statusText = new String(Files.readAllBytes(Paths.get("user_statuses/"+file.getName())));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int pos = file.getName().lastIndexOf(".");
                String user = file.getName().substring(0, pos);

                String[] words = statusText.split(" ");
                double count = 0.0;
                double summativeValence = 0.0;
                for(String word:words){
                    if(wordValenceVals.containsKey(word)){
                        summativeValence += wordValenceVals.get(word);
                        count+=1.0;
                    }
                }
                System.out.println(count);

                double averageValence = 0.0;
                if(count!=0){
                    averageValence = summativeValence/count;
                }

                userValence.put(user, averageValence);
            }
            return userValence;
        }
        else {
            return null;
        }
    }


//    public static void writeToCSV() {
//
//        try {
//            BufferedWriter out = null;
//
//            Iterable<CSVRecord> records = DocumentMaker.getRowsFromCSV();
//            HashMap<String, Double> userValences = anewUserValence();
//
//            FileWriter fstream = new FileWriter("anew.csv", true); //true tells to append data.
//            out = new BufferedWriter(fstream);
//
//            HashMap<String, List<String>> rows = CSVMaker.uniqueRowsFromCSV();
//
//            Iterator it = rows.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry pair = (Map.Entry)it.next();
//                String str = "";
//                str += pair.getKey() + "," + userValences.get(pair.getKey());
//                List<String> values = (List) pair.getValue();
//                for(int i=0; i<values.size(); i++){
//                    str+= ","+values.get(i);
//                }
//                out.write(str);
//                out.newLine();
//                it.remove(); // avoids a ConcurrentModificationException
//            }
//            out.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public static void main(String[] args){

//        HashMap<String, Double> map;
//        map = Anew.anewUserValence();
//        Iterator it = map.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
//            it.remove(); // avoids a ConcurrentModificationException
//        }
    }

}
