import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.util.simple.EncogUtility;

import java.io.File;

/**
 * Created by erikv on 2-12-2016.
 */
public class NEATNetworkWrapper extends NetworkWrapper{

    public NEATNetworkWrapper(String filePath) {
        NEATPopulation population = loadGenome(filePath);
        TrainEA trainer = NEATUtil.constructNEATTrainer(population, new TrainingSetScore(EncogUtility.loadEGB2Memory(new File(Const.ALL_TRAIN_SET) )));
        network = (NEATNetwork) trainer.getCODEC().decode(population.getBestGenome());
    }

}
