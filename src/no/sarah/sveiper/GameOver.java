package no.sarah.sveiper;

import no.sarah.sveiper.R;

import android.app.Activity;
import android.widget.RelativeLayout.LayoutParams;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.KeyEvent;


public class GameOver extends Activity {
	Typeface font;
	String difficultyString;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		difficultyString = getIntent().getExtras().getString("difficulty");


		RelativeLayout rl = new RelativeLayout(this);
		LinearLayout ll = new LinearLayout(this);
		LinearLayout ll2 = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		//TextView text = new TextView(this);
		//text.setTextSize(57);
		//text.setText("GAME OVER");

		ImageView gameOver = new ImageView(this);
		gameOver.setImageResource(R.drawable.gameover);
		ImageView image = new ImageView(this);
		image.setImageResource(R.drawable.sprengt);





		Button menuButton = new Button(this);
		menuButton.setText("Menu");
		menuButton.setTypeface(font);
		menuButton.setBackgroundResource(R.drawable.gamebuttons);
		menuButton.setWidth(dpToPixels(30));
		menuButton.setTextColor(Color.parseColor("#FFFFD5F7"));
		menuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startOver();
			}
		});

		Button retryButton = new Button(this);
		retryButton.setText("Retry");
		retryButton.setTypeface(font);
		retryButton.setBackgroundResource(R.drawable.gamebuttons);
		retryButton.setWidth(dpToPixels(50));
		retryButton.setTextColor(Color.parseColor("#FFFFD5F7"));
		retryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				retry();
			}
		});


		ll.addView(gameOver);
		ll.addView(image);
		ll2.addView(menuButton);
		ll2.addView(retryButton);


		LayoutParams llParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		LayoutParams ll2Params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);


		llParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		ll2Params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		ll2Params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		ll2.setPadding(40, 0, 40, 40);

		ll.setLayoutParams(llParams);
		ll2.setLayoutParams(ll2Params);
		rl.getRootView().setBackgroundColor(Color.parseColor("#FF310039"));

		rl.addView(ll);
		rl.addView(ll2);

		setContentView(rl);

	}

	void startOver() {
		this.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
	}

	void retry() {
		Intent i = new Intent(this, LagBrett.class);
		//maa ta toString som hack pga. ingen getmetode for enums.
		i.putExtra("difficulty", difficultyString);
		this.finish();
		startActivity(i);
		overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
	}

	//menu naar back blir klikket.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startOver();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public int dpToPixels(int dp) {
		Resources r = getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
	}
}
