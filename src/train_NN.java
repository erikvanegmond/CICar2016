import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;
import org.encog.util.simple.TrainingSetUtil;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.SupervisedHebbianNetwork;

import java.io.File;
import java.util.Arrays;

/**
 * Created by davidzomerdijk on 11/8/16.
 */



public class train_NN {


    public static void main(String[] args) {

        //create network
        BasicNetwork network = new BasicNetwork () ;
        network.addLayer(new BasicLayer(null,true,19));
        network.addLayer(new BasicLayer(new ActivationSigmoid() ,true, 20));
        network.addLayer(new BasicLayer(new ActivationSigmoid() ,false ,1));
        network.getStructure().finalizeStructure();
        System.out.println(  network.getLayerCount() );

        MLDataSet trainingSet = TrainingSetUtil.loadCSVTOMemory(
                CSVFormat.ENGLISH, "./train_data/aalborg5.csv", true, 19, 1);

        //System.out.println( trainingSet.get(1) );

        //create training object
        LevenbergMarquardtTraining train = new LevenbergMarquardtTraining(  network , trainingSet ) ;

        EncogUtility.trainToError(network, trainingSet, 0.0000001);
        EncogDirectoryPersistence.saveObject(new File("./trained_models/acc_NN"), network);

    }

}