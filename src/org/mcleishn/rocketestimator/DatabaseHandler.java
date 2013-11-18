package org.mcleishn.rocketestimator;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "engineManager";

	// Contacts table name
	private static final String TABLE_ENGINES = "engines";

	// Contacts Table Columns names
	private static final String KEY_NAME = "name";
	private static final String KEY_DIAMETER = "diameter";
	private static final String KEY_LENGTH = "length";
	private static final String KEY_DELAYS = "delays";
	private static final String KEY_PROP_WEIGHT = "propellant_weight";
	private static final String KEY_TOTAL_WEIGHT = "total_weight";
	private static final String KEY_MANUFACTURER = "manufacturer";
	private static final String KEY_TIME = "time";
	private static final String KEY_THRUST = "thrust";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_ENGINES_TABLE = "CREATE TABLE " + TABLE_ENGINES + "("
				+ KEY_NAME + " TEXT PRIMARY KEY," + KEY_DIAMETER + " FLOAT,"
				+ KEY_LENGTH + " FLOAT," + KEY_DELAYS + " TEXT,"
				+ KEY_PROP_WEIGHT + " FLOAT," + KEY_TOTAL_WEIGHT + " FLOAT,"
				+ KEY_MANUFACTURER + " TEXT" + ")";
		db.execSQL(CREATE_ENGINES_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENGINES);

		// Create tables again
		onCreate(db);

	}

	// Adding new contact
	public Boolean addEngine(String[] header, ArrayList<Vector<Double>> data) {
		SQLiteDatabase db = this.getWritableDatabase();

		if (getEngineCount() == 0 || getEngine(header[0]) == null) {

			ContentValues values = new ContentValues();
			values.put(KEY_NAME, header[0]);
			values.put(KEY_DIAMETER, Double.parseDouble(header[1]));
			values.put(KEY_LENGTH, Double.parseDouble(header[2]));
			values.put(KEY_DELAYS, header[3]);
			values.put(KEY_PROP_WEIGHT, Double.parseDouble(header[4]));
			values.put(KEY_TOTAL_WEIGHT, Double.parseDouble(header[5]));
			values.put(KEY_MANUFACTURER, header[6]);

			// Inserting Row
			db.insert(TABLE_ENGINES, null, values);

			String CREATE_DATA_TABLE = "CREATE TABLE IF NOT EXISTS "
					+ escape(header[0]) + "(" + KEY_TIME
					+ " FLOAT PRIMARY KEY," + KEY_THRUST + " FLOAT" + ")";
			db.execSQL(CREATE_DATA_TABLE);
			values = new ContentValues();
			for (int i = 0; i < data.get(0).size(); i++) {
				values.put(KEY_TIME, data.get(0).get(i));
				values.put(KEY_THRUST, data.get(1).get(i));
				db.insert(escape(header[0]), null, values);
			}
			db.close();
			return true;
		}
		db.close(); // Closing database connection
		return false;
	}

	// Getting single contact
	public String[] getEngine(String engine) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_ENGINES, new String[] { KEY_NAME,
				KEY_DIAMETER, KEY_LENGTH, KEY_DELAYS, KEY_PROP_WEIGHT,
				KEY_TOTAL_WEIGHT, KEY_MANUFACTURER }, KEY_NAME + "=?",
				new String[] { engine }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() > 0) {
			String[] engines = { cursor.getString(0), cursor.getString(1),
					cursor.getString(2), cursor.getString(3),
					cursor.getString(4), cursor.getString(5),
					cursor.getString(6) };
			// return contact
			return engines;
		} else {
			return null;
		}
	}

	public ArrayList<Vector<Double>> getData(String engine) {
		ArrayList<Vector<Double>> data = new ArrayList<Vector<Double>>();
		Vector<Double> time = new Vector<Double>();
		Vector<Double> thrust = new Vector<Double>();
		String selectQuery = "SELECT  * FROM " + escape(engine);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				time.add(Double.parseDouble(cursor.getString(0)));
				thrust.add(Double.parseDouble(cursor.getString(1)));
			} while (cursor.moveToNext());
		}

		data.add(time);
		data.add(thrust);

		// return contact list
		return data;
	}

	// Getting All Contacts
	public List<String[]> getAllEngines() {
		List<String[]> engineList = new ArrayList<String[]>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_ENGINES;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				String[] engines = { cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6) };
				engineList.add(engines);
			} while (cursor.moveToNext());
		}

		// return contact list
		return engineList;
	}

	// Getting contacts Count
	public int getEngineCount() {
		String countQuery = "SELECT  * FROM " + TABLE_ENGINES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		// cursor.close();

		// return count
		return cursor.getCount();
	}

	private String escape(String s) {
		return DatabaseUtils.sqlEscapeString(s);
	}
}
