import org.encog.ml.MLRegression;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.neural.neat.PersistNEATPopulation;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.util.simple.EncogUtility;
import race.TorcsConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by erikv on 6-12-2016.
 */
public class Test {
    public static void main(String args[]){
        TorcsConfiguration.getInstance().initialize(new File("torcs.properties"));

        NEATPopulation population = loadGenome(Const.ALL_NN_FNAME);
        TrainEA trainer = NEATUtil.constructNEATTrainer(population, new TrainingSetScore(EncogUtility.loadEGB2Memory(new File(Const.ALL_TRAIN_SET) )));
        MLRegression mlNetwork = (NEATNetwork) trainer.getCODEC().decode(population.getBestGenome());
        System.out.println("hi");
//        DriverFitness df = new DriverFitness();
//        NEATNetwork network = (NEATNetwork) mlNetwork;
//        double score = df.calculateScore(network);
//        System.out.println("score: "+score);
        trainer = NEATUtil.constructNEATTrainer(population, new DriverFitness());

        System.out.println("hi2");
        trainer.iteration();
        System.out.println("hi3");

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
}
