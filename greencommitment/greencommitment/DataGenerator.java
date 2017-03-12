package greencommitment;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;

import javax.xml.parsers.*;

import java.util.Date;
import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DataGenerator { // serialization
	private String address;
	private final int minCons;
	private final int maxCons;
	private int consX;
	private int consY;
	private Random random;
	private Socket socket;
	private OutputStream outputStream;
	private ObjectOutput objectOutput;

	public DataGenerator(int minCons, int maxCons, String address) {
		this.address = address;
		this.minCons = minCons;
		this.maxCons = maxCons;
		random = new Random();
	}

	public static void main(String[] args) throws UnknownHostException, InterruptedException {

		DataGenerator client = new DataGenerator(0, 100000, "Miskolc nemtom u 69.");
		client.connectTillSuccess(InetAddress.getLocalHost(), 8020);
		client.startStreaming();
	}

	public void startStreaming() {
		while (true) {
			sendCurrentConsuption();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
		Element x = doc.createElement("x");
		x.setTextContent(Integer.toString(consX));
		rate.appendChild(x);
		Element y = doc.createElement("y");
		y.setTextContent(Integer.toString(consY));
		rate.appendChild(y);
		Element date = doc.createElement("TimeStamp");
		date.setTextContent(getDate());
		rate.appendChild(date);

		return doc;
	}

	private String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyy.MM.dd HH:mm:ss");
		return format.format(new Date());
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
		return new int[] { consX, consY };
	}

	private void refreshConsuption() {
		consX = random.nextInt(maxCons - minCons) + minCons;
		if (maxCons - consX >= minCons) {
			consY = random.nextInt(maxCons - consX) + minCons;
		}
	}
}