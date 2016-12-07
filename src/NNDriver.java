import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.genome.IGenome;
import scr.Action;
import scr.SensorModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class NNDriver extends AbstractDriver {
    NNDriverGenome ourGenome;
    double[][] min_max_array;
    int direction;
    Boolean on_road;
    private float steerLock=(float) (0.785398 - 0.5);

    public NNDriver() {
        initialize();
        min_max_array = load_min_max("./train_data/min_max_array.mem");
        direction = 0; // remembers last directions
    }


    private void initialize() {
        this.enableExtras(new AutomatedClutch());
        this.enableExtras(new AutomatedGearbox());
        //this.enableExtras(new AutomatedRecovering());
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

    public double getTargetAngle(SensorModel sensors){
        double targetAngle=(sensors.getAngleToTrackAxis()-sensors.getTrackPosition()*0.5);
        return targetAngle;
    }
    @Override
    public void loadGenome(IGenome genome) {
        if (genome instanceof NNDriverGenome) {
            ourGenome = (NNDriverGenome) genome;
        } else {
            System.err.println("Invalid Genome assigned");
        }
    }

    @Override
    public double getAcceleration(SensorModel sensors) {
        return ourGenome.network.getAcceleration();
    }

    public double getBraking(SensorModel sensors) {
        return ourGenome.network.getBraking();
    }

    public double getSteering(SensorModel sensors) {
        return ourGenome.network.getSteering();
    }

    @Override
    public String getDriverName() {
        return "Group 21's Race Car";
    }

    @Override
    public Action controlWarmUp(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action controlQualification(SensorModel sensors) {
        Action action = new Action();
        System.out.println("racing q");

        return defaultControl(action, sensors);
    }

    @Override
    public Action controlRace(SensorModel sensors) {
        System.out.println("racing");
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action defaultControl(Action action, SensorModel sensors) {
        double extra_steer = 0;
        if (action == null) {
            action = new Action();
        }
        ourGenome.network.updateActions(sensors);
        action.brake = getBraking(sensors);
        action.accelerate= getAcceleration(sensors);
        action.steering = getSteering(sensors);
        return action;
    }
}