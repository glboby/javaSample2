package showManifest;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.border.EtchedBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.*;


class urlLoader {
	static String getPage(String _url) {
		String result = "";
		try {
			BufferedReader br = null;
			URL url = new URL(_url);
	        HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
	        br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));
	        String line;
	        while ((line = br.readLine()) != null) {
	            result = result + line.trim();
	        }
	        //System.out.println(result);
	        return result;
		} catch (Exception e) {
	        System.out.println(e.getMessage());
	        return result;
	    }
	}
}

class xmlParser {
	public static Element getRootNode(String _xml) {
		try {
	        InputSource is = new InputSource(new StringReader(_xml));
	        
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setNamespaceAware(true);
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        
	        Document doc = builder.parse(is);
	        Element element = doc.getDocumentElement();
	        
	        return element;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}

class CStream{
	CStream(String type,
			String name,
			String subtype,
			long chunks,
			long timescale,
			String url,
			int qlevels) {
		this.type = type;
		this.name = name;
		this.subtype = subtype;
		this.chunks = chunks;
		this.timescale = timescale;
		this.url = url;
		this.qlevels = qlevels;	
	}
	String type;
	String name;
	String subtype;
	long chunks;
	long timescale;
	String url;
	int qlevels;
	ArrayList<CChunk> qChunk = new ArrayList<CChunk>();
	ArrayList<Double> qCTime = new ArrayList<Double>();
	public String getType() {
		return type;
	}
	public String getName() {
		return name;
	}}

class CChunk {
	CChunk(long t, long d, long n, long r){
		this.t = t;
		this.d = d;
		this.n = n;
		this.r = r;
	}
	long t;
	long d;
	long n;
	long r;
}

class manifestParser {
	static Node nRoot;
	static int numStreams;
	static ArrayList<CStream> qStream = new ArrayList<CStream>();
	static void clearQueue() {
		qStream.clear();
	}
	static void parsing(Element _n) {
		nRoot = _n;
		NodeList streamList = _n.getChildNodes();
		numStreams = streamList.getLength();
		
		// loop for streams (tracks)
		for(int i = 0 ; i < numStreams; i++) {
			Node steamNode = streamList.item(i);
			
			if(steamNode.getNodeName().equals("StreamIndex")) {
				
				String type = "";
				String name = "";
				String subtype = "";
				long chunks = -1;
				long timescale = -1;
				String url = "";
				int qlevels = -1;
				
				NamedNodeMap streamAttr = steamNode.getAttributes();
				for(int j = 0, numAtt = streamAttr.getLength(); j < numAtt; j++) {
					Node nd = streamAttr.item(j);
					String _name = nd.getNodeName();
		    		String _value = nd.getTextContent();
		    		switch(_name) {
		    		case "Type":
		    			type = _value;
		    			break;
		    		case "Name":
		    			name = _value;
		    			break;
		    		case "Subtype":
		    			subtype = _value;
		    			break;
		    		case "Chunks":
		    			chunks = Long.parseLong(_value);
		    			break;
		    		case "TimeScale":
		    			timescale = Long.parseLong(_value);
		    			break;
		    		case "Url":
		    			url = _value;
		    			break;
		    		case "QualityLevels":
		    			qlevels = Integer.parseInt(_value);
		    			break;
		    		}
				}
				
				CStream stream = new CStream(type, name, subtype, chunks, timescale, url, qlevels);
				
				// find chunk
				NodeList chunkList = steamNode.getChildNodes();
				//int numChunk = chunkList.getLength();
				//NamedNodeMap streamAttr = steamNode.getAttributes();
				for(int m = 0, numChunk = chunkList.getLength(); m < numChunk; m++) {
					Node chunkNode = chunkList.item(m);
					if(chunkNode.getNodeName().equals("c")) {
						long t = -1;
						long d = -1;
						long n = -1;
						long r = 1;
						
						NamedNodeMap chunkAttr = chunkNode.getAttributes();
						for(int j = 0, numAtt = chunkAttr.getLength(); j < numAtt; j++) {
							Node nd = chunkAttr.item(j);
							String _name = nd.getNodeName();
				    		String _value = nd.getTextContent();
				    		switch(_name) {
				    		case "t":
				    			t = Long.parseLong(_value);
				    			break;
				    		case "d":
				    			d = Long.parseLong(_value);
				    			break;
				    		case "n":
				    			n = Long.parseLong(_value);
				    			break;
				    		case "r":
				    			r = Long.parseLong(_value);
				    			break;
				    		}
						}
						
						CChunk chunk = new CChunk(t, d, n, r);
						stream.qChunk.add(chunk);
					}
				}
				
				qStream.add(stream);
				//System.out.println(steamNode.getNodeName() + "[" + i + "]");
			}
		}
	}

	static void setChunkTime() {
		for(int i = 0, numStreams = qStream.size(); i < numStreams; i++) {
			CStream stream = qStream.get(i);
			long timescale = stream.timescale;
			double dStarttime = -1.0;
			for(int j = 0, numChunks = stream.qChunk.size(); j < numChunks; j++ ) {
				CChunk chunk = stream.qChunk.get(j);
				if(dStarttime == -1.0 && chunk.t > 0) {
					dStarttime = (double)chunk.t / (double)timescale;
					stream.qCTime.add(dStarttime);
				}
				double dDur = (double)chunk.d / (double)timescale;
				long repeat = chunk.r;
				double dEndtime = dStarttime;
				for(int k = 0; k < repeat; k++) {
					dEndtime = dEndtime + dDur;
					stream.qCTime.add(dEndtime);
				}
				dStarttime = dEndtime;
			}
			//System.out.println(stream.name + " : " + dStarttime);
		}
	}
	
	static void showStreams() {
		for(int i = 0, numStreams = qStream.size(); i < numStreams; i++) {
			showStream(i);
		}
	}
	
	static void showStream(int i) {
		CStream stream = qStream.get(i);
		for(int j = 0, numCTimes = stream.qCTime.size(); j < numCTimes; j++ ) {
			double dCTime = stream.qCTime.get(j);
			NumberFormat f = NumberFormat.getInstance();
			f.setGroupingUsed(false);
			System.out.printf("[%d][%2d] ", i, j);
			System.out.println(f.format(dCTime));
		}
	}
	
	static int getNumStreams() {
		return qStream.size();
	}
	
	static String getStreamType(int i) {
		return qStream.get(i).getType();
	}
	
	static String getStreamName(int i) {
		return qStream.get(i).getName();
	}
	
	static String getCTimeStringInStream(int i) {
		String ret = "";
		CStream stream = qStream.get(i);
		for(int j = 0, numCTimes = stream.qCTime.size(); j < numCTimes; j++ ) {
			double dCTime = stream.qCTime.get(j);
			if(j > 0)
				ret += System.lineSeparator(); // ret += "/r/n";
			NumberFormat f = NumberFormat.getInstance();
			f.setGroupingUsed(false);
			ret += f.format(dCTime);
		}
		return ret;
	}
}

class streamInfo {
	String type;
	String name;
	String time;
}

class SingleThreadEx2 implements Runnable {

	boolean bRun = false;
	String url;
	int refreshTimeMs;
	cTimeGUI pGui;
	
	public SingleThreadEx2(String url, cTimeGUI pGui) {
		this.url = url;
		this.refreshTimeMs = 2000;
		this.pGui = pGui;
		this.bRun = true;
	}
	@SuppressWarnings("null")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(bRun) {
			try {
				manifestParser.clearQueue();
				Thread.sleep(2000);
			} catch (InterruptedException  ie) {
				ie.printStackTrace();
			}
			
			String xml = urlLoader.getPage(url);
			System.out.println(xml);
			
			Element root = xmlParser.getRootNode(xml);
			manifestParser.parsing(root);
			manifestParser.setChunkTime();
			//manifestParser.showStreams();
			
			ArrayList<String> s = new ArrayList<String>();
			ArrayList<streamInfo> si = new ArrayList<streamInfo>();
			int n = manifestParser.getNumStreams();
			for(int i=0; i<n; i++) {
				streamInfo sinfo = new streamInfo(); 
				sinfo.type = manifestParser.getStreamType(i);
				sinfo.name = manifestParser.getStreamName(i);
				sinfo.time = manifestParser.getCTimeStringInStream(i);
				String _s = manifestParser.getCTimeStringInStream(i);
				if(_s.length() > 0) {
					s.add(_s);
					si.add(sinfo);
				}
				//s.add(manifestParser.getCTimeStringInStream(i));
			}
			//String ctimes = manifestParser.getCTimeStringInStream(0);
			//System.out.println(ctimes);
			//pGui.showCTimes(s);
			pGui.showCTimes2(si);
		}
	}

	public void stop() {
		this.bRun = false;
	}
	public void start() {
		this.bRun = true;
	}
	
}

class cTimeGUI extends JFrame implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String url = "";
	ArrayList <JTextArea> qArea = new ArrayList<JTextArea>();
	ArrayList <JScrollPane> qScroll = new ArrayList<JScrollPane>();
	
