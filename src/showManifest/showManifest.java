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


public class showManifest {

	public static void main(String[] args) {
		try {
			String url;
			if(args.length == 0)
				url = "http://localhost/manifest";
			else
				url = args[0];

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
