package no.sarah.sveiper;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Brettfelt extends Button {
	private final GestureDetector gestureDetector;
    private final int[] coordinates;
    private Brettfelt up;
    private Brettfelt down;
    private Brettfelt left;
    private Brettfelt right;
    private TextView minesLeft;
    private boolean currentClickIsDoubleClick;
    private boolean isClicked;
    private boolean isMarked;
    final int numberOfFields;
	final int numberOfMines;

    private int value;

	public Brettfelt(final Context context,
                     final int[] coordinates,
                     final int numberOfMines,
                     final int height,
                     final int width) {
		super(context);
		this.value = 9; //instansiated to 9, won't receive actual value until first field has been clicked.
		this.setBackgroundResource(R.drawable.unclicked);
		this.isClicked = false;
		this.numberOfMines = numberOfMines;
		this.numberOfFields = height * width;
		this.coordinates = new int[2];
		this.coordinates[0] = coordinates[0];
		this.coordinates[1] = coordinates[1];

        this.isMarked = false;
		//dobbelklikk:
	    gestureDetector = new GestureDetector(context, new GestureListener());
	    //hold inne: ?????
	    this.setOnTouchListener(new MyOnTouchEvent());
	}

    public void setValue(final int value) {
        this.value = value;
    }
	public void setMinesLeft(final TextView minesLeft) {
		this.minesLeft = minesLeft;
	}

    //set-metoder for nabopekere:
	public void setUp(final Brettfelt up) {
		this.up = up;
	}
    public void setDown(final Brettfelt down) {
		this.down = down;
	}
    public void setRight(final Brettfelt right) {
		this.right = right;
	}
    public void setLeft(final Brettfelt left) {
		this.left = left;
	}

	//nabopeker-sjekkere:
    boolean hasUp() {
    	return (up != null);
    }
    boolean hasDown() {
    	return (down != null);
    }
    boolean hasRight() {
    	return (right != null);
    }
    boolean hasLeft() {
    	return (left != null);
    }
    boolean hasUpperRight() {
    	return (up != null && right != null);
    }
    boolean hasUpperLeft() {
    	return (up != null && left != null);
    }
    boolean hasLowerRight() {
        return (down != null && right != null);
    }
    boolean hasLowerLeft() {
        return (down != null && left != null);
    }

    /**
     * Reveals bomb on field.
     */
    public void displayBombIfBomb() {
        if (value == -1) {
            setBackgroundResource(R.drawable.bombe);
        }
    }

    private void respondToClick() {
        if (isClicked) return;

        isClicked = true;

        removeMark();

        if (value==-1) {
            respondToBombClick();
            return;

        } else if (value>0 && value<9) {
            respondToNumberedFieldClick();

        } else if (value==0) {
            respondToEmptyFieldClick();

        } else if (value==9) {
            respondToFirstClick();

        } else {
            throw new IllegalArgumentException("Field value cannot be <0 or >9");
        }

        if (allBombFreeFieldsHaveBeenRevealed()) {
            finishGame();
        }
    }

    private void finishGame() {
        ((LagBrett) getContext()).wonTheGame();
    }

    private void respondToFirstClick() {
        //sett verdier paa alle osv:
        ((LagBrett) getContext()).setValueOfFieldsExceptOnFirstClicked(this.coordinates);
        //naa kan vi klikke:
        this.isClicked =false;
        this.isMarked =false;
        this.respondToClick();
    }

    private void respondToEmptyFieldClick() {
        ((LagBrett) getContext()).IncreaseClicked();
        this.setBackgroundResource(R.drawable.clicked);
        traverseEmptyFieldClick(this);
    }

    private void respondToNumberedFieldClick() {
        ((LagBrett) getContext()).IncreaseClicked();
        this.setBackgroundResource(R.drawable.clicked);
        this.setTextColor(Color.parseColor("#FFFFD5F7"));
        this.setText(""+value);
    }

    private void respondToBombClick() {
        ((LagBrett) getContext()).numberOfClickedFields++;
        this.setBackgroundResource(R.drawable.clicked);
        this.setBackgroundResource(R.drawable.bombe);
        this.setText("");
        ((LagBrett) getContext()).gameOver();
    }

    private void removeMark() {
        if (this.isMarked) {
            this.isMarked = false;
            addToMinesLeft();
        }
    }

    private void clickAllSurroundingUnclickedUnmarked() {
    	if (hasRight() && !this.right.isClicked && !this.right.isMarked) {this.right.respondToClick();}
    	if (hasUp() && !this.up.isClicked && !this.up.isMarked) {this.up.respondToClick();}
    	if (hasDown() && !this.down.isClicked && !this.down.isMarked) {this.down.respondToClick();}
    	if (hasLeft() && !this.left.isClicked && !this.left.isMarked) {this.left.respondToClick();}
    	if (hasUpperRight() && !this.up.right.isClicked && !this.up.right.isMarked) {this.up.right.respondToClick();}
    	if (hasLowerRight() && !this.down.right.isClicked && !this.down.right.isMarked) {this.down.right.respondToClick();}
    	if (hasLowerLeft() && !this.down.left.isClicked && !this.down.left.isMarked) {this.down.left.respondToClick();}
    	if (hasUpperLeft() && !this.up.left.isClicked && !this.up.left.isMarked) {this.up.left.respondToClick();}
    }

    private int findNumberOfMarkedAround() {
        int numberOfMarkedAround = 0;
        if (hasRight() && this.right.isMarked) {numberOfMarkedAround++;}
        if (hasUp() && this.up.isMarked) {numberOfMarkedAround++;}
        if (hasDown() && this.down.isMarked) {numberOfMarkedAround++;}
        if (hasLeft() && this.left.isMarked) {numberOfMarkedAround++;}
        if (hasUpperRight() && this.up.right.isMarked) {numberOfMarkedAround++;}
        if (hasLowerRight() && this.down.right.isMarked) {numberOfMarkedAround++;}
        if (hasLowerLeft() && this.down.left.isMarked) {numberOfMarkedAround++;}
        if (hasUpperLeft() && this.up.left.isMarked) {numberOfMarkedAround++;}
        return numberOfMarkedAround;
    }

    /**
     * Called if an empty field is clicked, and opens all reachable empty fields and their bordering number-fields.
     *
     * @param felt An empty field (value == 0).
     */
    private void traverseEmptyFieldClick(final Brettfelt felt) {
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

    private boolean allBombFreeFieldsHaveBeenRevealed() {
        return ((LagBrett) getContext()).numberOfClickedFields == numberOfFields-numberOfMines;
    }

    @Override
	public boolean onTouchEvent(final MotionEvent e) {
		final boolean hopp = gestureDetector.onTouchEvent(e);

		if (currentClickIsDoubleClick && findNumberOfMarkedAround() == value && isClicked) {
    		clickAllSurroundingUnclickedUnmarked();
    	} else if (!isClicked) {
			if (isMarked) {
				this.setBackgroundResource(R.drawable.marked);
			} else {
				this.setBackgroundResource(R.drawable.unclicked);
			}

			if (currentClickIsDoubleClick) {
				respondToClick();
			}
    	}
	    return hopp;
	}

	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
	    // event when double tap occurs
	    @Override
	    public boolean onDoubleTap(final MotionEvent e) {
	        return true;
	    }

	    //toggle-hack because this is somehow always called several times on doubleClick:
    	long test = System.currentTimeMillis();
	    public boolean onDoubleTapEvent(final MotionEvent e) {
	    	final long test2 = System.currentTimeMillis()-test;
	    	if (test2 < 100) {
	    		currentClickIsDoubleClick = false;
	    	} else {
	    		currentClickIsDoubleClick = true;
	    		test = System.currentTimeMillis();
	    	}
	        return true;
	    }

	    @Override
	    public boolean onDown(final MotionEvent e) {
	    	if (!isClicked) {
	    		if (isMarked) {
	    			isMarked = false;
	    			addToMinesLeft();
	    		} else {
	    			isMarked = true;
	    			subtractFromMinesLeft();
	    		}
	    	}
    		return true;
	    }
	}

	void addToMinesLeft() {
		//change minesLeft aka. number of marked mines:
		final String minesLeftInfo = minesLeft.getText().toString();
		final int minesLeftInt = getMinesLeftFromString(minesLeftInfo);
		final String minesLeftText = getTextOnlyFromMinesLeftString(minesLeftInfo);
		minesLeft.setText(minesLeftText + (minesLeftInt+1));
	}

	void subtractFromMinesLeft() {
		//change minesLeft aka. number of marked mines:
		final String minesLeftInfo = minesLeft.getText().toString();
		final int minesLeftInt = getMinesLeftFromString(minesLeftInfo);
		final String minesLeftText = getTextOnlyFromMinesLeftString(minesLeftInfo);
		minesLeft.setText(minesLeftText + (minesLeftInt-1));
	}

	private int getMinesLeftFromString(final String minesLeftInfo) {
		return Integer.parseInt(minesLeftInfo.replaceAll("^-?[^\\d-]+",""));
	}

	private String getTextOnlyFromMinesLeftString(final String minesLeftInfo) {
		return minesLeftInfo.replaceAll("-?\\d*$", "");
	}

	private class MyOnTouchEvent implements OnTouchListener {
		@Override
		public boolean onTouch(final View v, final MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN && !Brettfelt.this.isClicked){
				Brettfelt.this.setBackgroundResource(R.drawable.pressed);
			}
			if(event.getAction() == MotionEvent.ACTION_UP && !Brettfelt.this.isClicked){
				Brettfelt.this.setBackgroundResource(R.drawable.unclicked);
			}

			return false;
		}
	}
}