	JTextField textUrl;
	JButton btStart, btStop;
	
	SingleThreadEx2 r = null;
	boolean needAdditionalTextField = true;
    
	public cTimeGUI(String url) {
		super( "Manifest" );

        setLayout( new FlowLayout() );
        EtchedBorder eborder =  new EtchedBorder();   
        JLabel lbl = new JLabel( "Manifest - Chunk Time Viewer" );
        lbl.setBorder(eborder);

        
		JPanel panelIp = new JPanel();
	    JLabel lbIp = new JLabel("ManifestURL");
	    textUrl = new JTextField(64);
	    textUrl.setText(url);
        panelIp.add(lbIp);
        panelIp.add(textUrl);
        
        JPanel panelStart = new JPanel();
        btStart = new JButton("Start");
        panelStart.add(btStart);
        btStart.addActionListener(this);
        
        JPanel panelStop = new JPanel();
        btStop = new JButton("Stop");
        panelStop.add(btStop);
        btStop.addActionListener(this);
       
        
        add(panelIp);
        add(panelStart);
        add(panelStop);
        
        setSize( 1000, 800 );
        setVisible(true);
    }
	
	public void addTextField(int n) {
		int w = 16;
		int h = 20;
		for(int i = 0; i < n; i++) {
			JTextArea t = new JTextArea(h, w);
			t.setEditable(false);
	        JScrollPane s = new JScrollPane(t);
	        qArea.add(t);
	        qScroll.add(s);
	        add(s);
		}
        setVisible(true);
	}
	
