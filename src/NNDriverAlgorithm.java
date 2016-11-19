import cicontest.algorithm.abstracts.AbstractAlgorithm;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.Driver;
import race.TorcsConfiguration;

import java.io.File;

public class NNDriverAlgorithm extends AbstractAlgorithm {

    private static final long serialVersionUID = 654963126362653L;

    NNDriverGenome[] drivers = new NNDriverGenome[1];
    int[] results = new int[1];

    public Class<? extends Driver> getDriverClass() {
        return NNDriver.class;
    }

    public void run(boolean continue_from_checkpoint) {
        if (!continue_from_checkpoint) {
            //init NN
            NNDriverGenome genome = new NNDriverGenome();
            drivers[0] = genome;

            //Start a race
            NNRace race = new NNRace();
            race.setTrack("aalborg", "road");
            race.laps = 1;

            //for speedup set withGUI to false
            results = race.runRace(drivers, true);


            // Save genome/nn
           // DriversUtils.storeGenome(drivers[0]);
        }
        // create a checkpoint this allows you to continue this run later
        DriversUtils.createCheckpoint(this);
        //DriversUtils.clearCheckpoint();
    }

    public static void main(String[] args) {

        //Set path to torcs.properties
        TorcsConfiguration.getInstance().initialize(new File("torcs.properties"));
        /*
		 *
		 * Start without arguments to run the algorithm
		 * Start with -continue to continue a previous run
		 * Start with -show to show the best found
		 * Start with -show-race to show a race with 10 copies of the best found
		 * Start with -human to race against the best found
		 *
		 */
        NNDriverAlgorithm algorithm = new NNDriverAlgorithm();
        DriversUtils.registerMemory(algorithm.getDriverClass());
        if (args.length > 0 && args[0].equals("-show")) {
            new NNRace().showBest();
        } else if (args.length > 0 && args[0].equals("-show-race")) {
            new NNRace().showBestRace();
        } else if (args.length > 0 && args[0].equals("-human")) {
            new NNRace().raceBest();
        } else if (args.length > 0 && args[0].equals("-continue")) {
            if (DriversUtils.hasCheckpoint()) {
                DriversUtils.loadCheckpoint().run(true);
            } else {
                algorithm.run();
            }
        } else {
            algorithm.run();
        }
    }

}