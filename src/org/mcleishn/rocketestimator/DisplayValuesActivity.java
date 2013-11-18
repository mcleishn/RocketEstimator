package org.mcleishn.rocketestimator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayValuesActivity extends Activity {

	private Simulator flightSim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String message = intent.getStringExtra(AppActivity.EXTRA_MESSAGE);

		Log.d("simulation", "Retrieving, converting, and checking input");

		String values[] = message.split(", ");
		Double mass = convertMass(values[0], values[1]);
		Double diameter = convertDiameter(values[2], values[3]);
		String engine = values[4];
		//Double alt = convertAlt(values[5], values[6]);
		if (mass < 0 || diameter < 0) {
			TextView textView = new TextView(this);
			textView.setTextSize(40);
			textView.setText("Invalid mass or diameter." + values[0] + ", "
					+ values[2]);
			setContentView(textView);
		} else {

			Log.d("simulation", "Starting simulation with " + mass + ", "
					+ diameter + ", " + engine);// + ", " + alt);

			flightSim = new Simulator(mass, diameter, engine, this, 0.);//alt);
			flightSim.run();
			Double burnOutAlt = flightSim.getBurnOutAlt();
			Double burnOutAlt2 = burnOutAlt * 3.28;
			Double burnOutVel = flightSim.getBurnOutVel();
			Double burnOutVel2 = burnOutVel * 2.23694;
			Double coastAlt = flightSim.getCoastAlt();
			Double coastAlt2 = coastAlt * 3.28;
			Double coastTime = flightSim.getCoastTime();
			Double apogee = flightSim.getApogee();
			Double apogee2 = apogee * 3.28;

			setContentView(R.layout.activity_display_values);

			Log.d("simulation", "Setting results");

			TextView burn_out_alt = (TextView) findViewById(R.id.burn_out_alt);
			burn_out_alt.setText("" + String.format("%.2f", burnOutAlt)
					+ " m (" + String.format("%.2f", burnOutAlt2) + " ft)");
			TextView burn_out_vel = (TextView) findViewById(R.id.burn_out_vel);
			burn_out_vel.setText("" + String.format("%.2f", burnOutVel)
					+ " m/s (" + String.format("%.2f", burnOutVel2) + " mph)");
			TextView coast_alt = (TextView) findViewById(R.id.coast_alt);
			coast_alt.setText("" + String.format("%.2f", coastAlt) + " m ("
					+ String.format("%.2f", coastAlt2) + " ft)");
			TextView coast_time = (TextView) findViewById(R.id.coast_time);
			coast_time.setText("" + String.format("%.2f", coastTime) + " s");
			TextView apogeeText = (TextView) findViewById(R.id.apogee);
			apogeeText.setText("" + String.format("%.2f", apogee) + " m ("
					+ String.format("%.2f", apogee2) + " ft)");
		}
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_values, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private Double convertMass(String mass, String unit) {
		if (mass.equals("")) {
			return -1.0;
		} else {
			if (unit.equals("g")) {
				return Double.parseDouble(mass) / 1000.0;
			} else if (unit.equals("kg")) {
				return Double.parseDouble(mass);
			} else if (unit.equals("oz")) {
				return Double.parseDouble(mass) * 0.0283495;
			} else if (unit.equals("lbs")) {
				return Double.parseDouble(mass) * 0.453592;
			} else {
				return -1.;
			}
		}
	}

	private Double convertDiameter(String diameter, String unit) {
		if (diameter.equals("")) {
			return -1.0;
		} else {
			if (unit.equals("mm")) {
				return Double.parseDouble(diameter) / 1000.0;
			} else if (unit.equals("cm")) {
				return Double.parseDouble(diameter) / 100.0;
			} else if (unit.equals("m")) {
				return Double.parseDouble(diameter);
			} else if (unit.equals("in")) {
				return Double.parseDouble(diameter) * 0.0254;
			} else if (unit.equals("ft")) {
				return Double.parseDouble(diameter) * 0.3048;
			} else {
				return -1.;
			}
		}
	}

	@SuppressWarnings("unused")
	private Double convertAlt(String alt, String unit) {
		if (alt.equals("")) {
			return 0.0;
		} else {
			if (unit.equals("m")) {
				return Double.parseDouble(alt);
			} else if (unit.equals("Km")) {
				return Double.parseDouble(alt) * 1000.0;
			} else if (unit.equals("ft")) {
				return Double.parseDouble(alt) * 0.03048;
			} else if (unit.equals("mi")) {
				return Double.parseDouble(alt) * 1609.34;
			} else {
				return -1.;
			}
		}
	}
}
