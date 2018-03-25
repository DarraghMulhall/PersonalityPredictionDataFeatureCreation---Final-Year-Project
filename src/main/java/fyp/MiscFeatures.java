package fyp;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscFeatures {


    public static String columns(){
        return ",Total_Status_Count, Percentage_in_Caps, Words_per_Status, " +
               "Sentences_per_Status, Words_per_Sentence, Average_Status_Length, Punctuation, Swear_Words";
    }


    public static double userTotalStatusCounts(String user, HashMap<String, Integer> statusCounts){
        return (double) statusCounts.get(user);
    }

    public static double capitalizedWords(String statusText, Pattern pattern){
        String[] words;
        int capWords = 0;

        words = statusText.split("\\s+");
        for(int i=0; i<words.length; i++){
            if(words[i].equals(words[i].toUpperCase())){

                Matcher m = pattern.matcher(words[i]);

                if(m.find()){
                    if(!words[i].equals("*PROPNAME*")){
                        capWords++;
                    }
                }
            }
        }

        double capWordsPercent = (double)capWords / (double) words.length;

        DecimalFormat df = new DecimalFormat("#.###");
        return Double.valueOf(df.format(capWordsPercent));

    }


    public static double wordCountAverageAcrossStatuses(String user, HashMap<String, Integer> statusCounts, String statusText){
        return (double) statusText.split("\\s+").length / (double) statusCounts.get(user);
    }


    public static double averageSentencesPerStatus(String user, HashMap<String, Integer> statusCounts, String statusText){
        return (double) statusText.split("[!?.]+").length / (double) statusCounts.get(user);
    }


    public static double averageWordsPerSentence(String statusText){
        String[] sentences;

        sentences = statusText.split("[!?.]+");
        double averageWordsPerSentence = 0;
        for(int i=0; i<sentences.length; i++){
            averageWordsPerSentence +=  (double) sentences[i].split("\\s+").length / (double) sentences.length;
        }
        return averageWordsPerSentence;
    }


    public static double averageLengthPerStatus(String user, HashMap<String, Integer> statusCounts, String statusText){
        return (double) statusText.length() / (double) statusCounts.get(user);
    }


    public static double punctuationCount(String statusText){
        Pattern pattern = Pattern.compile("[,.!?'\":;-]");
        int count = 0;
        Matcher m;
        m = pattern.matcher(statusText);
        while (m.find())
            count++;


        return (double) count / (double) statusText.length();
    }

    public static double swearWordsPercentage(String statustText){
        String[] words = statustText.split("\\s+");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("swearWords.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String swearWords = "";
        try {
            swearWords = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] swears = swearWords.split(",");
        int count = 0;
        for(int i=0; i<words.length; i++){
            for(int j=0; j<swears.length; j++){
                if(words[i].equalsIgnoreCase(swears[j])){
                    count++;
                }
            }
        }
        return (double) count / (double) words.length;
    }



    public static double[][] obtainMiscFeatureValues(int numOfFeatures, HashMap<String, Integer> userStatusCount){
        File dir = new File("user_statuses");
        File[] directoryListing = dir.listFiles();
        String statusText;
        int pos = 0;
        String user = "";
        double[][] values = new double[directoryListing.length][numOfFeatures];
        int userNum = 0;
        for (File file : directoryListing) {
            statusText = "";
            try {
                statusText = new String(Files.readAllBytes(Paths.get("user_statuses/" + file.getName())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            pos = file.getName().lastIndexOf(".");
            user = file.getName().substring(0, pos);


            //add total status count feature
            values[userNum][0] = userTotalStatusCounts(user, userStatusCount);

            //add number of upper case words in the combined status text
            Pattern pattern = Pattern.compile("^[A-Z][A-Z]+[,.!?]*");
            values[userNum][1] = capitalizedWords(statusText, pattern);

            //add average words per status
            values[userNum][2] = wordCountAverageAcrossStatuses(user, userStatusCount, statusText);

            //add average number of sentences per status
            values[userNum][3] = averageSentencesPerStatus(user, userStatusCount, statusText);

            values[userNum][4] = averageWordsPerSentence(statusText);

            values[userNum][5] = averageLengthPerStatus(user, userStatusCount, statusText);

            values[userNum][6] = punctuationCount(statusText);

            values[userNum][7] = swearWordsPercentage(statusText);

            userNum++;
        }
        return values;
    }



    public static double[][] values(HashMap<String, Integer> userStatusCount){
        File dir = new File("user_statuses");
        File[] directoryListing = dir.listFiles();
        double[][] values = obtainMiscFeatureValues(8, userStatusCount);

        double[][] zScoreVals = ZScores.zScores(values);
        //CSVMaker.writeToCSV("h4lvd_features.csv", "features.csv", zScoreVals, ",Total_Status_Count, Percentage_in_Caps, Words_per_Status, " +
         //       "Sentences_per_" +
        //"Status, Words_per_Sentence, Average_Status_Length, Punctuation, Swear_Words");
        return zScoreVals;
    }


}
