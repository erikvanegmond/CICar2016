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

public class NNDriver extends AbstractDriver {
    NeuralNetworkWrapper accNN;
    NeuralNetworkWrapper brakeNN;
    NeuralNetworkWrapper steerNN;
    RecoverAuto recover;

    public NNDriver() {
        initialize();
        accNN = new NeuralNetworkWrapper("./trained_models/acc_NN");
        brakeNN = new NeuralNetworkWrapper("./trained_models/brake_NN");
        steerNN = new NeuralNetworkWrapper("./trained_models/steer_NN");
        recover = new RecoverAuto();
//        neuralNetwork = neuralNetwork.loadGenome();

    }

    private void initialize() {
        this.enableExtras(new AutomatedClutch());
        this.enableExtras(new AutomatedGearbox());
        this.enableExtras(new AutomatedRecovering());
        this.enableExtras(new ABS());
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
        double[] sensorArray = new double[22];
        sensorArray[0] = sensors.getSpeed();
        sensorArray[1] = sensors.getTrackPosition();
        sensorArray[2] = sensors.getAngleToTrackAxis();
        System.arraycopy(sensors.getTrackEdgeSensors(), 0, sensorArray, 3, 19);

        double output = accNN.getOutput(sensorArray);

        if( output > 0.7) {
            return 1;
        }
        else {
            return 0;
        }

    }

    public double getBraking(SensorModel sensors) {
        double[] sensorArray = new double[22];
        sensorArray[0] = sensors.getSpeed();
        sensorArray[1] = sensors.getTrackPosition();
        sensorArray[2] = sensors.getAngleToTrackAxis();

        System.arraycopy(sensors.getTrackEdgeSensors(), 0, sensorArray, 3, 19);

        double output = brakeNN.getOutput(sensorArray);

        if( output > 0.7) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public double getSteering(SensorModel sensors) {
        double[] sensorArray = new double[22];
        sensorArray[0] = sensors.getSpeed();
        sensorArray[1] = sensors.getTrackPosition();
        sensorArray[2] = sensors.getAngleToTrackAxis();

        System.arraycopy(sensors.getTrackEdgeSensors(), 0, sensorArray, 3, 19);

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
        this.recover.process(action, sensors);

        System.out.print("am I on track?: ");
        System.out.println(sensors.getTrackEdgeSensors()[18] );


        if( this.recover.getStuck() > 5) {
            System.out.println("Recovery that auto sweet sweet hard code!!!!");
            this.recover.process(action, sensors);
        }
        else {
            if( sensors.getTrackEdgeSensors()[18] == -1 ){
                //action.steering = (sensors.getAngleToTrackAxis() / 0.785398006439209D);
            }
            else{
                action.accelerate = getAcceleration(sensors);
                action.brake = getBraking(sensors);
                action.steering = getSteering(sensors);
            }

        }

        System.out.println("--------------" + getDriverName() + "--------------");
        System.out.println("Steering: " + action.steering);
        System.out.println("Acceleration: " + action.accelerate);
        System.out.println("Brake: " + action.brake);
        System.out.println("-----------------------------------------------");
        return action;
    }
}