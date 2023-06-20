package nss.my.iscte.student;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageDetails extends AppCompatActivity {

	TextView sFrom, sSubject, sDate, sMsg,avatar;
	WebView sHTMLmessage;
	ImageView btnBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_details);
		Toolbar toolbar = findViewById(R.id.my_toolbar);
		setSupportActionBar(toolbar);
		// add back arrow to toolbar
		if (getSupportActionBar() != null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}

		sFrom = (TextView) findViewById(R.id.txtSendBY);
		sSubject = (TextView) findViewById(R.id.txtTitle);
		sDate = (TextView) findViewById(R.id.txtDate);
		avatar = (TextView) findViewById(R.id.avatar);
		sMsg = (TextView) findViewById(R.id.txtEmailDetails);
		sHTMLmessage = (WebView) findViewById(R.id.webview);
		Bundle mBundle = getIntent().getExtras();
		if (mBundle != null) {
			avatar.setText(mBundle.getString("avatar"));
			((GradientDrawable) avatar.getBackground()).setColor(mBundle.getInt("colorAvatar"));
			sFrom.setText(mBundle.getString("sender"));
			sSubject.setText(mBundle.getString("title"));
			//sMsg.setText(mBundle.getString("details"));
			sDate.setText(mBundle.getString("date"));
			sHTMLmessage.loadData(mBundle.getString("messageHTML"), "text/html", "utf-8");
		}

		btnBack = (ImageView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
				mainIntent.putExtra("frameID", 1);
				finish();
				onBackPressed();
				//startActivity(mainIntent);
			}
		});
	}

}
