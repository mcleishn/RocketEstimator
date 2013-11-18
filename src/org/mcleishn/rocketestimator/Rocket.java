package org.mcleishn.rocketestimator;

public class Rocket {

	private Engine eng;
	private int DIM = 3;
	private Double DRAG = 0.75;
	private Double mass;
	private Double diameter;
	private Double pos[] = new Double[DIM];
	private Double vel[] = new Double[DIM];
	private Double posHalf[] = new Double[DIM];
	private Double velHalf[] = new Double[DIM];

	public Rocket(Double mass, Double diameter, Engine eng, Double groundAlt) {
		this.mass = mass;
		this.diameter = diameter;
		this.eng = eng;
		for (int i = 0; i < DIM; i++) {
			pos[i] = 0.0;
			posHalf[i] = 0.0;
			vel[i] = 0.0;
			velHalf[i] = 0.0;
		}
		pos[2] = groundAlt;
	}

	public Double getMass() {
		return mass;
	}

	public Double getArea() {
		return Math.PI * (diameter / 2.0) * (diameter / 2.0);
	}

	public Double[] getPos() {
		return pos;
	}

	public Double getPos(int i) {
		return pos[i];
	}

	public Double[] getVel() {
		return vel;
	}

	public Double getVel(int i) {
		return vel[i];
	}

	public Double getVelMag() {
		return Math.sqrt(vel[0] * vel[0] + vel[1] * vel[1] + vel[2] * vel[2]);
	}

	public Double getDrag() {
		return DRAG;
	}

	public int getEngPoints() {
		return eng.numPoints();
	}

	public Double getEngTime(int point) {
		return eng.getTime(point);
	}

	public void rk2(int point, double timeStep, double time) {
		Double[] NewPosition = new Double[DIM];
		Double[] NewVelocity = new Double[DIM];
		Double[] acc = new Double[DIM];
		Double[] accHalf = new Double[DIM];

		getAcc(point, acc, vel, time);

		for (int i = 0; i < DIM; i++) {// Dimensional loop
			posHalf[i] = pos[i] + (timeStep / 2.0) * vel[i];// Sets the half
															// step position
			velHalf[i] = vel[i] + (timeStep / 2.0) * acc[i];// Sets the half
															// step velocity
		}

		getAcc(point, accHalf, velHalf, time);

		for (int i = 0; i < DIM; i++) {// Dimensional loop
			NewPosition[i] = pos[i] + timeStep * velHalf[i];// Sets the new
															// position
			NewVelocity[i] = vel[i] + timeStep * accHalf[i];// Sets the new
															// velocity
			pos[i] = NewPosition[i];
			vel[i] = NewVelocity[i];// Update position and velocity
		}
		if (pos[2] <= 0.) {
			pos[2] = 0.;
		}
		if (vel[2] <= 0. && time <= eng.getTime(eng.numPoints() - 1)) {
			vel[2] = 0.;
		}
	}

	public void getAcc(int point, Double[] acc, Double[] vel, Double time) {
		acc[0] = 0.0;
		acc[1] = 0.0;
		acc[2] = ((-signOf(vel[2]) * windRestance(pos[2], vel[2]) + (thrust(point))) / (getMass() + eng
				.getMass(point))) - 9.80;
	}

	private int signOf(Double val) {
		if (val < 0) {
			return -1;
		} else {
			return 1;
		}
	}

	Double thrust(int i) {
		if (i > eng.numPoints() - 1)
			return 0.0;
		else
			return eng.getThrust(i);
	}

	private Double windRestance(Double alt, Double vel) {
		// return 0.5 * airDensity(alt) * getDrag() * getArea() * vel * vel;
		return 0.5 * 1.22 * getDrag() * getArea() * vel * vel;
	}

	@SuppressWarnings("unused")
	private Double airDensity(Double alt) {
		return airPressure(alt) / (287.053 * airTemp(alt));
	}

	private Double airPressure(Double alt) {
		return 101325 * Math.pow((airTemp(alt) / 288.15),
				-(9.8 / (287.053 * atomsphereLapseRate(alt))));
	}

	private Double airTemp(Double alt) {
		return 288.15 + atomsphereLapseRate(alt) * alt;
	}

	private Double atomsphereLapseRate(Double alt) {
		if (alt <= 11000) {
			return -0.0065;
		} else if (alt <= 20000) {
			return 0.0;
		} else if (alt <= 32000) {
			return 0.001;
		} else if (alt <= 47000) {
			return 0.0028;
		} else if (alt <= 51000) {
			return 0.0;
		} else if (alt <= 71000) {
			return -0.0028;
		} else if (alt <= 85000) {
			return -0.002;
		} else {
			return 0.0;
		}
	}
}
