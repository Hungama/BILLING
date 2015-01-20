import hungamalogging.hungamalogging;
import java.io.*;
import java.net.*;
import java.text.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.QueueBrowser;

import java.sql.DriverManager;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQQueueBrowser;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EtislatReceiver_TRANS extends Thread implements  Gateway
{
	public static String url_recv = ActiveMQConnection.DEFAULT_BROKER_URL+"?wireFormat.cacheEnabled=false&wireFormat.tightEncodingEnabled=false";
	public static String url_send = ActiveMQConnection.DEFAULT_BROKER_URL+"?wireFormat.cacheEnabled=false&wireFormat.tightEncodingEnabled=false";
	public static String url_sub = ActiveMQConnection.DEFAULT_BROKER_URL+"?wireFormat.cacheEnabled=false&wireFormat.tightEncodingEnabled=false";
	public Destination destination_recv, destination_send, destination_sendid,destination_sendx,destination_sub;
	public Session session_recv, session_send, session_sendid,session_sendx,session_sub;
	public MessageConsumer consumer,consumer_sub;
	public MessageProducer producer, producerid,producerx;
	public TextMessage message_send, message_recv,message_sendid,message_sendx,message_sub_recv;
	javax.jms.Connection connection_recv, connection_send, connection_sendid,connection_sendx,connection_sub;
	private static String subject_recv = "";
	private static String subject_send = "";
	private static String subject_sendid="",subject_sub="";
	public static Connection con = null;
	public static Statement stmt, stmtUpdate;
	public static CallableStatement cstmt = null;
	/***************** logger Variable **********/
	public static Calendar today = null;
	public static String strdate = "", mnthdir = "";
	public static String strtime = "";
	public static String errPath = "";
	public static File dir = null;
	public static ArrayList<String> keywordlist = new ArrayList<String >();
	public static HashMap<String, String> chargingmessagesub = new HashMap<String, String>();
	public static HashMap<String, String> chargingmessageresub = new HashMap<String, String>();
	private static final Logger logger = LoggerFactory.getLogger(EtislatReceiver_TRANS.class);
	private SMPPSession session = null;
	private static String remoteIpAddress;
	private static int remotePort;
	private static BindParameter bindParam;
	private final long reconnectInterval = 5000L; // 5 seconds
	// private static MessageIDGenerator messageIDGenerator = new
	// RandomMessageIDGenerator();
	public static String MID = "";
	String dsmlogs="/home/ivr/javalogs/Etislat/";
	//String delireceipt="deliveryreceipt";
	 String billing_ID = null;
     String msisdn = null;
     String event_type = null;
     String amount = null;
     String service_id = null;
     String operator = null;
     String circle = null;
     String useravailbal = null;
     String code = null;
     String trans_id = null;
     String chargeamt = null;
     String fall_back_seqs = null;
     String type_of_plan = null;
     String setPlan_id = null;
     String in_mode = null;
     String keyword=null;
     String p_id=null;
     String plan_id = null;
     String msg_to_send=null;
     String dnis = null;
     String send_to=null;
     String status="NA";
     String out_string=null;
     static Gateway gateway=null;
	static int ii=2;
     public int counter=0;
     ConnectionFactory connectionFactory_recv = null;
 	ConnectionFactory connectionFactory_send = null;
 	ConnectionFactory connectionFactory_sendid = null;	        	
 	ConnectionFactory connectionFactory_sub = null;//new for sub
	
 	
		// =======================================================
	public EtislatReceiver_TRANS(final String remoteIpAddress,final int remotePort, final BindParameter bindParam) throws IOException
	{

		EtislatReceiver_TRANS.remoteIpAddress = remoteIpAddress;
		EtislatReceiver_TRANS.remotePort = remotePort;
		EtislatReceiver_TRANS.bindParam = bindParam;
		ResourceBundle resource_source = ResourceBundle.getBundle("config/chargingmgr_source");
		subject_recv = resource_source.getString("MSGQUEUE");
		subject_sub = resource_source.getString("MSGSUBQUEUE");
		ResourceBundle resource_destination = ResourceBundle.getBundle("config/chargingmgr_destination");
		subject_send = resource_destination.getString("MSGQUEUE");
		subject_sendid = resource_destination.getString("UPDATEQUEUE");
		session = newSession();

		loadMessages();

	}
	public EtislatReceiver_TRANS(int counter)
	{
		this.counter=counter;
	     // Default Constructure ...
	}
		
		
	/*public synchronized Boolean GetMessageCount(Session queue_session,String _msgqueue)
	{
		int numMsgs = 0;
		try {

			QueueBrowser browser = queue_session.createBrowser(queue_session.createQueue(_msgqueue));
			ActiveMQQueueBrowser amqb = (ActiveMQQueueBrowser) browser;
			Enumeration e = browser.getEnumeration();
			if (e.hasMoreElements()) {
				// Message message = (Message) e.nextElement();
				System.out.println("Recods found in Queue"+ _msgqueue.toString());
				return true;
			}
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		System.out.println("Recods Not found in Queue"+ _msgqueue.toString());
		return false;
	}
	
*/	
	
	public void run()
		{
	        try
	        {
	        	System.out.println("Active message Queue established!  "+counter);
	        	
	        	if(counter<ii)
	        	{
		        	message_sub_recv = new ActiveMQTextMessage();
		        	connectionFactory_sub = new ActiveMQConnectionFactory(url_sub);//new for sub
		        	connection_sub=connectionFactory_sub.createConnection();//new for sub
		        	connection_sub.start();//new for sub
		        	session_sub = connection_sub.createSession(false,Session.AUTO_ACKNOWLEDGE);// new for sub
		        	destination_sub = session_sub.createQueue(subject_sub);//new for sub
		        	consumer_sub=session_sub.createConsumer(destination_sub);//new for sub
	        	}
	        	else
	        	{	
	        		message_recv = new ActiveMQTextMessage();
		        	connectionFactory_recv = new ActiveMQConnectionFactory(url_recv);
		        	connection_recv = connectionFactory_recv.createConnection();
		        	connection_recv.start();
		        	session_recv = connection_recv.createSession(false,Session.AUTO_ACKNOWLEDGE);
		        	destination_recv = session_recv.createQueue(subject_recv);
		        	consumer = session_recv.createConsumer(destination_recv);
	        	}
	        	
	        	message_send = new ActiveMQTextMessage();
	        	message_sendid = new ActiveMQTextMessage();
	        	
	        	connectionFactory_send = new ActiveMQConnectionFactory(url_send);
	        	connectionFactory_sendid = new ActiveMQConnectionFactory(url_send);	        	
	        	
	        	connection_send = connectionFactory_send.createConnection();
	        	connection_sendid=connectionFactory_sendid.createConnection();	        	
	        	

	        	connection_send.start();
	        	connection_sendid.start();
	        	
	        	
	        	session_send = connection_send.createSession(false,Session.AUTO_ACKNOWLEDGE);
	        	session_sendid = connection_send.createSession(false,Session.AUTO_ACKNOWLEDGE);
	        	
	        	destination_send = session_send.createQueue(subject_send);
	        	destination_sendid = session_sendid.createQueue(subject_sendid);
	        	
	        	
	        	producer = session_send.createProducer(destination_send);
	        	producerid = session_send.createProducer(destination_sendid);

		        	// ==============LOGGING=============================
	        	
	        	
	        	while (true)
	        	{
					//Thread.sleep(1000*60*60*12);
					//continue;
	        		
	        		if(counter>=ii )
        			{
	        			System.out.println("################# message reciving from resub q");
	        			
	        			//if(GetMessageCount(session_recv,subject_recv));
	        				//{
	        					message_recv = (TextMessage) consumer.receive();
	        			          System.out.println("################# message reciving from resub q DDDDDDDDDDONE");
	        				//}
        			}
	        		else
	        		{
	        			System.out.println("################# message reciving from sub q");
	        			//if(GetMessageCount(session_sub,subject_sub));
	        			//{      
						     message_sub_recv = (TextMessage) consumer_sub.receive();
						     message_recv=message_sub_recv;
	        			//}
	        			System.out.println("################# message reciving from sub q DDDDDDDDDDONE");
	        		}	        				        	
	        		String in_string="",starttime="",endtime="";
		        	if (message_recv instanceof TextMessage)
		        	{
			        		
		        		    TextMessage textMessage = (TextMessage) message_recv;
			        		in_string = textMessage.getText();
			        		System.out.println(" Received message '" + in_string + "'");
			        		String in_msg[] = in_string.split("#");
	
			        		billing_ID = in_msg[0];
			    			msisdn = in_msg[1];
			    			event_type = in_msg[2];
			    			amount = in_msg[3];
			    			service_id = in_msg[4];
			    			operator = in_msg[5];
			    			circle = in_msg[6];
			    			plan_id = in_msg[7];
			    			type_of_plan = in_msg[8];
			    			fall_back_seqs = in_msg[9];
			    			in_mode=in_msg[10];//FOR
			    			dnis=in_msg[11];
			    		//	dnis="34567";
			        		if(event_type.equalsIgnoreCase("sub"))
			        			msg_to_send=chargingmessagesub.get(plan_id);
			        		else
			        			msg_to_send=chargingmessageresub.get(plan_id);
			        		                               
				        	System.out.println(plan_id+"#"+msg_to_send);
							msg_to_send=msg_to_send.replaceAll("#","%23");
				        	hungamalogging.log(in_string+"#"+msg_to_send,"Receiver_",dsmlogs);
				        	//System.out.println("Received message>>"+in_string );
				        	//System.out.println("Sending message"+msg_to_send);
				        	final RegisteredDelivery registeredDelivery = new RegisteredDelivery();
							registeredDelivery.setSMSCDeliveryReceipt(SMSCDeliveryReceipt.SUCCESS_FAILURE);
							//System.out.println("While loop starts....");
							String messageId ="";
							try
							{
								//System.out.println("Going to check for unsub"+msisdn);
								String RESULT[]=checkUNSUB(msisdn,service_id,plan_id).split("#");
								//System.out.println("Check unsub Completed for msidn"+msisdn);
								if(RESULT[0].equals("1"))
								{
									starttime = getDateTime();
									//System.out.println("Message Submitted ");
									messageId = gateway.submitShortMessage("CPT", TypeOfNumber.UNKNOWN , NumberingPlanIndicator.UNKNOWN,dnis , TypeOfNumber.UNKNOWN , NumberingPlanIndicator.UNKNOWN, msisdn, new ESMClass(0), (byte)0, (byte)0,null, null, new RegisteredDelivery(1), (byte)0, new GeneralDataCoding(0), (byte)0,msg_to_send.getBytes());
									//String endtime = getDateTime();
									System.out.println("msgid recieved befor convert"+messageId);
									//messageId=new Integer(Integer.parseInt(messageId.toString(),16)).toString();
									messageId=new Long(Long.parseLong(messageId.toString(),16)).toString();
									String CONTENT="";
									//String endtime = getDateTime();
									out_string = "Etislat#OK#"+billing_ID+"#"+msisdn+"#"+event_type+"#NA#"+amount+"#"+amount+"#NA#2121#"+plan_id+"#"+messageId+"#SMS#"+status;
									System.out.println(CONTENT+"\nMessage submitted msisdn="+msisdn+",message_id is " + messageId);
									message_sendid.setText(out_string);
									producerid.send(message_sendid,2,9,100000000);
								}
								else
								{
									System.out.println("Unsub Request Recieved : "+msisdn);
									out_string = "Etislat#NOK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+RESULT[1]+"#"+amount+"#"+amount+"#Negative#2121#"+plan_id+"#"+billing_ID+"#SMS#"+RESULT[1]+"#";
									message_send.setText(out_string);
									producer.send(message_send,2,9,100000000);
									hungamalogging.log("PDUException sending chhrgingmessages#"+out_string+"#"+msg_to_send+"#e","ExceptionLog_",dsmlogs);		
								}//		
							}
							catch (PDUException e)
							{		
								System.out.println("IOExceptoin in send"+e);
								System.out.println("PDU Send Exception"+e);
								out_string = "Etislat#NOK#"+billing_ID+"#"+msisdn+"#"+event_type+"#-2#"+amount+"#"+amount+"#Negative#2121#"+plan_id+"#"+billing_ID+"#SMS#-2#";
								message_send.setText(out_string);
								producer.send(message_send,2,9,100000000);
								hungamalogging.log("PDUException sending chhrgingmessages#"+out_string+"#"+msg_to_send+"#"+e,"ExceptionLog_",dsmlogs);
							}
							catch (InvalidResponseException e)
							{		
								System.out.println("IOExceptoin in send"+e);
								System.out.println("invalid Response please check"+e);
								out_string = "Etislat#NOK#"+billing_ID+"#"+msisdn+"#"+event_type+"#-3#"+amount+"#"+amount+"#Negative#2121#"+plan_id+"#"+billing_ID+"#SMS#-3#";
								message_send.setText(out_string);
								producer.send(message_send,2,9,100000000);
								hungamalogging.log("InvalidResponseException sending chhrgingmessages#"+out_string+"#"+msg_to_send+"#"+e,"ExceptionLog_",dsmlogs);		
							}
							catch (NegativeResponseException e)
							{
								endtime = getDateTime();
								System.out.println("msisdn#"+msisdn +"starttime#"+starttime+"endtime"+endtime);
								System.out.println("NegativeResponseException in send"+e);
								String ex1=null;
								try
								{
									ex1=e.toString();
									ex1=ex1.substring(0,ex1.indexOf("found")-1);
									ex1=ex1.substring(ex1.lastIndexOf(" ")+1,ex1.length());
									ex1=ex1.trim();
								}
								catch(Exception p)
								{
									System.out.println("Exception in substring negative respone"+p);
									ex1="-1";
								}
								System.out.println("sending to update");
		
								out_string = "Etislat#NOK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+ex1+"#"+amount+"#"+amount+"#Negative#2121#"+plan_id+"#"+billing_ID+"#SMS#"+ex1+"#";
								message_send.setText(out_string);
								producer.send(message_send,2,9,100000000);
								hungamalogging.log("NegativeResponseException sending chhrgingmessages#"+out_string+"#"+msg_to_send+"#"+e,"ExceptionLog_",dsmlogs);
							}
							catch (IOException e)
							{
								System.out.println("IOExceptoin in send"+e);
								e.printStackTrace();
								out_string = "Etislat#NOK#"+billing_ID+"#"+msisdn+"#"+event_type+"#-4#"+amount+"#"+amount+"#Negative#2121#"+plan_id+"#"+billing_ID+"#SMS#-4#";
								message_send.setText(out_string);
								producer.send(message_send,2,9,100000000);
								hungamalogging.log("IOException sending chhrgingmessages#"+out_string+"#"+msg_to_send+"#"+e,"ExceptionLog_",dsmlogs);
								sendAlert("EtislateReceiver has been exited due to IOException error"+e);
								System.exit(0);
							}
							catch(Exception e)
							{		
								System.out.println("Exceptoin in send"+e);
								e.printStackTrace();
								out_string = "Etislat#NOK#"+billing_ID+"#"+msisdn+"#"+event_type+"#-5#"+amount+"#"+amount+"#Negative#2121#"+plan_id+"#"+billing_ID+"#SMS#-5#";
								message_send.setText(out_string);
								producer.send(message_send,2,9,100000000);
								hungamalogging.log("LastException sending chhrgingmessages#"+out_string+"#"+msg_to_send+"#"+e,"ExceptionLog_",dsmlogs);						
							}
		        	}
					else
					{
						System.out.println("************************* NO MESAGE to SEND **************************************");
						SLEEP(1000);
					}
	   		} // while ends
			}
			catch(Exception e)
		  	{
		  		System.out.println(" -------Main Exception SMPP Billing Application Stops------------ "+e);
		  		e.printStackTrace();
		  		hungamalogging.log("Recver#"+out_string+"#"+e,"ExceptionLog_",dsmlogs);
		  		try
		  		{
					sendAlert("EtislateReceiver has been exited due to some error"+e);
					System.exit(0);
	  			}
	 			catch(Exception E)
	  			{
					System.out.println(E);
	  			}
			}
	}

	public String getDateTime()
	{
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date date = new java.util.Date();
            return dateFormat.format(date);
        }

/************************************************* SLEEP ****************************************************************/
	public static void SLEEP(final int n)
	{
		try
		{
			Thread.sleep(n);
		}
		catch (final Exception e)
		{
			System.out.println("Exception while sleep : " + e);
		}
	}

			/********************************************** Calling URL Check or subscribe CHK,ACT,DCT,UPD **********************************************************************/


			public String submitShortMessage(final String serviceType,
					final TypeOfNumber sourceAddrTon,
					final NumberingPlanIndicator sourceAddrNpi,
					final String sourceAddr, final TypeOfNumber destAddrTon,
					final NumberingPlanIndicator destAddrNpi,
					final String destinationAddr, final ESMClass esmClass,
					final byte protocolId, final byte priorityFlag,
					final String scheduleDeliveryTime, final String validityPeriod,
					final RegisteredDelivery registeredDelivery,
					final byte replaceIfPresentFlag, final DataCoding dataCoding,
					final byte smDefaultMsgId, final byte[] shortMessage,
					final OptionalParameter... optionalParameters) throws PDUException,
					ResponseTimeoutException, InvalidResponseException,
					NegativeResponseException, IOException
			{
				//return "205918863";
				return getSession().submitShortMessage(serviceType, sourceAddrTon,
						sourceAddrNpi, sourceAddr, destAddrTon, destAddrNpi,
						destinationAddr, esmClass, protocolId, priorityFlag,
						scheduleDeliveryTime, validityPeriod, registeredDelivery,
						replaceIfPresentFlag, dataCoding, smDefaultMsgId, shortMessage,
						optionalParameters);
			}

			public void replaceShortMessage(final String messageId,
		            TypeOfNumber sourceAddrTon,
		            NumberingPlanIndicator sourceAddrNpi,
		            String sourceAddr,
		            String scheduleDeliveryTime,
		            String validityPeriod,
		            RegisteredDelivery registeredDelivery,
		            byte smDefaultMsgId,
		            byte[] shortMessage)
		     		throws PDUException,
		            ResponseTimeoutException,
		            InvalidResponseException,
		            NegativeResponseException,
		            IOException
		            {
						getSession().replaceShortMessage(messageId,
				 										sourceAddrTon,
														sourceAddrNpi,
														sourceAddr,
														scheduleDeliveryTime,
														validityPeriod,
														registeredDelivery,
														smDefaultMsgId,
														shortMessage);
		            }

			String toBinary(final byte[] bytes)
			{
				final StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
				for (int i = 0; i < Byte.SIZE * bytes.length; i++)
					sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0'
							: '1');
				return sb.toString();
			}



			/*************************************************************************************
			 * Create new {@link SMPPSession} complete with the
			 * {@link SessionStateListenerImpl}.
			 *
			 * @return the {@link SMPPSession}.
			 * @throws IOException
			 *             if the creation of new session failed.
			 *************************************************************************************/
			private SMPPSession newSession() throws IOException
			{
				final SMPPSession tmpSession = new SMPPSession(remoteIpAddress,remotePort, bindParam);
				tmpSession.setTransactionTimer(6000);
				tmpSession.addSessionStateListener(new SessionStateListenerImpl());
				// SetTING listener to receive deliver_sm
				try
				{
					message_sendx = new ActiveMQTextMessage();
					ConnectionFactory connectionFactory_sendx = new ActiveMQConnectionFactory(url_send);
					connection_sendx = connectionFactory_sendx.createConnection();
					connection_sendx.start();
					session_sendx = connection_sendx.createSession(false,Session.AUTO_ACKNOWLEDGE);
					destination_sendx = session_sendx.createQueue(subject_send);
					producerx = session_sendx.createProducer(destination_sendx);

				}
				catch(Exception ex)
				{
					hungamalogging.log("Recvercreatingqueue deliversm#"+ex,"ExceptionLog_",dsmlogs);
					System.out.println("exceptin in creating new queue"+ex);
					sendAlert("EtislateReceiver has been exited due to some error"+ex);
					System.exit(0);
				}

			/*	tmpSession.setMessageReceiverListener(new MessageReceiverListener()
				{
					public void onAcceptDeliverSm(final DeliverSm deliverSm)throws ProcessRequestException
					{
						String smsg=null;
						String msgid=null;
						String errcode=null;
						String event_type=null;
						String amount=null;
						String trans_id=null;
						try
						{
							smsg=new String(deliverSm.getShortMessage());
							System.out.println("msg in ondeliversm"+smsg);
							String log="Short Message>>"+smsg+ "#DestAddress>>"+deliverSm.getDestAddress()+ "#DestAddrNpi>>"+deliverSm.getDestAddrNpi()+ "#DestAddrTon>>"+deliverSm.getDestAddrTon()+ "#EsmClass>>"+deliverSm.getEsmClass()+ "#OptionalParametes>>"+deliverSm.getOptionalParametes()+"#RegisteredDelivery>>"+deliverSm.getRegisteredDelivery()+ "#ServiceType>>"+deliverSm.getServiceType()+"#SourceAddr>>"+deliverSm.getSourceAddr()+ "#SourceAddrNpi>>"+deliverSm.getSourceAddrNpi()+ "#SourceAddrTon>>"+deliverSm.getSourceAddrTon()+ "#SmDefaultMsgId>>"+deliverSm.getSmDefaultMsgId()+ "#CommandId>>"+deliverSm.getCommandId()+"#CommandStatus>>"+deliverSm.getCommandStatus()+ "#SequenceNumber>>"+deliverSm.getSequenceNumber();
							hungamalogging.log(log,"ReceiverDeliverSM_", dsmlogs);
							errcode=smsg.substring(smsg.indexOf("err:")+4,smsg.indexOf("text:")-1);
							errcode=errcode.trim();
							String stat=null;

							try
							{
								System.out.println("smsg in state>>"+stat);
								stat=smsg.substring(smsg.indexOf("stat:")+5,smsg.indexOf("err:")-1);
								stat=errcode.trim();
								System.out.println("state is>>"+stat);

							}
							catch(Exception ex)
							{
								stat="Exception";
								System.out.println("exception in state"+ex);
							}
							msgid=smsg.substring(smsg.indexOf("id:")+3,smsg.indexOf("sub:")-1);
							msgid=msgid.trim();
							System.out.println("converting to integer and string"+msgid);
							msgid=new Long((Long.parseLong(msgid))).toString();
							System.out.println("errocode received"+errcode);
							String msisdn=deliverSm.getSourceAddr();
							String dnis=deliverSm.getDestAddress();
							if (dnis.equalsIgnoreCase("34567"))
								amount="75";
							else if (dnis.equalsIgnoreCase("33567"))
								amount="50";
							else if (dnis.equalsIgnoreCase("32567"))
                                                                amount="30";
                                                        else
								amount="0";
							String out_string=null;
							if(errcode.equals("000"))
							{
								out_string = "Etislat#OK#"+msgid+"#"+msisdn+"#"+event_type+"#NA#"+amount+"#"+amount+"#NA#2121#NA#"+trans_id+"#SMS#"+msgid;
							}
							else
							{
								out_string = "Etislat#NOK#"+msgid+"#"+msisdn+"#"+event_type+"#"+errcode+"#"+amount+"#"+amount+"#"+stat+"#2121#NA#"+trans_id+"#SMS#"+msgid;
							}
							message_sendx.setText(out_string);
							producerx.send(message_sendx,2,9,100000000);
					    }
						catch(Exception e)
						{
							System.out.println("Exception while logging recieved parameters"+e);
							hungamalogging.log("logging recievedparameter1#"+e,"ExceptionLog_", dsmlogs);
						}
						if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass()))
						{
							// delivery receipt
							try
							{
								String log="Short Message>>"+smsg+ "#DestAddress>>"+deliverSm.getDestAddress()+ "#DestAddrNpi>>"+deliverSm.getDestAddrNpi()+ "#DestAddrTon>>"+deliverSm.getDestAddrTon()+ "#EsmClass>>"+deliverSm.getEsmClass()+ "#OptionalParametes>>"+deliverSm.getOptionalParametes()+"#RegisteredDelivery>>"+deliverSm.getRegisteredDelivery()+ "#ServiceType>>"+deliverSm.getServiceType()+"#SourceAddr>>"+deliverSm.getSourceAddr()+ "#SourceAddrNpi>>"+deliverSm.getSourceAddrNpi()+ "#SourceAddrTon>>"+deliverSm.getSourceAddrTon()+ "#SmDefaultMsgId>>"+deliverSm.getSmDefaultMsgId()+ "#CommandId>>"+deliverSm.getCommandId()+"#CommandStatus>>"+deliverSm.getCommandStatus()+ "#SequenceNumber>>"+deliverSm.getSequenceNumber();
								hungamalogging.log(log+"#DeliveryRecipt#","ReceiverDeliverSM_", dsmlogs);
							}
							catch (Exception e)
							{
								hungamalogging.log("logging recievedparameter2#"+e,"ExceptionLog_", dsmlogs);
								System.err.println("Failed getting delivery receipt"+e);
							}
						}


					}
					public void onAcceptAlertNotification(final AlertNotification alertNotification)
					{
						System.out.println("inside onAcceptAlertNotification");
					}

					@Override
					public DataSmResult onAcceptDataSm(DataSm arg0,
							org.jsmpp.session.Session arg1)
							throws ProcessRequestException {
						// TODO Auto-generated method stub
						return null;
					}
				}); */

				return tmpSession;
			}

			/*************************************************************************************
			 * \ Get the session. If the session still null or not in bound state, then
			 * IO exception will be thrown.
			 *
			 * @return the valid session.
			 * @throws IOException
			 *             if there is no valid session or session creation is invalid.
			 *************************************************************************************/
			private SMPPSession getSession() throws IOException
			{
				if (session == null)
				{
					logger.info("Initiate session for the first time to "
							+ remoteIpAddress + ":" + remotePort);
					session = newSession();
				}
				else if (!session.getSessionState().isBound())
				{
					throw new IOException("We have no valid session yet");
				}
				return session;
			}

			/***************************************************************************************
			 * \ Reconnect session after specified interval.
			 *
			 * @param timeInMillis
			 *            is the interval.
			 ****************************************************************************************/
			private void reconnectAfter(final long timeInMillis)
			{
				new Thread() {
					@Override
					public void run() {
						logger.info("Schedule reconnect after " + timeInMillis
								+ " millis");
						try {
							Thread.sleep(timeInMillis);
						} catch (final InterruptedException e) {
						}
						int attempt = 0;
						while (session == null
								|| session.getSessionState()
										.equals(SessionState.CLOSED)) {
							try {
								logger.info("Reconnecting attempt #" + (++attempt)
										+ "...");
								session = newSession();
							} catch (final IOException e) {
								logger.error("Failed opening connection and bind to "
										+ remoteIpAddress + ":" + remotePort, e);
								// wait for a second
								try {
									Thread.sleep(1000);
								} catch (final InterruptedException ee) {
								}
							}
						}
					}
				}.start();
			}

			/************************************************************************************************
			 * \ This class will receive the notification from {@link SMPPSession} for
			 * the state changes. It will schedule to re-initialize session.
			 *
			 *
			 ************************************************************************************************/
			private class SessionStateListenerImpl implements SessionStateListener
			{
				public void onStateChange(final SessionState newState,final SessionState oldState, final Object source)
				{
					if (newState.equals(SessionState.CLOSED))
					{
						logger.info("Session closed");
						reconnectAfter(reconnectInterval);
					}
				}
			}
			/*********************************************** Void Main ************************************************/

		public void loadMessages()
		{
			try
			{
				File f = new File("config/submessage.conf");
				File f1 = new File("config/resubmessage.conf");
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String str=null;
				while ((str = br.readLine()) != null)
				{
					chargingmessagesub.put(str.substring(0,str.indexOf("#")), str.substring(str.indexOf("#")+1));
				}
				fr=null;
				br=null;
				fr= new FileReader(f1);
				br = new BufferedReader(fr);
				str=null;
				while ((str = br.readLine()) != null)
				{
					chargingmessageresub.put(str.substring(0,str.indexOf("#")), str.substring(str.indexOf("#")+1));
				}
				br.close();
				fr.close();


			}
			catch(Exception ex)
			{
				System.out.println("Exception in loading messages "+ex);
			}

		}
	    public static void main(String[] args) throws IOException
	    {
		try
		{
					initDB();
					gateway = new EtislatReceiver_TRANS("10.71.128.47", 5020, new BindParameter(BindType.BIND_TX, "provectus2", "provec", null, TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
			    	for(int x=0;x<Integer.parseInt(args[0]);x++)
			    	{
			    		Thread _objthread = new Thread(new EtislatReceiver_TRANS(x));
			    		_objthread.start();
			    	}
		}
		catch(Exception ex)
		{
		        System.out.println("exception in thread main"+ex);
		        sendAlert("EtislateReceiver has been exited due to some error"+ex);
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
		                String _marr[]={"8588838347","8586968481","8586967042","8587800614","7838102430"};
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

		            public static String checkUNSUB(String msisdn,String service_id,String plan_id)
					    {
					    try{
					    	CallableStatement cst=dbConn().prepareCall("{call etislat_hsep.UNSUB_CHECK(?,?,?,?)}");
					    	cst.setString(1, msisdn);
					    	cst.setString(2, service_id);
					    	cst.setString(3, plan_id);
					    	cst.registerOutParameter(4, java.sql.Types.VARCHAR);
					    	cst.executeUpdate();
					    	String result = cst.getString(4);
					    	return result;
					    }
					    catch(Exception e)
					    {
					    	e.printStackTrace();
					    	return "0#-1";
					    }
					   // return "0#-1";
					    }
					   public static String ip,dsn,username,pwd,msgqueue;
					    public static void initDB()
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

							}
							catch(Exception e)
							{
								sendAlert("etislatsender exited pls check error"+e);
								e.printStackTrace();
								System.exit(0);
							}
					    }

					    public static Connection dbConn()
						{
							while(true)
							{
								try
								{
									if(con==null || con.isClosed() )
									{
										Class.forName("com.mysql.jdbc.Driver");
										con = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn, username, pwd);
										System.out.println("Database Connection established!");

									}

								}
								catch(Exception e)
								{
									e.printStackTrace();
									sendAlert("etislatsender exited pls check error"+e);
									System.exit(0);
								}
								return con;
							}
						}


	}
	interface Gateway
	{

	    /**
	 * Submit the short message. It has the same parameter as
	 * {@link SMPPSession#submitShortMessage(String, TypeOfNumber, NumberingPlanIndicator, String, TypeOfNumber, NumberingPlanIndicator, String, ESMClass, byte, byte, String, String, RegisteredDelivery, byte, DataCoding, byte, byte[], OptionalParameter...)}.
	 *
	 * @param serviceType
	 * @param sourceAddrTon
	 * @param sourceAddrNpi
	 * @param sourceAddr
	 * @param destAddrTon
	 * @param destAddrNpi
	 * @param destinationAddr
	 * @param esmClass
	 * @param protocolId
	 * @param priorityFlag
	 * @param scheduleDeliveryTime
	 * @param validityPeriod
	 * @param registeredDelivery
	 * @param replaceIfPresentFlag
	 * @param dataCoding
	 * @param smDefaultMsgId
	 * @param shortMessage
	 * @param optionalParameters
	 * @return
	 * @throws PDUException
	 * @throws ResponseTimeoutException
	 * @throws InvalidResponseException
	 * @throws NegativeResponseException
	 * @throws IOException
	 */
	  public String submitShortMessage(String serviceType,
        TypeOfNumber sourceAddrTon, NumberingPlanIndicator sourceAddrNpi,
        String sourceAddr, TypeOfNumber destAddrTon,
        NumberingPlanIndicator destAddrNpi, String destinationAddr,
        ESMClass esmClass, byte protocolId, byte priorityFlag,
        String scheduleDeliveryTime, String validityPeriod,
        RegisteredDelivery registeredDelivery, byte replaceIfPresentFlag,
        DataCoding dataCoding, byte smDefaultMsgId, byte[] shortMessage,
        OptionalParameter... optionalParameters) throws PDUException,
        ResponseTimeoutException, InvalidResponseException,
        NegativeResponseException, IOException;
}
