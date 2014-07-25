package de.otaris.zertapps.privacychecker.appDetails.privacyRating;

import java.util.List;

import de.otaris.zertapps.privacychecker.R;
import de.otaris.zertapps.privacychecker.R.id;
import de.otaris.zertapps.privacychecker.R.layout;
import de.otaris.zertapps.privacychecker.appDetails.Detail;
import de.otaris.zertapps.privacychecker.appDetails.DetailViewHelper;
import de.otaris.zertapps.privacychecker.appDetails.permissions.Permissions;
import de.otaris.zertapps.privacychecker.appDetails.permissions.PermissionsListItemAdapter;
import de.otaris.zertapps.privacychecker.database.model.AppExtended;
import de.otaris.zertapps.privacychecker.database.model.Permission;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class PrivacyRatingViewHelper extends DetailViewHelper {

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
		// TextView textView = (TextView) rowView
		// .findViewById(R.id.app_detail_list_item_name);
		// get permissions list from permissions detail
		PrivacyRating rating = (PrivacyRating) detail;
		AppExtended app = rating.getApp();
		List<Permission> permissionList = app.getPermissionList();

		// get permission list view
		ListView listView = (ListView) rowView
				.findViewById(R.id.app_detail_rating_permissions_list);

		// add list item adapter for permissions
		listView.setAdapter(new PermissionsListItemAdapter(context,
				permissionList));
		listView.setScrollContainer(false);

		// show max 4 permissions
		ViewGroup.LayoutParams updatedLayout = listView.getLayoutParams();
		final float scale = context.getResources().getDisplayMetrics().density;
		int pixels = (int) (22 * scale +0.5f);
		updatedLayout.height = pixels * listView.getCount();
		listView.setLayoutParams(updatedLayout);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				RelativeLayout overlay = (RelativeLayout) parent.getRootView().findViewById(R.id.app_detail_overlay);
				overlay.setVisibility(View.VISIBLE);
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.app_detail_rating_permission_overlay, overlay, false);
				Permission permission = (Permission) view.getTag(); 
				TextView permissionLabel = (TextView) layout.findViewById(R.id.app_detail_rating_permission_name);
				permissionLabel.setText(permission.getLabel());
				TextView permissionDescription = (TextView) layout.findViewById(R.id.app_detail_rating_permission_name);
				
				
				overlay.addView(layout);	
			
			}
		});
		
		return rowView;
	}

}