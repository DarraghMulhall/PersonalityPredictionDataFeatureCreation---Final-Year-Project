import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscFeatures {

    public static double userTotalStatusCounts(String user, HashMap<String, Integer> statusCounts){
//        HashMap<String, Integer> statusCounts = DocumentMaker.getUserStatusCount();
//        File dir = new File("user_statuses");
//        File[] directoryListing = dir.listFiles();
//        double[][] values = new double[directoryListing.length][1];
//
//        int userNum = 0, pos = 0;
//        String user = "";
//
//        for (File file : directoryListing) {
//            pos = file.getName().lastIndexOf(".");
//            user = file.getName().substring(0, pos);
//            values[userNum++][0] = (double) statusCounts.get(user);
//        }
//        return values;
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
        System.out.println(count + " " + (double) count / (double) words.length);
        return (double) count / (double) words.length;
    }



    public static double[][] obtainMiscFeatureValues(int numOfFeatures){
        File dir = new File("user_statuses");
        File[] directoryListing = dir.listFiles();
        HashMap<String, Integer> statusCounts = DocumentMaker.getUserStatusCount();
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
            values[userNum][0] = userTotalStatusCounts(user, statusCounts);

            //add number of upper case words in the combined status text
            Pattern pattern = Pattern.compile("^[A-Z][A-Z]+[,.!?]*");
            values[userNum][1] = capitalizedWords(statusText, pattern);

            //add average words per status
            values[userNum][2] = wordCountAverageAcrossStatuses(user, statusCounts, statusText);

            //add average number of sentences per status
            values[userNum][3] = averageSentencesPerStatus(user, statusCounts, statusText);

            values[userNum][4] = averageWordsPerSentence(statusText);

            values[userNum][5] = averageLengthPerStatus(user, statusCounts, statusText);

            values[userNum][6] = punctuationCount(statusText);

            values[userNum][7] = swearWordsPercentage(statusText);

            userNum++;
        }
        return values;
    }



    public static void main(String[] args){
        File dir = new File("user_statuses");
        File[] directoryListing = dir.listFiles();
        double[][] values = obtainMiscFeatureValues(8);

        double[][] zScoreVals = ZScores.zScores(values);
        CSVMaker.writeToCSV("h4lvd.csv", "misc.csv", zScoreVals, ",Total_Status_Count, Percentage_in_Caps, Words_per_Status, Sentences_per_" +
        "Status, Words_per_Sentence, Average_Status_Length, Punctuation, Swear_Words");

    }


}
