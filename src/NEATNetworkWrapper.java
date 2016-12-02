import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.neural.neat.PersistNEATPopulation;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.util.simple.EncogUtility;
import scr.SensorModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by erikv on 2-12-2016.
 */
public class NEATNetworkWrapper {

    private NEATNetwork network;
    private double[] actions;

    public NEATNetworkWrapper(String filePath) {
        NEATPopulation population = loadGenome(filePath);
        TrainEA trainer = NEATUtil.constructNEATTrainer(population, new TrainingSetScore(EncogUtility.loadEGB2Memory(new File(Const.ALL_TRAIN_SET) )));
        network = (NEATNetwork) trainer.getCODEC().decode(population.getBestGenome());
        long startTime = System.currentTimeMillis();
//        double[] d = {-2.18682,-2.42709,0.57912,-1.0,-1.0,-1.0,-1.0,-1.0,1.0};
//        System.out.println(network.compute(new BasicMLData(d)));
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("loaded network"+estimatedTime);
    }

    public void updateActions(SensorModel sensors){
        double[] sensorArray = makeSensorArray(sensors);
        MLData mlData = new BasicMLData(sensorArray);
        MLData result = network.compute(mlData);
        System.out.println(result.toString());
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


    private static NEATPopulation loadGenome(String file) {
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
