package de.otaris.zertapps.privacychecker;

import de.otaris.zertapps.privacychecker.database.Category;
import de.otaris.zertapps.privacychecker.database.CategoryDataSource;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * is called by HomeActivity, handles display of installed apps
 */
public class AppsByCategoryActivity extends SortableAppListActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	// overwrite default privacy sorting direction
	protected boolean privacyAscending = false;

	@Override
	protected int getTargetContainer() {
		return R.id.appsByCategoryContainer;
	}

	
	@Override
	protected AppsList configureAppsList(AppsList appsList) {
		Intent intent = getIntent();
		appsList.setCageoryId(intent.getIntExtra("id", -1));
		return appsList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// prepared for tab layout needed in future
		setContentView(R.layout.activity_apps_by_category);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		CategoryDataSource categoryData = new CategoryDataSource(this);
		categoryData.open();
		Category category = categoryData.getCategoryById(getIntent().getIntExtra("id", -1));
		actionBar.setTitle(category.getLabel());
		categoryData.close();

		// For each of the sections in the app, add a tab to the action bar.
		actionBar.addTab(actionBar.newTab().setText(R.string.title_alphabet)
				.setTabListener(this).setIcon(R.drawable.ascending));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_privacy)
				.setTabListener(this).setIcon(R.drawable.descending));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_functional)
				.setTabListener(this).setIcon(R.drawable.descending));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.apps_by_category, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {

		switch (tab.getPosition()) {
		case 0:
			updateListView(tab, AppsListOrder.ALPHABET, alphabetAscending);
			break;
		case 1:
			updateListView(tab, AppsListOrder.PRIVACY_RATING, privacyAscending);
			break;
		case 2:
			updateListView(tab, AppsListOrder.FUNCTIONAL_RATING, functionalAscending);
			break;
		default:
			break;
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {

		switch (tab.getPosition()) {
		case 0:
			// change sorting direction
			alphabetAscending = !alphabetAscending;
			updateListView(tab, AppsListOrder.ALPHABET, alphabetAscending);
			break;
		case 1:
			// change sorting direction
			privacyAscending = !privacyAscending;
			updateListView(tab, AppsListOrder.PRIVACY_RATING, privacyAscending);
			break;
		case 2:
			// change sorting direction
			functionalAscending = !functionalAscending;
			updateListView(tab, AppsListOrder.FUNCTIONAL_RATING, functionalAscending);
			break;
		default:
			break;
		}
	}

	/**
	 * Auto-generated code A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_apps_by_category,
					container, false);
			return rootView;
		}
	}

}