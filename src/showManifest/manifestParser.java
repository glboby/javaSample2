package showManifest;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import structs.CChunk;
import structs.CStream;

import java.text.NumberFormat;
import java.util.ArrayList;

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
			long timescale = stream.GetTimescale();
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
