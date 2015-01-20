import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;
import java.util.*;
import java.sql.*;

import java.util.StringTokenizer;
import java.text.SimpleDateFormat;
import org.apache.log4j.*;
import org.apache.log4j.BasicConfigurator;


public class Callinit
{
	private static String application_IP="192.168.4.80";
	private static String ocmp_IP="192.168.100.217";
	private static String ocmp_Port="5443";

	static Connection dbConnection=null;
	private static final long serialVersionUID = 1L;
	private String tdServiceID = null;
	FileInputStream fis = null;
	BufferedReader br = null;
  	DataInputStream dis = null;

	private static final String CIR_NAME = "cireq.xml";
	private static final String KEY_SID = "@SID@";
	private static final String KEY_CONNECTION = "@CONNECTION@";
	private static final String KEY_LOCALURI = "@LOCALURI@";
	private static final String REPORT = "http://"+application_IP+"/obDialer/obd";
	private static final String CONTENT = "http://"+application_IP+"/RobiRadio/OBD/obdMain.jsp";
	String ani 	= null;
	String bni 	= null;
	String cuki = null;
	String CONTENT_NEW = null;
	private static final String CIR_TEMPLATE = "<ci-request xmlns=\"http://www.hp.com/ocmp/2004/07/ci-request\" version=\"1.0\">\n"
		+ "<service-id>"
		+ KEY_SID
		+ "</service-id>\n"
		+ "<connection>"
		+ KEY_CONNECTION
		+ "</connection>\n"
		+ "<fields>\n"
		+ "<field name=\"localuri\" value=\""
		+ KEY_LOCALURI
		+ "\"/>\n" + "</fields>\n" + "</ci-request>";

		/*+ "<report>"
				+ REPORT
		+ "</report>\n"*/

	public void obDialer(String transid,String ani, String bni,String song,String recording)
	{

		BufferedReader rd = null;
		String line = null;
		String urlStr = null;

		ani = "sip:"+ani;
		bni = "sip:"+bni;
		System.out.println("TRANSID passed : "+transid);
		System.out.println("ANI passed : "+ani);
		System.out.println("BNI passed : "+bni);
		System.out.println("MSG passed : "+recording);
		System.out.println("SONG passed : "+song);
		this.tdServiceID = "test";
		CONTENT_NEW=CONTENT+"?ani="+ani+"&amp;bni="+bni+"&amp;recording="+recording+"&amp;song="+song+"&amp;transid="+transid;
		System.out.println("CONTENT : "+CONTENT_NEW);
		//urlStr = "https://" + ocmp_IP+ ":" + ocmp_Port + "/router/callinit.do";
		urlStr = "https://10.2.73.156/airtel/airtel.php?msisdn=9910040744&mode=cc&reqtype=2&planid=29&subchannel=cc&rcode=100,101,102&serviceid=1511";

		try
		{
			//Create URL object for callinit
			URL url = new URL(urlStr);
			System.setProperty("mail.mime.multipart.ignoreexistingboundaryparameter", "true");
			//Verify Hostname
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
			InputStream trustStore = new FileInputStream("./NEWkeystore.keystore");
			char[] password = "hunivr999".toCharArray();
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

			//Create URL for Authentication
			/*URL accURL = new URL(
					"https://"+ocmp_IP+":"+ocmp_Port+"/router/j_security_check?j_username=ocadmin&j_password=ocadmin&username=ocadmin&password=ocadmin&mitLogin=true&submit=Login");
			HttpsURLConnection connAccess = (HttpsURLConnection) accURL.openConnection();

			//Set Connection Parameters
			connAccess.setSSLSocketFactory(sslFactory);
			connAccess.setDoInput(true);
			connAccess.setDoOutput(true);
			connAccess.setRequestProperty("Content-Type", "text/html");
			connAccess.setRequestProperty("Cookie", cuki);
			connAccess.connect();

			//Read Cookies
			cuki = connAccess.getRequestProperty("Cookie");

			//Read URL Response
			rd = new BufferedReader(new InputStreamReader(connAccess.getInputStream()));
			while ((line = rd.readLine()) != null)
			{
				//System.out.println(line);
			}

			//Create URL for posting callinit data
			HttpsURLConnection conn1 = (HttpsURLConnection)url.openConnection();

			//Process CIR_TEMPLATE
			StringBuffer cir = new StringBuffer(CIR_TEMPLATE);
			replace(cir, KEY_SID, tdServiceID);
			replace(cir, KEY_CONNECTION, bni);
			replace(cir, KEY_LOCALURI, ani);
			//replace(cir, CONTENT, CONTENT_NEW);
			System.out.println(cir);



			//Set Connection Parameters
			conn1.setSSLSocketFactory(sslFactory);
			conn1.setDoOutput(true);
			conn1.setDoInput(true);
			conn1.setRequestMethod("POST");
			conn1.setRequestProperty("Connection", "Keep-Alive");
			conn1.setRequestProperty("Accept-Charset", "UTF-8");
			conn1.setRequestProperty("Content-Length", ""+cir.length()+"");
			conn1.setUseCaches(false);
			conn1.setRequestProperty("Content-Type", "multipart/form-data; boundary=--ak");
			conn1.setRequestProperty("Cookie", cuki);

			DataOutputStream output = new DataOutputStream (conn1.getOutputStream());
			output.writeBytes("----ak\r\n");
			output.writeBytes("Content-Disposition: form-data; name=\"userfile\";"+ " filename="+CIR_NAME+"\r\n");
			output.writeBytes("\r\n");
			output.writeBytes(cir.toString());
			output.writeBytes("\r\n");
			output.writeBytes("----ak--\r\n");
			output.flush();
			output.close();

			//Read URL response
			System.out.println("Resp Code:" + conn1.getResponseCode());
			System.out.println("Resp Message:"+ conn1.getResponseMessage());
			rd = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
			FileWriter f = new FileWriter("c:/Temp/obd_init.txt",true);
			while ((line = rd.readLine()) != null)
			{
				//System.out.println(line);
				f.write(line+"\n");
			}
			f.close();*/
		}
		catch (Exception e) {
			System.out.println("Unable to initialize URL : "+e);
		}
	}

