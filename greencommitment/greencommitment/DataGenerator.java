package greencommitment;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DataGenerator { // serialization
	private final int minCons;
	private final int maxCons;
	private int consX;
	private int consY;
	private Random random;
	private Socket socket;
	private OutputStream outputStream;
	private ObjectOutput objectOutput;

	public DataGenerator(int minCons, int maxCons) {
		this.minCons = minCons;
		this.maxCons = maxCons;
		this.consX = (int) (minCons + maxCons) / 3;
		this.consY = (int) (minCons + maxCons) / 3;
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
			Document doc = createDoc();
			objectOutput.writeObject(doc);
		} catch (IOException e) {
			System.err.println("IOException");
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			System.err.println("ParserConfigurationException");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private Document createDoc() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element rootelem = doc.createElement("measure");
		doc.appendChild(rootelem);
		Element rate = doc.createElement("rate");
		rate.setAttribute("x", Integer.toString(consX));
		rate.setAttribute("y", Integer.toString(consX));
		return doc;
	}

	public void connectTillSuccess(InetAddress ipAddress, int port) {
		boolean connected = false;
		while (!connected) {
			connected = connectToServer(ipAddress, port);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void connectTillSuccess(String ipAddress, int port) {
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

	public int[] getConsuption() {
		refreshConsuption();
		return new int[]{consX, consY};
	}

	private void refreshConsuption() {
		consX = random.nextInt(maxCons - minCons) + minCons;
		if(maxCons - consX >= minCons){
			consY = random.nextInt(maxCons - consX) + minCons;
		}
	}
}