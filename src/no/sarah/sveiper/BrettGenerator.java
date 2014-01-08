package no.sarah.sveiper;
import java.util.Random;

	
public class BrettGenerator {
	int[][] brett;
	int[] dontHitThis;
	//takes dimensions and number of mines as input.
	
	BrettGenerator(int x, int y, int antallMiner) {
		brett = new int[x][y];
		dontHitThis = new int[2];
		dontHitThis[0] = x;
		fyllMedMiner(antallMiner);
		fyllMedTall();
	}
	
	BrettGenerator(int x, int y, int antallMiner, int[] avoidThisField) {
		brett = new int[x][y];
		dontHitThis = avoidThisField;
		fyllMedMiner(antallMiner);
		fyllMedTall();
	}
	
	private void fyllMedTall() {
		for (int i=0; i<brett.length; i++) {
			for (int j=0; j<brett[0].length; j++) {
				//hvis mine:
				if (brett[i][j] == -1) {
					
					//unngaa kanter:
					if (i>0) {
						if (brett[i-1][j]!=-1) {
							brett[i-1][j]++;
						}
					}
					
					if (i<(brett.length-1)) {
						if (brett[i+1][j]!=-1) {
							brett[i+1][j]++;
						}
					}
					
					if (j>0) {
						if (brett[i][j-1]!=-1) {
							brett[i][j-1]++;
						}
					}
					
					if (j<(brett[0].length-1)) {
						if (brett[i][j+1]!=-1) {
							brett[i][j+1]++;
						}
					}
					
					
					//TODO: Fiks dette paa en bedre maate. Naa dobbeltester jeg.
					if (i>0 && j>0) {
						if (brett[i-1][j-1]!=-1) {
							brett[i-1][j-1]++;
						}
					}
					
					if (i<(brett.length-1) && j<(brett[0].length-1)) {
						if (brett[i+1][j+1]!=-1) {
							brett[i+1][j+1]++;
						}
					}
					
					if (i<(brett.length-1) && j>0) {
						if (brett[i+1][j-1]!=-1) {
							brett[i+1][j-1]++;
						}
					}
					
					if (i>0 && j<(brett[0].length-1)) {
						if (brett[i-1][j+1]!=-1) {
							brett[i-1][j+1]++;
						}
					}
				}
			}
		}
	}

	//takes dimension as input.
	BrettGenerator(int x, int y) {
		brett = new int[x][y];
		int antallMiner = (x*y) / 25; //1 mine per 25 squares
		fyllMedMiner(antallMiner);
		fyllMedTall();

	}
	
	//all input is default
	BrettGenerator() {
		int dimension = 20;
		brett = new int[dimension][dimension];
		int antallMiner = (dimension*dimension)/25;
		fyllMedMiner(antallMiner);
		fyllMedTall();
	}

	private void fyllMedMiner(int antallMiner) {
		int[][] mineposisjoner = genererMineposisjonKoordinater(antallMiner);
		for (int i=0; i<mineposisjoner.length; i++) {
				//plasser mine p?? brett, med koordinater fra mineposisjoner:
				brett[mineposisjoner[i][0]][mineposisjoner[i][1]] = -1;
		}
	}
	
	//lag mineposisjoner randomized.
	//genererer array av vektorer med koordinater:
	private int[][] genererMineposisjonKoordinater(int antallMiner) {
		
		
		//generatedPosition [x, y]
		int[] generatedPosition = new int[2];
		
		int xLengde = this.brett.length;
		int yLengde = this.brett[0].length;
		
		//fordi [0][0] er defaultverdi i vektor og det skal vaere mulig med mine \
		//paa [0][0] maa vi fylle arrayen med verdier det ikke gaar an aa treffe \
		//man kan ikke treffe xLengde paa x fordi indeksering gaar fra 0-(lengde-1):
		//array containing vectors with x and y coordinates:
		int[][] mineposisjoner = fillWithUnhittableValues(new int[antallMiner][2]);
		Random r = new Random();
		for (int i=0; i<antallMiner; i++) {

			generatedPosition[0] = r.nextInt(xLengde);
			generatedPosition[1] = r.nextInt(yLengde);

			//sjekk om posisjonen alt eksisterer (kan ikke ha to miner samme sted):
			if (!contains(mineposisjoner, generatedPosition)) {

				//lagre generert posisjon i arrayen som skal returneres:
				mineposisjoner[i][0] = generatedPosition[0];
				mineposisjoner[i][1] = generatedPosition[1];
			} else {
				//dersom den fantes, kjoer loopen paa nytt.
				i--;
			}
		}
		return mineposisjoner;
	}
	
	private boolean contains(int[][] posisjoner, int[] posisjon) {
		for (int i=0; i<posisjoner.length; i++) {
			if (posisjoner[i][0]==posisjon[0] && posisjoner[i][1]==posisjon[1]) {
				return true;
			}
		}	
		return false;
	}
	
	private int[][] fillWithUnhittableValues(int[][] array) {
		for (int i=0; i<array.length; i++) {
			array[i][0] = dontHitThis[0];
			array[i][1] = dontHitThis[1];
		}
		return array;
	}
	
}
