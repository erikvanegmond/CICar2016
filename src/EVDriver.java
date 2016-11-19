import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.controller.extras.AutomatedRecovering;
import cicontest.torcs.genome.IGenome;
import scr.Action;
import scr.SensorModel;

public class EVDriver extends AbstractDriver {
    NeuralNetworkWrapper accEV;
    NeuralNetworkWrapper brakeEV;
    NeuralNetworkWrapper steerEV;

    public EVDriver() {
        initialize();
        accEV = new NeuralNetworkWrapper("");
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
        double output = accEV.getOutput(sensorArray);
        System.out.println(output);
        return output;
    }

    public double getBraking(SensorModel sensors) {
        double[] sensorArray = new double[19];
        sensorArray[0] = sensors.getSpeed();
        System.arraycopy(sensors.getTrackEdgeSensors(), 0, sensorArray, 1, 18);//(Object src, int srcPos, Object dest, int destPos, int length)
        double output = brakeEV.getOutput(sensorArray);
        return output;
    }
        public double getSteering(SensorModel sensors) {
        double[] sensorArray = new double[19];
        sensorArray[0] = sensors.getSpeed();
        System.arraycopy(sensors.getTrackEdgeSensors(), 0, sensorArray, 1, 18);//(Object src, int srcPos, Object dest, int destPos, int length)
        double output = steerEV.getOutput(sensorArray);
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
        action.steering = getAcceleration(sensors);
        action.brake = getAcceleration(sensors);

        System.out.println("--------------" + getDriverName() + "--------------");
        System.out.println("Steering: " + action.steering);
        System.out.println("Acceleration: " + action.accelerate);
        System.out.println("Brake: " + action.brake);
        System.out.println("-----------------------------------------------");
        return action;
    }
}