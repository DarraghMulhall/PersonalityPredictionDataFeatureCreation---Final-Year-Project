package fyp;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class POSTagging {

    public static String[] posTags = {"CC", "CD", "DT", "EX", "FW", "IN", "JJ", "NN", "PRP",
     "RB", "TO", "VB"};


    public static String columns(){
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < posTags.length; i++) {
            result.append("," + posTags[i]);
        }
        String newHeader = result.toString();
        return newHeader;
    }


    public static double[][] tagging(HashMap<String, Integer> userStatusCount){

        POSModel model = null;
        try {
            InputStream modelIn = new FileInputStream("en-pos-maxent.bin");
            model = new POSModel(modelIn);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        POSTaggerME tagger = new POSTaggerME(model);

        File dir = new File("user_statuses");
        File[] directoryListing = dir.listFiles();
        double[][] values = new double[directoryListing.length][posTags.length];

        int userNum = 0;
        for (File file : directoryListing) {
            String statusText = "";
            try {
                statusText = new String(Files.readAllBytes(Paths.get("user_statuses/" + file.getName())));
            } catch (IOException e) {
                e.printStackTrace();
            }

            int pos = file.getName().lastIndexOf(".");
            String user = file.getName().substring(0, pos);



            String[] words = statusText.split(" ");
            for (int i = 0; i < words.length; i++) {
                words[i] = words[i].replaceAll("[-+.^:,]", "");
            }


            String userTags[] = tagger.tag(words);
            LinkedHashMap<String, Double> tagCounts = new LinkedHashMap<>();

            int userStatusNum = userStatusCount.get(user);

            double additive = 1.0/userStatusNum;
            DecimalFormat df = new DecimalFormat("#.##");
            additive = Double.valueOf(df.format(additive));

            for (int i = 0; i < userTags.length; i++) {
                int index = Arrays.asList(posTags).indexOf(userTags[i]);
                if(!(index > -1)){
                    if(userTags[i].contains("VB")){
                        values[userNum][Arrays.asList(posTags).indexOf("VB")] += Double.valueOf(df.format(additive));
                    }
                    else if(userTags[i].contains("NN")){
                        values[userNum][Arrays.asList(posTags).indexOf("NN")] += Double.valueOf(df.format(additive));
                    }
                    else if(userTags[i].contains("JJ")){
                        values[userNum][Arrays.asList(posTags).indexOf("JJ")] += Double.valueOf(df.format(additive));
                    }

                }
                else  values[userNum][index] += Double.valueOf(df.format(additive));
            }
            userNum++;
        }
        return values;
    }


    public static HashMap<String, Object> numDistinctTagsPerUser(HashMap<String, Object> map){
        HashMap<String, Object> distinctTagCount = new HashMap<>();

        for (Map.Entry<String, Object> entry: map.entrySet()) {
            int count = 0;
            LinkedHashMap<String, Double> tagMap = (LinkedHashMap<String, Double>) entry.getValue();
            for (Map.Entry<String, Double> entry2: tagMap.entrySet()){
                if(entry2.getValue()!=0.0){
                    count++;
                }
            }
            distinctTagCount.put(entry.getKey(), (Object) count);
        }
        return distinctTagCount;
    }



    public static double[][] values(HashMap<String, Integer> userStatusCount){



        double[][] values = tagging(userStatusCount);
        double[][] zScoreVals = ZScores.zScores(values);
        //HashMap<String, Object> tagMap = numDistinctTagsPerUser(map);
       //CSVMaker.writeToCSV("aff_features.csv", "pos_features.csv", zScoreVals, newHeader);
        return zScoreVals;
    }
}
