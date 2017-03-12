package greencommitment;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class DataGenerator { // serialization
	private final int minCons;
	private final int maxCons;
	private int cons;
	private Random random;
	private Socket socket;
	private OutputStream outputStream;
	private ObjectOutput objectOutput;

	public DataGenerator(int minCons, int maxCons) {
		this.minCons = minCons;
		this.maxCons = maxCons;
		this.cons = (int) (minCons + maxCons) / 2;
		random = new Random();
	}

	public static void main(String[] args) throws UnknownHostException, InterruptedException {

		DataGenerator client = new DataGenerator(0, 100000);
		client.connectTillSuccess(InetAddress.getLocalHost(), 8020);
		while (true) {
			client.sendCurrentConsuption();
			Thread.sleep(1000);
		}
	}

	public void sendCurrentConsuption() {
		getConsuption();
		try {
			objectOutput.writeObject(cons);
		} catch (IOException e) {
			System.err.println("IOException");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	public void connectTillSuccess(InetAddress ipAddress, int port){
		boolean connected = false;
		while(!connected){
			connected = connectToServer(ipAddress, port);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void connectTillSuccess(String ipAddress, int port){
		connectTillSuccess(ipAddress, port);
	}

	public boolean connectToServer(InetAddress ipAddress, int port) {
		return connectToServer(ipAddress.toString(), port);
	}

	public boolean connectToServer(String ipAddress, int port) {
		try {
			socket = new Socket(ipAddress, port);
			outputStream = socket.getOutputStream();
			objectOutput = new ObjectOutputStream(outputStream);
			return true;
		} catch (UnknownHostException e) {
			System.err.println("UnknownHostException\nThe server is unreachable.");
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public int getConsuption() {
		refreshConsuption();
		return cons;
	}

	private void refreshConsuption() {
		cons = random.nextInt(maxCons - minCons) + minCons;
	}
}