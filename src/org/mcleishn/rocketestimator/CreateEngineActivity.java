package org.mcleishn.rocketestimator;

import java.util.ArrayList;
import java.util.Vector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CreateEngineActivity extends Activity {

	Vector<Double> time = new Vector<Double>();;
	Vector<Double> thrust = new Vector<Double>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_engine);
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
		getMenuInflater().inflate(R.menu.create_engine, menu);
		return true;
	}

	public void deletePoint(View view) {

		Log.d("engine", "Delteing data point");

		if (time.size() > 0) {
			time.remove(time.size() - 1);
			thrust.remove(thrust.size() - 1);
			setPoints();
		}
	}

	public void addPoint(View view) {

		Log.d("engine", "Adding data point");

		EditText t = (EditText) findViewById(R.id.time_input);
		if (isDouble(t.getText().toString())) {
			time.add(Double.parseDouble(t.getText().toString()));
			t = (EditText) findViewById(R.id.thrust_input);
			if (isDouble(t.getText().toString())) {
				thrust.add(Double.parseDouble(t.getText().toString()));
				setPoints();
			} else {
				time.remove(time.size() - 1);
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

	private void setPoints() {
		TextView d = (TextView) findViewById(R.id.data);
		String text = "";
		for (int i = 0; i < time.size(); i++) {
			text += time.get(i) + "\t" + thrust.get(i) + "\n";
		}
		d.setText(text);
	}

	public void createEngine(View view) {

		Log.d("engine", "Adding engine to database");

		DatabaseHandler db = new DatabaseHandler(this);

		String[] header = new String[7];
		ArrayList<Vector<Double>> data = new ArrayList<Vector<Double>>();
		data.add(time);
		data.add(thrust);

		EditText t = (EditText) findViewById(R.id.motor_name);
		header[0] = t.getText().toString();
		t = (EditText) findViewById(R.id.motor_diameter);
		header[1] = t.getText().toString();
		t = (EditText) findViewById(R.id.motor_length);
		header[2] = t.getText().toString();
		t = (EditText) findViewById(R.id.motor_delays);
		header[3] = t.getText().toString();
		t = (EditText) findViewById(R.id.motor_propWeight);
		header[4] = t.getText().toString();
		t = (EditText) findViewById(R.id.motor_weight);
		header[5] = t.getText().toString();
		t = (EditText) findViewById(R.id.motor_manufacturer);
		header[6] = t.getText().toString();

		for (int i = 1; i < header.length; i++) {
			if (header[i] == null || header[i] == "") {
				header[i] = "NULL";
			}
		}

		if (time.size() > 0 && header[0] != "") {
			db.addEngine(header, data);
			Intent returnIntent = new Intent();
			returnIntent.putExtra("result", "created");
			setResult(RESULT_OK, returnIntent);
			finish();
		}
	}
}
