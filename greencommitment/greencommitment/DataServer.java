package greencommitment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DataServer {

	HashMap<String, Data> dataBank = new HashMap<String, Data>();

	int port;
	ServerSocket serverSocket;
	Socket socket;
	DocumentBuilder documentbuilder;
	InputStream inputStream;
	ObjectInput objectInput;
	File folder;
	Transformer transformer;
	Document document;

	public static void main(String[] args) {
		DataServer server = new DataServer(8020);
		server.startSever();
		server.startIO();
		server.load();
		server.startObserving();
	}

	public void load() {

		File[] files = folder.listFiles();
		for (File file : files) {
			try {
				Document doc = documentbuilder.parse(file);
				Data data = new Data(file.getName(), doc);
				dataBank.put(data.getName(), data);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void checkSave(){
		
		for (Data data : dataBank.values()) {
			if(data.isSaveNeeded()){
				dataSave(data.getDoc());
				data.resetTimer();
			}
		}
	}
	
	private void dataSave(Document doc){
		try {
			String address = doc.getDocumentElement().getTagName();
			File file = new File(folder.getAbsolutePath() + "/" + address + ".xml");
			file.createNewFile();
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);

			transformer.transform(source, result);
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
	}

	public void startObserving() {
		while (true) {
			try {
				Document received = (Document) objectInput.readObject();
				String address = received.getDocumentElement().getTagName();
				if (dataBank.containsKey(address)) {
					//append(received);
				} else {
					Data newData = createData(received);
					dataBank.put(newData.getName(), newData);
				}

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			

		}

	}

	private Data createData(Document received) {
		Document newDocument = documentbuilder.newDocument();
		Element root = received.getDocumentElement(); //jóroot
		newDocument.appendChild(root);
		Element timeStamp = (Element) root.getAttributeNode("TimeStamp"); //jótimestamp
		Element measOri = (Element) root.getAttributeNode("Measurement");
		NodeList measures = measOri.getChildNodes();
		Element measure = newDocument.createElement("measure");
		for (int i = 0; i < measures.getLength(); i++) {
			measure.appendChild(measures.item(i));
		}
		measure.setAttribute("TimeStamp", timeStamp.getNodeValue());
		root.appendChild(measure);
		
		
		
		Data newData = new Data(root.getTagName(), newDocument);
		return newData;
	}


	class Data {

		String name;
		Date lastSave;
		Document doc;
		int saveDelay;

		public boolean isSaveNeeded() {
			Date now = new Date();
			if (now.getTime() - lastSave.getTime() > 2000) {
				return true;
			}
			return false;
		}

		public void resetTimer() {
			lastSave = new Date();
		}

		public Data(String name, Document doc) {
			this.name = name;
			this.doc = doc;
			lastSave = new Date();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getLastSave() {
			return lastSave;
		}

		public void setLastSave(Date lastSave) {
			this.lastSave = lastSave;
		}

		public Document getDoc() {
			return doc;
		}

		public void setDoc(Document doc) {
			this.doc = doc;
		}

	}

	public void startIO() {

		folder = new File("XMLData");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			documentbuilder = factory.newDocumentBuilder();
			document = documentbuilder.newDocument();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
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
