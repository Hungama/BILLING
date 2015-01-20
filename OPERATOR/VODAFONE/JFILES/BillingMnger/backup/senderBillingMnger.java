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

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

	public class senderBillingMnger extends Thread{
	public static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	public Destination destination;
	public Session session;
	public MessageProducer producer;
	public TextMessage message;
	javax.jms.Connection connection;
    private static String subject = "";
	public static Connection con_select=null,con_update=null;
	public static Statement stmt,stmtUpdate;
	public static CallableStatement cstmt=null;
	public String ip=null,dsn=null,username=null,pwd=null,msgqueue=null;
	public static FileAppender appender = null, err_App = null;
	public static Logger logger = null,err_log=null;
	public static File dir=null;
	public static String strdate  = "",mnthdir="";
	public static String strtime  = "",Path="";
	public static Calendar today = null;
	public int _priority=1;


	static HashMap<String,String> hashMap= new HashMap<String,String>(16,.5f);

	public senderBillingMnger()
	{
		try
		{

		    ResourceBundle resource = ResourceBundle.getBundle("config/chargingmgr_source");
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
	public void readBillType()
	{
			try
			{
				Connection con_readBillType = dbConn();
				Statement stmt = con_readBillType.createStatement();
				String qquery = "select plan_id,s_id,type_of_plan,fall_back_seqs from tbl_plan_bank;";
				qquery=qquery.trim();
				//System.out.println(qquery);
				Thread.sleep(1000);
				ResultSet rs= stmt.executeQuery(qquery);
				if(rs!=null)
				{
					while(rs.next())
					{
						try
						{
							String plan_id=rs.getString("plan_id");
							String s_id = rs.getString("s_id");
							String type_of_plan = rs.getString("type_of_plan"); /* 0-Fixed Plan,1-Flexiable(StepCharging),2-Negitive*/
							String fall_back_seqs = rs.getString("fall_back_seqs"); /* 0-Default*/
							String key_value = type_of_plan+"#"+fall_back_seqs;
							String key =plan_id+"-"+s_id;
							hashMap.put(key,key_value);
							System.out.println(key+"-->"+key_value);
						}
						catch(Exception e)
						{
							hunLog(e.toString(),'e');
							e.printStackTrace();
						}
					}
			   }
			}catch(Exception e1)
			{
				hunLog(e1.toString(),'e');
				e1.printStackTrace();
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
			message = new ActiveMQTextMessage();
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		    connection = connectionFactory.createConnection();
		    connection.start();
		    session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
	        destination = session.createQueue(subject);
	        producer = session.createProducer(destination);
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
				String qquery = "select billing_ID,msisdn,event_type,amount,service_id,operator,circle,plan_id,MODE from tbl_billing_reqs nolock where status=0  limit 0,50";//and msisdn in('8699509743','9324963670')
				qquery=qquery.trim();
				//System.out.println(qquery);
				Thread.sleep(1000);
				ResultSet rs= stmt.executeQuery(qquery);
				if(rs!=null)
				{
					while(rs.next())
					{
						try
						{
							String billing_ID=rs.getString("billing_ID");
							String msisdn = rs.getString("msisdn");
							String event_type = rs.getString("event_type");
							String amount = rs.getString("amount");
							String service_id = rs.getString("service_id");
							String operator = rs.getString("operator");
							String circle = rs.getString("circle");
							String plan_id = rs.getString("plan_id");
							String in_mode = rs.getString("MODE");
							String out_string = null;
							String key=plan_id+"-"+service_id;
							if(hashMap.get(key)!=null)
							{
								out_string = billing_ID+"#"+msisdn+"#"+event_type+"#"+amount+"#"+service_id+"#"+operator+"#"+circle+"#"+plan_id+"#"+hashMap.get(key);
								System.out.println(out_string);
								if(event_type.equalsIgnoreCase("SUB") && in_mode.equalsIgnoreCase("IVR"))
									_priority=9;
								else if(event_type.equalsIgnoreCase("SUB") && !(in_mode.equalsIgnoreCase("IVR")))
									_priority=7;
								else
								 	_priority=6;

								message.setText(out_string);
								//producer.send(message);
								producer.send(message,2,_priority,10000000);//2=PERSISTENT,1=NON PERSISTENT
								hunLog(out_string,'s');
								stmtUpdate.executeUpdate("update tbl_billing_reqs set status=2 where billing_ID='"+billing_ID+"'");
							}
							else
							{
								System.out.println("Here is else hashMap is null"+ msisdn);
								stmtUpdate.executeUpdate("update tbl_billing_reqs set status=-1 where billing_ID='"+billing_ID+"'");
							}

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
				if(strdate.equals(mystrdate))
				{
					err_log.info("#"+mystrdate+"#"+mystrtime+"#"+log);
				}
				else
				{
					mnthdir=formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
					Path="/home/ivr/javalogs/BillingMnger/Sender/"+mnthdir+"/";
					dir=new File(Path);
					if(!dir.exists())
					  dir.mkdirs();

					err_App = new FileAppender(new PatternLayout(),Path+"Error_"+strdate+".log");
					err_App.setAppend(true);
					err_log = Logger.getLogger("HunLogger");
					err_log.addAppender(err_App);
					err_log.info("#"+mystrdate+"#"+mystrtime+"#"+log);
				 }

			break;

			case's':
				if(strdate.equals(mystrdate))
				{
					logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
				}
				else
				{
					mnthdir=formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
					Path="/home/ivr/javalogs/BillingMnger/Sender/"+mnthdir+"/";
					dir=new File(Path);
					if(!dir.exists())
					  dir.mkdirs();

					appender = new FileAppender(new PatternLayout(),Path+"Sender_"+strdate+".log");
					appender.setAppend(true);
					logger = Logger.getLogger("SendLogger");
					logger.addAppender(appender);
					logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
				 }

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
			Path="/home/ivr/javalogs/BillingMnger/Sender/"+mnthdir+"/";
			dir=new File(Path);
			if(!dir.exists())
				dir.mkdirs();


			appender = new FileAppender(new PatternLayout(),Path+"Sender_"+strdate+".log");
			appender.setAppend(true);
			logger = Logger.getLogger("SendLogger");
			logger.addAppender(appender);
			//========================Error Logger================================
			err_App = new FileAppender(new PatternLayout(),Path+"Error_"+strdate+".log");
			err_App.setAppend(true);
			err_log = Logger.getLogger("HunLogger");
			err_log.addAppender(err_App);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		senderBillingMnger sBM = new senderBillingMnger();
		sBM.readBillType();
		sBM.start();
	}

}
