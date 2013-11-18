package org.mcleishn.rocketestimator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class AppActivity extends Activity {

	public final static String EXTRA_MESSAGE = "org.mcleishn.rocketestimator.MESSAGE";
	private String mass_spinner[];
	private String diameter_spinner[];
	// private String altitude_spinner[];
	private ArrayList<String> manufacturer_spinner;
	private String selectedManu;
	private ArrayList<Integer> size_spinner;
	private HashMap<String, SparseArray<ArrayList<String>>> motors = new HashMap<String, SparseArray<ArrayList<String>>>();
	private HashMap<String, ArrayList<Integer>> sizes = new HashMap<String, ArrayList<Integer>>();
	private ArrayList<String> motor_spinner;
	private DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app);
		db = new DatabaseHandler(this);

		Log.d("input", "Setting up choices");

		setupMassSpinner();
		setupDiameterSpinner();
		setupManufacturerSpinner();
		// setupAltitudeSpinner();
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setupMassSpinner() {

		Log.d("input", "Setting the mass unit options");

		mass_spinner = new String[4];
		mass_spinner[0] = "g";
		mass_spinner[1] = "Kg";
		mass_spinner[2] = "oz";
		mass_spinner[3] = "lbs";
		Spinner s = (Spinner) findViewById(R.id.mass_spinner);
		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mass_spinner);
		s.setAdapter(adapter);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setupDiameterSpinner() {

		Log.d("input", "Setting the diameter unit options");

		diameter_spinner = new String[5];
		diameter_spinner[0] = "mm";
		diameter_spinner[1] = "cm";
		diameter_spinner[2] = "m";
		diameter_spinner[3] = "in";
		diameter_spinner[4] = "ft";
		Spinner s = (Spinner) findViewById(R.id.diameter_spinner);
		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, diameter_spinner);
		s.setAdapter(adapter);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setupManufacturerSpinner() {

		Log.d("input", "Setting up the motor selection from the database");

		manufacturer_spinner = new ArrayList<String>();
		sizes = new HashMap<String, ArrayList<Integer>>();
		motors = new HashMap<String, SparseArray<ArrayList<String>>>();
		String all = "(--ALL--)";
		manufacturer_spinner.add(all);
		sizes.put(all, new ArrayList<Integer>());
		motors.put(all, new SparseArray<ArrayList<String>>());

		Log.d("input", "Reading database");

		List<String[]> engines = db.getAllEngines();
		for (String[] header : engines) {
			String manu = header[6];
			Integer diam = (int) Double.parseDouble(header[1]);
			String motor = header[0];
			if (!manufacturer_spinner.contains(manu)) {
				manufacturer_spinner.add(manu);
			}
			Collections.sort(manufacturer_spinner);
			if (sizes.containsKey(manu)) {
				if (!sizes.get(manu).contains(diam)) {
					sizes.get(manu).add(diam);
				}
			} else {
				ArrayList<Integer> size = new ArrayList<Integer>();
				size.add(diam);
				sizes.put(manu, size);
			}
			if (!sizes.get(all).contains(diam)) {
				sizes.get(all).add(diam);
			}
			Collections.sort(sizes.get(manu));
			Collections.sort(sizes.get(all));
			if (motors.containsKey(manu)) {
				if (motors.get(manu).get(diam) != null
						&& !motors.get(all).get(diam).contains(motor)) {
					motors.get(manu).get(diam).add(motor);
				} else {
					ArrayList<String> engine = new ArrayList<String>();
					engine.add(motor);
					motors.get(manu).put(diam, engine);
				}
			} else {
				SparseArray<ArrayList<String>> motorMap = new SparseArray<ArrayList<String>>();
				ArrayList<String> engine = new ArrayList<String>();
				engine.add(motor);
				motorMap.put(diam, engine);
				motors.put(manu, motorMap);
			}
			if (motors.get(all).get(diam) != null
					&& !motors.get(all).get(diam).contains(motor)) {
				motors.get(all).get(diam).add(motor);
			} else {
				ArrayList<String> engine = new ArrayList<String>();
				engine.add(motor);
				motors.get(all).put(diam, engine);
			}
			Collections.sort(motors.get(manu).get(diam));
			Collections.sort(motors.get(all).get(diam));
		}

		Log.d("input", "Motor selection spinners ready");

		Spinner s = (Spinner) findViewById(R.id.manufacturer_spinner);
		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, manufacturer_spinner);
		s.setAdapter(adapter);
		s.setOnItemSelectedListener(new ManufacturerSelectionListener());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setupSizeSpinner(String manufacturer) {

		Log.d("input",
				"Setting up motor size selection from manufacturer selected");

		size_spinner = sizes.get(manufacturer);
		Spinner s = (Spinner) findViewById(R.id.size_spinner);
		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, size_spinner);
		s.setAdapter(adapter);
		s.setOnItemSelectedListener(new SizeSelectionListener());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setupMotorSpinner(String manufacturer, Integer size) {

		Log.d("input",
				"Setting up motor selection from manufacturer and size selected");

		motor_spinner = motors.get(manufacturer).get(size);
		Spinner s = (Spinner) findViewById(R.id.motor_spinner);
		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, motor_spinner);
		s.setAdapter(adapter);
	}

	/*
	 * private void setupAltitudeSpinner() {
	 * 
	 * Log.d("input", "Setting the altitude unit options");
	 * 
	 * altitude_spinner = new String[4]; altitude_spinner[0] = "m";
	 * altitude_spinner[1] = "Km"; altitude_spinner[2] = "ft";
	 * altitude_spinner[3] = "mi"; Spinner s = (Spinner)
	 * findViewById(R.id.altitude_spinner); ArrayAdapter adapter = new
	 * ArrayAdapter(this, android.R.layout.simple_spinner_item,
	 * altitude_spinner); s.setAdapter(adapter); }
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app, menu);
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

	public void estimateValues(View view) {

		Log.d("input", "Reading input and starting simulation");

		Intent intent = new Intent(this, DisplayValuesActivity.class);

		EditText rocketMass = (EditText) findViewById(R.id.rocket_mass);
		EditText rocketDiam = (EditText) findViewById(R.id.rocket_diameter);
		// EditText groundAlt = (EditText) findViewById(R.id.ground_altitude);

		String mass = rocketMass.getText().toString();

		Spinner s = (Spinner) findViewById(R.id.mass_spinner);
		String massUnit = s.getSelectedItem().toString();

		String diameter = rocketDiam.getText().toString();

		s = (Spinner) findViewById(R.id.diameter_spinner);
		String diamUnit = s.getSelectedItem().toString();

		s = (Spinner) findViewById(R.id.motor_spinner);
		String motor = s.getSelectedItem().toString();

		// String altitude = groundAlt.getText().toString();
		// s = (Spinner) findViewById(R.id.altitude_spinner);

		// String altUnit = s.getSelectedItem().toString();

		Log.d("input", "Checking that all neccessary input has been entered");

		if (isDouble(mass) && isDouble(diameter)) {

			Log.d("input", "Starting Simulation");

			intent.putExtra(EXTRA_MESSAGE, mass + ", " + massUnit + ", "
					+ diameter + ", " + diamUnit + ", " + motor);/*
																 * + ", " +
																 * altitude +
																 * ", " +
																 * altUnit);
																 */
			startActivity(intent);
		} else {

			Log.d("input", "Bad input, forcing values");

			if (!isDouble(diameter)) {
				rocketDiam.setText(((Spinner) findViewById(R.id.size_spinner))
						.getSelectedItem().toString());
				rocketDiam.requestFocus();
				rocketDiam.selectAll();
			}
			if (!isDouble(mass)) {
				rocketMass.setText("0.0");
				rocketMass.requestFocus();
				rocketMass.selectAll();
			}
		}
	}

	public boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public class ManufacturerSelectionListener implements
			OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int pos,
				long id) {

			parent.getItemAtPosition(pos);

			for (int i = 0; i < manufacturer_spinner.size(); i++) {
				if (pos == i) {
					setupSizeSpinner(manufacturer_spinner.get(i));
					selectedManu = manufacturer_spinner.get(i);
					break;
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	}

	public class SizeSelectionListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int pos,
				long id) {

			parent.getItemAtPosition(pos);

			for (int i = 0; i < size_spinner.size(); i++) {
				if (pos == i) {
					setupMotorSpinner(selectedManu, size_spinner.get(i));
					break;
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	public void createEngine(View view) {

		Log.d("input", "Starting user engine creation");

		Intent intent = new Intent(this, CreateEngineActivity.class);
		startActivityForResult(intent, 1);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {
				setupMassSpinner();
				setupDiameterSpinner();
				setupManufacturerSpinner();
			}
		}
	}// onActivityResult
}
