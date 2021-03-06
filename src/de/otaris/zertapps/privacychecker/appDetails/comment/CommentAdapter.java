package de.otaris.zertapps.privacychecker.appDetails.comment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.otaris.zertapps.privacychecker.R;
import de.otaris.zertapps.privacychecker.database.interfaces.App;
import de.otaris.zertapps.privacychecker.database.model.Comment;

/**
 * Adapter that handles the headlines of comments (date + version) and the
 * comment text itself.
 */
public class CommentAdapter extends ArrayAdapter<Comment> {

	private final Context context;
	private final List<Comment> values;
	private final App app;

	public CommentAdapter(Context context, List<Comment> values, App app) {
		super(context, R.layout.app_detail_comment_item, values);
		this.context = context;
		this.values = values;
		this.app = app;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.app_detail_comment_item,
				parent, false);

		// get views from layout
		TextView contentView = (TextView) rowView
				.findViewById(R.id.app_detail_comment_item_content);
		TextView versionView = (TextView) rowView
				.findViewById(R.id.app_detail_comment_item_version);
		TextView dateView = (TextView) rowView
				.findViewById(R.id.app_detail_comment_item_date);

		// get date
		Date date = new Date((long) values.get(position).getTimestamp() * 1000L);

		// format date
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MMM yyyy",
				parent.getContext().getResources().getConfiguration().locale);
		String stringDate = dateFormat.format(date);

		// set the appropriate information to the views
		dateView.setText(stringDate);

		boolean matchesCurrentVersion = app.getVersion().equals(
				values.get(position).getVersion());
		int versionText = (matchesCurrentVersion) ? R.string.comment_current_version
				: R.string.comment_old_version;
		versionView.setText(versionText);
		contentView.setText(values.get(position).getContent());

		return rowView;
	}
}
