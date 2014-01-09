package no.sarah.sveiper.activity;

import no.sarah.sveiper.Difficulty;
import no.sarah.sveiper.LagBrett;
import no.sarah.sveiper.R;
import no.sarah.sveiper.activity.Highscore;

import android.widget.RelativeLayout.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StartSpill extends Activity {
	LinearLayout container;
	ImageView logo;
	ImageView instructions;
	LinearLayout chooseDifficulty;
	RelativeLayout difficultyContainer;
	Difficulty difficulty;
	Typeface font;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		font = Typeface.createFromAsset(getAssets(), "creative_block.ttf");
		container = new LinearLayout(this);
		logo = new ImageView(this);
		instructions = new ImageView(this);
		chooseDifficulty = new LinearLayout(this);
		difficultyContainer = new RelativeLayout(this);

		logo.setImageResource(R.drawable.header2);
		LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		logoParams.topMargin = dpToPixels(-50);
		logo.setLayoutParams(logoParams);


		instructions.setImageResource(R.drawable.instructions);

		container.setOrientation(LinearLayout.VERTICAL);
		chooseDifficulty.setOrientation(LinearLayout.HORIZONTAL);


		TextView fyll1 = new TextView(this);
		TextView fyll2 = new TextView(this);
		TextView fyll3 = new TextView(this);
		TextView fyll4 = new TextView(this);
		fyll1.setWidth(dpToPixels(10));
		fyll2.setWidth(dpToPixels(10));
		fyll3.setWidth(dpToPixels(10));
		fyll4.setWidth(dpToPixels(10));

		OnTouchListener pressedListener =  new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
    				v.setBackgroundResource(R.drawable.clicked);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
    				v.setBackgroundResource(R.drawable.unclicked);
                }
                return false;
            }
		};

		Button easy = new Button(this);
		easy.setBackgroundResource(R.drawable.unclicked);
		easy.setText("Lett");
		easy.setTypeface(font);
		easy.setMinWidth(dpToPixels(90));
		easy.setTextColor(Color.parseColor("#FFFFD5F7"));
		easy.setOnTouchListener(pressedListener);

		Button medium = new Button(this);
		medium.setBackgroundResource(R.drawable.unclicked);
		medium.setText("Ok");
		medium.setTypeface(font);
		medium.setMinWidth(dpToPixels(90));
		medium.setTextColor(Color.parseColor("#FFFFD5F7"));
		medium.setOnTouchListener(pressedListener);


		Button hard = new Button(this);
		hard.setBackgroundResource(R.drawable.unclicked);
		hard.setText("Umulig");
		hard.setTypeface(font);
		hard.setMinWidth(dpToPixels(90));
		hard.setTextColor(Color.parseColor("#FFFFD5F7"));
		hard.setOnTouchListener(pressedListener);


		Button highscores = new Button(this);
		highscores.setBackgroundResource(R.drawable.gamebuttons);
		highscores.setText("Highscores");
		highscores.setTypeface(font);
		highscores.setTextColor(Color.parseColor("#FFFFD5F7"));
		highscores.setWidth(dpToPixels(200));


		chooseDifficulty.addView(fyll1);
		chooseDifficulty.addView(easy);
		chooseDifficulty.addView(fyll2);
		chooseDifficulty.addView(medium);
		chooseDifficulty.addView(fyll3);
		chooseDifficulty.addView(hard);
		chooseDifficulty.addView(fyll4);

		setDifficultyParams();
		container.addView(logo);
		difficultyContainer.addView(chooseDifficulty);
		container.addView(difficultyContainer);
		container.addView(instructions);
		container.addView(highscores);
		container.setBackgroundColor(Color.parseColor("#FF310039"));
		setContentView(container);


		easy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startGame(Difficulty.EASY);
			}
		});

		medium.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startGame(Difficulty.MEDIUM);
			}
		});

		hard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startGame(Difficulty.HARD);
			}
		});

		highscores.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToHighScores();
			}
		});

	}

	void setDifficultyParams() {
		LayoutParams difficultyParams = new LayoutParams(
		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		difficultyParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		chooseDifficulty.setLayoutParams(difficultyParams);
	}

	void startGame(Difficulty difficulty) {
		Intent i = new Intent(this, LagBrett.class);
		//maa ta toString som hack pga. ingen getmetode for enums.
		i.putExtra("difficulty", difficulty.toString());
		startActivity(i);
	}

	void goToHighScores() {
		Intent i = new Intent(this, Highscore.class);
		startActivity(i);
	}

	public int dpToPixels(int dp) {
		Resources r = getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
	}


}
