import hungamalogging.hungamalogging;
import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.SessionStateListener;
import java.net.*;
import java.io.*;
//import java.text.*;
public class EtislatReceiverTF_REC_InsertQueue extends Thread
{




	public static String url_recv = ActiveMQConnection.DEFAULT_BROKER_URL;
	public static String url_send = ActiveMQConnection.DEFAULT_BROKER_URL;
	public static Destination destination_recv;
	public static Destination destination_send;
	public static Destination destination_sendid;
	public Destination destination_sendx;
	public Destination destination_sendx1;
	public static Destination destination_sendx2;
	public static Session session_recv;
	public static Session session_send;
	public Session session_sendid;
	public Session session_sendx;
	public Session session_sendx1;
	public static Session session_sendx2;
	public static MessageConsumer consumer;
	public static MessageProducer producer;
	public static MessageProducer producerid;
	public MessageProducer producerx;
	public MessageProducer producerx1;
	public static MessageProducer producerx2;
	public static TextMessage message_send;
	public static TextMessage message_recv;
	public TextMessage message_sendid;
	public TextMessage message_sendx;
	public TextMessage message_sendx1;
	public static TextMessage message_sendx2;
	static javax.jms.Connection connection_recv;
	static javax.jms.Connection connection_send;
	javax.jms.Connection connection_sendid;
	javax.jms.Connection connection_sendx;
	javax.jms.Connection connection_sendx1;
	static javax.jms.Connection connection_sendx2;
	private static String subject_recv = "";
	private static String subject_send = "";
	private static String subject_send1 = "";
	public static Connection con = null;
	public static Statement stmt, stmtUpdate;
	public static CallableStatement cstmt = null;
	/***************** logger Variable **********/

	public static File dir = null;
	private static SMPPSession session = null;
	private static String remoteIpAddress;
	private static int remotePort;
	private static BindParameter bindParam;
	private final long reconnectInterval = 5000L;
	public static String MID = "";
	static String dsmlogs="/home/ivr/javalogs/EtislatTF";


	public static String ip=null,dsn=null,username=null,pwd=null,msgqueue=null;
	public static Connection conn=null;
	public static ArrayList<String> OUT_QUE = new ArrayList<String>();
	String message_id = null;
	String msisdn = null;
	String event_type = null;
	String amount = null;
	String service_id = null;
	String operator = null;
	String circle = null;
	String in_mode = null;
	String plan_id = null;
	String msg_to_send=null;
	String dnis = null;
	String send_to=null;
	String date_time=null;
	String etype=null;
	EtislatReceiverTF_REC_InsertQueue globobj =null;

		// =======================================================
		public EtislatReceiverTF_REC_InsertQueue() throws IOException
		{
			EtislatReceiverTF_REC_InsertQueue.remoteIpAddress = remoteIpAddress;
			EtislatReceiverTF_REC_InsertQueue.remotePort = remotePort;
			EtislatReceiverTF_REC_InsertQueue.bindParam = bindParam;
			ResourceBundle resource_source = ResourceBundle.getBundle("config/chargingmgr_source");
			subject_recv = resource_source.getString("MSGQUEUE");
			ResourceBundle resource_destination = ResourceBundle.getBundle("config/chargingmgr_destination");
			subject_send = resource_destination.getString("MSGQUEUE");
			subject_send1 = resource_destination.getString("MSGQUEUELOG");
			System.out.println("creatin new Session");
		}

	/********************************************** Calling URL Check or subscribe CHK,ACT,DCT,UPD **********************************************************************/

	private void sendQueeue() throws IOException
		{
			try
			{
				message_sendx = new ActiveMQTextMessage();
				ConnectionFactory connectionFactory_sendx = new ActiveMQConnectionFactory(url_send);
				connection_sendx = connectionFactory_sendx.createConnection();
				connection_sendx.start();
				session_sendx = connection_sendx.createSession(false,Session.AUTO_ACKNOWLEDGE);
				destination_sendx = session_sendx.createQueue(subject_send);
				producerx = session_sendx.createProducer(destination_sendx);

				message_sendx1 = new ActiveMQTextMessage();
				ConnectionFactory connectionFactory_sendx1 = new ActiveMQConnectionFactory(url_send);
				connection_sendx1 = connectionFactory_sendx1.createConnection();
				connection_sendx1.start();
				session_sendx1 = connection_sendx1.createSession(false,Session.AUTO_ACKNOWLEDGE);
				destination_sendx1 = session_sendx1.createQueue(subject_send1);
				producerx1 = session_sendx1.createProducer(destination_sendx1);



			}
			catch(Exception ex)
			{
				hungamalogging.log("RECEIVER#Exception in creating active  mq"+ex,"ExceptionLog_",dsmlogs);
				System.out.println("Exception in creatin new queue"+ex);
			}
			try
			{

					String out_string = "8860382137#Stop All#38567";
					System.out.println("outstring to set"+out_string);
					try
					{
						message_sendx.setText(out_string);
						producerx.send(message_sendx,2,9,100000000);
						System.out.println("producerx.send(message_sendx,2,9,100000000)");
					}
					catch(Exception ex)
					{
						System.out.println("exception in pushing dblogger"+msisdn+"#"+ex);
						ex.printStackTrace();
	//					hungamalogging.log("RECEIVER#exception in processing default message"+log+"#"+ex,"ExceptionLog_",dsmlogs);
					}
			}		
			catch(Exception ex)
			{
				System.out.println("Exception in sending msg to producerlogger#"+msisdn+"#"+ex);
			}
		}


			/*********************************************** Void Main ************************************************/
	    public static void main(String[] args) throws IOException
	    {

			EtislatReceiverTF_REC_InsertQueue eti=new EtislatReceiverTF_REC_InsertQueue();
			eti.sendQueeue();
    	}

/*		public static void Init_Queue()
		{
			try
			{
				System.out.println("Active message Queue established!");
				message_send = new ActiveMQTextMessage();
				message_recv = new ActiveMQTextMessage();
				ConnectionFactory connectionFactory_recv = new ActiveMQConnectionFactory(url_recv);
				ConnectionFactory connectionFactory_send = new ActiveMQConnectionFactory(url_send);
				connection_recv = connectionFactory_recv.createConnection();
				connection_send = connectionFactory_send.createConnection();
				connection_recv.start();
				connection_send.start();
				session_recv = connection_recv.createSession(false,Session.AUTO_ACKNOWLEDGE);
				session_send = connection_send.createSession(false,Session.AUTO_ACKNOWLEDGE);
				destination_recv = session_recv.createQueue(subject_recv);
				destination_send = session_send.createQueue(subject_send);
				producer = session_send.createProducer(destination_send);
				consumer = session_recv.createConsumer(destination_recv);
				producerid = session_send.createProducer(destination_sendid);
				message_sendx2 = new ActiveMQTextMessage();
				ConnectionFactory connectionFactory_sendx2 = new ActiveMQConnectionFactory(url_send);
				connection_sendx2 = connectionFactory_sendx2.createConnection();
				connection_sendx2.start();
				session_sendx2 = connection_sendx2.createSession(false,Session.AUTO_ACKNOWLEDGE);
				destination_sendx2 = session_sendx2.createQueue(subject_send1);
				producerx2 = session_sendx2.createProducer(destination_sendx2);
			}
			catch(Exception e)
			{
				System.out.println("Exception while initializing Queue's "+e);
			}
		}*/
}
