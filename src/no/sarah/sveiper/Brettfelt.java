package no.sarah.sveiper;

import java.util.Timer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//@SuppressLint("ViewConstructor")
public class Brettfelt extends Button {
	private boolean marked;
	private GestureDetector gestureDetector;
	private boolean doubleClick;
	private Brettfelt up;
	private Brettfelt down;
	private Brettfelt left;
	private Brettfelt right;
	private boolean clicked;
	private TextView minesLeft;
	private int[] coordinates;
	final int numberOfFields;
	final int numberOfMines;
	final int hoyde;
	final int bredde;


	//-1 = bomb, 0 = blank, 0< = number of bombs bordering
	private int value;

	public Brettfelt(Context context, int[] coordinates, int numberOfMines, int hoyde, int bredde) {
		super(context);
		this.value = 9; //can never be 9 - this is for determening first click.
		this.setBackgroundResource(R.drawable.unclicked);
		this.clicked = false;
		this.numberOfMines = numberOfMines;
		this.numberOfFields = hoyde*bredde;
		this.coordinates = new int[2];
		this.coordinates[0] = coordinates[0];
		this.coordinates[1] = coordinates[1];

		this.hoyde = hoyde;
		this.bredde = bredde;
		this.marked = false;
		//dobbelklikk:
	    gestureDetector = new GestureDetector(context, new GestureListener());
	    //hold inne:
	    this.setOnTouchListener(new MyOnTouchEvent());
	}
	public void setMinesLeft(TextView minesLeft) {
		this.minesLeft = minesLeft;
	}
	//set-metoder for pekere
	public void setUp(Brettfelt up) {
		this.up = up;
	}
	public void setDown(Brettfelt down) {
		this.down = down;
	}
	public void setRight(Brettfelt right) {
		this.right = right;
	}
	public void setLeft(Brettfelt left) {
		this.left = left;
	}
	public void setCoordinates(int[] coordinates) {
		this.coordinates = coordinates;
	}
	public void setValue(int value) {
		this.value = value;
	}
	//sjekk om har peker til:
    boolean hasUp() {
    	return (this.up != null);
    }
    boolean hasDown() {
    	return (this.down != null);
    }
    boolean hasRight() {
    	return (this.right != null);
    }
    boolean hasLeft() {
    	return (this.left != null);
    }
    boolean hasUpperRight() {
    	return (this.up != null && this.right != null);
    }
    boolean hasUpperLeft() {
    	return (this.up != null && this.left != null);
    }
    boolean hasLowerRight() {
    	return (this.down != null && this.right != null);
    }
    boolean hasLowerLeft() {
    	return (this.down != null && this.left != null);
    }
    void respondToClick() {
    	if (!this.clicked) {
    		//if field is marked and then clicked, mark needs to be removed and subtracted from markedcount:
    		this.clicked = true;
    		if (this.marked) {
    			this.marked = false;
    			addToMinesLeft();
    		}

    		if (this.value==-1) { //bomb
        		((LagBrett) getContext()).numberOfClickedFields++;
        		this.setBackgroundResource(R.drawable.clicked);
    			this.setBackgroundResource(R.drawable.bombe);
    			this.setText("");
    			((LagBrett) getContext()).gameOver();
    			return;

    		} else if (this.value>0 && this.value<9) { //number bordering
        		((LagBrett) getContext()).IncreaseClicked();
        		this.setBackgroundResource(R.drawable.clicked);
    			this.setTextColor(Color.parseColor("#FFFFD5F7"));
    			this.setText(""+value);

    		} else if (this.value==0) { //empty field
        		((LagBrett) getContext()).IncreaseClicked();
        		this.setBackgroundResource(R.drawable.clicked);
    			traverseZeroClick(this);

    		} else if (this.value==9) { //first click
    			//sett verdier paa alle osv:
    			((LagBrett) getContext()).setValueOfFieldsExceptOnFirstClicked(this.coordinates);
    			//naa kan vi klikke:
    			this.clicked=false;
    			this.marked=false;
    			this.respondToClick();
    		}
    	}
    	checkIfWon();
    }
    int findNumberOfMarkedAround() {
    	int numberOfMarkedAround = 0;
    	if (this.hasRight() && this.right.marked) {numberOfMarkedAround++;}
    	if (this.hasUp() && this.up.marked) {numberOfMarkedAround++;}
    	if (this.hasDown() && this.down.marked) {numberOfMarkedAround++;}
    	if (this.hasLeft() && this.left.marked) {numberOfMarkedAround++;}
    	if (this.hasUpperRight() && this.up.right.marked) {numberOfMarkedAround++;}
    	if (this.hasLowerRight() && this.down.right.marked) {numberOfMarkedAround++;}
    	if (this.hasLowerLeft() && this.down.left.marked) {numberOfMarkedAround++;}
    	if (this.hasUpperLeft() && this.up.left.marked) {numberOfMarkedAround++;}
    	return numberOfMarkedAround;
    }
    void clickAllSurroundingUnclickedUnmarked() {
    	if (this.hasRight() && !this.right.clicked && !this.right.marked) {this.right.respondToClick();}
    	if (this.hasUp() && !this.up.clicked && !this.up.marked) {this.up.respondToClick();}
    	if (this.hasDown() && !this.down.clicked && !this.down.marked) {this.down.respondToClick();}
    	if (this.hasLeft() && !this.left.clicked && !this.left.marked) {this.left.respondToClick();}
    	if (this.hasUpperRight() && !this.up.right.clicked && !this.up.right.marked) {this.up.right.respondToClick();}
    	if (this.hasLowerRight() && !this.down.right.clicked && !this.down.right.marked) {this.down.right.respondToClick();}
    	if (this.hasLowerLeft() && !this.down.left.clicked && !this.down.left.marked) {this.down.left.respondToClick();}
    	if (this.hasUpperLeft() && !this.up.left.clicked && !this.up.left.marked) {this.up.left.respondToClick();}
    }
    public void displayBombIfBomb() {
    	if (this.value == -1) {
			this.setBackgroundResource(R.drawable.bombe);
    	}
    }
    void checkIfWon() {
    	if (((LagBrett) getContext()).numberOfClickedFields == numberOfFields-numberOfMines) {
			((LagBrett) getContext()).wonTheGame();
    	}
    }
    void traverseZeroClick(Brettfelt felt) {
    	if (felt.hasUp()) {
    		this.up.respondToClick();
    	}
    	if (felt.hasDown()) {
    		this.down.respondToClick();
    	}
    	if (felt.hasRight()) {
    		this.right.respondToClick();
    	}
    	if (felt.hasLeft()) {
    		this.left.respondToClick();
    	}
    	if (felt.hasUpperRight()) {
    		this.up.right.respondToClick();
    	}
    	if (felt.hasUpperLeft()) {
    		this.up.left.respondToClick();
    	}
    	if (felt.hasLowerRight()) {
    		this.down.right.respondToClick();
    	}
    	if (felt.hasLowerLeft()) {
    		this.down.left.respondToClick();
    	}

    }
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		boolean hopp = gestureDetector.onTouchEvent(e);