	private void replace(StringBuffer buf, String key, String value)
	{
		replace(buf, key, value, true);
	}

	private void replace(StringBuffer buf, String key, String value, boolean trim)
	{
		int pos = buf.indexOf(key);
		buf.replace(pos, pos + key.length(), trim ? value.trim() : value);
	}


	static ReadData readConfig;
	public static void main (String args[])
	{
		try
		{
			Callinit c=new Callinit();

			/* Read Config File and Init Logger */
			readConfig = new ReadData();
			readConfig.getLogger().info("Read Config File... Done");

			/* Create DataBase Connection */
			readConfig.getLogger().info("Connecting with DataBase...");

			boolean ret;
      while(true)
			{
				ret =readConfig.initLogger();
				if(ret == false)
				{
					System.out.println("Failed to init Logger with date ");
					System.exit(1);
				}
		    c.obDialer("101","CCXML-ENABLER@192.168.4.17", "12345@192.168.4.126", "abc123.wav","c:/");
		    //c.obDialer("101","30485711", "08587900178", "abc123.wav","c:/");
		    //c.obDialer("101","30485711", "8587900178", "abc123.wav","c:/");
		    //c.obDialer("101","30485711", "8587900178", "abc123.wav","c:/");
		    //c.obDialer("101","30485711", "8586968485", "abc123.wav","c:/");
		    //c.obDialer("101","30485711", "8586968485", "abc123.wav","c:/");

				Thread.sleep(5*60000);
			}

		}catch(Exception e)
		{
			System.out.println(e);
		}
	}

