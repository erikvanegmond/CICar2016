import cicontest.torcs.race.RaceResult;
import cicontest.torcs.race.RaceResults;
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
        return true;
    }

    private static double computeFitness(RaceResult result) {
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
            RaceResults raceResults = race.runRace(drivers, false);
            return (RaceResult) raceResults.values().toArray()[0];
        }else{
            return new RaceResult();
        }
    }

}
