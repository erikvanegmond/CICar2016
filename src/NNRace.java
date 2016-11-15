import cicontest.algorithm.abstracts.AbstractRace;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.controller.Human;

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
}
