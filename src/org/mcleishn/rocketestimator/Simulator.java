package org.mcleishn.rocketestimator;

import android.content.Context;
import android.util.Log;

public class Simulator {

	private Rocket rocket;
	private Double burnOutAlt;
	private Double burnOutVel;
	private Double coastAlt;
	private Double coastTime;
	private Double apogee;
	private Double groundAlt;

	public Simulator(Double mass, Double diameter, String engine,
			Context context, Double alt) {
		groundAlt = alt;
		rocket = new Rocket(mass, diameter, new Engine(engine, context),
				groundAlt);
		burnOutAlt = 0.;
		burnOutVel = 0.;
		coastAlt = 0.;
		coastTime = 0.;
		apogee = 0.;
		Log.d("data", "#" + mass + ", " + diameter + ", " + engine + ", "
				+ groundAlt);
	}

	public void run() {
		Double time = 0.0;
		Double timeStep = 0.0078125;
		Log.d("data", "" + time + ", " + rocket.getPos(2));
		for (int i = 0; i < rocket.getEngPoints() - 1; i++) {
			if (rocket.getPos(2) > apogee) {
				apogee = rocket.getPos(2);
			}
			time = rocket.getEngTime(i);
			rocket.rk2(i, (rocket.getEngTime(i + 1) - time), time);
			Log.d("data", "" + time + ", " + rocket.getPos(2));
		}
		time = rocket.getEngTime(rocket.getEngPoints() - 1);
		rocket.rk2(rocket.getEngPoints(), timeStep, time);
		Log.d("data", "" + time + ", " + rocket.getPos(2));
		burnOutVel = rocket.getVelMag();
		burnOutAlt = rocket.getPos(2) - groundAlt;
		Double burnOutTime = time;
		time += Math.ceil(burnOutTime / timeStep) * timeStep - burnOutTime;
		for (;; time += timeStep) {
			if (rocket.getPos(2) > apogee) {
				apogee = rocket.getPos(2);
			} else {
				break;
			}
			rocket.rk2(rocket.getEngPoints(), timeStep, time);
			Log.d("data", "" + time + ", " + rocket.getPos(2));
		}
		apogee -= groundAlt;
		coastTime = time - burnOutTime;
		coastAlt = apogee - burnOutAlt;
	}

	public Double getBurnOutAlt() {
		return burnOutAlt;
	}

	public Double getBurnOutVel() {
		return burnOutVel;
	}

	public Double getCoastAlt() {
		return coastAlt;
	}

	public Double getCoastTime() {
		return coastTime;
	}

	public Double getApogee() {
		return apogee;
	}
}
