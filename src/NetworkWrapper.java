import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.PersistNEATPopulation;
import scr.SensorModel;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by erikv on 2-12-2016.
 */
public abstract class NetworkWrapper {
    public MLRegression network;
    public double[] actions;

    public void updateActions(SensorModel sensors){
        double[] sensorArray = makeSensorArray(sensors);
        MLData mlData = new BasicMLData(sensorArray);
        MLData result = network.compute(mlData);
        actions=result.getData();
    }

    private double[] makeSensorArray(SensorModel sensors) {
        double[] sensorArray = new double[9];

        sensorArray[0] = sensors.getSpeed();
        sensorArray[1] = sensors.getTrackPosition();
        sensorArray[2] = sensors.getAngleToTrackAxis();

        sensorArray[3] = getTargetAngle(sensors);

        sensorArray[4] = sensors.getTrackEdgeSensors()[4];
        sensorArray[5] = sensors.getTrackEdgeSensors()[6];
        sensorArray[6] = sensors.getTrackEdgeSensors()[9];
        sensorArray[7] = sensors.getTrackEdgeSensors()[12];
        sensorArray[8] = sensors.getTrackEdgeSensors()[14];
        return sensorArray;
    }

    public double getTargetAngle(SensorModel sensors){
        double targetAngle=(sensors.getAngleToTrackAxis()-sensors.getTrackPosition()*0.5);
        return targetAngle;
    }


    public static NEATPopulation loadGenome(String file) {
        PersistNEATPopulation persistNEATPopulation = new PersistNEATPopulation();
        try {
            FileInputStream input = new FileInputStream(file);
            NEATPopulation population = (NEATPopulation) persistNEATPopulation.read(input);
            input.close();
            return population;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getSteering() {
        return actions[2];
    }
    public double getBraking() {
        return actions[1];
    }
    public double getAcceleration() {
        return actions[0];
    }


}
