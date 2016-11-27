import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.controller.extras.AutomatedRecovering;
import cicontest.torcs.genome.IGenome;
import scr.Action;
import scr.SensorModel;

import javax.sound.midi.Soundbank;
import javax.sound.sampled.BooleanControl;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class NNDriver extends AbstractDriver {
    NeuralNetworkWrapper accNN;
    NeuralNetworkWrapper brakeNN;
    NeuralNetworkWrapper steerNN;
    RecoverAuto recover;
    double[][] min_max_array;
    int direction;
    Boolean on_road;
    private float steerLock=(float) (0.785398 - 0.5);

    public NNDriver() {
        initialize();
        accNN = new NeuralNetworkWrapper("./trained_models/acc_NN");
        brakeNN = new NeuralNetworkWrapper("./trained_models/brake_NN");
        steerNN = new NeuralNetworkWrapper("./trained_models/steer_NN");
        recover = new RecoverAuto();
        on_road = true;
        min_max_array = load_min_max("./train_data/min_max_array.mem");
        direction = 0; // remembers last directions
//        neuralNetwork = neuralNetwork.loadGenome();

    }


    private void initialize() {
        this.enableExtras(new AutomatedClutch());
        this.enableExtras(new AutomatedGearbox());
        this.enableExtras(new AutomatedRecovering());
        this.enableExtras(new ABS());
    }

    //I found this function online and fitted it to my needs
    public double[][] load_min_max(String inFile) {
        // Read from disk using FileInputStream
        FileInputStream f_in = null;
        try {
            f_in = new FileInputStream(inFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Read object using ObjectInputStream
        ObjectInputStream obj_in = null;
        try {
            obj_in = new ObjectInputStream(f_in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Read an object
        try {
            return (double[][]) obj_in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    double[] normalize_array(double[] input ){
        double value;
        double min;
        double max;

        double[] output = new double[ input.length ];

        for(int i=0; i<input.length; i++ ){

            value = input[i];
            min = this.min_max_array[i][0];
            max = this.min_max_array[i][1];
            output[i] = (value - min) / (max - min);
        }

        return output;
    }

    @Override
    public void loadGenome(IGenome genome) {
        if (genome instanceof DefaultDriverGenome) {
            DefaultDriverGenome myGenome = (DefaultDriverGenome) genome;
        } else {
            System.err.println("Invalid Genome assigned");
        }
    }

    @Override
    public double getAcceleration(SensorModel sensors) {
        double[] sensorArray = new double[8]; // new double[22];
        sensorArray[0] = sensors.getSpeed();
        sensorArray[1] = sensors.getTrackPosition();
        sensorArray[2] = sensors.getAngleToTrackAxis();

        sensorArray[3] = sensors.getTrackEdgeSensors()[4];
        sensorArray[4] = sensors.getTrackEdgeSensors()[6];
        sensorArray[5] = sensors.getTrackEdgeSensors()[9];
        sensorArray[6] = sensors.getTrackEdgeSensors()[12];
        sensorArray[7] = sensors.getTrackEdgeSensors()[14];
        //System.arraycopy(sensors.getTrackEdgeSensors(), 0, sensorArray, 3, 19);

        double output = accNN.getOutput(sensorArray);

        return output;

    }

    public double getBraking(SensorModel sensors) {
        double[] sensorArray = new double[8];
        sensorArray[0] = sensors.getSpeed();
        sensorArray[1] = sensors.getTrackPosition();
        sensorArray[2] = sensors.getAngleToTrackAxis();

        sensorArray[3] = sensors.getTrackEdgeSensors()[4];
        sensorArray[4] = sensors.getTrackEdgeSensors()[6];
        sensorArray[5] = sensors.getTrackEdgeSensors()[9];
        sensorArray[6] = sensors.getTrackEdgeSensors()[12];
        sensorArray[7] = sensors.getTrackEdgeSensors()[14];
        //System.arraycopy(sensors.getTrackEdgeSensors(), 0, sensorArray, 3, 19);
        //normalize the input since the NN is trained on normalized data.
        sensorArray= normalize_array( sensorArray );
        double output = brakeNN.getOutput(sensorArray);

        return output;
    }

    public double getSteering(SensorModel sensors) {
        double[] sensorArray = new double[7];
        //sensorArray[0] = sensors.getSpeed();
        sensorArray[0] = sensors.getTrackPosition();
        sensorArray[1] = sensors.getAngleToTrackAxis();

        sensorArray[2] = sensors.getTrackEdgeSensors()[4];
        sensorArray[3] = sensors.getTrackEdgeSensors()[6];
        sensorArray[4] = sensors.getTrackEdgeSensors()[9];
        sensorArray[5] = sensors.getTrackEdgeSensors()[12];
        sensorArray[6] = sensors.getTrackEdgeSensors()[14];

        //System.arraycopy(sensors.getTrackEdgeSensors(), 0, sensorArray, 3, 19);

        double output = steerNN.getOutput(sensorArray);
        return output;
    }



    @Override
    public String getDriverName() {
        return "Neuron Controller";
    }

    @Override
    public Action controlWarmUp(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action controlQualification(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action controlRace(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action defaultControl(Action action, SensorModel sensors) {
        if (action == null) {
            action = new Action();
        }
        //process whether we all stuck or not


        System.out.print("am I on track?: ");
        System.out.println(sensors.getTrackEdgeSensors()[18] );

        action.steering = getSteering(sensors);

        if(sensors.getSpeed() < 61){
            action.accelerate = 1;
        }
        //action.accelerate = getAcceleration(sensors);
        action.brake = getBraking(sensors);


        System.out.println(sensors.getTrackPosition()) ;
        System.out.println("--------------" + getDriverName() + "--------------");
        System.out.println("Steering: " + action.steering);
        System.out.println("Acceleration: " + action.accelerate);
        System.out.println("Brake: " + action.brake);
        System.out.println("-----------------------------------------------");
        return action;
    }
}