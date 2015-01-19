import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
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

	public class SMSlogin extends Thread{
	public static String url_recv = ActiveMQConnection.DEFAULT_BROKER_URL;
	public Destination destination_recv;
	public Session session_recv;
	public MessageConsumer consumer;
	public TextMessage message_recv;
	javax.jms.Connection connection_recv;
    private static String subject_recv = "";
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

	public SMSlogin()
	{
		try
		{

		    ResourceBundle resource_source = ResourceBundle.getBundle("config/smsmgr_source");
			String msgqueue_send=resource_source.getString("MSGQUEUE_LOGIN");
		    subject_recv = msgqueue_send;
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
		try
		{
			System.out.println("Active message Queue established!");
			message_recv = new ActiveMQTextMessage();
			ConnectionFactory connectionFactory_recv = new ActiveMQConnectionFactory(url_recv);
		    connection_recv = connectionFactory_recv.createConnection();
		    connection_recv.start();
		    session_recv = connection_recv.createSession(false,Session.AUTO_ACKNOWLEDGE);
	        destination_recv = session_recv.createQueue(subject_recv);
	        consumer = session_recv.createConsumer(destination_recv);
		}
		catch(Exception e)
		{
			e.printStackTrace();

		}
		while(true)
		{
			try
			{
				message_recv = (TextMessage) consumer.receive();
		        if (message_recv instanceof TextMessage)
		        {
	                TextMessage textMessage = (TextMessage) message_recv;
	                String in_string = textMessage.getText();
	                System.out.println(" Received message '"+ in_string + "'");
	                log(in_string);
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

	public void log(String str)
	{
		try
		{
			String in_str[] = str.split("#");
			Calendar today = Calendar.getInstance();
			String strlogfile = in_str[0]+formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
			FileOutputStream outfile = new FileOutputStream("./log/" + strlogfile + ".txt",true);
			PrintStream outprint = new PrintStream(outfile);
  			System.out.println(str);
			outprint.println(str);
			outprint.close();
			outfile.close();
		}
		catch(Exception ee)
		{
			System.out.println("Exception:" + ee.toString());
			ee.printStackTrace();
		}
	}

	public static void main(String args[])
	{
		SMSlogin rBM = new SMSlogin();
		rBM.start();
	}

}


