import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.neural.neat.NEATNetwork;

/**
 * Created by erikv on 2-12-2016.
 */
public class DriverFitness implements CalculateScore {

    @Override
    public double calculateScore(MLMethod mlMethod) {
        NEATNetwork network = (NEATNetwork)mlMethod;
        int[] result = runRace(new NNDriverGenome(network));
        return computeFitness(result);
    }

    @Override
    public boolean shouldMinimize() {
        return false;
    }

    @Override
    public boolean requireSingleThreaded() {
        return false;
    }

    private static double computeFitness(int[] result) {
        return result[0];
    }

    private int[] runRace(AbstractGenome controller) {
        //init NN
        NNDriverGenome[] drivers = new NNDriverGenome[1];
        if(controller instanceof NNDriverGenome) {
            NNDriverGenome genome = (NNDriverGenome) controller;

            drivers[0] = genome;

            //Start a race
            NNRace race = new NNRace();
            race.setTrack("aalborg", "road");
            race.laps = 1;

            //for speedup set withGUI to false
            return race.runRace(drivers, false);
        }else{
            return new int[1];
        }
    }
}
