import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.torcs.genome.IGenome;
import scr.Action;
import scr.SensorModel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class DefaultDriver extends AbstractDriver{

	//universal constants
	final float sin5 = (float) 0.08716;
	final float cos5 = (float) 0.99619;

	//Initial settings
	private int stuck=0;
	// current clutch
	private float clutch=0;


	//Gene Values;
	/* Gear Changing Constants*/
	final int[]  gearUp={5000,6000,6000,6500,7000,0};
	final int[]  gearDown={0,2500,3000,3000,3500,3500};

	/* Stuck constants*/
	final int  stuckTime = 25;
	final float  stuckAngle = (float) 0.523598775; //PI/6

	/* Accel and Brake Constants*/
	final float maxSpeedDist=10;
	final float maxSpeed=60;

	/* Steering constants*/
	final float steerLock=(float) 0.785398;
	final float steerSensitivityOffset=(float) 80.0;
	final float wheelSensitivityCoeff=1;

	/* ABS Filter Constants */
	final float wheelRadius[]={(float) 0.3179,(float) 0.3179,(float) 0.3276,(float) 0.3276};
	final float absSlip=(float) 2.0;
	final float absRange=(float) 3.0;
	final float absMinSpeed=(float) 3.0;
	
	/* Clutching Constants */
	final float clutchMax=(float) 0.5;
	final float clutchDelta=(float) 0.05;
	final float clutchRange=(float) 0.82;
	final float	clutchDeltaTime=(float) 0.02;
	final float clutchDeltaRaced=10;
	final float clutchDec=(float) 0.01;
	final float clutchMaxModifier=(float) 1.3;
	final float clutchMaxTime=(float) 1.5;



    private Writer train_data;

	public DefaultDriver(){
		System.out.println("driver started");
//        Date d = new java.util.Date();
		try {
		    String fname = "train_data"+System.currentTimeMillis()+".csv";
            System.out.println(fname);
            train_data = new BufferedWriter(new FileWriter(fname, true));
		}catch ( IOException e){
		    e.printStackTrace();
//            System.out.println();
		}

	}

	@Override
	public String getDriverName() {
		return null;
	}

	@Override
	public Action controlWarmUp(SensorModel sensorModel) {
		return controlRace(sensorModel);
	}

	@Override
	public Action controlQualification(SensorModel sensorModel) {
		return controlRace(sensorModel);
	}


	@Override
	public void loadGenome(IGenome iGenome) {

	}

	public void reset() {
		System.out.println("Restarting the race!");
		
	}

	public void shutdown() {
		try {
			train_data.close();
		}catch (IOException e){}
		System.out.println("Bye bye!");		
	}


	private int getGear(SensorModel sensors){
	    int gear = sensors.getGear();
	    double rpm  = sensors.getRPM();

	    // if gear is 0 (N) or -1 (R) just return 1 
	    if (gear<1)
	        return 1;
	    // check if the RPM value of car is greater than the one suggested 
	    // to shift up the gear from the current one     
	    if (gear <6 && rpm >= gearUp[gear-1])
	        return gear + 1;
	    else
	    	// check if the RPM value of car is lower than the one suggested 
	    	// to shift down the gear from the current one
	        if (gear > 1 && rpm <= gearDown[gear-1])
	            return gear - 1;
	        else // otherwhise keep current gear
	            return gear;
	}

	@Override
	public double getSteering(SensorModel sensors){
		// steering angle is compute by correcting the actual car angle w.r.t. to track 
		// axis [sensors.getAngle()] and to adjust car position w.r.t to middle of track [sensors.getTrackPos()*0.5]
		double targetAngle=(float) (sensors.getAngleToTrackAxis()-sensors.getTrackPosition()*0.5);
	    // at high speed reduce the steering command to avoid loosing the control
	    if (sensors.getSpeed() > steerSensitivityOffset)
	        return (double) (targetAngle/(steerLock*(sensors.getSpeed()-steerSensitivityOffset)*wheelSensitivityCoeff));
	    else
	        return (targetAngle)/steerLock;

	}

	@Override
	public double getAcceleration(SensorModel sensors)
	{
	    // checks if car is out of track
	    if (sensors.getTrackPosition() < 1 && sensors.getTrackPosition() > -1)
	    {
	        // reading of sensor at +5 degree w.r.t. car axis
			double rxSensor=(double) sensors.getTrackEdgeSensors()[10];
	        // reading of sensor parallel to car axis
			double sensorsensor=(double) sensors.getTrackEdgeSensors()[9];
	        // reading of sensor at -5 degree w.r.t. car axis
			double sxSensor=(double) sensors.getTrackEdgeSensors()[8];

			double targetSpeed;

	        // track is straight and enough far from a turn so goes to max speed
	        if (sensorsensor>maxSpeedDist || (sensorsensor>=rxSensor && sensorsensor >= sxSensor))
	            targetSpeed = maxSpeed;
	        else
	        {
	            // approaching a turn on right
	            if(rxSensor>sxSensor)
	            {
	                // computing approximately the "angle" of turn
					double h = sensorsensor*sin5;
					double b = rxSensor - sensorsensor*cos5;
					double sinAngle = b*b/(h*h+b*b);
	                // estimate the target speed depending on turn and on how close it is
	                targetSpeed = maxSpeed*(sensorsensor*sinAngle/maxSpeedDist);
	            }
	            // approaching a turn on left
	            else
	            {
	                // computing approximately the "angle" of turn
					double h = sensorsensor*sin5;
					double b = sxSensor - sensorsensor*cos5;
					double sinAngle = b*b/(h*h+b*b);
	                // estimate the target speed depending on turn and on how close it is
	                targetSpeed = maxSpeed*(sensorsensor*sinAngle/maxSpeedDist);
	            }

	        }

	        // accel/brake command is exponentially scaled w.r.t. the difference between target speed and current one
	        return (2/(1+Math.exp(sensors.getSpeed() - targetSpeed)) - 1);
	    }
	    else
	        return 0.3; // when out of track returns a moderate acceleration command

	}

	@Override
	public Action controlRace(SensorModel sensors){
//		System.out.println(sensorsToString(sensors));
//		try {
//			input.append(sensorsToString(sensors));
//		}catch (IOException e){}
//		System.out.println("hi");
		// check if car is currently stuck
		if ( Math.abs(sensors.getAngleToTrackAxis()) > stuckAngle )
	    {
			// update stuck counter
	        stuck++;
	    }
	    else
	    {
	    	// if not stuck reset stuck counter
	        stuck = 0;
	    }

		// after car is stuck for a while apply recovering policy
	    if (stuck > stuckTime)
	    {
	    	/* set gear and sterring command assuming car is 
	    	 * pointing in a direction out of track */
	    	
	    	// to bring car parallel to track axis
			double steer = (double) (- sensors.getAngleToTrackAxis() / steerLock);
	        int gear=-1; // gear R
	        
	        // if car is pointing in the correct direction revert gear and steer  
	        if (sensors.getAngleToTrackAxis()*sensors.getTrackPosition()>0)
	        {
	            gear = 1;
	            steer = -steer;
	        }
	        clutch = clutching(sensors, clutch);
	        // build a CarControl variable and return it
	        Action action = new Action ();
	        action.gear = gear;
	        action.steering = steer;
	        action.accelerate = 1.0;
	        action.brake = 0;
	        action.clutch = clutch;
            writeToFile(sensors, action);
            return action;
	    }

	    else // car is not stuck
	    {
	    	// compute accel/brake command
			double accel_and_brake = getAcceleration(sensors);
	        // compute gear
	        int gear = getGear(sensors);
	        // compute steering
			double steer = getSteering(sensors);
	        

	        // normalize steering
	        if (steer < -1)
	            steer = -1;
	        if (steer > 1)
	            steer = 1;
	        
	        // set accel and brake from the joint accel/brake command 
			double accel,brake;
	        if (accel_and_brake>0)
	        {
	            accel = accel_and_brake;
	            brake = 0;
	        }
	        else
	        {
	            accel = 0;
	            // apply ABS to brake
	            brake = filterABS(sensors,-accel_and_brake);
	        }
	        
	        clutch = clutching(sensors, clutch);
	        
	        // build a CarControl variable and return it
	        Action action = new Action ();
	        action.gear = gear;
	        action.steering = steer;
	        action.accelerate = accel;
	        action.brake = brake;
	        action.clutch = clutch;
            writeToFile(sensors, action);
			return action;
	    }
	}

	private double filterABS(SensorModel sensors,double brake){
		// convert speed to m/s
		double speed = (double) (sensors.getSpeed() / 3.6);
		// when spedd lower than min speed for abs do nothing
	    if (speed < absMinSpeed)
	        return brake;
	    
	    // compute the speed of wheels in m/s
		double slip = 0.0f;
	    for (int i = 0; i < 4; i++)
	    {
	        slip += sensors.getWheelSpinVelocity()[i] * wheelRadius[i];
	    }
	    // slip is the difference between actual speed of car and average speed of wheels
	    slip = speed - slip/4.0f;
	    // when slip too high applu ABS
	    if (slip > absSlip)
	    {
	        brake = brake - (slip - absSlip)/absRange;
	    }
	    
	    // check brake is not negative, otherwise set it to zero
	    if (brake<0)
	    	return 0;
	    else
	    	return brake;
	}
	
	float clutching(SensorModel sensors, float clutch)
	{
	  	 
	  float maxClutch = clutchMax;

	  // Check if the current situation is the race start
	  if (sensors.getCurrentLapTime()<clutchDeltaTime  && getStage()==Stage.RACE && sensors.getDistanceRaced()<clutchDeltaRaced)
	    clutch = maxClutch;

	  // Adjust the current value of the clutch
	  if(clutch > 0)
	  {
	    double delta = clutchDelta;
	    if (sensors.getGear() < 2)
		{
	      // Apply a stronger clutch output when the gear is one and the race is just started
		  delta /= 2;
	      maxClutch *= clutchMaxModifier;
	      if (sensors.getCurrentLapTime() < clutchMaxTime)
	        clutch = maxClutch;
		}

	    // check clutch is not bigger than maximum values
		clutch = Math.min(maxClutch,clutch);

		// if clutch is not at max value decrease it quite quickly
		if (clutch!=maxClutch)
		{
		  clutch -= delta;
		  clutch = Math.max((float) 0.0,clutch);
		}
		// if clutch is at max value decrease it very slowly
		else
			clutch -= clutchDec;
	  }
	  return clutch;
	}
	
	public float[] initAngles()	{
		
		float[] angles = new float[19];

		/* set angles as {-90,-75,-60,-45,-30,-20,-15,-10,-5,0,5,10,15,20,30,45,60,75,90} */
		for (int i=0; i<5; i++)
		{
			angles[i]=-90+i*15;
			angles[18-i]=90-i*15;
		}

		for (int i=5; i<9; i++)
		{
				angles[i]=-20+(i-5)*5;
				angles[18-i]=20-(i-5)*5;
		}
		angles[9]=0;
		return angles;
	}

	private void writeToFile(SensorModel sensors, Action action){
        try {
            String line = actionsToString(action) +","+sensorsToString(sensors) +"\n";
            train_data.append(line);
        } catch (IOException e) {
        }

    }

	private String sensorsToString(SensorModel sensors){
		String sensorString = "";
		sensorString += sensors.getSpeed();
        sensorString += ",";
        sensorString += sensors.getTrackPosition();
		sensorString += ",";
		sensorString += sensors.getAngleToTrackAxis();
        sensorString += ",";
        sensorString += sensors.getTrackEdgeSensors()[0];
        sensorString += ",";
        sensorString += sensors.getTrackEdgeSensors()[1];
        sensorString += ",";
        sensorString += sensors.getTrackEdgeSensors()[2];
        sensorString += ",";
        sensorString += sensors.getTrackEdgeSensors()[3];
        sensorString += ",";
        sensorString += sensors.getTrackEdgeSensors()[4];
        sensorString += ",";
        sensorString += sensors.getTrackEdgeSensors()[5];
        sensorString += ",";
        sensorString += sensors.getTrackEdgeSensors()[6];
        sensorString += ",";
        sensorString += sensors.getTrackEdgeSensors()[7];
		sensorString += ",";
        sensorString += sensors.getTrackEdgeSensors()[8];
        sensorString += ",";
        sensorString += sensors.getTrackEdgeSensors()[9];
        sensorString += ",";
        sensorString += sensors.getTrackEdgeSensors()[9];
        sensorString += ",";
        sensorString += sensors.getTrackEdgeSensors()[10];
		return sensorString.replace("[", "").replaceAll("]", "");
	}

	private String actionsToString(Action action){
		String actionString = "";
		actionString += action.accelerate;
		actionString += ",";
		actionString += action.brake;
		actionString += ",";
		actionString += action.steering;
		return actionString;

	}

}
