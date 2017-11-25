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


//    public static HashMap<String, Object> tagging(){
//        HashMap<String, Object> tagCountsAllUsers = new HashMap<>();
//        POSModel model = null;
//        try {
//            InputStream modelIn = new FileInputStream("en-pos-maxent.bin");
//            model = new POSModel(modelIn);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        POSTaggerME tagger = new POSTaggerME(model);
//
//        File dir = new File("user_statuses");
//        File[] directoryListing = dir.listFiles();
//        HashMap<String, Integer> userStatusCount = DocumentMaker.getUserStatusCount();
//
//        for (File file : directoryListing) {
//            String statusText = "";
//            try {
//                statusText = new String(Files.readAllBytes(Paths.get("user_statuses/" + file.getName())));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            int pos = file.getName().lastIndexOf(".");
//            String user = file.getName().substring(0, pos);
//
//            String[] words = statusText.split(" ");
//            for (int i = 0; i < words.length; i++) {
//                words[i] = words[i].replaceAll("[-+.^:,]", "");
//            }
//
//
//            String userTags[] = tagger.tag(words);
//            LinkedHashMap<String, Double> tagCounts = new LinkedHashMap<>();
//
//            int userStatusNum = userStatusCount.get(user);
//
//
//            for (int i = 0; i < posTags.length; i++) {
//                tagCounts.put(posTags[i], 0.0);
//            }
//
//            double additive = 1.0/userStatusNum;
//            DecimalFormat df = new DecimalFormat("#.##");
//            additive = Double.valueOf(df.format(additive));
//
//            for (int i = 0; i < userTags.length; i++) {
//                if(!tagCounts.containsKey(userTags[i])){
//                    if(userTags[i].contains("VB")){
//                        tagCounts.put("VB", Double.valueOf(df.format(tagCounts.get("VB") + additive)));
//                    }
//                    else if(userTags[i].contains("NN")){
//                        tagCounts.put("NN", Double.valueOf(df.format(tagCounts.get("NN") + additive)));
//                    }
//                    else if(userTags[i].contains("JJ")){
//                        tagCounts.put("JJ", Double.valueOf(df.format(tagCounts.get("JJ") + additive)));
//                    }
//
//                }
//                else  tagCounts.put(userTags[i], Double.valueOf(df.format(tagCounts.get(userTags[i]) + additive)));
//            }
//
//            tagCountsAllUsers.put(user, (Object) tagCounts);
//        }
//        return tagCountsAllUsers;
//    }


public static double[][] tagging(){

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
    HashMap<String, Integer> userStatusCount = DocumentMaker.getUserStatusCount();

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



    public static void main(String[] args){



//        Iterator it = map.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            String user = (String) pair.getKey();
//            System.out.println(user+"\n\n\n");
//            HashMap<String, HashMap<String, Integer>> values = (HashMap) pair.getValue();
//            Iterator it2 = values.entrySet().iterator();
//            while(it2.hasNext()){
//                Map.Entry pair2 = (Map.Entry)it2.next();
//                System.out.println(pair2.getKey() +" "+pair2.getValue());
//                it2.remove();
//            }
//
//            it.remove(); // avoids a ConcurrentModificationException
//        }

        StringBuffer result = new StringBuffer();
        //result.append( optional separator );
        for (int i = 0; i < posTags.length; i++) {
            result.append("," + posTags[i]);
        }
        String newHeader = result.toString();

        double[][] values = tagging();
        double[][] zScoreVals = ZScores.zScores(values);
        //HashMap<String, Object> tagMap = numDistinctTagsPerUser(map);
        CSVMaker.writeToCSV("mypersonality_final.csv", "pos2.csv", zScoreVals, newHeader);
    }
}
