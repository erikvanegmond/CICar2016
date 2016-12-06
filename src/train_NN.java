import org.encog.ml.data.MLDataSet;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.species.Species;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.neural.neat.PersistNEATPopulation;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.util.simple.EncogUtility;

import java.io.*;

/**
 * Created by davidzomerdijk on 11/8/16.
 */


public class train_NN {


    public static void main(String[] args) {

        int iterations;
        BufferedWriter training_log = null;
        try {
            String fname = Const.TRAINING_LOG;
            System.out.println(fname);
            training_log = new BufferedWriter(new FileWriter(fname, true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        MLDataSet trainingSet = EncogUtility.loadEGB2Memory(new File(Const.ALL_TRAIN_SET));


        System.out.println("Creating new NEAT population");
        //input count, output count, population size
        NEATPopulation pop;// = loadPop(Const.ALL_NN_FNAME);
        boolean usepop = false;
        if (usepop) {
            pop = loadPop(Const.ALL_NN_FNAME);
        } else {
            pop = new NEATPopulation(trainingSet.getInputSize(), trainingSet.getIdealSize(), 10);
            pop.setInitialConnectionDensity(1.0);// not required, but speeds training
            pop.reset();
        }

        EvolutionaryAlgorithm trainer;
        boolean usedata = true;
        if(usedata) {
            iterations = 100;
            trainer = NEATUtil.constructNEATTrainer(pop, new TrainingSetScore(trainingSet));
        }else{
            iterations = 100;
            System.out.println("use an actual driver");
            trainer = NEATUtil.constructNEATTrainer(pop, new DriverFitness());
        }
        // Evolve the network
        for (int i = 0; i < iterations; i++) {
            trainer.iteration();
            writeToFile(training_log, trainer);
            System.out.println("Epoch #" + trainer.getIteration()
                    + ", Error:" + trainer.getError()
                    + ", Species:" + pop.getSpecies().size()
                    + ", Pop: " + pop.getPopulationSize());
        }
        System.out.println("Training finished");
        try {
            training_log.close();
        }catch (IOException e){}
        savePop(pop);
    }

    private static void writeToFile(Writer writer, EvolutionaryAlgorithm trainer){
        try {
            StringBuilder buildString = new StringBuilder();
            buildString.append(trainer.getIteration() + ",");
            buildString.append(trainer.getError() + ",");
            Population pop = trainer.getPopulation();
            for (final Species s : pop.getSpecies()) {
                for (final Genome member : s.getMembers()) {
                    buildString.append(member.getScore() + ",");
                }
            }
            buildString.append(trainer.getPopulation().getSpecies().size());
            buildString.append("\n");
            writer.append(buildString.toString());
        } catch (IOException e) {
        }
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

