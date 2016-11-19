import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;

import java.io.File;

/**
 * Created by davidzomerdijk on 11/19/16.
 */
public class TrainNnSteer {
    public static void main(String[] args) {

        //create network
        BasicNetwork network = new BasicNetwork () ;
        network.addLayer(new BasicLayer(null,true,22));
        //network.addLayer(new BasicLayer(new ActivationTANH(),true, 10));
        network.addLayer(new BasicLayer(new ActivationTANH(),false ,1));
        network.getStructure().finalizeStructure();

        MLDataSet trainingSet = EncogUtility.loadEGB2Memory(new File("./train_data/trainingset_steering") );



        //create training object
        Backpropagation train = new Backpropagation(  network , trainingSet ) ;

        EncogUtility.trainToError(network, trainingSet, 0.018);
        EncogDirectoryPersistence.saveObject(new File("./trained_models/steer_NN"), network);

        System.out.println("Neural Network Results:");
        for (MLDataPair pair : trainingSet ) {
            final MLData output = network.compute( pair.getInput () ) ;
            if(output.getData(0) < 0) {
                System.out.println("actual=" + output.getData(0) + ", ideal=" + pair.getIdeal().getData(0));
            }
        }


    }
}
