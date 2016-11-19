/**
 * Created by davidzomerdijk on 11/19/16.
 */
import com.opencsv.CSVReader;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.util.simple.EncogUtility;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by davidzomerdijk on 11/19/16.
 */
public class TorcsData {
    ArrayList<String[]> Data_list;


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

    public MLDataSet make_data_set(double[][] double_array, String target_variable){
        int rows = double_array.length;
        int cols = double_array[0].length;
        //cols - 3 since there are three target columns
        double[][] input = new double[rows][ cols - 3 ];
        double[][] ideal = new double[rows][1];

        //>>>>>>>>Randomize the order of the rows

        //making input data
        for(int r=0; r<rows; r++){
            //skip first 3 rows
            for(int c=3; c <cols; c++){
                input[r][c-3] = double_array[r][c] ;
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
            default:
                target_column = 0;
                System.out.println("error: wrong target variable input.");
        }

        //creating ideal data
        for(int r=0; r<rows; r++){
            ideal[r][0] = double_array[r][target_column];
        }

        MLDataSet training_set = new BasicMLDataSet(input, ideal);
        return training_set;
    }


    public static void main(String[] args) {

        TorcsData Data = new TorcsData();
        Data.make_arraylist( "./train_data/aalborg.csv");
        Data.make_arraylist( "./train_data/f-speedway.csv");
        Data.make_arraylist( "./train_data/alpine-1.csv");
        double [][] double_array = Data.make_double_array();

        //creating datasets
        MLDataSet acc_data = Data.make_data_set(double_array, "acceleration");
        MLDataSet brake_data = Data.make_data_set(double_array, "brake");
        MLDataSet steering_data = Data.make_data_set(double_array, "steering");

        //saving datasets
        EncogUtility.saveEGB (new File("./train_data/trainingset_acc"), acc_data);
        EncogUtility.saveEGB (new File("./train_data/trainingset_brake"), brake_data);
        EncogUtility.saveEGB (new File("./train_data/trainingset_steering"), steering_data);

        System.out.println(acc_data.getInputSize());
        System.out.println(acc_data.get(1));



    }

}