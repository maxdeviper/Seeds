package com.diadementi.seeds.views;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codecamp14.seeds.R;
import com.codecamp14.seeds.models.Category;
import com.diadementi.seeds.controllers.AddCampaignFragment;
import com.diadementi.seeds.helpers.UrlLink;
import com.diadementi.seeds.models.Campaign;
import com.diadementi.seeds.views.ListFragment.Type;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {
	Intent i;
	Type t = Type.PUB;
	String mode = "view";
	Campaign c;
	ImageView cImage;
	TextView cTitle;
	TextView cDesc;
	TextView cCat;
	TextView cDescText;
	Category cat;
	Button commentBtn;
	Button donateBtn;
	SharedPreferences pref;
	String email;
	TextView cTimeCreated;
	TextView cAuthor;
	public static final String PREFS_NAME = "MyPrefsFile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		LinearLayout l = (LinearLayout) findViewById(R.id.action);
		l.setVisibility(View.GONE);
		pref = getSharedPreferences(PREFS_NAME, 0);
		email = pref.getString("email", null);
		cImage = (ImageView) this.findViewById(R.id.imageView);
		cTitle = (TextView) this.findViewById(R.id.title);
		cCat = (TextView) this.findViewById(R.id.category);
		cDesc = (TextView) this.findViewById(R.id.desc);
		cDescText = (TextView) this.findViewById(R.id.descText);
		commentBtn = (Button) this.findViewById(R.id.comment);
		donateBtn = (Button) this.findViewById(R.id.donate);
		cTimeCreated = (TextView) this.findViewById(R.id.createdAt);
		cAuthor = (TextView) this.findViewById(R.id.author);
		i = getIntent();
		String type = i.getExtras().containsKey("type") ? i
				.getStringExtra("type") : "PUB";
		t = Type.valueOf(type);
		mode = i.getExtras().containsKey("mode") ? i.getStringExtra("mode")
				: mode;
		if (savedInstanceState == null) {
			mode = i.getExtras().containsKey("mode") ? i.getStringExtra("mode")
					: mode;
			// mode = i.getStringExtra("mode");
			// mode=TextUtils.isEmpty(mode)?"view":mode;

			switch (mode) {
			case "add":
				// getSupportFragmentManager().beginTransaction()
				// .add(R.id.container, new CampaignFragment())
				// .commit();
				Intent in = new Intent(this, Add_EditActivity.class);
				in.putExtras(i);
				in.putExtra("mode", "add");
				startActivity(in);
				break;
			case "edit":
				// getSupportFragmentManager().beginTransaction()
				// .add(R.id.container, new CampaignFragment())
				// .commit();
				Intent inte = new Intent(this, Add_EditActivity.class);
				inte.putExtras(i);
				inte.putExtra("mode", "edit");
				startActivity(inte);
				break;
			default:
				// String url = i.getStringExtra("url");
				// getSupportFragmentManager().beginTransaction()
				// .add(R.id.container, t.equals(Type.PRI)?new
				// DetailsFragment(url,MODE.PRI):new DetailsFragment(url))
				// .commit();

				String json = "";
				json = i.getExtras().containsKey("json") ? i
						.getStringExtra("json") : null;
				if (!TextUtils.isEmpty(json)) {
					Log.e("c in json", json);
					c = new Gson().fromJson(json, Campaign.class);
					Picasso.with(this).load(c.getImageUrl())
							.placeholder(R.drawable.blank_campaign)
							.error(R.drawable.ic_launcher).into(cImage);
					Log.e("c title", c.getTitle());
					cTitle.setText(toCapFirst(c.getTitle()));
					cat = c.getCategory();
					cCat.setText(c.getCategory().getName()
							.toUpperCase(Locale.getDefault()));
					cDescText.setText(c.getDesc());
					String createdAt = c.getTimeCreated();
					createdAt = convertTime(createdAt);
					cAuthor.setText(String.format("by %s",c.getCreator()));
					if (createdAt != null) {
						cTimeCreated.setText(createdAt);
					} else {
						cTimeCreated.setVisibility(View.INVISIBLE);
					}
				}

			}

		}
	}

	private String toCapFirst(String s) {
		return s = s.substring(0, 1).toUpperCase(Locale.getDefault())
				+ s.substring(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.campaign_display, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		 switch (id) {
		// // Respond to the action bar's Up/Home button
		// // case android.R.id.home:
		// // NavUtils.navigateUpFromSameTask(this);
		// // return true;
		 case R.id.action_share:
		 sharePost();
		 return true;
		 case R.id.donate:
		 donate();
		 return true;
		 }
		return super.onOptionsItemSelected(item);
	}

	public void comment(View view) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "commented", Toast.LENGTH_LONG).show();
	}

	public void sharePost() {
		// TODO Auto-generated method stub
		String url = UrlLink.getCampaignView(c.getId());
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, url);
		startActivity(shareIntent);
	}

	public void donate() {
		String url = UrlLink.donate + "/" + c.getId() + "/" + email;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

	private String convertTime(String cDate) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date mdateObject = format.parse(cDate);
			long time = mdateObject.getTime();
			long now = new Date().getTime();
			return DateUtils.getRelativeTimeSpanString(time, now,
					DateUtils.DAY_IN_MILLIS).toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
