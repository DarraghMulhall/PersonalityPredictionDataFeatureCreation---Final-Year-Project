package fyp;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class H4LVD {


    public static String columns(){
        String[] categories = getCategories();
        //making new additions to csv header
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < categories.length; i++) {
            result.append("," + categories[i]);
        }
        String newHeader = result.toString();
        return newHeader;
    }


    public static String[] getCategories(){

        BufferedReader br = null;
        String line = "";
        try {
            br = new BufferedReader(new FileReader("h4lvd.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] header = line.split(" ");
        String[] categories = new String[header.length-4];
        //excluding first and last 2 words which are not categories
        for(int i=2; i<header.length-2; i++){
            categories[i-2] = header[i];
        }
        return categories;
    }


    public static HashMap<String, String[]> wordWithCategories(){
        HashMap<String, String[]> wordAndCategories = new HashMap<>();
        try {

            // open input stream test.txt for reading purpose.
            BufferedReader br = new BufferedReader(new FileReader("h4lvd.txt"));

            String line = br.readLine();

            String[] categories = getCategories();


            line = br.readLine();
            while ((line != null)) {
                String[] tokens = line.split("\\s+");
                String word = tokens[0];
                //skipping multiple options words
                if(word.contains("#")){
                    line = br.readLine();
                    continue;
                }
                int indexOfStopper;

                if(tokens[1].equals("Lvd")){
                    indexOfStopper = tokens.length;
                }
                else {
                    indexOfStopper = Arrays.asList(tokens).indexOf("|");
                }

                String[] categoriesForWord =  new String[indexOfStopper-2];
                //skip first 2 words as they are not categories and last word which is "|"

                for(int i=2; i<indexOfStopper; i++){
                    categoriesForWord[i-2] = tokens[i];
                }
                wordAndCategories.put(word, categoriesForWord);
                line = br.readLine();

            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return wordAndCategories;
    }


    public static double[][] userCategories(HashMap<String, Integer> userStatusCount){
        HashMap<String, String[]> wordsAndCategories = wordWithCategories();

        File dir = new File("user_statuses");
        File[] directoryListing = dir.listFiles();
        String[] categories = getCategories();
        double[][] values = new double[directoryListing.length][categories.length];

        int userNum = 0;
        for (File file : directoryListing) {



            LinkedHashMap<String, Double> userH4Counts = new LinkedHashMap<>();

            for(int i=0; i<categories.length; i++){
                userH4Counts.put(categories[i], 0.0);
            }


            String statusText = "";
            try {
                statusText = new String(Files.readAllBytes(Paths.get("user_statuses/" + file.getName())));
            } catch (IOException e) {
                e.printStackTrace();
            }

            int pos = file.getName().lastIndexOf(".");
            String user = file.getName().substring(0, pos);


            int userStatusNum = userStatusCount.get(user);
            double additive = 1.0/userStatusNum;
            DecimalFormat df = new DecimalFormat("#.##");
            additive = Double.valueOf(df.format(additive));

            String[] words = statusText.split(" ");

            String category = "";

            for(int i=0; i<words.length; i++){
                if(wordsAndCategories.containsKey(words[i].toUpperCase())){
                    for(int j=0; j<wordsAndCategories.get(words[i].toUpperCase()).length; j++){
                        category = wordsAndCategories.get(words[i].toUpperCase())[j];
                        int index = Arrays.asList(categories).indexOf(category);
                        if(index > -1)
                            values[userNum][index] += Double.valueOf(df.format(additive));
                    }
                }
            }
            userNum++;
        }
        return values;
    }

    //called in project specific to facebook or twitter
    public static double[][] values(HashMap<String, Integer> userStatusCount){
        double[][] values = userCategories(userStatusCount);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("h4lvd.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        double[][] zScoreVals = ZScores.zScores(values);
        return zScoreVals;

        //CSVMaker.writeToCSV("pos_features.csv", "h4lvd_features.csv", zScoreVals, newHeader);
    }
}