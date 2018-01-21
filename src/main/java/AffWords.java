import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class AffWords {

    //int arg: 0-Valence, 1-Arousal, 2-Dominance
    public static HashMap<String, Double> crrWordEmotionVals(int emotion){
        HashMap<String, Double> wordValence = new HashMap<String, Double>();
        try {

            // open input stream test.txt for reading purpose.
            BufferedReader br = new BufferedReader(new FileReader("crr.csv"));
            String line  = br.readLine();
            line  = br.readLine();
            while ((line != null)) {
                String[] values = line.split(",");
                //in the file crr.txt, valence values are found at column 2, arousal at 5 and dominance at 8
                wordValence.put(values[1].toLowerCase(), Double.parseDouble(values[emotion+(2*(emotion+1))]));
                line = br.readLine();
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return wordValence;
    }


    public static double[][] crrUserEmotion(int emotion){
        File dir = new File("user_statuses");
        File[] directoryListing = dir.listFiles();
        HashMap<String, Double> wordValenceVals = crrWordEmotionVals(emotion);
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
                //a value of 0 would mean a low valence, whereas a user with no valence words found should be neutral, i.e 5
                else{
                    averageValence = 5.0;
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

        //0 for valence, 1 for arousal, 2 for dominance
        double[][] valenceVals = crrUserEmotion(2);
        double[][] zScoreVals = ZScores.zScores(valenceVals);

        CSVMaker.writeToCSV("valence_arousal.csv", "valence_arousal_dom.csv", zScoreVals, ",Dominance");

    }

}
