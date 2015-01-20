import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;	
import org.apache.log4j.*;
import java.util.*;
import java.io.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

	public class VODMdblogger extends Thread{
	public static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	public Destination destination,destinationQ;
	public Session session,sessionQ;
	public MessageConsumer consumer;
	public MessageProducer producerQ;
	public TextMessage message,messageQ;
	javax.jms.Connection connection,connectionQ;
    private static String subject = "";
	public static Connection con=null;
	public static Statement stmt,stmtUpdate;
	public static CallableStatement cstmt=null;
	public String ip=null,dsn=null,username=null,pwd=null,msgqueue=null;

	/***************** logger Variable  **********/
	public static Calendar today = null;
	public static String strdate  = "",mnthdir="";
	public static String strtime  = "";
	public static String errPath="";
	public static FileAppender err_App = null,log_app=null;
	public static Logger logger = null,logger1=null;
	public static File dir=null;

	public VODMdblogger()
	{
		try
		{

		    ResourceBundle resource = ResourceBundle.getBundle("config/chargingmgr_destination");
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
				con = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn, username, pwd);
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
					hunLog(e1.toString(),'e');
					e1.printStackTrace();
				}
			}
		}
	}
	public void run()
	{
		String operator=null;
		String status=null;
		String billing_ID=null;
		String msisdn=null;
		String event_type=null,hun_type="SUB";
		String amount=null;
		String service_id=null;
		String avl_amt=null;
		String chr_amt=null;
		String trans_id=null;
		String pre_post=null;
		Connection con1=null;
		String plan_id = null;
		String response = null;
		String mode=null;
		try
		{
			con1=dbConn();
			message = new ActiveMQTextMessage();
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		    connection = connectionFactory.createConnection();
		    connection.start();
		    session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
	        destination = session.createQueue(subject);
	        consumer = session.createConsumer(destination);
	        System.out.println("Active message Queue established!");
	      //==============LOGGING=============================
			messageQ = new ActiveMQTextMessage();
			connectionQ = connectionFactory.createConnection();
			connectionQ.start();
			sessionQ = connectionQ.createSession(false,Session.AUTO_ACKNOWLEDGE);
			destinationQ = sessionQ.createQueue("HUNLOG");
			producerQ = sessionQ.createProducer(destinationQ);

		}
		catch(Exception e)
		{
			hunLog(e.toString(),'e');
			e.printStackTrace();
		}
		while(true)
				{
					try
					{
						message = (TextMessage) consumer.receive();
				        if (message instanceof TextMessage)
				        {
			                TextMessage textMessage = (TextMessage) message;
			                String in_string = textMessage.getText();
			                System.out.println(" DBlogger message '"+ in_string + "'");
			                hunLog(in_string,'l');
			                String in_msg[] = in_string.split("#");
			                operator=in_msg[0];
			               // System.out.println("calling Procedure  for Operator -"+operator);
						

			                if("VODM".equalsIgnoreCase(operator))
			                {
									status     = in_msg[1];
									billing_ID = in_msg[2];
									msisdn     = in_msg[3];
									event_type = in_msg[4];
									avl_amt    = in_msg[5];
									chr_amt    = in_msg[6];
									amount     = in_msg[7];
									pre_post   = in_msg[8];
									service_id = in_msg[9];
									plan_id    = in_msg[10];
									trans_id   = in_msg[11];
									response   = in_msg[12];
									mode       = in_msg[13];

									System.out.println(msisdn+"#"+operator+"#"+trans_id+"#"+mode+"#"+event_type+"#"+response);

									hun_type=event_type;

									if("TOPUP".equalsIgnoreCase(event_type))
									    hun_type="TOPUP";
									else
									if("GRACE".equalsIgnoreCase(event_type))
									     hun_type="RESUB";
									else if("Parking".equalsIgnoreCase(event_type))
									     hun_type="SUB";
									else
									     hun_type=event_type;
									if("TOPUP".equalsIgnoreCase(event_type))
									{

										if("ok".equalsIgnoreCase(status))
										{

												cstmt = con1.prepareCall("{call BILLING_"+hun_type.toUpperCase()+"_"+status.toUpperCase()+"(?,?,?,?,?,?,?,?,?,?,?)}");
												cstmt.setString(1, mode);
												cstmt.setString(2, msisdn);
												cstmt.setString(3, event_type);
												cstmt.setString(4, avl_amt);
												cstmt.setString(5, chr_amt);
												cstmt.setString(6, amount);
												cstmt.setString(7, pre_post);
												cstmt.setString(8, service_id);
												cstmt.setString(9, plan_id);
												cstmt.setString(10,trans_id);
												cstmt.setString(11,billing_ID);
												cstmt.execute();
												cstmt.close();

										}
										else
										{
											cstmt = con1.prepareCall("{call BILLING_"+hun_type.toUpperCase()+"_"+status.toUpperCase()+"(?,?,?,?,?,?,?,?,?,?,?)}");
											cstmt.setString(1, mode);
											cstmt.setString(2, msisdn);
											cstmt.setString(3, event_type);
											cstmt.setString(4, avl_amt);
											cstmt.setString(5, response);
											cstmt.setString(6, amount);
											cstmt.setString(7, chr_amt);
											cstmt.setString(8, service_id);
											cstmt.setString(9, plan_id);
											cstmt.setString(10, trans_id);
											cstmt.setString(11, billing_ID);
											cstmt.execute();
											cstmt.close();
											System.out.println(" response '"+ response + "'");
										}
								}

								else
								{
						 

									if("ok".equalsIgnoreCase(status))
									{
										System.out.println("PROC call BILLING_"+hun_type.toUpperCase()+"_"+status.toUpperCase()+"('"+mode+"','"+msisdn+"','"+event_type+"','"+avl_amt+"','"+chr_amt+"','"+amount+"','"+pre_post+"','"+service_id+"','"+plan_id+"','"+trans_id+")");

											cstmt = con1.prepareCall("{call BILLING_"+hun_type.toUpperCase()+"_"+status.toUpperCase()+"(?,?,?,?,?,?,?,?,?,?)}");
											cstmt.setString(1, mode);
											cstmt.setString(2, msisdn);
											cstmt.setString(3, event_type);
											cstmt.setString(4, avl_amt);
											cstmt.setString(5, chr_amt);
											cstmt.setString(6, amount);
											cstmt.setString(7, pre_post);
											cstmt.setString(8, service_id);
											cstmt.setString(9, plan_id);
											cstmt.setString(10,trans_id);
											cstmt.execute();
											cstmt.close();

									}
									else
									{
										System.out.println("PROC call BILLING_"+hun_type.toUpperCase()+"_"+status.toUpperCase()+"('"+mode+"','"+msisdn+"','"+event_type+"','"+avl_amt+"','"+response+"','"+amount+"','"+pre_post+"','"+service_id+"','"+plan_id+"','"+trans_id+")");

										cstmt = con1.prepareCall("{call BILLING_"+hun_type.toUpperCase()+"_"+status.toUpperCase()+"(?,?,?,?,?,?,?,?,?,?)}");
										cstmt.setString(1, mode);
										cstmt.setString(2, msisdn);
										cstmt.setString(3, event_type);
										cstmt.setString(4, avl_amt);
										cstmt.setString(5, response);
										//cstmt.setString(5,chr_amt);
										cstmt.setString(6, amount);
										cstmt.setString(7, chr_amt);
										cstmt.setString(8, service_id);
										cstmt.setString(9, plan_id);
										cstmt.setString(10, trans_id);
										cstmt.execute();
										cstmt.close();
										System.out.println(" response '"+ response + "'");
									}
								}
								System.out.println("called procedure "+operator+" BILLING_"+hun_type.toUpperCase()+"_"+status.toUpperCase());
								sleep(20);
							}
				        }//if message ends
					}//try ends

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
						con1=dbConn();
					}

				}catch(Exception e1)
				{
					hunLog(e1.toString(),'e');
					e1.printStackTrace();
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

				switch(file)
				{
				 case'e':
				 	messageQ.setText("ERROR#"+log);
					producerQ.send(messageQ);
				/*if(strdate.equals(mystrdate))
				{
					logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
				}
				else
				{
					mnthdir=formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
					errPath="../logger/"+mnthdir+"/";
					dir=new File(errPath);
					if(!dir.exists())
					dir.mkdir();

					strdate = mystrdate;
					err_App = new FileAppender(new PatternLayout(),errPath+"ERROR_"+strdate+".log");
					err_App.setAppend(true);

					logger = Logger.getLogger("Hun-ER-Logger");
					logger.addAppender(err_App);
					logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
				}*/
				break;
			  case 'l':
					messageQ.setText("Logger#"+log);
					producerQ.send(messageQ);
				  /*
			     if(strdate.equals(mystrdate))
					{
						logger1.info("#"+mystrdate+"#"+mystrtime+"#"+log);
					}
					else
					{
						mnthdir=formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
						errPath="../logger/"+mnthdir+"/";
						dir=new File(errPath);
						if(!dir.exists())
						dir.mkdir();

						strdate = mystrdate;
						log_app = new FileAppender(new PatternLayout(),errPath+"Logger_"+strdate+".log");
						log_app.setAppend(true);


						logger1 = Logger.getLogger("Hun-TD-Logger");
						logger1.addAppender(log_app);
						logger1.info("#"+mystrdate+"#"+mystrtime+"#"+log);
					}*/
			  break;
			  	}//switch end s


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
			/*errPath="../logger/"+mnthdir+"/";
			dir=new File(errPath);
			if(!dir.exists())
				dir.mkdirs();

			err_App = new FileAppender(new PatternLayout(),errPath+"ERROR_"+strdate+".log");
			err_App.setAppend(true);
			logger = Logger.getLogger("Hun-ER-Logger");
			logger.addAppender(err_App);


			//======================TATA Appender=================
			log_app = new FileAppender(new PatternLayout(),errPath+"Logger_"+strdate+".log");
			log_app.setAppend(true);
			logger1 = Logger.getLogger("Hun-TD-Logger");
			logger1.addAppender(log_app);*/



		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		VODMdblogger sBM = new VODMdblogger();
		sBM.start();
	}

}
