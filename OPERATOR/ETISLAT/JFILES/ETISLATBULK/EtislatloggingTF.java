import hungamalogging.hungamalogging;
import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class EtislatloggingTF extends Thread
{
	public static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	public Destination destination,destinationid;
	public MessageProducer producerid;
	public Session session,sessionid;
	public MessageConsumer consumer,consumerid;
	public TextMessage message,messageid;
	javax.jms.Connection connection,connectionid;
    private static String subject = "";
	public static Connection con=null;
	public static Statement stmt,stmtUpdate;
	public String ip=null,dsn=null,username=null,pwd=null,msgqueue=null;
	public static String errPath="";
	String dsmlogs="/home/ivr/javalogs/EtislatTF";
	public static HashMap<String, String> serviceid = new HashMap<String, String>();
	public EtislatloggingTF()
	{
		try
		{
		    ResourceBundle resource = ResourceBundle.getBundle("config/chargingmgr_destination");
			ip=resource.getString("IP");
			dsn=resource.getString("DSN");
			username=resource.getString("USERNAME");
			pwd=resource.getString("PWD");
			subject=resource.getString("MSGQUEUELOG");
		    System.out.println("IP: "+ip+" DATABASE :"+dsn+" USER :"+username+" PWD:"+pwd);
		    System.out.println("starting new Thread");
			start();
		}
		catch(Exception e)
		{
			System.out.println("exceptino very first"+e);
			hungamalogging.log("dbloggin creatingqueue#"+e,"ExceptionLogging_",dsmlogs);
			e.printStackTrace();
			System.exit(0);
		}
	}
	public Connection dbConn()
	{
		while(true)
		{
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn, username, pwd);
				System.out.println("Database Connection established!");
				return con;
			}
			catch(Exception e)
			{
				System.out.println("exception inside doconn"+e);
				hungamalogging.log("dblogger making connection#"+e,"ExceptionLogging_",dsmlogs);
				e.printStackTrace();
			}
		}
	}
	public void run()
	{
		System.out.println("inside run method");
		con=dbConn();
		System.out.println("Calling to updatemethods");
		doLogging();
	}

	public void doLogging()
	{

		String msisdn=null;
		String in_string="";
		String keyword;
		try
		{

			message = new ActiveMQTextMessage();
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		    connection = connectionFactory.createConnection();
		    connection.start();
		    session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
	        destination = session.createQueue(subject);
	        consumer = session.createConsumer(destination);
	        System.out.println("Active message Queue established!");
	    }
		catch(Exception e)
		{
			System.out.println("exception in run"+e);
			hungamalogging.log(e.toString(),"ExceptionLog_",dsmlogs);
			hungamalogging.log("dblogger creating queue#"+e,"ExceptionLogging_",dsmlogs);
			e.printStackTrace();
		}
		while(true)
		{
			try
			{
				message = (TextMessage) consumer.receive();
				if(con==null || con.isClosed())
				{
					System.out.println("connectio  has been null");
					con=dbConn();
					System.out.println("now the connectin is"+con);
				}
				CallableStatement cstmt=null;
				cstmt = con.prepareCall("{call etislat_hsep.deliverylogging(?,?,?,?)}");
				String code  = "";
				String errmsg="",msgid="";
		        if (message instanceof TextMessage)
		        {
					try
					{

						if(cstmt.isClosed() || cstmt==null)
						{
							if(con==null || con.isClosed())
							{
								System.out.println("connectio  has been null");
								con=dbConn();
								System.out.println("now the connectin is"+con);
							}
							cstmt = con.prepareCall("{call etislat_hsep.deliverylogging(?,?,?,?)}");
						}
						TextMessage textMessage = (TextMessage) message;
						in_string = textMessage.getText();
						System.out.println("logging  message '"+ in_string + "'");
						hungamalogging.log(in_string,"dblogging_",dsmlogs);
						String in_msg[] = in_string.split("#");
						msisdn=in_msg[0];
						code  = in_msg[1];
						errmsg=in_msg[2];
                                          msgid=in_msg[3];
						System.out.println("dblogger kewords receive>>"+msisdn+code+errmsg);
						cstmt.setString(1, msisdn);
						cstmt.setString(2, code);
						cstmt.setString(3, errmsg);
						cstmt.setString(4, msgid);
						cstmt.execute();
//Thread.sleep(2000);			
	}
					catch(Exception ex)
					{

						System.out.println("LOgging codeException 5"+ex);
						cstmt.close();
						hungamalogging.log("logging#"+code+"#"+errmsg+"#"+ex,"ExceptionLogging_",dsmlogs);
					}
				}

			}//try ends
			catch(Exception e)
			{
				//cstmst.close();


				e.printStackTrace();
				hungamalogging.log(""+e,"ExceptionLogging_",dsmlogs);
				sendAlert("EtislatloggingTF exited");
				System.exit(0);

			}
		}

	}
	public static void main(String args[])
	{
		try
		{

			new EtislatloggingTF();
			sleep(100);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	public static void sendAlert(String err)
    {
        try
        {
            String line="",response="";
            err=err.replaceAll(" ","%20");
            if(err.length()>=160)
            {
                    err=err.substring(0,150);
            }
            String _marr[]={"8588838347","8586968481","8586967042","8587800614"};
            for(int i=0;i<_marr.length;i++)
            {
                String err_url="http://192.168.100.212:1111/HMXP/push.jsp?smppgateway=HMXP&msisdn="+_marr[i]+"&shortcode=590999&msgtype=plaintext&msg="+err;
                System.out.println("URL called"+err_url);
                 URL chargrequest = new URL(err_url);
                 HttpURLConnection chargingconn = (HttpURLConnection)chargrequest.openConnection();
                 if(chargingconn.getResponseCode()== HttpURLConnection.HTTP_OK)
                 {
                   BufferedReader in = new BufferedReader(new InputStreamReader(chargingconn.getInputStream()));
                   System.out.println("*******************START*************************");
                   while ((line=in.readLine()) != null)
                   {
                           System.out.println(line);
                           response = response + line;
                   }
                   //System.out.println("*******************END*************************");
                   in.close();
                   chargingconn.disconnect();
                 }
            }//for ends
        }
        catch(Exception e)
        {
                System.out.println("Error @ Send_err"+e);
        }
    }
}
