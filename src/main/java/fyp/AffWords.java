package fyp;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class AffWords {


    public static String columns(){
        return ",Valence,Crr_arousal,Dominance";
    }

    //int arg: 0-Valence, 1-Arousal, 2-Dominance
    public static HashMap<String, Double[]> crrWordEmotionVals(){
        HashMap<String, Double[]> wordValence = new HashMap<>();
        try {

            // open input stream test.txt for reading purpose.
            BufferedReader br = new BufferedReader(new FileReader("crr.csv"));
            String line  = br.readLine();
            line  = br.readLine();
            while ((line != null)) {
                String[] values = line.split(",");
                //in the file crr.txt, valence values are found at column 2, arousal at 5 and dominance at 8
                wordValence.put(values[1].toLowerCase(), new Double[] {Double.parseDouble(values[2]), Double.parseDouble(values[5]),
                        Double.parseDouble(values[8])});
                line = br.readLine();
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return wordValence;
    }


    private static double averageEmotionValue(int emotion, String[] words, HashMap<String, Double[]> wordValenceVals){
        double count = 0.0;
        double summativeValence = 0.0;
        for(String word:words){
            if(wordValenceVals.containsKey(word.toLowerCase())){
                summativeValence += wordValenceVals.get(word.toLowerCase())[emotion];
                count+=1.0;
            }
        }
        double averageValence = 0.0;
        if(count!=0){
            averageValence = summativeValence/count;
        }
        //a value of 0 would mean a low valence, whereas a user with no valence words found should be neutral, i.e 5
        else{
            averageValence = 5.0;
        }
        return  averageValence;
    }


    public static double[][] crrUserEmotion(){
        File dir = new File("user_statuses");
        File[] directoryListing = dir.listFiles();
        HashMap<String, Double[]> wordValenceVals = crrWordEmotionVals();
        double[][] values = new double[directoryListing.length][3];
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

                for(int i=0; i<3; i++){
                    double averageValue = averageEmotionValue(i, words, wordValenceVals);
                    DecimalFormat df = new DecimalFormat("#.##");
                    averageValue = Double.valueOf(df.format(averageValue));
                    values[userNum][i] = averageValue;
                }


                userNum++;
            }
            return values;
        }
        else {
            return null;
        }
    }


    public static double[][] values(){


        double[][] valenceVals = crrUserEmotion();
        double[][] zScoreVals = ZScores.zScores(valenceVals);

        return zScoreVals;
        //CSVMaker.writeToCSV("mypersonality_final.csv", "aff_features.csv", zScoreVals, ",Valence,Crr_arousal,Dominance");

    }

}
