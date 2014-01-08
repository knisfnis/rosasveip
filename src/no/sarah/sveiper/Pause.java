package no.sarah.sveiper;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class Pause extends Activity {
	RelativeLayout container;
	LinearLayout buttonContainer;
	Button resumeButton;
	Button restartButton;
	Button menuButton;
	Intent returnIntent;
	Typeface font;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		font = Typeface.createFromAsset(getAssets(), "creative_block.ttf");

		
		buttonContainer = new LinearLayout(this);
		container = new RelativeLayout(this);
		
		resumeButton = new Button(this);
		restartButton = new Button(this);
		menuButton = new Button(this);
		
		buttonContainer.setOrientation(LinearLayout.VERTICAL);
		RelativeLayout.LayoutParams buttonContainerParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		buttonContainerParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		buttonContainer.setLayoutParams(buttonContainerParams);
		
		RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		container.setLayoutParams(containerParams);
		
		TextView fyll1 = new TextView(this);
		fyll1.setHeight(20);
		TextView fyll2 = new TextView(this);
		fyll2.setHeight(20);

		
		
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
		
		resumeButton.setText("Resume");
		resumeButton.setWidth(250);
		resumeButton.setTypeface(font);
		resumeButton.setBackgroundResource(R.drawable.unclicked);
		resumeButton.setTextColor(Color.parseColor("#FFFFD5F7"));
		resumeButton.setOnTouchListener(pressedListener);
		
		restartButton.setText("Restart");
		restartButton.setWidth(250);
		restartButton.setTypeface(font);
		restartButton.setBackgroundResource(R.drawable.unclicked);
		restartButton.setTextColor(Color.parseColor("#FFFFD5F7"));
		restartButton.setOnTouchListener(pressedListener);

		menuButton.setText("Menu");
		menuButton.setWidth(250);
		menuButton.setTypeface(font);
		menuButton.setBackgroundResource(R.drawable.unclicked);
		menuButton.setTextColor(Color.parseColor("#FFFFD5F7"));
		menuButton.setOnTouchListener(pressedListener);





		
		resumeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Pause.this.endActivity("resume");
		    	}
		});
		
		restartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Pause.this.endActivity("restart");
		    	}
		});
		
		menuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Pause.this.endActivity("menu");
		    	}
			
			
		});
		

           


		buttonContainer.addView(resumeButton);
		buttonContainer.addView(fyll1);
		buttonContainer.addView(restartButton);
		buttonContainer.addView(fyll2);
		buttonContainer.addView(menuButton);

		container.addView(buttonContainer);
		
		container.getRootView().setBackgroundColor(Color.parseColor("#FF310039"));
		setContentView(container);
	}
	
	void endActivity(String result) {
		 Intent returnIntent = new Intent();
		 returnIntent.putExtra("result",result);
		 setResult(RESULT_OK,returnIntent);     
		 finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	endActivity("resume");
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
