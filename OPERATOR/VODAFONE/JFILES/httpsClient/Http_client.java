import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;


public class Http_client
{
	FileInputStream fis = null;
	BufferedReader br = null;
  	DataInputStream dis = null;
  	//java -Djavax.net.ssl.truststore=
//keytool -import -file selfsigned.pem -alias server -keystore server.jks
String cuki = null;
	

	/*+ "<report>"
		+ REPORT
+ "</report>\n"*/

public void callURL()
{

	BufferedReader rd = null;
	String line = null;
	String urlStr = null;

	
	
	urlStr = "https://10.43.248.137/submanager/Update?msisdn=917838102430&service_id=HNG_ENTRMNTPORTAL&class_vod=ENTRMNTPORTAL&txnid=3542312061&charging_mode=10DAYS&CIRCLE_ID=13";
	System.out.println("url_hit : "+urlStr);
	try
	{
			//Create URL object for callinit
		URL url = new URL(urlStr);
		System.setProperty("mail.mime.multipart.ignoreexistingboundaryparameter", "true");
		HostnameVerifier hv = new HostnameVerifier()
		{
			public boolean verify(String urlHostName, SSLSession session)
			{
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
		
		//Create HTTPS Connection
		HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
		
		//Handle SSL
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		InputStream trustStore = new FileInputStream("/home/ivr/jfiles/httpsClient/cacerts");
		char[] password = "1234567".toCharArray();
		keyStore.load(trustStore,password);
		trustStore.close();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(keyStore);
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(null, tmf.getTrustManagers(), null);
		SSLSocketFactory sslFactory = ctx.getSocketFactory();
		
		//Set Connection Parameters
		conn.setSSLSocketFactory(sslFactory);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Content-Type", "text/html");
		
		//Handle Cookies
			String cookieHeader = conn.getHeaderField("set-cookie");
		if(cookieHeader != null)
		{
			int index = cookieHeader.indexOf(";");
			if(index >= 0)
				cuki = cookieHeader;
		}
	
		//Read URL Response
		rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = rd.readLine()) != null)
		{
			System.out.println(line);
		}
	}
	catch (Exception e) 
	{
		System.out.println("Unable to initialize URL : "+e);
	}
	}




	public static void main (String args[])
	{
		try
		{
			Http_client c=new Http_client();
		   // c.obDialer("101","CCXML-ENABLER@192.168.4.17", "12345@192.168.4.126", "abc123.wav","c:/");
			 c.callURL();
	
		}catch(Exception e)
		{
			System.out.println(e);
		}
	}
}



