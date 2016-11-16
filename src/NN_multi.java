/**
 * Created by davidzomerdijk on 11/9/16.
 */

import java.util.Arrays;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.comp.layer.InputLayer;
import org.neuroph.util.TransferFunctionType;

    /**
     * This sample shows how to create, train, save and load simple Multi Layer Perceptron
     */
    public class NN_multi {
        public static void main(String[] args) {

// create training set (logical XOR function)
//            DataSet trainingSet = new DataSet(2, 1);
//            trainingSet.addRow(new DataSetRow(new double[]{0, 0}, new double[]{0}));
//            trainingSet.addRow(new DataSetRow(new double[]{0, 1}, new double[]{1}));
//            trainingSet.addRow(new DataSetRow(new double[]{1, 0}, new double[]{1}));
//            trainingSet.addRow(new DataSetRow(new double[]{1, 1}, new double[]{0}));


            DataSet trainingSet = DataSet.createFromFile("./train_data/aalborg5.csv", 19, 1, ",", true);
// create multi layer perceptron
            MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(TransferFunctionType.TANH, 19, 10, 1);

// learn the training set
            myMlPerceptron.learn(trainingSet);
            //NeuralNetwork myNN = new NeuralNetwork();
            //Layer Z2 = new Layer( 10 );
            //InputLayer Z1 = new InputLayer(18);
            //myNN.addLayer(Z1);


            myMlPerceptron.IterativeLearning
// test perceptron
            System.out.println("Testing trained neural network");
            testNeuralNetwork(myMlPerceptron, trainingSet);

// save trained neural network
            myMlPerceptron.save("trained_models/myMlPerceptron.nnet");

// load saved neural network
            NeuralNetwork loadedMlPerceptron = NeuralNetwork.createFromFile("trained_models/myMlPerceptron.nnet");

// test loaded neural network
            System.out.println("Testing loaded neural network");
            testNeuralNetwork(loadedMlPerceptron, trainingSet);

        }

        public static void testNeuralNetwork(NeuralNetwork nnet, DataSet testSet) {

            for (DataSetRow dataRow : testSet.getRows()) {
                nnet.setInput(dataRow.getInput());
                nnet.calculate();
                double[] networkOutput = nnet.getOutput();
                System.out.print("Input: " + Arrays.toString(dataRow.getInput()));
                System.out.println(" Output: " + Arrays.toString(networkOutput));
            }
        }
    }

