import cicontest.algorithm.abstracts.AbstractAlgorithm;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.race.Race;
import cicontest.torcs.race.RaceResult;
import cicontest.torcs.race.RaceResults;
import race.TorcsConfiguration;
import scr.Controller;

import java.io.File;

public class NNDriverAlgorithm_2 extends AbstractAlgorithm {

    private static final long serialVersionUID = 654963126362653L;

    NNDriverGenome[] drivers = new NNDriverGenome[1];
    int[] results = new int[1];

    public Class<? extends Driver> getDriverClass() {
        return NNDriver_2.class;
    }

    public void run(boolean continue_from_checkpoint) {
        Race race;
        NNDriver_2 driver;
        RaceResults rrs;
        RaceResult rr;

        if (!continue_from_checkpoint) {
            //init NN
            driver = new NNDriver_2();
            //driver.init();
            race = new Race();
            race.setTrack("Road", "Aalborg");
            race.setStage(Controller.Stage.RACE);
            race.setTermination(Race.Termination.LAPS, 2);
            race.addCompetitor(driver);
            rrs = race.run();
            rr = rrs.get(driver);
            System.out.println(rr.getBestLapTime());
            System.out.println("test1");



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
        NNDriverAlgorithm_2 algorithm = new NNDriverAlgorithm_2();
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

        System.exit(0);
    }


}