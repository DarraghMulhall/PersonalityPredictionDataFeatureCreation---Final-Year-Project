package fyp;


import java.util.HashMap;

public class WriteFeaturesToCSV {


    private static double[][] merge2dArrays(double[][] array1, double[][] array2, double[][] array3, double[][] array4){
        double[][] newArray = new double[array1.length][array1[0].length + array2[0].length + array3[0].length + array4[0].length];
        for(int i=0; i<array1.length; i++) {
            for(int j=0; j<array1[0].length; j++){
                newArray[i][j] = array1[i][j];
            }
            for(int j=0; j<array2[0].length; j++){
                newArray[i][array1[0].length + j] = array2[i][j];
            }
            for(int j=0; j<array3[0].length; j++){
                newArray[i][array1[0].length+array2[0].length+j] = array3[i][j];
            }
            for(int j=0; j<array4[0].length; j++){
                newArray[i][array1[0].length+array2[0].length+array3[0].length+j] = array4[i][j];
            }
        }
        return newArray;
    }


    public static void writeAllFeatures(HashMap<String, Integer> userStatusCount, String inputCSV){
        System.out.println("before aff");
        double[][] aff_values = AffWords.values();
        System.out.println("before pos");
        double[][] pos_values = POSTagging.values(userStatusCount);
        System.out.println("before h4");
        double[][] h4lvd_values = H4LVD.values(userStatusCount);
        System.out.println("before misc");
        double[][] misc_values = MiscFeatures.values(userStatusCount);

        double[][] all_values = merge2dArrays(aff_values, pos_values, h4lvd_values, misc_values);




        String aff_cols = AffWords.columns();
        String pos_cols = POSTagging.columns();
        String h4lvd_cols = H4LVD.columns();
        String misc_cols = MiscFeatures.columns();

        CSVMaker.writeToCSV(inputCSV, "features.csv", all_values, aff_cols+pos_cols+h4lvd_cols+misc_cols);
    }
}
