package greencommitment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DataServer {

	int port;
	ServerSocket serverSocket;
	Socket socket;
	DocumentBuilder documentbuilder;
	InputStream inputStream;
	ObjectInput objectInput;
	File folder;

	public static void main(String[] args) {
		DataServer server = new DataServer(8020);
		server.startSever();
		server.startIO();
		//server.startObserving();
	}

	public void startIO() {

		folder = new File("XMLData");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			documentbuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void startSever() {
		try {
			serverSocket = new ServerSocket(port);
			socket = serverSocket.accept();
			inputStream = socket.getInputStream();
			objectInput = new ObjectInputStream(inputStream);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public DataServer(int port) {
		this.port = port;
	}

}
