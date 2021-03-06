import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.CalculateScore;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;

import java.io.File;

/**
 * Created by davidzomerdijk on 11/19/16.
 */
public class TrainNnTargetAngle {
    public static void main(String[] args) {

        //create network
        //create network
        BasicNetwork network = new BasicNetwork () ;
        network.addLayer(new BasicLayer(null,true,2));
        network.addLayer(new BasicLayer(new ActivationSigmoid() ,true, 10));
//        network.addLayer(new BasicLayer(new ActivationSigmoid() ,true, 10));
        network.addLayer(new BasicLayer(new ActivationTANH() ,true ,1));
        network.getStructure().finalizeStructure();
        network.reset();

//        NEATPopulation pop = new NEATPopulation(2, 1, 100);
//        pop.setInitialConnectionDensity(1.0);
//        pop.reset();



        MLDataSet trainingSet = EncogUtility.loadEGB2Memory(new File("./train_data/trainingset_targetAngle") );

//        CalculateScore score = new TrainingSetScore(trainingSet);

        //create training object
//        final EvolutionaryAlgorithm train = NEATUtil.constructNEATTrainer(pop, score);
        LevenbergMarquardtTraining train = new LevenbergMarquardtTraining(  network , trainingSet ) ;

        EncogUtility.trainToError(network, trainingSet, 0.028);

//        do{
//            train.iteration();
//            System.out.println("Epoch #"+ train.getIteration() + " Error:"+ train.getError());
//        }while(train.getError() > 0.03);
//
//        NEATNetwork network = (NEATNetwork) train.getCODEC().decode(train.getBestGenome());


        EncogDirectoryPersistence.saveObject(new File("./trained_models/targetAngle_NN"), network);

        System.out.println("Neural Network Results:");
        for (MLDataPair pair : trainingSet ) {
            final MLData output = network.compute( pair.getInput () ) ;
            System.out.println("actual=" + output.getData(0) +  ", ideal=" + pair.getIdeal().getData(0) ) ;
        }

        System.exit(0);

    }
}
