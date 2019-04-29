package tir_16;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.utility.Delay;

public class Robotten {

	static EV3LargeRegulatedMotor Hjul_Højre = new EV3LargeRegulatedMotor(MotorPort.A);
	static EV3LargeRegulatedMotor Hjul_Venstre = new EV3LargeRegulatedMotor(MotorPort.D);
	static EV3UltrasonicSensor sensor = new EV3UltrasonicSensor(SensorPort.S1);

	static DifferentialPilot pilot = new DifferentialPilot(5.5, 12, Hjul_Højre, Hjul_Venstre, true);
	final static SampleProvider sp = sensor.getDistanceMode();

	static double sensor_stop = 0.2; // afstand robotten stopper fra glasset, ganges med 100 for cm
	static double sensor_stop_cm = sensor_stop * 100;

	static float Afstand_Kørt; // variable der lagre hvor langt robotten har kørt

	// metode til at køre frem indtil vi når en kop, samt gemme afstanden som
	// variablen, 'Afstand_KØRT

	
	//metode: robotten kører frem og stopper når den er 20cm fra et objekt
	public static void go() {
		pilot.setTravelSpeed(7);
		pilot.travel(-100, true);

		float[] data = new float[1]; // sensor opsamler data og skal lagre det i et array

		while (pilot.isMoving()) {
			sp.fetchSample(data, 0);
			System.out.println(data[0]);

			if (data[0] <= sensor_stop && data[0] != 0) {
				pilot.stop();
			}
		}

		System.out.println(" " + -pilot.getMovement().getDistanceTraveled());
		
		Afstand_Kørt = -pilot.getMovement().getDistanceTraveled();
		System.out.println("afstand til kop" + Afstand_Kørt+sensor_stop_cm );

	}


	public static void main(String[] args) {

		Robotten.go();
		// sensor.disable();   //virker som en lejos bug men robotten looper, hvis sensoren slukkes
	

		pilot.travel(Afstand_Kørt);
		
		Katapult.motor_speed(); // udregner fart baseret på afstand til kop
		Katapult.set_acc();
		
		System.out.println("vektor cm pr. sek: " +(int)Katapult.Hastighed_cm_sek());
		System.out.println("rotationshastighed, grader: " +(int)Katapult.speed);
		System.out.println("afstand til kop "+(int)Katapult.x);

		Button.waitForAnyPress();

		

			if (Katapult.Kast.getMaxSpeed() < Katapult.speed) 												
				{
					System.out.println("OBS batteriet er ikke ladet nok til at gennemføre skudet");
					Sound.twoBeeps();
					Button.waitForAnyPress();
				}
	
		
		Katapult.SKYD();// affyrer bolden
		
		//Katapult.AutoReset();  //virker
		
		boolean miss = true;
		
		//et mode hvori man kan justere katapultens skud uden at starte programmet forfra
		while (miss) {
			System.out.println("Ramt? Tryk Enter for at justere, eller afslut med Escape");

			int indput = Button.waitForAnyPress();

			if (indput == Button.ID_ENTER) {
				System.out.println("nuværende styrke: " + Katapult.speed);
				System.out.println("OP for at forøge styrken med 3%");
				System.out.println("NED ned for at mindske styrken med 3%");

				boolean juster = true;

				while (juster) {

					indput = Button.waitForAnyPress();

					if (indput == Button.ID_UP) {
						Katapult.speed = Katapult.speed * 1.03;
						Katapult.acc=(int) (Katapult.acc*1.03);
						System.out.println("ny styrke: " + (int) Katapult.speed);
						System.out.println("afslut med Enter");
					}

					
					else if (indput == Button.ID_DOWN) {
						Katapult.speed = Katapult.speed * 0.97;
						Katapult.acc=(int) (Katapult.acc*0.97);
						System.out.println("ny styrke: " + (int) Katapult.speed);
						System.out.println("afslut med Enter");
					}
					else if (indput==Button.ID_RIGHT)
					{
						Katapult.ManueltReset();
						
					}

					
					else if (indput == Button.ID_ENTER) {
						juster = false;
					}
				}
				System.out.println("skyd");
			
			
			
				Katapult.SKYD();
			//	Katapult.AutoReset();

			}

			else if (indput == Button.ID_ESCAPE) {
				miss = false;
			}

		}
		Sound.beepSequenceUp();
	}

}
