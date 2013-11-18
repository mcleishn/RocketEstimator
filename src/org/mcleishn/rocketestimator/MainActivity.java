package org.mcleishn.rocketestimator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	private DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TextView textView = new TextView(this);
		// textView.setTextSize(40);
		// textView.setText("Loading Engine Data. This may take some time.");
		// setContentView(textView);

		db = new DatabaseHandler(this);

		new DatabasePrep().execute();

	}

	private class DatabasePrep extends AsyncTask<Void, String, Void> {
		private ProgressDialog progressDialog;
		private String message = "This may take some time.\n";

		// declare other objects as per your need
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setIndeterminate(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setCancelable(false);
			progressDialog.setMax(380);
			progressDialog.setTitle("Loading Engine Data");
			progressDialog.setMessage(message);
			progressDialog.show();
			// do initialization of required objects objects here
		}

		@Override
		protected Void doInBackground(Void... params) {
			Log.d("startup", "Setting up engine database");

			publishProgress("0", message);

			Log.d("startup", "Currently " + db.getEngineCount()
					+ " engines in database");

			if (db.getEngineCount() < 380) {
				setupDatabase();
			}

			Log.d("startup", "Database set with " + db.getEngineCount()
					+ " engines");

			return null;
		}

		protected void onProgressUpdate(String... progress) {
			progressDialog.setMessage(progress[1]);
			progressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			setContentView(R.layout.activity_main);
		}

		private void setupDatabase() {
			InputStream input = null;
			AssetManager assets = getAssets();
			String[] fileNames = null;
			try {
				fileNames = assets.list("engines");
			} catch (IOException e) {
				e.printStackTrace();
			}
			Integer engineCount = 0;
			for (String fileName : fileNames) {
				Log.d("startup", "Reading file: " + fileName);

				try {
					input = assets.open("engines/" + fileName);
				} catch (IOException e) {
					Log.d("startup", "IOException1");
					e.printStackTrace();
				}
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input));
				String line = null;
				try {
					line = reader.readLine();
				} catch (IOException e) {
					Log.d("startup", "IOException2");
					e.printStackTrace();
				}
				do {
					String columns[] = line.split("\\s+");
					if (columns.length == 7 && columns[0].charAt(0) != ';') {

						Log.d("startup", "Reading data for " + columns[0]);

						publishProgress(engineCount.toString(), message
								+ columns[0]);

						try {
							line = reader.readLine();
						} catch (IOException e) {
							Log.d("startup", "IOException3");
							e.printStackTrace();
						}
						int dataPoints = 0;
						Vector<Double> time = new Vector<Double>();
						Vector<Double> thrust = new Vector<Double>();
						while (line.charAt(0) != ';') {
							String values[] = line.split("\\s+");
							time.add(Double
									.parseDouble(values[values.length - 2]));
							thrust.add(Double
									.parseDouble(values[values.length - 1]));
							try {
								line = reader.readLine();
							} catch (IOException e) {
								Log.d("location", "IOException4");
								e.printStackTrace();
							}
							dataPoints++;
							if (thrust.get(dataPoints - 1) == 0) {
								break;
							}
						}
						ArrayList<Vector<Double>> data = new ArrayList<Vector<Double>>();
						data.add(time);
						data.add(thrust);

						Log.d("startup", "Adding " + columns[0]
								+ " to database");

						if (db.addEngine(columns, data)) {
							engineCount++;
							publishProgress(engineCount.toString(), message
									+ columns[0]);
						}
					}
					try {
						line = reader.readLine();
					} catch (IOException e) {
						Log.d("location", "IOException5");
						e.printStackTrace();
					}
				} while (line != null);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void acceptDisclaimer(View view) {
		Intent intent = new Intent(this, AppActivity.class);
		startActivity(intent);
	}

}
