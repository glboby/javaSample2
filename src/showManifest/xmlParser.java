package showManifest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

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
