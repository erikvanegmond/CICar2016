import cicontest.algorithm.abstracts.AbstractRace;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.controller.Human;
import cicontest.torcs.race.Race;
import cicontest.torcs.race.RaceResult;
import cicontest.torcs.race.RaceResults;
import scr.Controller;

public class NNRace extends AbstractRace {

	public int[] runQualification(NNDriverGenome[] drivers, boolean withGUI){
		NNDriver[] driversList = new NNDriver[drivers.length + 1 ];
		for(int i=0; i<drivers.length; i++){
			driversList[i] = new NNDriver();
			driversList[i].loadGenome(drivers[i]);
		}
		return runQualification(driversList, withGUI);
	}

	
	public int[] runRace(NNDriverGenome[] drivers, boolean withGUI){
		int size = Math.min(10, drivers.length);
		NNDriver[] driversList = new NNDriver[size];
		for(int i=0; i<size; i++){
			driversList[i] = new NNDriver();
			driversList[i].loadGenome(drivers[i]);
		}
		return runRace(driversList, withGUI, true);
	}

	
	
	public void showBest(){
		if(DriversUtils.getStoredGenome() == null ){
			System.err.println("No best-genome known");
			return;
		}
		
		NNDriverGenome best = (NNDriverGenome) DriversUtils.getStoredGenome();
		NNDriver driver = new NNDriver();
		driver.loadGenome(best);
		
		NNDriver[] driversList = new NNDriver[1];
		driversList[0] = driver;
		runQualification(driversList, true);
	}
	
	public void showBestRace(){
		if(DriversUtils.getStoredGenome() == null ){
			System.err.println("No best-genome known");
			return;
		}
	
		NNDriver[] driversList = new NNDriver[1];
		
		for(int i=0; i<10; i++){
			NNDriverGenome best = (NNDriverGenome) DriversUtils.getStoredGenome();
			NNDriver driver = new NNDriver();
			driver.loadGenome(best);
			driversList[i] = driver;
		}
		
		runRace(driversList, true, true);
	}
	
	public void raceBest(){
		
		if(DriversUtils.getStoredGenome() == null ){
			System.err.println("No best-genome known");
			return;
		}
		
		Driver[] driversList = new Driver[10];
		for(int i=0; i<10; i++){
			NNDriverGenome best = (NNDriverGenome) DriversUtils.getStoredGenome();
			NNDriver driver = new NNDriver();
			driver.loadGenome(best);
			driversList[i] = driver;
		}
		driversList[0] = new Human();
		runRace(driversList, true, true);
	}

	public RaceResults runResultRace(Driver[] drivers, boolean withGUI, boolean randomOrder) {
		int[] fitness = new int[drivers.length];
		if(drivers.length > 10) {
			throw new RuntimeException("Only 10 drivers are allowed in a RACE");
		} else {
			Race race = new Race();
			race.setTrack(this.tracktype, this.track);
			race.setTermination(Race.Termination.LAPS, this.laps);
			race.setStage(Controller.Stage.RACE);
			Driver[] results = drivers;
			int i = drivers.length;

			for(int var8 = 0; var8 < i; ++var8) {
				Driver driver = results[var8];
				race.addCompetitor(driver);
			}

			if(randomOrder) {
				race.shuffleOrder();
			}

			RaceResults raceResults;
			if(withGUI) {
				raceResults = race.runWithGUI();
			} else {
				raceResults = race.run();
			}

			for(i = 0; i < drivers.length; ++i) {
				fitness[i] = ((RaceResult)raceResults.get(drivers[i])).getPosition();
			}

			this.printResults(drivers, raceResults);
			return raceResults;
		}
	}
}
