package showManifest;

import org.w3c.dom.Element;
import structs.streamInfo;

import java.util.ArrayList;

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
			if(xml.length() == 0) {
				pGui.showMessage("error in URL");
			}
			else {
				//System.out.println(xml);

				Element root = xmlParser.getRootNode(xml);
				manifestParser.parsing(root);
				manifestParser.setChunkTime();
				//manifestParser.showStreams();

				ArrayList<String> s = new ArrayList<String>();
				ArrayList<streamInfo> si = new ArrayList<streamInfo>();
				int n = manifestParser.getNumStreams();
				for (int i = 0; i < n; i++) {
					streamInfo sinfo = new streamInfo();
					sinfo.type = manifestParser.getStreamType(i);
					sinfo.name = manifestParser.getStreamName(i);
					sinfo.time = manifestParser.getCTimeStringInStream(i);
					String _s = manifestParser.getCTimeStringInStream(i);
					if (_s.length() > 0) {
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
	}

	public void stop() {
		this.bRun = false;
	}
	public void start() {
		this.bRun = true;
	}

}
