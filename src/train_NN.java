import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.SupervisedHebbianNetwork;

import java.util.Arrays;

/**
 * Created by davidzomerdijk on 11/8/16.
 */

public class train_NN {


    public static void main(String[] args) {

        DataSet trainingSet = DataSet.createFromFile("./train_data/aalborg4.csv", 19, 1, ",", true);
        System.out.println(trainingSet.toString());
        SupervisedHebbianNetwork NN_acc = new SupervisedHebbianNetwork( 19, 1);
        System.out.println("start learning");
        NN_acc.learn(trainingSet);

        System.out.println("the code has run");

      for(DataSetRow dataRow : trainingSet.getRows() ) {NN_acc.setInput(dataRow.getInput());
          NN_acc.calculate();
          double[ ] networkOutput = NN_acc.getOutput();
          System.out.println("data: " + Arrays.toString(dataRow.getDesiredOutput() ));
          System.out.print("Input: " + Arrays.toString(dataRow.getInput()) );
          System.out.println(" Output: " + Arrays.toString(networkOutput) );
      }

    }

}