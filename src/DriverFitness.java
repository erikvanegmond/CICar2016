import cicontest.torcs.race.RaceResult;
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
        RaceResult result = runRace(new NNDriverGenome(network));
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

    private static double computeFitness(RaceResult result) {
        if (result.getLaps() == 0) {
            return 0;
        }
        // todo incorporate crashes / damage
        return result.getDistance();
    }

    private RaceResult runRace(AbstractGenome controller) {
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
            return race.runRace(drivers, false).get(0);
        }else{
            return new RaceResult();
        }
    }
}