	public void fetchOBD()
	{
		String line="321";
		String bni="8080";//+ocmp_IP;
		String transid=null,ani=null,recording=null,song=null;
		try
		{
			Statement stmt=dbConnection.createStatement();
			ResultSet results = stmt.executeQuery(
					"select transid,ani,bni,recording,song from dbo.tbl_ded_schedule where status=1");

			readConfig.getLogger().info("Fetching OBD values");
			while(results.next())
			{
				transid = results.getString("transid");
				bni = results.getString("ani");//dedicated by
				ani = results.getString("bni");//dedicated to
				recording = results.getString("recording");
				song = results.getString("song");

				System.out.println("transid ="+transid);
				System.out.println("ani="+ani);
				System.out.println("bni="+bni);
				System.out.println("recording="+recording);
				System.out.println("song="+song);

				obDialer(transid,bni,ani,song,recording);
			}
			stmt.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}


	/**************************************************************************************************/
	/* Class      : ReadData                                                                          */
	/* Functions  : 1.Reads Configuration file,can get all values using appropriate Getter()          */
	/*              2.Initializes Logger                                                              */
	/**************************************************************************************************/

	class ReadData
	{
		private static String dataBaseIP;
		private static String userName;
		private static String passWord;
		private static String dataBase;
		private static String logPath;
		private static Connection chargingDBConnection;
		private static Logger logger = Logger.getLogger(Callinit.class);
			String pattern;
	    PatternLayout layout;
			String log_date;
			SimpleDateFormat sdf;
			FileAppender appender;



		/************************************************************************************************
		 *  Constructor
		 *  Arguments     :   ---
		 *  Purpose       :		Constructor with no arguments js opens configration file
		 and reads data from it
		 *  Return type   :   boolean
		 *************************************************************************************************/

		ReadData()
		{
			Properties prpts=new Properties();
			try{
				prpts.load(new FileInputStream("DB_DETAILS.CFG"));

				dataBaseIP  = prpts.getProperty("OBD_SERVER_IP");
				userName    = prpts.getProperty("OBD_SERVER_USERNAME");
				passWord    = prpts.getProperty("OBD_SERVER_PASSWORD");
				dataBase    = prpts.getProperty("OBD_SERVER_DATABASE");

				logPath			= prpts.getProperty("LOG_PATH");
				System.out.println("creating file");
				boolean ret =initLogger();
				if(ret == false)
				{
					System.out.println(className()+"Failed to init Logger with date ");
					System.exit(1);
				}

				getLogger().info(className()+"\t\tData From Configuration");
				getLogger().info(className()+"IP\t: ["+dataBaseIP+"]");
				getLogger().info(className()+"USERNAME: ["+userName+"]");
				getLogger().info(className()+"PASSWORD: ["+passWord+"]");
				getLogger().info(className()+"DATABASE: ["+dataBase+"]");
				getLogger().info(className()+"LOG_PATH: ["+logPath+"]");
			}
			catch(Exception e)
			{
				getLogger().error(className()+e);
			}
		}

		/************************************************************************************************
		 *  Function Name :   initLogger
		 *  Arguments     :   ---
		 *  Purpose       :   Initializes Logger and sets path and name of Log file
		 *  Return type   :   boolean
		 *************************************************************************************************/

		boolean initLogger()
		{
			try
			{
		    System.out.println("Inside createFile()");
				sdf = new SimpleDateFormat("dd_MM_yy");
				String new_date=sdf.format(new java.util.Date());
				if(log_date==null)
				{
					System.out.println("Create File, date == null");
					pattern = "[ %-5p] [%d{ISO8601}] [%8t] %x - %m%n";
					layout = new PatternLayout(pattern);
					log_date=new_date;
					String fileName=this.logPath+"Logs_"+new_date+".txt";
					appender = new FileAppender(layout,fileName,true);
					logger.addAppender(appender);
					System.out.println("Path"+this.logPath+fileName);
					getLogger().info("\n/************************************* Java OBD Interface Module ******"+
							"********************************");
				}
				else
				{
		    	System.out.println("Inside createFile() "+new_date);
					if(log_date.equals(new_date))
					{
						System.out.println("\nSame date new:"+new_date+" old:"+log_date);
					}
					else
					{
						logger.removeAppender(appender);
						pattern = "[ %-5p] [%d{ISO8601}] [%8t] %x - %m%n";
						layout = new PatternLayout(pattern);
						sdf = new SimpleDateFormat("dd_MM_yy");
						log_date=new_date;
						String fileName=this.logPath+"Logs_"+new_date+".txt";
						appender = new FileAppender(layout,fileName,true);
						logger.addAppender(appender);
						System.out.println("Path"+this.logPath+fileName);
						getLogger().info("\n/************************************* Java OBD Interface Module ******"+
								"********************************");
					}
				}

			}
			catch(Exception e)
			{
				getLogger().error(className()+"[initLogger]    : FAILED");
				return false;
			}
			return true;
		}

		boolean createFile()
		{
			try{
				String new_date=sdf.format(new java.util.Date());
				System.out.println("Inside createFile() "+new_date);

				if(log_date.equals(new_date))
				{
						getLogger().info("\nSame date new:"+new_date+" old:"+log_date);
				}
				else
				{
					log_date=new_date;
					System.out.println("Inside createFile() "+new_date);
					String fileName=this.logPath+"Logs_"+new_date+".txt";
					appender = new FileAppender(layout,fileName,true);
					logger.addAppender(appender);
					System.out.println("Path"+this.logPath+fileName);
					getLogger().info("\n/************************************* Java OBD Interface Module ******"+
							"********************************");
				}
			}
			catch(Exception e)
			{
				getLogger().error(className()+"[createFile]    : FAILED");
				return false;
			}
			return true;

		}
		/* Getter functions for getting values
		read from Configuration file */
			Logger getLogger()
			{
				return logger;
			}

		String getDataBaseIP()
		{
			return dataBaseIP;
		}
		String getUserName()
		{
			return userName;
		}

		String getPassWord()
		{
			return passWord;
		}
		String getDataBase()
		{
			return dataBase;
		}
		String getLogPath()
		{
			return logPath;
		}


		/************************************************************************************************
		 *  Function Name :   className
		 *  Arguments     :   ---
		 *  Purpose       :   It returns class name
		 *  Return type   :   boolean
		 *************************************************************************************************/

		String className()
		{
			return "[ReadData] ";
		}
	}


