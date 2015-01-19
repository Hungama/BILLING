import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
import java.util.*;
import java.io.*;


import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

	public class SMSreciverBulk extends Thread{
	public static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

	public Destination destination,destination_log;
	public Session session,session_log;
	public MessageConsumer consumer;
	public MessageProducer producer,producer_log;
	public TextMessage messageQ,message_log;
	javax.jms.Connection connection,connection_log;
    private static String subject_recv = "";
    private static String subject_send = "";
	public static Connection con=null;
	public static Statement stmt,stmtUpdate;
	public static CallableStatement cstmt=null;
/***************** logger Variable  **********/
    public static Calendar today = null;
	public static String strdate  = "",mnthdir="";
	public static String strtime  = "";
	public static String errPath="";
	public static File dir=null;
//=======================================================

	public SMSreciverBulk()
	{
		try
		{

		    /*sourceBundle resource_source = ResourceBundle.getBundle("config/smsmgr_source");
			String msgqueue=resource_source.getString("SMSQ");
			subject = msgqueue;*/

		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

//=======================================================
	public void run()
	{
		String msgid = null;
    	String ani = null;
    	String message = null;
    	String date_time = null;
    	String dnis = null;
    	String type = null;
    	String operator = null;
    	String circle = null;
    	String priority = null;
    	String out_string = null;
    	String smsURL = null;
    	String cp_id = null;
    	String mig = null;
    	String amount = null;
		try
		{
			System.out.println("Active message Queue established!");
			messageQ = new ActiveMQTextMessage();
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			connection = connectionFactory.createConnection();
		    connection.start();
		    session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
		    destination = session.createQueue("BULKSMS");
	        consumer = session.createConsumer(destination);

	        //=================================================
			connection_log = connectionFactory.createConnection();
			connection_log.start();
			session_log = connection_log.createSession(false,Session.AUTO_ACKNOWLEDGE);
			destination_log = session.createQueue("HUNLOG");
			producer_log = session_log.createProducer(destination_log);
			message_log = new ActiveMQTextMessage();
			System.out.println("HUNLOG Queue Connetion");
		}
		catch(Exception e)
		{
			e.printStackTrace();

		}
		while(true)
		{
			try
			{
				messageQ = (TextMessage) consumer.receive();
		        if (messageQ instanceof TextMessage)
		        {
					//String response_time="";
	                TextMessage textMessage = (TextMessage) messageQ;
	                String in_string = textMessage.getText();
			String msg_type="";	                
			System.out.println(" Received message '"+ in_string + "'");
	                String in_msg[] = in_string.split("#");
                   	msgid = in_msg[0];
                	ani = in_msg[1];
                	message = in_msg[2];
                	//message = message.replaceAll(" ","%20");
                	date_time = in_msg[3];
                	dnis = in_msg[4];
                	amount = in_msg[5];
	                msg_type = in_msg[6];
                	 sleep(20);

					    String response ="-1";
					    java.util.Date dt=new java.util.Date();
						if(!msg_type.equalsIgnoreCase("Enagagement") || (dt.getHours()>=9 && dt.getHours()<=21)  )
						{

						SendSMSBulk ob=new SendSMSBulk();
						if("1".equalsIgnoreCase(amount))
						{
							cp_id="Q4sv4jPncR+t32gSY3+Wrw==";
							mig="migseven";
						}
						else
						{
							cp_id="Q4sv4jPncR8IMiDLcsWQCA==";
							mig="migthirty";
						}

							response = ob.sendSMSBulk(ani,"VAS220300",dnis,"6666817","123456",mig,message,cp_id);
						}
						System.out.println("======================Before response===============");
						Calendar today=Calendar.getInstance();
						String response_time  = formatN(""+today.get(Calendar.YEAR),4) +"-"+ formatN(""+(today.get(Calendar.MONTH)+1),2) +"-"+ formatN(""+today.get(Calendar.DATE),2) +" "+ formatN(""+today.get(Calendar.HOUR_OF_DAY),2) +":"+ formatN(""+today.get(Calendar.MINUTE),2) +":"+ formatN(""+today.get(Calendar.SECOND),2);// +"."+formatN(""+today.get(Calendar.MILLISECOND),0);
							//System.out.println("response_time----------------------> "+response_time);
					//message = message.replaceAll("%20"," ");
					 out_string = msgid+"#"+ani+"#"+message+"#"+date_time+"#"+dnis+"#"+response_time+"#"+response+"#";



               	    message_log.setText("SMSReceiver#SMSBULK#"+out_string);
				    producer_log.send(message_log);

//				    System.out.println(" Sent message '" + message_log.getText());
	            }//try
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}//while

	}

	private String formatN(String str, int x)
	{
		try
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
		catch(Exception e)
		{
			System.out.println("Error @ formatN "+ e);
			return " err";
		}
	}

	public static void main(String args[])
	{
		SMSreciverBulk rBM = new SMSreciverBulk();
		rBM.start();
	}

}


