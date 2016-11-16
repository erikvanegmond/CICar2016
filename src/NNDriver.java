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

    public NNDriver() {
        initialize();
        accNN = new NeuralNetworkWrapper();
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
        double[] sensorArray = new double[19];
        sensorArray[0] = sensors.getSpeed();
        System.arraycopy(sensors.getTrackEdgeSensors(), 0, sensorArray, 1, 18);//(Object src, int srcPos, Object dest, int destPos, int length
        double output = accNN.getOutput(sensorArray);
        System.out.println(output);
        return output;
    }

    public double getBraking(SensorModel sensors) {
        double[] sensorArray = new double[19];
        sensorArray[0] = sensors.getSpeed();
        System.arraycopy(sensors.getTrackEdgeSensors(), 0, sensorArray, 1, 18);//(Object src, int srcPos, Object dest, int destPos, int length)
        double output = brakeNN.getOutput(sensorArray);
        return output;
    }
        public double getSteering(SensorModel sensors) {
        double[] sensorArray = new double[19];
        sensorArray[0] = sensors.getSpeed();
        System.arraycopy(sensors.getTrackEdgeSensors(), 0, sensorArray, 1, 18);//(Object src, int srcPos, Object dest, int destPos, int length)
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
        action.accelerate = getAcceleration(sensors);
//        action.steering = getAcceleration(sensors);
//        action.brake = getAcceleration(sensors);

        action.steering = DriversUtils.alignToTrackAxis(sensors, 0.5);

        if (sensors.getSpeed() > 60.0D) {
           // action.accelerate = 0.0D;
            action.brake = 0.0D;
        }

        action.accelerate = getAcceleration(sensors);
        if (sensors.getSpeed() > 70.0D) {
           // action.accelerate = 0.0D;
            action.brake = -1.0D;
        }

        if (sensors.getSpeed() <= 60.0D) {
           // action.accelerate = (80.0D - sensors.getSpeed()) / 80.0D;
            action.brake = 0.0D;
        }

        if (sensors.getSpeed() < 30.0D) {
           // action.accelerate = 1.0D;
            action.brake = 0.0D;
        }

        System.out.println("--------------" + getDriverName() + "--------------");
        System.out.println("Steering: " + action.steering);
        System.out.println("Acceleration: " + action.accelerate);
        System.out.println("Brake: " + action.brake);
        System.out.println("-----------------------------------------------");
        return action;
    }
}