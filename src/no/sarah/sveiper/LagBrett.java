package no.sarah.sveiper;
//import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class LagBrett extends Activity{
	Difficulty difficulty;
	Handler mHandler;
	long mStartTime;
	Runnable mUpdateTimeTask;
	LinearLayout container;
	LinearLayout headerContainer;
	LinearLayout minesLeftBoard;
	ImageView skeletonHead;
	ImageView header;
	RelativeLayout underHeader;
	LinearLayout board;
	TextView minesLeftField;
	TextView visAntallMiner;
	TextView timer;
	int numberOfClickedFields;
	Brettfelt[][] displayBrett;
	int hoyde;
	int bredde;
	int antallMiner;
	BrettGenerator brettverdier;
	SharedPreferences highscores;
	boolean gameIsRunning;
	PauseDialog pauseDialog;
	//this intent for restarting the game:
	Intent thisIntent;
	Typeface font;
	int fieldSize;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		fieldSize = 35;
		//saves intent for later restoration
		thisIntent = getIntent();
		
		//hack pga. ingen getEnum, saa konverterer fra String:
		difficulty = Difficulty.valueOf(getIntent().getExtras().getString("difficulty"));
		
		setBoardSize();
		setNumberOfMines();

		//Brettet av brettfelt:
		displayBrett = new Brettfelt[hoyde][bredde];
		numberOfClickedFields = 0;
		generateLayoutViews();
		timer.setText("00:00:00");
		setAttributesOfViews();
		createFieldGridView();
		addViewsToLayout();
	}

	//determines board size after screen-size:
	private void setBoardSize() {
		int heightResolution = getScreenHeight();
		int widthResolution = getScreenWidth();
		//global variables:
		hoyde = 0;
		bredde = 0;
		while (heightResolution>((hoyde*(dpToPixels(fieldSize)+4))+(pixelsToDp(110)))) {
			hoyde++;
		}
		while (widthResolution>(bredde*(dpToPixels(fieldSize)+4))) {
			bredde++;
		}
	}
	
	//determines number of mines after board size:
	private void setNumberOfMines() {
		int totalNumberOfFields = hoyde*bredde;
		switch (difficulty) {
		case EASY: antallMiner=totalNumberOfFields/(int) 10; break;
		case MEDIUM: antallMiner=totalNumberOfFields/(int) 6; break;
		case HARD: antallMiner=totalNumberOfFields/(int) 4; break;
		}
	}
	
	//creates randomized board with mines except on the first field already clicked:
	public void setValueOfFieldsExceptOnFirstClicked(int[] firstClicked) {

		int[] temp = new int[2];
		temp[0] = firstClicked[1];
		temp[1] = firstClicked[0];

		brettverdier = new BrettGenerator(hoyde, bredde, antallMiner, firstClicked);
		setVerdier();
		setPointersToSurroundingFieldsForAllFields();
		startGame();
	}
	
	//called in method above. Stores the randomized values in the board.
	private void setVerdier() {
		for (int i=0; i<hoyde; i++) {
			for (int j=0; j<bredde; j++) {
				displayBrett[i][j].setValue(brettverdier.brett[i][j]);
			}
		}
	}
	
	//starts game incl. timer:
	private void startGame() {
		this.startTimer();
		gameIsRunning = true;
	}

	private void setPointersToSurroundingFieldsForAllFields() {
		for (int i=0; i<hoyde; i++) {
			for (int j=0; j<bredde; j++) {
				if (i+1<hoyde) {
					displayBrett[i][j].setDown(displayBrett[i+1][j]);
				}

				if (i-1>=0) {
					displayBrett[i][j].setUp(displayBrett[i-1][j]);
				}

				if (j+1<bredde) {
					displayBrett[i][j].setRight(displayBrett[i][j+1]);
				}

				if (j-1>=0) {
					displayBrett[i][j].setLeft(displayBrett[i][j-1]);
				}
			}
		}
	}
	
	//called when mine is clicked:
	public void gameOver() {
		gameIsRunning = false;
		exposeBombs();
		Intent i = new Intent(this, GameOver.class);
		i.putExtra("difficulty", difficulty.toString());
		this.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		startActivity(i);
	}
	
	//exposes all bombs when gameOver
	private void exposeBombs() {
				for (int i=0; i<hoyde; i++) {
			for (int j=0; j<bredde; j++) {
				displayBrett[i][j].displayBombIfBomb();
			}
		}
	}
	
	//called when all fields but bombs are clicked:
	public void wonTheGame() {
		gameIsRunning = false;
		String recordType = getRecordType();
		boolean newRecord = !recordType.equals("norecord");
		if (newRecord) {
			saveNewRecord(recordType);
		}
		Intent i = new Intent(this, WonTheGame.class);
		i.putExtra("time", timer.getText());
		i.putExtra("newRecord", newRecord);
		i.putExtra("difficulty", difficulty.toString());
		this.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
		startActivity(i);
	}
		
	//converts density independent pixels to pixels
	public int dpToPixels(int dp) {
		Resources r = getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
	}
	
	//converts pixels to density independent pixels
	public int pixelsToDp(int pixels) {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		float logicalDensity = metrics.density;
		return (int) (pixels * logicalDensity + 0.5);
	}
	
	public void IncreaseClicked() {
		this.numberOfClickedFields++;
	}
	
	//generates the visible board:
	void createFieldGridView() {
		Brettfelt felt;
		int[] coordinates = new int[2];
				
		for (int i=0; i<hoyde; i++) {
			LinearLayout row = new LinearLayout(this);
			//innholdet i radene = horisontalt.
			row.setOrientation(LinearLayout.HORIZONTAL);
			//Button felt = new Button(this);
			for (int j=0; j<bredde; j++) {
				//set value:
				coordinates[0] = i;
				coordinates[1] = j;
				felt = new Brettfelt(this, coordinates, antallMiner, hoyde, bredde);
				felt.setMinesLeft(minesLeftField);
				//height/width-dp = 40
			
				
				int pixels = dpToPixels(fieldSize);
				felt.setHeight(pixels);
				felt.setWidth(pixels);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(pixels, pixels);
				pixels = dpToPixels(5);
				params.setMargins(pixels, 0, pixels, 0);
				felt.setLayoutParams(params); 
				row.addView(felt);
				displayBrett[i][j] = felt;
			}
			board.addView(row);
		}
	}
	
	//instansiates all View objects:
	void generateLayoutViews() {
		container = new LinearLayout(this);
		headerContainer = new LinearLayout(this);
		timer = new TextView(this);
		minesLeftBoard = new LinearLayout(this);
		header = new ImageView(this);
		skeletonHead = new ImageView(this);
		minesLeftField = new TextView(this);
		underHeader = new RelativeLayout(this);
		board = new LinearLayout(this);
	}
	
	//sets attributes of View objects:
	void setAttributesOfViews() {
		setOrientationOfContainers();
		setHeaderAttributes();
		setMinesLeftOnBoardAttributes();
		setUnderHeaderAttributes();
		setBoardAttributes();
	}
	
	//sets attributes of MinesLeftOnBoard-field
	void setMinesLeftOnBoardAttributes() {
		minesLeftBoard.setOrientation(LinearLayout.HORIZONTAL);
		FrameLayout.LayoutParams minesLeftBoardParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		minesLeftBoard.setPadding(0, 0, 20, 20);
		minesLeftBoard.setLayoutParams(minesLeftBoardParams);
		minesLeftBoard.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
		minesLeftField.setText("X "+ antallMiner);
	}
	
	//set orientation of LinearLayout-containers:
	void setOrientationOfContainers() {
		container.setOrientation(LinearLayout.VERTICAL);
		board.setOrientation(LinearLayout.VERTICAL);
		headerContainer.setOrientation(LinearLayout.HORIZONTAL);
	}
	
	//sets attributes of the Header
	void setHeaderAttributes() {
		font = Typeface.createFromAsset(getAssets(), "creative_block.ttf");
		timer.setTypeface(font);
		minesLeftField.setTypeface(font);
		headerContainer.setBackgroundResource(R.drawable.header_bg);
		header.setImageResource(R.drawable.header);
		skeletonHead.setImageResource(R.drawable.skeleton);
		FrameLayout.LayoutParams headerParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		FrameLayout.LayoutParams headerContainerParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		header.setLayoutParams(headerParams);
		headerContainer.setLayoutParams(headerContainerParams);
	}
	
	//adds the Views to the layout:make visible
	void addViewsToLayout() {
		headerContainer.addView(header);
		headerContainer.addView(timer);
		minesLeftBoard.addView(skeletonHead);
		minesLeftBoard.addView(minesLeftField);
		headerContainer.addView(minesLeftBoard);
		underHeader.addView(board);
		container.addView(headerContainer);
		container.addView(underHeader);
		this.setContentView(container);
	}
	
	//sets attributes to everything below Header:
	void setUnderHeaderAttributes() {
		RelativeLayout.LayoutParams underHeaderParams = new RelativeLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		underHeader.setLayoutParams(underHeaderParams);
		underHeader.setBackgroundColor(Color.parseColor("#FF310039"));
	}
	
	//sets attributes to the actual board:
	void setBoardAttributes() {
		RelativeLayout.LayoutParams boardParams = new RelativeLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

		boardParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		board.setLayoutParams(boardParams);
	}
	
	@SuppressWarnings("deprecation") //bc is here to support S2
	int getScreenHeight() {
		Display display = getWindowManager().getDefaultDisplay();
		return display.getHeight();
	}
	
	@SuppressWarnings("deprecation") //bc is here to support S2
	int getScreenWidth() {
		Display display = getWindowManager().getDefaultDisplay();
		return display.getWidth();
	}
	
	
	///////// TIMER METHODS //////////
	public void startTimer() {
		setUpTimer();
		createRunnable();
        mStartTime = SystemClock.uptimeMillis();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 100);
	}
	
	public void resumeTimer() {
        mStartTime = SystemClock.uptimeMillis();
        mHandler.postDelayed(mUpdateTimeTask, 100);
        noEDetPause = false;
	}
	
	long time;
	long displayTime;
	boolean noEDetPause;
	public void pauseTimer() {
		if (gameIsRunning) {
			//test = hack pga. bug: den hopper ca. 3 sekunder om man har dobbelt opp med pause.
			if (!noEDetPause) {
				time += elapseTime;
			}
			noEDetPause = true;
			mHandler.removeCallbacks(mUpdateTimeTask);
		}
	}

	private void setUpTimer() {
		mHandler = new Handler();
		createRunnable();
		time = 0;
		displayTime = 0;
	}
	
	long elapseTime;
	private void createRunnable() {
		mUpdateTimeTask = new Runnable() {
			 public void run() {

		           final long start = mStartTime;
		           elapseTime = SystemClock.uptimeMillis() - start;
	               displayTime = time + elapseTime;
		           int seconds = (int) (displayTime / 1000);
		           int minutes = seconds / 60;
		           int miliseconds = (int) (displayTime / 10);
		           miliseconds = miliseconds % 100;
		           seconds     = seconds % 60;

		           if (minutes < 10) {
		        	   if (seconds < 10) {
		        		   if (miliseconds < 10) {
		        			   timer.setText("0" + minutes + ":0" + seconds + ":0" + miliseconds);
		        		   } else {
		        			   timer.setText("0" + minutes + ":0" + seconds + ":" + miliseconds);
		        		   }
		        	   } else {
		        		   if (miliseconds < 10) {
		        			   timer.setText("0" + minutes + ":" + seconds + ":0" + miliseconds);
		        		   } else {
		        			   timer.setText("0" + minutes + ":" + seconds + ":" + miliseconds);
		        		   }
		        	   }
		           } else {
		        	   if (seconds < 10) {
		        		   if (miliseconds < 10) {
		        			   timer.setText("" + minutes + ":0" + seconds + ":0" + miliseconds);
		        		   } else {
		        			   timer.setText("" + minutes + ":0" + seconds + ":" + miliseconds);
		        		   }
		        	   } else {
		        		   if (miliseconds < 10) {
		        			   timer.setText("" + minutes + ":" + seconds + ":0" + miliseconds);
		        		   } else {
		        			   timer.setText("" + minutes + ":" + seconds + ":" + miliseconds);
		        		   }
		        	   }
		           }

		           // add a delay to adjust for computation time
		           long delay = (10 - (displayTime%10));

		           mHandler.postDelayed(this, delay);
		       }
		    };
	}
	//////////////////////////////////
	
	static int NUMBEROFRECORDSFOREACHDIFFICULTY = 3;
	private String getRecordType() {
		int currentScore = getNumbersFromString((String) timer.getText());

		highscores = getSharedPreferences("highscores", MODE_PRIVATE);

		int i = 1;
		String difficultyType;
		while (0==0) {
			//for instance HARD1, HARD2, HARD3:
			difficultyType = difficulty.toString() + i;
			if ((highscores.getString(difficultyType, "200:00").equals("Ingen rekord")) ||
					(currentScore<getNumbersFromString(highscores.getString(difficultyType, "200:00")))) {
				break;
			} else if (i == NUMBEROFRECORDSFOREACHDIFFICULTY) {
				difficultyType = "norecord";
				break;
			}
			i++;
		} 
		return difficultyType;
	}
	
	private void saveNewRecord(String recordType) {

		int recordNumber = Integer.parseInt(""+recordType.charAt(recordType.length()-1));

		highscores = getSharedPreferences("highscores", MODE_PRIVATE);
        SharedPreferences.Editor ed = highscores.edit();

        String newRecord = (String) timer.getText();
		String oldRecord;
        while (recordNumber<=NUMBEROFRECORDSFOREACHDIFFICULTY) {
        	//hente rekorden som alt staar der:
        	oldRecord = highscores.getString(difficulty.toString()+recordNumber, "Ingen rekord");
        	//lagre den nye rekorden
        	ed.putString(difficulty.toString()+recordNumber, newRecord);
        	//paa neste steg skal rekorden som stod der fra foer flyttes ned:
        	newRecord = new String(oldRecord);
            recordNumber++;
        }
        ed.commit();
	}
	
	//cleans String to contain only numbers:
	private int getNumbersFromString(String time) {
		return Integer.parseInt(time.replaceAll("[^\\d.]", ""));
	}
	
	//////////PAUSE//////////////////
	@Override
	public void onRestart() {	
	    super.onRestart();
	}
	
	boolean isAlreadyPauseScreen;
	
	@Override
	public void onStop() {		
	    super.onStop();
	    if (!isAlreadyPauseScreen && gameIsRunning) {
	    	startPause();
	    }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	if (gameIsRunning) {
	    		onStop();
	    		isAlreadyPauseScreen = true;


	    	} else { this.finish(); }
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void startPause () {
		pauseTimer();
		Intent i = new Intent(this, Pause.class);
		startActivityForResult(i, 1);
		//isAlreadyPauseScreen = true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if(resultCode == RESULT_OK){
				String result= data.getStringExtra("result");
				
				if (result.equals("resume")) {
					this.isAlreadyPauseScreen = false;
			    	this.resumeTimer();
				} else if (result.equals("menu")) {
					this.finish();
				} else if (result.equals("restart")) {
					this.gameIsRunning = false;
					this.restartGame();
				}
				
				
			}
			if (resultCode == RESULT_CANCELED) {
				//Write your code on no result return 
			}
		}
	}


	void restartGame() {
		finish();
		startActivity(thisIntent);	}

	class PauseDialog extends AlertDialog {
		public PauseDialog(Context context)
		{
			super(context);
		}

		public PauseDialog(Context context, int theme)
		{
			super(context, theme);
		}
		@Override
		public void onBackPressed() {
			LagBrett.this.isAlreadyPauseScreen = false;
			super.onBackPressed();
			LagBrett.this.resumeTimer();
		}
	}
}


