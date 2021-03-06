package de.otaris.zertapps.privacychecker;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.otaris.zertapps.privacychecker.appDetails.AppDetailsActivity;
import de.otaris.zertapps.privacychecker.appsList.AppListItemAdapter;
import de.otaris.zertapps.privacychecker.appsList.CategoryListActivity;
import de.otaris.zertapps.privacychecker.appsList.InstalledAppsActivity;
import de.otaris.zertapps.privacychecker.database.DatabaseHelper;
import de.otaris.zertapps.privacychecker.database.dataSource.AppCompactDataSource;
import de.otaris.zertapps.privacychecker.database.model.AppCompact;

/**
 * Is called when app is started, handles navigation to installedAppsActivity
 * and AllAppsActivity
 * 
 * Splash Screen Code taken from:
 * http://www.41post.com/4588/programming/android-coding-a-loading-screen-part-1
 */
public class HomeActivity extends Activity {

	private List<AppCompact> latestAppsList;

	// A ProgressDialog object
	private ProgressDialog progressDialog;

	private static boolean finishedDatabaseInitialization = false;

	/**
	 * Connect to local database and retrieve last updated apps. Store them in a
	 * list. Do this at this stage to avoid retrieving those apps everytime you
	 * return to the homescreen.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		// DatabaseHelper dbHelper = new DatabaseHelper(this);
		// dbHelper.fillDatabaseFromDevice();
		// dbHelper.exportDatabase(this);

		SharedPreferences wmbPreference = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
		if (isFirstRun) {
			// Code to run once
			SharedPreferences.Editor editor = wmbPreference.edit();
			editor.putBoolean("FIRSTRUN", false);
			editor.commit();

			// Initialize a LoadViewTask object and call the execute() method
			new InitializeDatabaseTask().execute();
		}

		// retrieve apps for recent apps list
		prepareLatestAppsList();

		UserStudyLogger.LOGGING_ENABLED = false;
		UserStudyLogger.getInstance().log("activity_home");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_imprint) {
			Intent intent = new Intent(this, ImprintActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Everytime you return to the homescreen the list is displayed. Populate
	 * now and not earlier to avoid null, or in other words populate once the
	 * homescreen/app was completely initialised.
	 */
	@Override
	public void onResume() {
		super.onResume();

		SharedPreferences wmbPreference = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);

		// only call if not first run and finished loading the database
		if (!isFirstRun && finishedDatabaseInitialization)
			prepareLatestAppsList();

		populateLatestAppListView();
	}

	/**
	 * Auto-generated code A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_home, container,
					false);
			return rootView;
		}
	}

	/**
	 * Method to display installed apps by starting InstalledAppsActivity
	 * 
	 * @param view
	 *            : View
	 */
	public void displayInstalledApps(View view) {
		Log.i("HomeActivity", "called display installed apps");

		// to pass the information to run InstalledAppActivity
		Intent intent = new Intent(this, InstalledAppsActivity.class);
		startActivity(intent);
	}

	/**
	 * Method to display in LogCat that Display allApps is called
	 * 
	 * @param view
	 *            : View
	 */
	public void displayAllApps(View view) {
		Log.i("HomeActivity", "called display all apps");
		Intent intent = new Intent(this, CategoryListActivity.class);
		startActivity(intent);
	}

	/**
	 * retrieve the data for the list of most recently updated apps
	 */
	private void prepareLatestAppsList() {
		AppCompactDataSource appData = new AppCompactDataSource(this);
		appData.open();

		latestAppsList = appData.getLastUpdatedApps(4);
		appData.close();
	}

	/**
	 * Show latest apps in the list view. The list of apps is created on start.
	 */
	private void populateLatestAppListView() {
		// Setup custom list adapter to display apps with icon, name and rating.
		AppListItemAdapter adapter = new AppListItemAdapter(this,
				getPackageManager(), latestAppsList);
		ListView laList = (ListView) findViewById(R.id.latest_apps_listview);
		laList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(view.getContext(),
						AppDetailsActivity.class);
				AppCompact app = (AppCompact) parent
						.getItemAtPosition(position);
				intent.putExtra("AppCompact", app);
				startActivity(intent);
			}

		});
		laList.setAdapter(adapter);
	}

	/**
	 * Asynchronously handles database import and the insertion of uncovered
	 * apps into the database while displaying a loading splash screen.
	 */
	private class InitializeDatabaseTask extends AsyncTask<Void, Integer, Void> {
		// Before running code in the separate thread
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(HomeActivity.this,
					getText(R.string.splash_alert_title),
					getText(R.string.splash_alert_text), false, false);
		}

		// The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params) {

			// Get the current thread's token
			synchronized (this) {
				try {
					// import database from asset
					DatabaseHelper dbHelper = new DatabaseHelper(
							HomeActivity.this);
					dbHelper.importDatabase();
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("HomeActivity", "DB import failed: " + e.getMessage());
				}

				// scan device for installed apps and insert the missing ones
				// into the database
				AppController appController = new AppController();
				appController.insertUncoveredInstalledApps(HomeActivity.this,
						getPackageManager());
			}

			return null;
		}

		// after executing the code in the thread
		@Override
		protected void onPostExecute(Void result) {
			// close the progress dialog
			progressDialog.dismiss();

			HomeActivity.finishedDatabaseInitialization = true;

			// initialize the app list
			prepareLatestAppsList();
			populateLatestAppListView();
		}
	}
}
