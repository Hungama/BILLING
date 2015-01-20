
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.*;
import java.util.*;
import java.io.*;
import java.net.URLEncoder;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

	public class CIMDsender extends Thread{
	public static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	public static Destination destination,destinationQ,destination_vh1,destination_redfm;
	public static Session session,sessionQ,session_redfm,session_vh1;
	public static MessageProducer producer,producerQ,producer_redfm,producer_vh1;
	public static TextMessage message,messageQ,message_redfm,message_vh1;
	static javax.jms.Connection connection,connectionQ,connection_redfm,connection_vh1;
    private static String subject = "";
	public static Connection con_select=null,con_update=null;
	public static Statement stmt,stmtUpdate,stmtcnt;
	public static CallableStatement cstmt=null;
	public String ip=null,dsn=null,username=null,pwd=null,msgqueue=null;
	public static RollingFileAppender appender = null, err_App = null;
	public static Logger logger = null,err_log=null;
	public static File dir=null;
	public static String strdate  = "",mnthdir="";
	public static String strtime  = "",Path="";
	public static Calendar today = null;
	public int _priority=1;
	public static int waitFlag=1;



	static HashMap<String,String> hashMap= new HashMap<String,String>(16,.5f);

	public CIMDsender()
	{
		try
		{

		    ResourceBundle resource = ResourceBundle.getBundle("config/CIMD_DB");
			ip=resource.getString("IP");
			dsn=resource.getString("DSN");
			username=resource.getString("USERNAME");
			pwd=resource.getString("PWD");
			msgqueue=resource.getString("MSGQUEUE");
		    System.out.println("IP: "+ip+" DATABASE :"+dsn+" USER :"+username+" PWD:"+pwd);
		    subject = msgqueue;


		}
		catch(Exception e)
		{
			hunLog(e.toString(),'e');
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
				Connection con = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn, username, pwd);
				System.out.println("Database Connection established!");
				return con;
			}catch(Exception e)
			{
				hunLog(e.toString(),'e');
				e.printStackTrace();
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public void run()
	{
		try
		{
			con_select = dbConn();
			con_update = dbConn();
			stmt = con_select.createStatement();
			stmtcnt = con_select.createStatement();
			stmtUpdate = con_update.createStatement();
			message = new ActiveMQTextMessage();
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			//==========================================================\\
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(subject);
			producer = session.createProducer(destination);

			messageQ = new ActiveMQTextMessage();
			connectionQ = connectionFactory.createConnection();
			connectionQ.start();
			sessionQ = connectionQ.createSession(false,Session.AUTO_ACKNOWLEDGE);
			destinationQ = sessionQ.createQueue("HUNLOG");
			producerQ = sessionQ.createProducer(destinationQ);


	        System.out.println(msgqueue+" Active message Queue established!");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			hunLog(e.toString(),'e');
		}
		while(true)
		{
			try
			{


				String source="54646",dest="1234567890",msg="",rowid="",iStatus="0",out_string="",iType,circle,date_time;
				int flag=-1;
				int cnt=0;
				String qquery = "select msgid,ani,message,date_time,status,dnis,flag,type,circle from tbl_sms nolock where status=0 and flag<>1  and message is not null limit  0,500";//and ani in ('8586968482','8527000779','8587800614','7838884633')
					//System.out.println(qquery);

					ResultSet rs1=stmtcnt.executeQuery("select count(*)cnt from tbl_sms nolock where status=0 and flag=1 and message is not null");
					while(rs1.next())
					  {
						   cnt= rs1.getInt("cnt");


					  }
				if(cnt>0)
				{
					Thread.sleep(1000);
					ResultSet rs= stmt.executeQuery(qquery);
					if(rs!=null)
					{
						while(rs.next())
						{
							try
							{
								System.out.println("CIMD sending mesage ");
								rowid	= rs.getString("msgid");
								dest	= rs.getString("ani");
								source	= rs.getString("dnis");
								msg		= rs.getString("message");
								msg=msg.replaceAll("_","%5f");
							/*	try
								{
									msg =URLEncoder.encode(msg,"UTF-8");
								}					
								catch(Exception e)
								{
									System.out.println("Error in encoding msg "+e);
								}*/
								iStatus	= rs.getString("Status");
								flag	= rs.getInt("flag");
								iType	= rs.getString("type");
								circle	=rs.getString("circle");
								date_time	=rs.getString("date_time");
								out_string=rowid+"#"+dest+"#"+source+"#"+msg+"#"+iType+"#";
								String l="SMSSender#"+rowid+"#"+dest+"#"+msg+"#"+date_time+"#"+source+"#"+iType+"#VODM#"+circle+"#"+iStatus+"#NODND";
								switch(flag)
								{
									case 1:
											message.setText(out_string);
											producer.send(message);
											waitFlag=1;
											//Entertainment Portal 54646
											break;
									case 2:
											message_redfm.setText(out_string);
											producer_redfm.send(message_redfm);
											//REDFM
											break;
									case 3:
											message_vh1.setText(out_string);
											producer_vh1.send(message_vh1);
											//VH1
											break;
									default:
											message.setText(out_string);
											producer.send(message);
											//Entertainment Portal 54646
											break;

								}

								hunLog(l,'s');
								stmtUpdate.executeUpdate("delete from tbl_sms where msgid = '"+rowid+"' and ani='"+dest+"'");
								stmtUpdate.executeUpdate("delete from tbl_sms where message is null ");
								sleep(250);


							}
							catch(Exception e)
							{
								hunLog(e.toString(),'e');
								e.printStackTrace();
								try
								{
									if(e.toString().startsWith("com.mysql.jdbc.CommunicationsException:"))
									{
										System.out.println("DB Connectivity Failure!!! Retries to connect DB");
										Thread.sleep(10000);
										dbConn();
										stmt = con_select.createStatement();
										stmtUpdate = con_update.createStatement();
									}

								}catch(Exception e1)
								{
									e1.printStackTrace();
									hunLog(e1.toString(),'e');
									System.exit(0);
								}
							}
						}//while rs  ends
					}//if ends
				}//if cnt ends
				else
				{

					sleep(1000);
					waitFlag=0;

				}
			}//try ends
			catch(Exception e)
			{
				hunLog(e.toString(),'e');
				try
				{
					e.printStackTrace();
					if(e.toString().startsWith("com.mysql.jdbc.CommunicationsException:"))
					{
						System.out.println("DB Connectivity Failure!!! Retries to connect DB");
						Thread.sleep(10000);
						dbConn();
						stmt = con_select.createStatement();
						stmtUpdate = con_update.createStatement();
					}

				}catch(Exception e1)
				{
					e1.printStackTrace();
					hunLog(e1.toString(),'e');
					System.exit(0);
				}
			}
		}

	}
//========================================================
//  Appended on 06/06/11
//========================================================
public void hunLog(String log,char file)
	{

		try
		{
			Calendar mytoday = Calendar.getInstance();
			String mystrdate = formatN(""+mytoday.get(Calendar.YEAR),4) + formatN(""+(mytoday.get(Calendar.MONTH)+1),2) + formatN(""+mytoday.get(Calendar.DATE),2);
			String mystrtime = formatN(""+mytoday.get(Calendar.HOUR_OF_DAY),2)+formatN(""+mytoday.get(Calendar.MINUTE),2)+formatN(""+mytoday.get(Calendar.SECOND),2);
			System.out.println("Writing  Sending Logs -"+ log);
			switch(file)
			{
			case'e':

				 messageQ.setText("Error#"+log);
				producerQ.send(messageQ);

			break;

			case's':

				 messageQ.setText("SMSSender#"+mystrtime+"#"+log);
				producerQ.send(messageQ);

			break;
			}//swtich ends
		}
		catch(Exception e)
		{
			System.out.println("Error @hunlog"+e);
		}


	}
//========================================================
private static String formatN(String str, int x)
		{
			int len;
			String ret_str="";
			len = str.length();
			if (len >= x)
				ret_str = str;
			else
			{
				for(int i=0; i<x-len; i++)
					ret_str = ret_str + "0";
				ret_str = ret_str + str;
			}
			return ret_str;
		}
//========================================================
//========================================================


	public static void main(String args[])
	{
		try
		{
			today = Calendar.getInstance();
			strdate = formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
			strtime = formatN(""+today.get(Calendar.HOUR_OF_DAY),2)+formatN(""+today.get(Calendar.MINUTE),2)+formatN(""+today.get(Calendar.SECOND),2);
			mnthdir=formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
			Path="../SMS/"+mnthdir+"/";
			dir=new File(Path);
			if(!dir.exists())
				dir.mkdirs();


			appender = new RollingFileAppender(new PatternLayout(),Path+"SMS_"+strdate+".log");
			appender.setAppend(true);
			logger = Logger.getLogger("SendLogger");
			logger.addAppender(appender);
			//========================Error Logger================================
			err_App = new RollingFileAppender(new PatternLayout(),Path+"Error_"+strdate+".log");
			err_App.setAppend(true);
			err_log = Logger.getLogger("HunLogger");
			err_log.addAppender(err_App);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		CIMDsender sBM = new CIMDsender();

		//CIMDsender.SendEnquire se=sBM.new SendEnquire();
	//	new SendEnquire().start();
		sBM.start();
	}
//========================================================================
	 /*static class SendEnquire extends Thread
	{

		int count=0,rnd=0;
		public void run()
		{
			try
			{
				while(true)
				{
						Thread.sleep(50000);
			    		rnd=(int)(Math.random()*1000)+1;
			    		Date dt=new Date();
			    		if(waitFlag==0)
			    		{
							message.setText(rnd+"#8586968482#54646#ENQ#ENQ#");
			    		    producer.send(message);
			    		    System.out.println("Sending Enquire Packet"+ dt.getHours()+":"+dt.getMinutes()+":"+dt.getSeconds());
						}

				}

			}
			catch (Exception e)
			{
				System.out.println("Exception in static  class "+e );
			}

		}

	}*/
	//========================================================================
}

