import cicontest.algorithm.abstracts.AbstractRace;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.controller.Human;

public class EVRace extends AbstractRace {

	public int[] runQualification(EVDriverGenome[] drivers, boolean withGUI){
		EVDriver[] driversList = new EVDriver[drivers.length + 1 ];
		for(int i=0; i<drivers.length; i++){
			driversList[i] = new EVDriver();
			driversList[i].loadGenome(drivers[i]);
		}
		return runQualification(driversList, withGUI);
	}

	
	public int[] runRace(EVDriverGenome[] drivers, boolean withGUI){
		int size = Math.min(10, drivers.length);
		EVDriver[] driversList = new EVDriver[size];
		for(int i=0; i<size; i++){
			driversList[i] = new EVDriver();
			driversList[i].loadGenome(drivers[i]);
		}
		return runRace(driversList, withGUI, true);
	}

	
	
	public void showBest(){
		if(DriversUtils.getStoredGenome() == null ){
			System.err.println("No best-genome known");
			return;
		}
		
		EVDriverGenome best = (EVDriverGenome) DriversUtils.getStoredGenome();
		EVDriver driver = new EVDriver();
		driver.loadGenome(best);
		
		EVDriver[] driversList = new EVDriver[1];
		driversList[0] = driver;
		runQualification(driversList, true);
	}
	
	public void showBestRace(){
		if(DriversUtils.getStoredGenome() == null ){
			System.err.println("No best-genome known");
			return;
		}
	
		EVDriver[] driversList = new EVDriver[1];
		
		for(int i=0; i<10; i++){
			EVDriverGenome best = (EVDriverGenome) DriversUtils.getStoredGenome();
			EVDriver driver = new EVDriver();
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
			EVDriverGenome best = (EVDriverGenome) DriversUtils.getStoredGenome();
			EVDriver driver = new EVDriver();
			driver.loadGenome(best);
			driversList[i] = driver;
		}
		driversList[0] = new Human();
		runRace(driversList, true, true);
	}
}
