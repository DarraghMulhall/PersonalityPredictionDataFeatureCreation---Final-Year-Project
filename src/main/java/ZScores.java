import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ZScores {

    public static double[] mean(double[][] values) {
        double[] means = new double[values[0].length];
        double sum;
        for(int j=0; j<values[0].length; j++){
            sum = 0.0;
            for (int i=0; i<values.length; i++){
                sum += values[i][j];
            }
            means[j] = sum/(double) values.length;
        }
        return means;
    }

    public static double[] standardDeviation(double[][] values, double[] means) {
        double[] standardDevs = new double[values[0].length];
        double sum = 0.0;
        for(int j=0; j<values[0].length; j++){
            sum = 0.0;
            for (int i=0; i<values.length; i++){
                sum += Math.pow(values[i][j] - means[j], 2);
            }
            standardDevs[j] = Math.sqrt(sum/(double) values.length);
        }


        return standardDevs;
    }


//    public static HashMap<String, Object> zScores(HashMap<String, Double> map){
//        double mean = mean(map);
//        double sd = standardDeviation(map);
//        HashMap<String, Object> zScoresPerUser = new HashMap<>();
//        DecimalFormat df = new DecimalFormat("#.##");
//
//        for (Map.Entry<String, Double> entry: map.entrySet()) {
//            double score = (entry.getValue() - mean)/sd;
//            score = Double.valueOf(df.format(score));
//            zScoresPerUser.put(entry.getKey(), score);
//        }
//        return  zScoresPerUser;
//    }
    public static double[][] zScores(double[][] values){
        double[] means = mean(values);


        double[] standardDevs = standardDeviation(values, means);
        double[][] zScores = new double[values.length][values[0].length];

        DecimalFormat df = new DecimalFormat("#.##");

        double score;
        for (int i=0; i<values.length; i++) {
            for(int j=0; j<values[i].length; j++){
                if(standardDevs[j] == 0.0){
                    score = 0.0;
                }
                else {
                    score = (values[i][j] - means[j])/standardDevs[j];
                }
                score = Double.valueOf(df.format(score));
                zScores[i][j] = score;
            }

        }
        return zScores;
    }
}