		if (doubleClick && findNumberOfMarkedAround()==this.value && clicked) {
			Log.d("mmm", "og hit samtidig");
    		clickAllSurroundingUnclickedUnmarked();
    	} else if (!clicked) {
			if (marked) {
				this.setBackgroundResource(R.drawable.marked);
			} else {
				this.setBackgroundResource(R.drawable.unclicked);
			}

			if (doubleClick) {
				respondToClick();
			}
    	}
	    return hopp;
	}

	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
	    // event when double tap occurs
	    @Override
	    public boolean onDoubleTap(MotionEvent e) {
	        return true;
	    }

	    //toggle/t-hack because this is somehow always called several times on doubleClick:
    	long test = System.currentTimeMillis();
	    public boolean onDoubleTapEvent(MotionEvent e) {
	    	long test2 = System.currentTimeMillis()-test;
	    	if (test2 < 100) {
	    		doubleClick = false;
	    	} else {
	    		doubleClick = true;
	    		test = System.currentTimeMillis();
	    	}
	    	Log.d("mmm", ""+test2);
	        return true;
	    }

	    @Override
	    public boolean onDown(MotionEvent e) {
	    	if (!clicked) {
	    		if (marked) {
	    			marked = false;
	    			addToMinesLeft();
	    		} else {
	    			marked = true;
	    			subtractFromMinesLeft();
	    		}
	    	}
    		return true;
	    }
	}

	void addToMinesLeft() {
		//change minesLeft aka. number of marked mines:
		String minesLeftInfo = minesLeft.getText().toString();
		int minesLeftInt = getMinesLeftFromString(minesLeftInfo);
		String minesLeftText = getTextOnlyFromMinesLeftString(minesLeftInfo);
		minesLeft.setText(minesLeftText + (minesLeftInt+1));
	}

	void subtractFromMinesLeft() {
		//change minesLeft aka. number of marked mines:
		String minesLeftInfo = minesLeft.getText().toString();
		int minesLeftInt = getMinesLeftFromString(minesLeftInfo);
		String minesLeftText = getTextOnlyFromMinesLeftString(minesLeftInfo);
		minesLeft.setText(minesLeftText + (minesLeftInt-1));
	}

	int getMinesLeftFromString(String minesLeftInfo) {
		return Integer.parseInt(minesLeftInfo.replaceAll("^-?[^\\d-]+",""));
	}

	String getTextOnlyFromMinesLeftString(String minesLeftInfo) {
		return minesLeftInfo.replaceAll("-?\\d*$", "");
	}

	private class MyOnTouchEvent implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN && !Brettfelt.this.clicked){
				Brettfelt.this.setBackgroundResource(R.drawable.pressed);
			}
			if(event.getAction() == MotionEvent.ACTION_UP && !Brettfelt.this.clicked){
				Brettfelt.this.setBackgroundResource(R.drawable.unclicked);
			}

			return false;
		}
	}
}


