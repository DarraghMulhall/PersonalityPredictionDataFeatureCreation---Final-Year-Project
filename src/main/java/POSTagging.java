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


    public static HashMap<String, Object> tagging(){
        HashMap<String, Object> tagCountsAllUsers = new HashMap<>();
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
        HashMap<String, ArrayList<String>> userStatuses = DocumentMaker.getUserStatuses();

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

            int userStatusNum = userStatuses.get(user).size();


            for (int i = 0; i < posTags.length; i++) {
                tagCounts.put(posTags[i], 0.0);
            }

            double additive = 1.0/userStatusNum;
            DecimalFormat df = new DecimalFormat("#.##");
            additive = Double.valueOf(df.format(additive));

            for (int i = 0; i < userTags.length; i++) {
                if(!tagCounts.containsKey(userTags[i])){
                    if(userTags[i].contains("VB")){
                        tagCounts.put("VB", Double.valueOf(df.format(tagCounts.get("VB") + additive)));
                    }
                    else if(userTags[i].contains("NN")){
                        tagCounts.put("NN", Double.valueOf(df.format(tagCounts.get("NN") + additive)));
                    }
                    else if(userTags[i].contains("JJ")){
                        tagCounts.put("JJ", Double.valueOf(df.format(tagCounts.get("JJ") + additive)));
                    }

                }
                else  tagCounts.put(userTags[i], Double.valueOf(df.format(tagCounts.get(userTags[i]) + additive)));
            }

            tagCountsAllUsers.put(user, (Object) tagCounts);
        }
        return tagCountsAllUsers;
    }


    public static void main(String[] args){

        HashMap<String, Object> map = tagging();

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
        for (int i = 0; i < posTags.length; i++) {
            result.append(","+posTags[i]);
            //result.append( optional separator );
        }
        String newHeader = result.toString();
        CSVMaker.writeToCSV("mypersonality_final.csv", "pos.csv", map, newHeader);
    }
}
