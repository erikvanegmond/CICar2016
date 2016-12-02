/**
 * Created by davidzomerdijk on 11/19/16.
 */
import com.opencsv.CSVReader;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.util.simple.EncogUtility;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by davidzomerdijk on 11/19/16.
 */
public class TorcsData {
    ArrayList<String[]> Data_list;
    double threshold = 0.0001;
    double[][] normalize_list;


    public TorcsData() {
        this.Data_list = new ArrayList<String[]>();
    }

    public ArrayList<String[]> make_arraylist( String csv_path){
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(csv_path));
            String[] line;
            //this because of the header
            reader.readNext();

            while ((line = reader.readNext()) != null) {
                this.Data_list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Data_list;
    }

    public double[][] make_double_array(){
        int rows = this.Data_list.size();
        int cols = this.Data_list.get(0).length;

        double[][] double_array = new double[ rows ][ cols ];

        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++){
                double_array[r][c] = Double.parseDouble(this.Data_list.get(r)[c]) ;

            }
        }

        return double_array;
    }


    public double[][] boolean_steering_set(double[][] double_array) {
        double[][] array1 = new double[double_array.length][double_array[0].length ];

        for(int r= 0; r<double_array.length; r++ ){
            for(int c = 0 ; c< double_array[0].length ;c++){
                array1[r][c] = double_array[r][c];
            }
        }

        for (int i = 0; i < array1.length; i++) {
            if (array1[i][2] > this.threshold) {
                array1[i][2] = 1;
            } else {
                array1[i][2] = 0;
            }
        }

        return array1;
    }

    public double[][] actual_steering(double[][] boolean_set, double[][] double_array){
        int count_1 = 0;
        for(int i = 0; i<boolean_set.length;i++){
            if(boolean_set[i][2] == 1){
                count_1++;
            }
        }

        double[][] array2 = new double[count_1][double_array[0].length];

        int counter = 0;
        for(int r= 0; r<double_array.length; r++ ){
            if(double_array[r][2] > this.threshold){
                for(int c = 0 ; c< double_array[0].length;c++){
                    array2[counter][c] = double_array[r][c];
                }
                counter ++;
            }
        }
        return array2;
    }

    //this function creates a list of min and max values for the data.
    //one can specify how many output values there are since we do not normalize them.
    public double[][] create_min_max(double[][] input, int number_of_output){
        int rows = input.length;
        int cols = input[0].length;
        double min;
        double max;
        double value;

        double[][] min_max_array = new double[cols - number_of_output][2];

        for(int c= number_of_output ; c<cols; c++){
            min = input[0][c];
            max = input[0][c];

            for( int r=1; r<rows; r++){
                value = input[r][c];
                if( value > max){
                    max = value;
                }
                if( value < min ){
                    min = value;
                }

            }
            min_max_array[c - number_of_output][0] = min;
            min_max_array[c - number_of_output][1] = max;
        }

        return min_max_array;
    }

    public double[][] make_normalized_array(double[][] double_array, int number_of_output){
        int rows = double_array.length;
        int cols = double_array[0].length;
        double min;
        double max;
        double value;
        double norm_value;

        double[][] min_max =  create_min_max(double_array, number_of_output);
        storeArray(min_max,"./train_data/min_max_array.mem");


        double[][] min_max_array = new double[cols - number_of_output][2];

        for(int c=number_of_output  ; c<cols; c++){

            for( int r=1; r<rows; r++){
                value = double_array[r][c];
                min = min_max[c - number_of_output][0];
                max = min_max[c - number_of_output][1];
                norm_value = (value - min) / (max - min);
                double_array[r][c] = norm_value ;
            }

        }

        return double_array;
    }

    //Stores an Array
    public void storeArray(double[][] myArray, String inFile) {
        ObjectOutputStream out = null;

        try {
            out = new ObjectOutputStream(new FileOutputStream(inFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.writeObject(myArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MLDataSet make_data_set(double[][] double_array, String target_variable){
        double[][] input;
        double[][] ideal;
        int rows;
        int cols;

        //>>>>>>>>Randomize the order of the rows
        //making input data
        if (target_variable.equals("targetAngle")){
            rows = double_array.length;
            cols = 2;
            //cols - 3 since there are three target columns
            input = new double[rows][2];
            ideal = new double[rows][1];
            for (int r = 0; r < rows; r++) {
                //skip first 3 rows
                for (int c = 1; c < 3; c++) {
                    input[r][c - 1] = double_array[r][c];
                }
            }
        }
        else {
            rows = double_array.length;
            cols = double_array[0].length;
            //cols - 3 since there are three target columns
            input = new double[rows][ cols - 3 ];
            if(target_variable.equals("all")){
                ideal = new double[rows][3];
            }else{
                ideal = new double[rows][1];
            }
            for (int r = 0; r < rows; r++) {
                //skip first 3 rows
                for (int c = 3; c < cols; c++) {
                    input[r][c - 3] = double_array[r][c];
                }
            }
        }

        int target_column;
        switch(target_variable) {
            case "acceleration": target_column = 0;
                break;
            case "brake": target_column = 1;
                break;
            case "steering": target_column = 2;
                break;
            case "targetAngle": target_column = 3;
                break;
            case "all": target_column = -1;
                break;
            default:
                target_column = 0;
                System.out.println("error: wrong target variable input.");
        }

        //creating ideal data
        if (target_column >= 0) {
            for (int r = 0; r < rows; r++) {
                ideal[r][0] = double_array[r][target_column];
            }
        }else{
            for (int r = 0; r < rows; r++) {
                for (int i = 0; i < 3; i++) {
                    ideal[r][i] = double_array[r][i];
                }
            }
        }
        MLDataSet training_set = new BasicMLDataSet(input, ideal);
        return training_set;
    }

    public double[][] removeColumn(double[][] double_array, int column){
        int temp;
        double[][] new_array = new double[double_array.length][double_array[0].length - 1];
        for(int r = 0; r<new_array.length;r++){
            temp = 0;
            for(int c = 0; c<new_array[0].length;c++){
                if(column==c ){
                    temp++;
                }
                new_array[r][c] = double_array[r][c+temp];
            }
        }

        return new_array;
    }


    public static void main(String[] args) {

        TorcsData Data = new TorcsData();
        Data.make_arraylist( "./new_train_data/train_data_aalborg.csv");
        Data.make_arraylist( "./new_train_data/train_data_alpine1.csv");
        Data.make_arraylist( "./new_train_data/train_data_brondehach.csv");
        Data.make_arraylist( "./new_train_data/train_data_dirt1.csv");
        Data.make_arraylist( "./new_train_data/train_data_E-track.csv");
//        Data.make_arraylist( "./new_train_data/aalborg_zeroone.csv");
//        Data.make_arraylist( "./new_train_data/track_2_zeroone.csv");
//        Data.make_arraylist( "./new_train_data/track_3_zeroone.csv");
//        Data.make_arraylist( "./new_train_data/track_4_zerone.csv");
        //Data.make_arraylist( "./new_train_data/forza_slow2.csv");
        //Data.make_arraylist( "./train_data/aalborg.csv");
        //Data.make_arraylist( "./train_data/f-speedway.csv");
        //Data.make_arraylist( "./train_data/alpine-1.csv");
        //Data.make_arraylist( "./new_train_data/track_1.csv");
        //Data.make_arraylist( "./new_train_data/track_2.csv");
        //Data.make_arraylist( "./new_train_data/track_3.csv");
        //Data.make_arraylist( "./new_train_data/track_4.csv");



        double [][] double_array = Data.make_double_array(); //here we put everything in a double[][]
        double_array = Data.make_normalized_array(double_array, 3); //here we normalize the data

        //creating datasets
        MLDataSet acc_data = Data.make_data_set(double_array, "acceleration");
        MLDataSet all_data = Data.make_data_set(double_array, "all");
        MLDataSet brake_data = Data.make_data_set(double_array, "brake");
        MLDataSet targetAngle_data = Data.make_data_set(double_array, "targetAngle");

        //steering doesn't take speed variable
        double[][] double_array_steer = Data.removeColumn(double_array, 3);

        double[][] steering_data_boolean = Data.boolean_steering_set(double_array_steer);
        double[][] steering_data_actual = Data.actual_steering(steering_data_boolean, double_array_steer);

        MLDataSet steering_data = Data.make_data_set( double_array_steer , "steering");
        MLDataSet steering_data_bool = Data.make_data_set( steering_data_boolean , "steering");
        MLDataSet steering_data_when_true = Data.make_data_set(steering_data_actual, "steering");

        //saving datasets
        EncogUtility.saveEGB (new File("./train_data/trainingset_acc"), acc_data);
        EncogUtility.saveEGB (new File("./train_data/trainingset_all"), all_data);
        EncogUtility.saveEGB (new File("./train_data/trainingset_brake"), brake_data);
        EncogUtility.saveEGB (new File("./train_data/trainingset_targetAngle"), targetAngle_data);
        EncogUtility.saveEGB (new File("./train_data/trainingset_steering"), steering_data);
        EncogUtility.saveEGB (new File("./train_data/trainingset_steering_boolean"), steering_data_bool);
        EncogUtility.saveEGB (new File("./train_data/trainingset_steering_when_true"), steering_data_when_true);
        //EncogUtility.saveEGB (new File("./train_data/trainingset_steering"), steering_data);

        System.out.println(all_data.getInputSize());
        System.out.println(all_data.getIdealSize());



    }

}