	public void showCTimes(ArrayList<String> s) {
		int num = s.size();
		if(needAdditionalTextField == true) {
			addTextField(num);
		}
		needAdditionalTextField = false;
		for(int i=0; i<num; i++) {
			qArea.get(i).setText(s.get(i));
		}
		setVisible(true);
	}
	
	public void showCTimes2(ArrayList<streamInfo> si) {
		int num = si.size();
		if(needAdditionalTextField == true) {
			addTextField(num);
		}
		needAdditionalTextField = false;
		for(int i=0; i<num; i++) {
			qArea.get(i).setText(si.get(i).type);
			qArea.get(i).append(System.lineSeparator());
			qArea.get(i).append(si.get(i).name);
			qArea.get(i).append(System.lineSeparator());
			qArea.get(i).append(si.get(i).time);
		}
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if((JButton)obj == btStart) {
        	String url = textUrl.getText();
        	//if( r == null) {
        		r = new SingleThreadEx2(url, this);
        		//r.start();
        		Thread t1 = new Thread(r, "A");
        		t1.start();
        	//}
        } else if ((JButton)obj == btStop) {
        	r.stop();
        } else {
            ///lbl.setText("Not supported");
        }
    }
}

public class showManifest {

	public static void main(String[] args) {
		try {
			String url = args[0];
			
			cTimeGUI cTimeGui = new cTimeGUI(url);
	    	cTimeGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			//SingleThreadEx2 r = new SingleThreadEx2(url);
			//r.run();
			
			//System.out.println("* Finished");
		} catch (Exception e) {
            System.out.println(e.getMessage());
        }
	}
}
