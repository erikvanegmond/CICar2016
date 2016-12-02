import org.encog.ml.data.MLDataSet;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.neural.neat.PersistNEATPopulation;
import org.encog.util.simple.EncogUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by erikv on 2-12-2016.
 */
public class Evolve {

    public static void main(String[] args){


        int iterations = 1000;


        MLDataSet trainingSet = EncogUtility.loadEGB2Memory(new File("./train_data/trainingset_all"));


        System.out.println("Creating new NEAT population");
        //input count, output count, population size
        NEATPopulation pop;// = loadPop(Const.ALL_NN_FNAME);
        boolean usepop = true;
        if (usepop) {
            pop = loadPop(Const.ALL_NN_FNAME);
        } else {
            pop = new NEATPopulation(trainingSet.getInputSize(), trainingSet.getIdealSize(), 10);
            pop.setInitialConnectionDensity(1.0);// not required, but speeds training
            pop.reset();
        }

        EvolutionaryAlgorithm trainer = NEATUtil.constructNEATTrainer(pop, new DriverFitness());

        // Evolve the network
        for (int i = 0; i < iterations; i++) {
            trainer.iteration();
            System.out.println("Epoch #" + trainer.getIteration()
                    + ", Error:" + trainer.getError()
                    + ", Species:" + pop.getSpecies().size()
                    + ", Pop: " + pop.getPopulationSize());
        }
        System.out.println("Training finished");
        savePop(pop);
    }

    private static NEATPopulation loadPop(String file) {
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

    private static void savePop(NEATPopulation population) {
        PersistNEATPopulation persistNEATPopulation = new PersistNEATPopulation();
        try {
            System.out.println("Saving at: " + Const.ALL_NN_FNAME);
            FileOutputStream output = new FileOutputStream(Const.ALL_NN_FNAME);
            persistNEATPopulation.save(output, population);
            output.close();
            System.out.println("Saving done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
