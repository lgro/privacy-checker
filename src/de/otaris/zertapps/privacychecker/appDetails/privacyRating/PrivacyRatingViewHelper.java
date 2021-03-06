package de.otaris.zertapps.privacychecker.appDetails.privacyRating;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.otaris.zertapps.privacychecker.PrivacyCheckerAlert;
import de.otaris.zertapps.privacychecker.R;
import de.otaris.zertapps.privacychecker.RatingController;
import de.otaris.zertapps.privacychecker.appDetails.Detail;
import de.otaris.zertapps.privacychecker.appDetails.DetailViewHelper;
import de.otaris.zertapps.privacychecker.appDetails.rateApp.RatingElement;
import de.otaris.zertapps.privacychecker.appDetails.rateApp.Registry;
import de.otaris.zertapps.privacychecker.database.dataSource.AppExtendedDataSource;
import de.otaris.zertapps.privacychecker.database.dataSource.AppPermissionDataSource;
import de.otaris.zertapps.privacychecker.database.dataSource.PermissionExtendedDataSource;
import de.otaris.zertapps.privacychecker.database.model.AppExtended;
import de.otaris.zertapps.privacychecker.database.model.AppPermission;
import de.otaris.zertapps.privacychecker.database.model.Permission;
import de.otaris.zertapps.privacychecker.database.model.PermissionExtended;
import de.otaris.zertapps.privacychecker.totalPrivacyRatingAlgorithm.TotalPrivacyRatingAlgorithm;
import de.otaris.zertapps.privacychecker.totalPrivacyRatingAlgorithm.TotalPrivacyRatingAlgorithmFactory;

/**
 * Displays the total privacy rating and its three components (automatic,
 * non-expert and expert rating).
 * 
 * After clicking a "show more" button to extend the detail view, an explanation
 * of the privacy rating and a list of permissions is shown.
 * 
 * When the user selects a permission from this list, an overlay displaying the
 * permission's label and explanation is shown.
 * 
 */
public class PrivacyRatingViewHelper extends DetailViewHelper {

	protected TextView privacyRatingTextView;
	protected TextView privacyRatingCountTextView;
	protected TextView automaticRatingTextView;
	protected TextView nonExpertRatingTextView;
	protected TextView expertRatingTextView;
	protected TextView percentageExplanation;
	protected ImageView privacyRatingIconTextView;
	protected ListView permissionListView;
	protected TextView permissionsListTitle;
	protected RelativeLayout showMoreGroup;
	protected TextView categoryComparison;

	/**
	 * initialize all relevant views
	 * 
	 * @param contextView
	 *            view that wraps the relevant subviews
	 */
	protected void initializeViews(View contextView) {
		privacyRatingTextView = (TextView) contextView
				.findViewById(R.id.app_detail_privacy_rating_value);
		privacyRatingCountTextView = (TextView) contextView
				.findViewById(R.id.app_detail_privacy_rating_count);
		automaticRatingTextView = (TextView) contextView
				.findViewById(R.id.app_detail_privacy_rating_automatic_text);
		nonExpertRatingTextView = (TextView) contextView
				.findViewById(R.id.app_detail_privacy_rating_nonexpert_text);
		expertRatingTextView = (TextView) contextView
				.findViewById(R.id.app_detail_privacy_rating_expert_text);
		privacyRatingIconTextView = (ImageView) contextView
				.findViewById(R.id.app_detail_privacy_rating_image);
		permissionListView = (ListView) contextView
				.findViewById(R.id.app_detail_rating_permissions_list);
		showMoreGroup = (RelativeLayout) contextView
				.findViewById(R.id.app_detail_privacy_rating_show_more_group);
		permissionsListTitle = (TextView) contextView
				.findViewById(R.id.app_details_privacy_rating_permissions_title);
		categoryComparison = (TextView) contextView
				.findViewById(R.id.app_detail_privacy_rating_category);
		percentageExplanation = (TextView) contextView
				.findViewById(R.id.app_detail_permissions_explanation);
	}

	private double roundToOneDecimalPlace(float f) {
		return (java.lang.Math.round(f * 10) / 10.0);
	}

	@Override
	public View getView(Context context, ViewGroup parent, Detail detail)
			throws IllegalArgumentException {
		if (!(detail instanceof PrivacyRating))
			throw new IllegalArgumentException(
					"Illegal Detail Object. Expected Rating.");

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.app_detail_privacy_rating,
				parent, false);

		initializeViews(rowView);

		// privacy rating
		PrivacyRating rating = (PrivacyRating) detail;
		AppExtended app = rating.getApp();
		privacyRatingTextView.setText(roundToOneDecimalPlace(app
				.getPrivacyRating()) + "");

		privacyRatingTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callPrivacyRatingInfo(v.getContext());
			}
		});

		// privacy rating count
		privacyRatingCountTextView.setText("("
				+ (app.getNonExpertRating().size() + app.getExpertRating()
						.size()) + ")");

		// automatic rating
		automaticRatingTextView.setText(roundToOneDecimalPlace(app
				.getCategoryWeightedAutoRating()) + "");

		// non-expert rating
		nonExpertRatingTextView.setText(roundToOneDecimalPlace(app
				.getTotalNonExpertRating())
				+ " ("
				+ app.getNonExpertRating().size() + ")");

		// expert rating
		expertRatingTextView.setText(roundToOneDecimalPlace(app
				.getTotalExpertRating())
				+ " ("
				+ app.getExpertRating().size()
				+ ")");

		// privacy rating locks-icon
		privacyRatingIconTextView.setImageResource(new RatingController()
				.getIconRatingLocks(app.getPrivacyRating()));

		if (app.getCategory() != null) {
			if (app.getCategory().getAverageAutoRating() > app
					.getAutomaticRating()) {
				categoryComparison.setText(context.getResources().getString(
						R.string.app_detail_privacy_rating_category_worse)
						+ " " + app.getCategory().getLabel());

			} else {
				categoryComparison.setText(context.getResources().getString(
						R.string.app_detail_privacy_rating_category_better)
						+ " " + app.getCategory().getLabel());
			}
		} else {
			categoryComparison.setVisibility(ViewGroup.GONE);
		}

		List<Permission> permissionList = app.getPermissionList();
		AppPermissionDataSource appPermissionData = new AppPermissionDataSource(
				context);
		PermissionExtendedDataSource permissionExtendedData = new PermissionExtendedDataSource(
				context);
		appPermissionData.open();
		permissionExtendedData.open();

		// populate AppPermission into PermissionExtended
		ArrayList<PermissionExtended> permissionExtendedList = new ArrayList<PermissionExtended>();

		for (Permission permission : permissionList) {
			AppPermission appPermission = appPermissionData
					.getAppPermissionByAppAndPermissionId(app.getId(),
							permission.getId());
			PermissionExtended permExt = permissionExtendedData
					.extendPermission(appPermission);
			permissionExtendedList.add(permExt);
		}
		appPermissionData.close();
		permissionExtendedData.close();

		if (permissionList.size() <= 0) {
			// set no permissions required title
			permissionsListTitle
					.setText(context
							.getResources()
							.getString(
									R.string.app_details_privacy_rating_permissions_title_no_permissions));
			percentageExplanation.setVisibility(ViewGroup.GONE);
		} else {
			// add list item adapter for permissions
			permissionListView.setAdapter(new PermissionsListItemAdapter(
					context, permissionExtendedList));
			permissionListView.setScrollContainer(false);

			// set click listener for list items
			permissionListView
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							// get previously selected permission that need to
							// be displayed
							PermissionExtended permission = (PermissionExtended) parent
									.getItemAtPosition(position);

							// display permission as alert dialog
							PrivacyCheckerAlert
									.callPermissionDialogPermissionExtended(
											permission, view.getContext());
						}
					});

			// scale list depending on its size
			setListViewHeigthBasedOnChildren(permissionListView,
					permissionList.size());
		}

		// get "show more" button
		ToggleButton showMoreButton = (ToggleButton) rowView
				.findViewById(R.id.app_detail_privacy_rating_more);
		// set click listener
		showMoreButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton toggleButton,
							boolean isChecked) {

						if (isChecked) {
							// show explanation and permissions list
							showMoreGroup.setVisibility(View.VISIBLE);
						} else {
							// hide explanation and permissions list
							showMoreGroup.setVisibility(View.GONE);
						}

					}
				});

		return rowView;
	}

	/**
	 * Scale a listView depending on the number of elements you want to display.
	 * 
	 * @param listView
	 * @param numberOfElements
	 *            the number of list elements to be displayed at maximum
	 */
	private void setListViewHeigthBasedOnChildren(ListView listView,
			int numberOfElements) {

		// get adapter from list view
		PermissionsListItemAdapter adapter = (PermissionsListItemAdapter) listView
				.getAdapter();
		int totalHeight = 0;

		// accumulate total height by measuring each list element
		for (int i = 0; i < numberOfElements; i++) {
			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		// update list layout to maximum height
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (adapter.getCount() - 1));
		listView.setLayoutParams(params);

		// notify list view about layout changes
		listView.requestLayout();
	}

	/**
	 * Displays an info alert dialog with the explanation of the priavcy rating
	 * composition.
	 * 
	 * @param context
	 */
	private void callPrivacyRatingInfo(Context context) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.app_detail_alert_dialog);
		dialog.getWindow()
				.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

		TextView titleTextView = (TextView) dialog
				.findViewById(R.id.app_detail_alert_dialog_textview_title);
		titleTextView.setText(R.string.privacy_rating_composition);

		TextView messageTextview = (TextView) dialog
				.findViewById(R.id.app_detail_alert_dialog_textview_description);
		messageTextview
				.setText(R.string.app_details_privacy_rating_explanation);

		Button okButton = (Button) dialog
				.findViewById(R.id.app_detail_alert_dialog_button_ok);
		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
}
