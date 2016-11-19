import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
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
import org.encog.ml.data.basic.BasicMLData;

import java.io.File;
import java.util.Arrays;

/**
 * Created by davidzomerdijk on 11/8/16.
 */



public class train_NN {


    public static void main(String[] args) {

        //create network
        BasicNetwork network = new BasicNetwork () ;
        network.addLayer(new BasicLayer(null,true,22));
        network.addLayer(new BasicLayer(new ActivationSigmoid() ,true, 30));
        network.addLayer(new BasicLayer(new ActivationSigmoid() ,false ,1));
        network.getStructure().finalizeStructure();

        MLDataSet trainingSet = EncogUtility.loadEGB2Memory(new File("./train_data/trainingset_acc") );
        System.out.println( trainingSet.get(381) );

        //create training object
        LevenbergMarquardtTraining train = new LevenbergMarquardtTraining(  network , trainingSet ) ;

        EncogUtility.trainToError(network, trainingSet, 0.06);
        EncogDirectoryPersistence.saveObject(new File("./trained_models/acc_NN"), network);

        System.out.println("Neural Network Results:");
        for (MLDataPair pair : trainingSet ) {
            final MLData output = network.compute( pair.getInput () ) ;
            System.out.println("actual=" + output.getData(0) +  ", ideal=" + pair.getIdeal().getData(0) ) ;
        }



    }

}