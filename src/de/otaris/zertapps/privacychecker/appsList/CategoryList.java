package de.otaris.zertapps.privacychecker.appsList;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import de.otaris.zertapps.privacychecker.R;
import de.otaris.zertapps.privacychecker.database.dataSource.CategoryDataSource;
import de.otaris.zertapps.privacychecker.database.model.Category;

public class CategoryList extends ListFragment {

	public CategoryList() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get all categories from database
		CategoryDataSource categoryData = new CategoryDataSource(getActivity());
		categoryData.open();
		List<Category> categories = categoryData.getAllCategories();
		categoryData.close();

		// initialize category list item adapter
		CategoryListItemAdapter adapter = new CategoryListItemAdapter(
				getActivity(), categories);

		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list_fragment, container, false);
	}

	@Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), AppsByCategoryActivity.class);

		// put id from rowView tag as extra to the AppsByCategoryActivity
		intent.putExtra("id", (Integer) v.getTag());

		startActivity(intent);
	}
}