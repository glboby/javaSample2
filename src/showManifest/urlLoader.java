package showManifest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
