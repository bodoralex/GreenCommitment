package greencommitment;

import java.util.Random;

public class DataGenerator { //serialization

	final int minCons;
	final int maxCons;
	int cons;
	Random random;

	public DataGenerator(int minCons, int maxCons) {
		this.minCons = minCons;
		this.maxCons = maxCons;
		this.cons = (int) (minCons + maxCons) / 2;
		random = new Random();
	}

	public int getConsuption() {
		refreshConsuption();
		return cons;
	}

	private void refreshConsuption() {
		cons = random.nextInt(maxCons - minCons) + minCons;
	}

}
