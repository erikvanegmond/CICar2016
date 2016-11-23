import java.io.File;

import cicontest.algorithm.abstracts.AbstractAlgorithm;
import cicontest.algorithm.abstracts.AbstractRace;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.controller.Human;
import cicontest.torcs.race.Race;
import cicontest.torcs.race.RaceResult;
import cicontest.torcs.race.RaceResults;

import cicontest.torcs.race.RaceResults;
import race.TorcsConfiguration;
import scr.Controller;

public class DefaultDriverAlgorithm extends AbstractAlgorithm {

    private static final long serialVersionUID = 654963126362653L;

    DefaultDriverGenome[] drivers = new DefaultDriverGenome[1];
    //int[] results = new int[1];

    public Class<? extends Driver> getDriverClass() {
        return DefaultDriver.class;
    }

    public void run(boolean continue_from_checkpoint) {
        Race race;
        DefaultDriver driver;
        RaceResults rrs;
        RaceResult rr;

        if (!continue_from_checkpoint) {

            driver = new DefaultDriver();
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
            driver = new DefaultDriver();
            //driver.init();

            System.out.println("round 1");
            driver = new DefaultDriver();
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
            driver = new DefaultDriver();
            //driver.init();

            System.out.println("round 2");
            driver = new DefaultDriver();
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
            driver = new DefaultDriver();
            //driver.init();

            System.out.println("round 2");
            driver = new DefaultDriver();
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
            driver = new DefaultDriver();
            //driver.init();

            System.out.println("round 3");
            driver = new DefaultDriver();
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
            driver = new DefaultDriver();
            //driver.init();

            System.out.println("round 4");
            driver = new DefaultDriver();
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
            driver = new DefaultDriver();
            //driver.init();

            System.out.println("round 5");
            driver = new DefaultDriver();
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
            driver = new DefaultDriver();
            //driver.init();


            System.out.println("round 6");
            driver = new DefaultDriver();
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
            driver = new DefaultDriver();
            //driver.init();

            System.out.println("round 7");
            driver = new DefaultDriver();
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
            driver = new DefaultDriver();
            //driver.init();


//
//            DefaultDriverGenome genome = new DefaultDriverGenome();
//            drivers[0] = genome;
//
//            //Start a race
//            Race race = new Race();
//            race.setTrack("Road", "Aalborg");
//            race.setTermination(Race.Termination.LAPS, 1);
//            race.setStage(Controller.Stage.RACE);
//            race.addCompetitor(new DefaultDriver());
//            //race.addCompetitor(new DefaultDriver());
//            //race.addCompetitor(new Human());
//            Boolean withGUI = false;
//            RaceResults results;
//            if (withGUI) {
//                results = race.runWithGUI();
//            } else {
//                results = race.run();
//            }
//
//            System.out.println(results.get(0));
//            //System.out.println(results.get(1).getBestLapTime() );
//
//
//            System.out.println("print1");
//
//
//            race = new Race();
//            race.setTrack("Road", "Aalborg");
//            race.setTermination(Race.Termination.LAPS, 1);
//            race.setStage(Controller.Stage.RACE);
//            race.addCompetitor(new DefaultDriver());
//            //race.addCompetitor(new DefaultDriver());
//            //race.addCompetitor(new Human());
//            //Boolean withGUI = false;
//            //RaceResults results;
//            if (withGUI) {
//                results = race.runWithGUI();
//            } else {
//                results = race.run();
//            }
//            System.out.println(results.get(0).getLastlap() );
//
//
//
//            System.out.println("print2");
//
//
////
//            Race race = new Race();
//
//
//            DefaultDriverGenome genome = new DefaultDriverGenome();
//            drivers[0] = genome;
//
//
//            //race.setTrack("aalborg", "road");
//            race.setTrack("Forza", "road");
//            race.setTermination(Race.Termination.DISTANCE, 1000);
//            race.addCompetitor(, 0);
//            RaceResults results;
//            results = race.runWithGUI();


                // Save genome/nn
                //DriversUtils.storeGenome(drivers[0]);

//            //init NN
//            DefaultDriverGenome genome = new DefaultDriverGenome();
//            drivers[0] = genome;
//
//            //Start a race
//            DefaultRace race = new DefaultRace();
//            //race.setTrack("aalborg", "road");
//            race.setTrack("Forza", "road");
//            race.laps = 2;
//
//
//
//            //for speedup set withGUI to false
//            results = race.runRace(drivers, false);
//
//
//            // Save genome/nn
//            DriversUtils.storeGenome(drivers[0]);
            }
            // create a checkpoint this allows you to continue this run later
            //DriversUtils.createCheckpoint(this);
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
        DefaultDriverAlgorithm algorithm = new DefaultDriverAlgorithm();

        DriversUtils.registerMemory(algorithm.getDriverClass());
        if (args.length > 0 && args[0].equals("-show")) {
            new DefaultRace().showBest();
        } else if (args.length > 0 && args[0].equals("-show-race")) {
            new DefaultRace().showBestRace();
        } else if (args.length > 0 && args[0].equals("-human")) {
            new DefaultRace().raceBest();
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