package no.sarah.sveiper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class Highscore extends Activity {
	Typeface font;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		font = Typeface.createFromAsset(getAssets(), "creative_block.ttf");

		TextView scoreBoard;
		SharedPreferences highscores;
		RelativeLayout rl = new RelativeLayout(this);
		LinearLayout ll = new LinearLayout(this);
		LinearLayout ll1 = new LinearLayout(this);
		LinearLayout ll2 = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
			
			
			
		highscores = getSharedPreferences("highscores", MODE_PRIVATE);
		String highscoreEasyString1 = highscores.getString("EASY1", "Ingen rekord");
		String highscoreEasyString2 = highscores.getString("EASY2", "Ingen rekord");
		String highscoreEasyString3 = highscores.getString("EASY3", "Ingen rekord");
		String highscoreMediumString1 = highscores.getString("MEDIUM1", "Ingen rekord");
		String highscoreMediumString2 = highscores.getString("MEDIUM2", "Ingen rekord");
		String highscoreMediumString3 = highscores.getString("MEDIUM3", "Ingen rekord");
		String highscoreHardString1 = highscores.getString("HARD1", "Ingen rekord");
		String highscoreHardString2 = highscores.getString("HARD2", "Ingen rekord");
		String highscoreHardString3 = highscores.getString("HARD3", "Ingen rekord");

		scoreBoard = new TextView(this);

		scoreBoard.setText("Lett:\n");
		scoreBoard.append("1.  "+ highscoreEasyString1 + "\n");
		scoreBoard.append("2.  "+ highscoreEasyString2 + "\n");
		scoreBoard.append("3.  "+ highscoreEasyString3 + "\n");
		scoreBoard.append("\n\nOk:\n");
		scoreBoard.append("1.  "+ highscoreMediumString1 + "\n");
		scoreBoard.append("2.  "+ highscoreMediumString2 + "\n");
		scoreBoard.append("3.  "+ highscoreMediumString3 + "\n");
		scoreBoard.append("\n\nUmulig:\n");
		scoreBoard.append("1.  "+ highscoreHardString1 + "\n");
		scoreBoard.append("2.  "+ highscoreHardString2 + "\n");
		scoreBoard.append("3.  "+ highscoreHardString3 + "\n");
		
		scoreBoard.setTypeface(font);
		scoreBoard.setTextColor(Color.parseColor("#FFFFD5F7"));
		scoreBoard.setTextSize(25);
		
		
		ImageView highscore = new ImageView(this);
		highscore.setImageResource(R.drawable.highscores);

		Button backButton = new Button(this);
		backButton.setText("Back"); 
		backButton.setBackgroundResource(R.drawable.gamebuttons);
		backButton.setTypeface(font);
		backButton.setWidth(dpToPixels(1000));
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startOver();
			}
		});


		ll.addView(highscore);
		ll1.addView(scoreBoard);
		ll2.addView(backButton);

		RelativeLayout.LayoutParams ll2Params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams ll1Params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);


		ll1Params.addRule(RelativeLayout.CENTER_IN_PARENT);
		ll2Params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		ll2Params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		ll2.setPadding(40, 0, 40, 40);

		ll1.setLayoutParams(ll1Params);
		ll2.setLayoutParams(ll2Params);
		rl.getRootView().setBackgroundColor(Color.parseColor("#FF310039"));

		rl.addView(ll);
		rl.addView(ll1);
		rl.addView(ll2);
		setContentView(rl);

	}

	void startOver() {
		this.finish();
	}

	//restart naar back blir klikket.

	public int dpToPixels(int dp) {
		Resources r = getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
	}
}


