package org.mcleishn.rocketestimator;

import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;

public class Engine {
	private Vector<Double> thrust;
	private Vector<Double> time;
	private Vector<Double> engMass;
	private int dataPoints;
	private DatabaseHandler db;

	public Engine(String engine, Context context) {
		db = new DatabaseHandler(context);
		thrust = new Vector<Double>();
		time = new Vector<Double>();
		engMass = new Vector<Double>();
		dataPoints = 0;
		getData(engine);
	}

	private void getData(String engine) {
		ArrayList<Vector<Double>> data = db.getData(engine);
		time = data.get(0);
		thrust = data.get(1);
		dataPoints = time.size();
		String[] header = db.getEngine(engine);
		engMass.add(Double.parseDouble(header[5]));
		Double slope = -Double.parseDouble(header[4])
				/ time.get(dataPoints - 1);
		for (int i = 1; i < dataPoints; i++) {
			Double mass = slope * time.get(i) + engMass.get(0);
			engMass.add(mass);
		}
		db.close();
	}

	public double getTime(int point) {
		return time.get(point);
	}

	public double getThrust(int point) {
		return thrust.get(point);
	}

	public double getMass(int point) {
		if (point > numPoints() - 1) {
			return engMass.get(numPoints() - 1);
		} else {
			return engMass.get(point);
		}
	}

	public int numPoints() {
		return dataPoints;
	}
}
