package tir_16;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

public class Katapult {

	static EV3LargeRegulatedMotor Kast = new EV3LargeRegulatedMotor(MotorPort.B);
	static EV3LargeRegulatedMotor Kast1 = new EV3LargeRegulatedMotor(MotorPort.C);

	// Mål i cm
	static double KOP_HØJDE = 6;
	static double KOP_RADIUS = 5;

	static double HØJDE_SKUD = 40;
	static double ARM_LÆNGDE = 22;
	static double AFSTAND_SENSOR = 31; // fra slippepunkt til sensor
	static double vinkel = Math.toRadians(30);

	static int grader = 100; // hvor langt lademekanismen skal rotere
	static double g = -981; // tyngdekraft i cm/s^2
	static double speed;
	static int acc; // enhed: degress/second/second, en kraft

	static double afstand = Robotten.Afstand_Kørt;
	static double x = afstand + AFSTAND_SENSOR + Robotten.sensor_stop_cm + KOP_RADIUS; // totale afstand i x retning fra skud til mål

	
	// metode til at finde tiden bolden skal være i luften før den rammer koppen,
	// udledt fra skrå kast formlerne se evt bilag
	
	static double Tid() {
		double p1 = KOP_HØJDE - (x * Math.sin(vinkel) / Math.cos(vinkel)) - HØJDE_SKUD;
		double p2 = p1 / (0.5 * g);
		double p3 = Math.sqrt(p2);

		return p3;

	}

	static double Hastighed_cm_sek() {
		double V_x = x / Tid();
		return V_x / Math.cos(vinkel);
	}

	static double motor_speed() {
		double omkreds = 2 * Math.PI * ARM_LÆNGDE;
		speed = (Hastighed_cm_sek() / omkreds) * 360;
		return speed;
	}

	// udledt fra potensregrssion af data fra forsøg. se evt bilag
	static void set_acc() {
		acc = (int) (2.38 * Math.pow(Hastighed_cm_sek(), 1.27));
	}

	static void SKYD() {
		Kast.setSpeed(100);
		Kast1.setSpeed(100);

		Kast.setAcceleration(1000);
		Kast1.setAcceleration(1000);

		Kast.rotate(grader, true);
		Kast1.rotate(grader, true);

		Delay.msDelay(3000);
		Sound.beepSequenceUp();

		Kast.setSpeed((int) speed);
		Kast1.setSpeed((int) speed);
		Kast.setAcceleration(acc);
		Kast1.setAcceleration(acc);

		Delay.msDelay(4000);

		Kast.rotate(-grader + 5, true);
		Kast1.rotate(-grader + 5, false);
		
		Delay.msDelay(1000);

	}

	//Metoden fungerer ikke med hardware
	static void AutoReset() {
		Kast.setSpeed(20);
		Kast1.setSpeed(20);
		Kast.setAcceleration(500);

		Kast1.setAcceleration(500);
		Kast.setStallThreshold(3, 150);
		Kast1.flt();
		// Kast1.setStallThreshold(2, 100);

		while (!Kast.isStalled()) // ||(!Kast1.isStalled()))
		{
			Kast.rotate(-7, true);
			Delay.msDelay(100);
		}
		Kast.stop(true);
		Kast1.stop(true);
		Delay.msDelay(2000);

		Kast.setStallThreshold(50, 1000);

	}

	static void ManueltReset() {

		Kast.rotate(-2, true);
		Kast1.rotate(-2, true);

	}

	public static void main(String[] args) {

	}

}

/*
 * static double Tid_y(){
 * 
 * double V_x = hastighed_v0_ms * Math.cos(vinkel); double V_y = hastighed_v0_ms
 * * Math.sin(vinkel);
 * 
 * double t=0; double andengradsligning = 0.5*g*t*t+V_y*t+højde_skud-højde_kop;
 * 
 * //Determinant double D = (V_y*V_y)-(4*0.5*g*(højde_skud-højde_kop));
 * 
 * double T1=(-V_y+Math.sqrt(D))/(2*0.5*g); double
 * T2=(-V_y-Math.sqrt(D))/(2*0.5*g);
 * 
 * if (T1<0) {T1=T2;} return T1; }
 */
