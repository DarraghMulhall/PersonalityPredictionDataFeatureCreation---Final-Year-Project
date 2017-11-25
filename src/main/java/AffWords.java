import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class AffWords {

    public static HashMap<String, Double> anewWordValenceVals(){
        HashMap<String, Double> wordValence = new HashMap<String, Double>();
        try {

            // open input stream test.txt for reading purpose.
            BufferedReader br = new BufferedReader(new FileReader("crr.csv"));
            String line  = br.readLine();
            line  = br.readLine();
            while ((line != null)) {
                //String[] values = line.split("\\s+");
                String[] values = line.split(",");
                wordValence.put(values[1].toLowerCase(), Double.parseDouble(values[2]));
                line = br.readLine();

            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return wordValence;
    }


//    public static HashMap<String, Double> anewUserValence(){
//        File dir = new File("user_statuses");
//        File[] directoryListing = dir.listFiles();
//        if (directoryListing != null) {
//            HashMap<String, Double> wordValenceVals = anewWordValenceVals();
//            HashMap<String, Double> userValence = new HashMap<>();
//            for (File file : directoryListing) {
//                String statusText = "";
//                try {
//                    statusText = new String(Files.readAllBytes(Paths.get("user_statuses/"+file.getName())));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                int pos = file.getName().lastIndexOf(".");
//                String user = file.getName().substring(0, pos);
//
//                String[] words = statusText.split(" ");
//                for (int i = 0; i < words.length; i++) {
//                    words[i] = words[i].replaceAll("[-+.^:,]", "");
//                }
//                double count = 0.0;
//                double summativeValence = 0.0;
//                for(String word:words){
//                    if(wordValenceVals.containsKey(word.toLowerCase())){
//                        summativeValence += wordValenceVals.get(word.toLowerCase());
//                        count+=1.0;
//                    }
//                }
//                double averageValence = 0.0;
//                if(count!=0){
//                    averageValence = summativeValence/count;
//                }
//                DecimalFormat df = new DecimalFormat("#.##");
//                averageValence = Double.valueOf(df.format(averageValence));
//                userValence.put(user, averageValence);
//
//                System.out.println("\n\n");
//            }
//            return userValence;
//        }
//        else {
//            return null;
//        }
//    }

    public static double[][] anewUserValence(){
        File dir = new File("user_statuses");
        File[] directoryListing = dir.listFiles();
        HashMap<String, Double> wordValenceVals = anewWordValenceVals();
        double[][] values = new double[directoryListing.length][1];
        if (directoryListing != null) {

            int userNum = 0;
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
                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].replaceAll("[-+.^:,]", "");
                }
                double count = 0.0;
                double summativeValence = 0.0;
                for(String word:words){
                    if(wordValenceVals.containsKey(word.toLowerCase())){
                        summativeValence += wordValenceVals.get(word.toLowerCase());
                        count+=1.0;
                    }
                }
                double averageValence = 0.0;
                if(count!=0){
                    averageValence = summativeValence/count;
                }
                DecimalFormat df = new DecimalFormat("#.##");
                averageValence = Double.valueOf(df.format(averageValence));
                values[userNum][0] = averageValence;

                userNum++;
            }
            return values;
        }
        else {
            return null;
        }
    }





    public static void main(String[] args){

//        HashMap<String, Object> map;
//        map = AffWords.anewUserValence();
//        Iterator it = map.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
//            it.remove(); // avoids a ConcurrentModificationException
//        }
        double[][] valenceVals = anewUserValence();
        //map = AffWords.anewUserValence();





        double[][] zScoreVals = ZScores.zScores(valenceVals);



        CSVMaker.writeToCSV("mypersonality_final.csv", "valence.csv", zScoreVals, ",Valence");

    }

}
