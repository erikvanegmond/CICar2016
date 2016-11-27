import javafx.scene.paint.Stop;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;

import java.io.File;

/**
 * Created by davidzomerdijk on 11/19/16.
 */
public class TrainNnBrake {
    public static void main(String[] args) {

        //create network
        BasicNetwork network = new BasicNetwork () ;
        network.addLayer(new BasicLayer(null,true,8));
        network.addLayer(new BasicLayer(new ActivationSigmoid() ,true, 30));
        network.addLayer(new BasicLayer(new ActivationLinear() ,false ,1));

        network.getStructure().finalizeStructure();
        network.reset();

        MLDataSet trainingSet = EncogUtility.loadEGB2Memory(new File("./train_data/trainingset_brake") );
        System.out.println( trainingSet.get(381) );

        //create training object
        LevenbergMarquardtTraining train = new LevenbergMarquardtTraining(  network , trainingSet ) ;

        EncogUtility.trainToError(network, trainingSet, 0.035);
        EncogDirectoryPersistence.saveObject(new File("./trained_models/brake_NN"), network);

        System.out.println("Neural Network Results:");
        for (MLDataPair pair : trainingSet ) {
            final MLData output = network.compute( pair.getInput () ) ;
            System.out.println("actual=" + output.getData(0) +  ", ideal=" + pair.getIdeal().getData(0) ) ;
        }




    }
}
