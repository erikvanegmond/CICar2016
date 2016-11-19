import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import scr.SensorModel;

import java.io.*;
import org.neuroph.core.NeuralNetwork;

public class NeuralNetworkWrapper implements Serializable {

    private static final long serialVersionUID = -88L;



//    public NeuralNetworkWrapper() {
//        this("./trained_models/acc_NN");
//    }

    BasicNetwork NN_acc ;
    public NeuralNetworkWrapper(String filePath) {
        NN_acc = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(filePath));

    }


    public double getOutput(double[] input) {
        MLData input_NN = new BasicMLData(input);
        System.out.println(input.length);
        MLData output = NN_acc.compute(input_NN);
        return output.getData(0);
    }


    //Store the state of this neural network
    public void storeGenome() {
        ObjectOutputStream out = null;
        try {
            //create the memory folder manually
            out = new ObjectOutputStream(new FileOutputStream("memory/mydriver.mem"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.writeObject(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load a neural network from memory
    public NeuralNetwork loadGenome() {

        // Read from disk using FileInputStream
        FileInputStream f_in = null;
        try {
            f_in = new FileInputStream("memory/mydriver.mem");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Read object using ObjectInputStream
        ObjectInputStream obj_in = null;
        try {
            obj_in = new ObjectInputStream(f_in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read an object
        try {
            if (obj_in != null) {
                return (NeuralNetwork) obj_in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
