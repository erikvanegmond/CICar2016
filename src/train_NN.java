import org.encog.ml.CalculateScore;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.CompoundOperator;
import org.encog.ml.ea.opp.selection.TruncationSelection;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.species.Species;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.hyperneat.HyperNEATCODEC;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.neural.neat.PersistNEATPopulation;
import org.encog.neural.neat.training.opp.*;
import org.encog.neural.neat.training.opp.links.MutatePerturbLinkWeight;
import org.encog.neural.neat.training.opp.links.MutateResetLinkWeight;
import org.encog.neural.neat.training.opp.links.SelectFixed;
import org.encog.neural.neat.training.opp.links.SelectProportion;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.util.simple.EncogUtility;
import race.TorcsConfiguration;

import java.io.*;

/**
 * Created by davidzomerdijk on 11/8/16.
 */


public class train_NN {


    public static void main(String[] args) {
        TorcsConfiguration.getInstance().initialize(new File("torcs.properties"));

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
        boolean usepop = true;
        if (usepop) {
            pop = loadPop(Const.ALL_NN_FNAME);
        } else {
            pop = new NEATPopulation(trainingSet.getInputSize(), trainingSet.getIdealSize(), 2);
            pop.setInitialConnectionDensity(1.0);// not required, but speeds training
            pop.reset();
        }

        EvolutionaryAlgorithm trainer;
        boolean usedata = false;
        if(usedata) {
            iterations = 100;
            trainer = NEATUtil.constructNEATTrainer(pop, new TrainingSetScore(trainingSet));
        }else{
            iterations = 100;
            System.out.println("use an actual driver");
            trainer = constructNEATTrainer(pop, new DriverFitness());
        }
        // Evolve the network
        for (int i = 0; i < iterations; i++) {
            System.out.println("Iterating....");
            System.out.println("============================================================");
            System.out.println("============================================================");
            System.out.println("============================================================");
            System.out.println("============================================================");
            trainer.iteration();
            System.out.println("Iteration done...");
            System.out.println("============================================================");
            System.out.println("============================================================");
            System.out.println("============================================================");
            System.out.println("============================================================");
//            writeToFile(training_log, trainer);
//            System.out.println("Epoch #" + trainer.getIteration()
//                    + ", Error:" + trainer.getError()
//                    + ", Species:" + pop.getSpecies().size()
//                    + ", Pop: " + pop.getPopulationSize());
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
            int pop_count = 0;
            for (final Species s : pop.getSpecies()) {
                for (final Genome member : s.getMembers()) {
                    buildString.append(member.getScore() + ",");
                    pop_count++;
                }
            }
            while(pop_count < 10){
                buildString.append(",");
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

    public static TrainEA constructNEATTrainer(final NEATPopulation population,
                                               final CalculateScore calculateScore) {
        final TrainEA result = new TrainEA(population, calculateScore);
        result.setSpeciation(new OriginalNEATSpeciation());

        result.setSelection(new TruncationSelection(result, 0.3));
        final CompoundOperator weightMutation = new CompoundOperator();
        weightMutation.getComponents().add(
                0.1125,
                new NEATMutateWeights(new SelectFixed(1),
                        new MutatePerturbLinkWeight(0.02)));
        weightMutation.getComponents().add(
                0.1125,
                new NEATMutateWeights(new SelectFixed(2),
                        new MutatePerturbLinkWeight(0.02)));
        weightMutation.getComponents().add(
                0.1125,
                new NEATMutateWeights(new SelectFixed(3),
                        new MutatePerturbLinkWeight(0.02)));
        weightMutation.getComponents().add(
                0.1125,
                new NEATMutateWeights(new SelectProportion(0.02),
                        new MutatePerturbLinkWeight(0.02)));
        weightMutation.getComponents().add(
                0.1125,
                new NEATMutateWeights(new SelectFixed(1),
                        new MutatePerturbLinkWeight(1)));
        weightMutation.getComponents().add(
                0.1125,
                new NEATMutateWeights(new SelectFixed(2),
                        new MutatePerturbLinkWeight(1)));
        weightMutation.getComponents().add(
                0.1125,
                new NEATMutateWeights(new SelectFixed(3),
                        new MutatePerturbLinkWeight(1)));
        weightMutation.getComponents().add(
                0.1125,
                new NEATMutateWeights(new SelectProportion(0.02),
                        new MutatePerturbLinkWeight(1)));
        weightMutation.getComponents().add(
                0.03,
                new NEATMutateWeights(new SelectFixed(1),
                        new MutateResetLinkWeight()));
        weightMutation.getComponents().add(
                0.03,
                new NEATMutateWeights(new SelectFixed(2),
                        new MutateResetLinkWeight()));
        weightMutation.getComponents().add(
                0.03,
                new NEATMutateWeights(new SelectFixed(3),
                        new MutateResetLinkWeight()));
        weightMutation.getComponents().add(
                0.01,
                new NEATMutateWeights(new SelectProportion(0.02),
                        new MutateResetLinkWeight()));
        weightMutation.getComponents().finalizeStructure();

        result.setChampMutation(weightMutation);
        result.addOperation(0.5, new NEATCrossover());
        result.addOperation(0.494, weightMutation);
        result.addOperation(0.0005, new NEATMutateAddNode());
        result.addOperation(0.005, new NEATMutateAddLink());
        result.addOperation(0.0005, new NEATMutateRemoveLink());
        result.getOperators().finalizeStructure();

        if (population.isHyperNEAT()) {
            result.setCODEC(new HyperNEATCODEC());
        } else {
            result.setCODEC(new NEATCODEC());
        }

        return result;
    }


}

