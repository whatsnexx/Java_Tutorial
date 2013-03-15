package script;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.catalina.util.Base64;



public class Main {

	static {
		// for localhost testing only	
		javax.net.ssl.HttpsURLConnection
				.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {

					public boolean verify(String hostname,
							javax.net.ssl.SSLSession sslSession) {
						if (hostname.equals("localhost")) {
							return true;
						}
						return false;
					}
				});
	}
	
	public static void main(String[] args){

		String response = "";
		
		final TrustManager[] trustAllCerts = new X509TrustManager[] { new X509TrustManager() {
	        @Override
	        public void checkClientTrusted( final X509Certificate[] chain, final String authType ) {
	        }
	        @Override
	        public void checkServerTrusted( final X509Certificate[] chain, final String authType ) {
	        }
	        @Override
	        public X509Certificate[] getAcceptedIssuers() {
	            return null;
	        }
	    } };
		
		try {
		SSLContext sslContext = SSLContext.getInstance("SSL");

		sslContext.init(null, trustAllCerts, new SecureRandom());
		
		URL url = new URL("https://localhost/TicketbusService/Rest/ticketbusservice.svc/SendEvent/{accountId}/{subjectTypeId}/{termName}/{executionEnvironment}/{subjectCode}");
		
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		
		conn.setSSLSocketFactory(sslContext.getSocketFactory());
		
		String auth = "{username}:{password}";
		byte[] authBytes = auth.getBytes();
		String authEncoded = Base64.encode(authBytes);
		conn.setRequestProperty("Authorization", authEncoded);
		
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "text/xml");
		
		String input = "<AttributeList xmlns=\"http://schemas.whatsnexx.com/v1/tbx/\"><Attribute><Name>Yo</Name><Value>Dude</Value></Attribute></AttributeList>";

		OutputStream os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();
		
		//Now check the result
		Scanner scanner;
				
		if (conn.getResponseCode() != 200) {
			response += conn.getResponseCode()
					+ " error returned from server";
			if (conn.getErrorStream() != null) {
				scanner = new Scanner(conn.getErrorStream());
				response += "Error From Server \n\n";
				scanner.useDelimiter("\\Z");
				response += scanner.next();
				scanner.close();
			}
		} else {
			scanner = new Scanner(conn.getInputStream());
			response += "Response From Server \n\n";
			scanner.useDelimiter("\\Z");
			response += scanner.next();
			scanner.close();
		}
		conn.disconnect();
		} catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
			e.printStackTrace();
		} 
		System.out.println(response);
	}
}
