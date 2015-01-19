import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;


import java.util.*;
import java.util.Date;
import java.io.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQQueueBrowser;
import org.apache.activemq.command.ActiveMQTextMessage;

	public class SMSsender extends Thread{
	public static String url_digi = ActiveMQConnection.DEFAULT_BROKER_URL;
	public static String url_login = ActiveMQConnection.DEFAULT_BROKER_URL;
	public Destination destination_digi,destination_login;
	public Session session_digi,session_login;
	public MessageProducer producer_digi,producer_login;
	public TextMessage message_digi,message_login;
	javax.jms.Connection connection_digi,connection_login;
    private static String subject_digi = "",subject_login = "";
	public static Connection con_select=null,con_update=null;
	public static Statement stmt,stmtUpdate;
	public static CallableStatement cstmt=null;
	public String ip=null,dsn=null,username=null,pwd=null,msgqueue_digi=null,msgqueue_login=null;
	public static File dir=null;
	public static String strdate  = "",mnthdir="";
	public static String strtime  = "",Path="";
	public static Calendar today = null;
	public int _priority=1;


	public SMSsender()
	{
		try
		{

		    ResourceBundle resource = ResourceBundle.getBundle("config/smsmgr_source");
			ip=resource.getString("IP");
			dsn=resource.getString("DSN");
			username=resource.getString("USERNAME");
			pwd=resource.getString("PWD");
			msgqueue_digi=resource.getString("MSGQUEUE_DIGI");
			msgqueue_login=resource.getString("MSGQUEUE_LOGIN");
		    System.out.println("IP: "+ip+" DATABASE :"+dsn+" USER :"+username+" PWD:"+pwd);
		    subject_digi = msgqueue_digi;
			subject_login = msgqueue_login;
		}
		catch(Exception e)
		{
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
			stmtUpdate = con_update.createStatement();
			message_digi = new ActiveMQTextMessage();
			message_login = new ActiveMQTextMessage();
			ConnectionFactory connectionFactory_digi = new ActiveMQConnectionFactory(url_digi);
			ConnectionFactory connectionFactory_login = new ActiveMQConnectionFactory(url_login);
			connection_digi = connectionFactory_digi.createConnection();
			connection_login = connectionFactory_login.createConnection();
		    connection_digi.start();
		    connection_login.start();
		    session_digi = connection_digi.createSession(false,Session.AUTO_ACKNOWLEDGE);
		    session_login = connection_login.createSession(false,Session.AUTO_ACKNOWLEDGE);
		    destination_digi = session_digi.createQueue(subject_digi);
		    destination_login = session_login.createQueue(subject_login);
	        producer_digi = session_digi.createProducer(destination_digi);
	        producer_login = session_login.createProducer(destination_login);
	        System.out.println(msgqueue_digi+" Active message Queue established!");
	        System.out.println(msgqueue_login+" Active message Queue established!");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		while(true)
		{
			try
			{
				String qquery = "select msgid,ani,message,date_time,dnis,type,operator,circle,priority from tbl_new_sms nolock where status=0 and message is not null order by priority limit 100";
				Date gsk=new Date();
				qquery ="select msgid,ani,message,date_time,dnis,type,operator,circle,priority from tbl_new_sms nolock where status=0 and message is not null order by priority limit 100";

				qquery=qquery.trim();
				System.out.println("-------->  "+qquery);
				Thread.sleep(1000);
				ResultSet rs= stmt.executeQuery(qquery);
				if(rs!=null)
				{
					while(rs.next())
					{
						try
						{
							int msgid = rs.getInt("msgid");
							String ani = rs.getString("ani").trim();
							String message=rs.getString("message").trim();
							String date_time = rs.getString("date_time").trim();
							String dnis=rs.getString("dnis").trim();
							String type = rs.getString("type").trim();
							String operator = rs.getString("operator").trim();
							String circle = rs.getString("circle");
							circle=circle.trim();
							String priority = rs.getString("priority").trim();
							String out_string = null;
							out_string = msgid+"#"+ani+"#"+message+"#"+date_time+"#"+dnis+"#"+type+"#"+operator+"#"+circle+"#"+priority;
							System.out.println(out_string);
							if(priority.equalsIgnoreCase("1"))
								_priority=6;
							else if(priority.equalsIgnoreCase("2"))
								_priority=5;
							else if(priority.equalsIgnoreCase("3"))
								_priority=4;
							else if(priority.equalsIgnoreCase("4"))
								_priority=3;
							else
								_priority=2;

							//if("DIGI".equalsIgnoreCase(operator))
							//{
								message_digi.setText(out_string);
								producer_digi.send(message_digi,2,_priority,10000000);//2=PERSISTENT,1=NON PERSISTENT
								//System.out.println("No. of Messages in "+msgqueue_digi+" is "+message_digi_cnt);
							//}
							message_login.setText(operator+"_SENDER#"+out_string);
							producer_login.send(message_login,2,_priority,10000000);//2=PERSISTENT,1=NON PERSISTENT
							stmtUpdate.executeUpdate("delete from tbl_new_sms where msgid='"+msgid+"'");
							sleep(50);

						}
						catch(Exception e)
						{
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
								System.exit(0);
							}
						}
					}//while rs  ends
				}//if ends
					}//try ends
			catch(Exception e)
			{
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
					System.exit(0);
				}
			}
		}

	}

	/*public int GetMessageCount(String operator1)
    {
		int numMsgs = 0;
		try {
			if("TATM".equalsIgnoreCase(operator1))
			{
				QueueBrowser browser = session_digi.createBrowser(session_digi.createQueue(subject_digi));
				ActiveMQQueueBrowser amqb=(ActiveMQQueueBrowser) browser;
				Enumeration e = browser.getEnumeration();
	    	   	while (e.hasMoreElements())
	    	   	{
	    		  //Message message = (Message) e.nextElement();
	    		   numMsgs++;
	    		   e.nextElement();
	    		}
			}
			return numMsgs;
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return numMsgs;
		}
    }*/

	public static void main(String args[])
	{
		SMSsender sBM = new SMSsender();
		sBM.start();
	}

}
