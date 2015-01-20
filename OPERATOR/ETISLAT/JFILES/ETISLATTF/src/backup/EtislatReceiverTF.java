stopppped
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
public class EtislatReceiverTF extends Thread implements  Gateway
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
	static Gateway gateway=null;
	EtislatReceiverTF globobj =null;

		// =======================================================
		public EtislatReceiverTF(final String remoteIpAddress,final int remotePort, final BindParameter bindParam) throws IOException
		{
			EtislatReceiverTF.remoteIpAddress = remoteIpAddress;
			EtislatReceiverTF.remotePort = remotePort;
			EtislatReceiverTF.bindParam = bindParam;
			ResourceBundle resource_source = ResourceBundle.getBundle("config/chargingmgr_source");
			subject_recv = resource_source.getString("MSGQUEUE");
			ResourceBundle resource_destination = ResourceBundle.getBundle("config/chargingmgr_destination");
			subject_send = resource_destination.getString("MSGQUEUE");
			subject_send1 = resource_destination.getString("MSGQUEUELOG");
			System.out.println("creatin new Session");
			session = newSession();
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
//				return "123";
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
					sendAlert("Etislate Receiver has been exited due to some error"+ex);
					System.exit(0);
				}
				//System.out.println("inside smsssion"+message_sendx+"and "+producerx);
				 System.out.println("Starting............. SMPP Session");
				final SMPPSession tmpSession = new SMPPSession(remoteIpAddress,remotePort, bindParam);
				tmpSession.setTransactionTimer(6000);
				tmpSession.addSessionStateListener(new SessionStateListenerImpl());
				 System.out.println("Returning SMPP Session herrrrrrrrrrrrrrrrr");
				tmpSession.setMessageReceiverListener(new MessageReceiverListener()
				{


					public void onAcceptDeliverSm(final DeliverSm deliverSm)throws ProcessRequestException
					{
						String smsg=null;
						String out_string=null;
						String log=null;
						System.out.println("receved deliversm");
						if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass()))
						{
							System.out.println("it is a delivery receipt");
							try
							{
								smsg=new String(deliverSm.getShortMessage());

								//log="Short Message>>"+smsg+"DestAddress "+deliverSm.getDestAddress();
								String ani=deliverSm.getSourceAddr();
								log="#SourceAddr "+ani+"#DestAddress "+deliverSm.getDestAddress() +"#"+"DestAddrNpi "+deliverSm.getDestAddrNpi()+ "#DestAddrTon "+deliverSm.getDestAddrTon()+ "#EsmClass "+deliverSm.getEsmClass()+ "#OptionalParametes "+deliverSm.getOptionalParametes()+ "#RegisteredDelivery "+deliverSm.getRegisteredDelivery()+ "#ServiceType "+deliverSm.getServiceType()+ "SourceAddrNpi "+deliverSm.getSourceAddrNpi()+ "#SourceAddrTon "+deliverSm.getSourceAddrTon()+ "#SmDefaultMsgId "+deliverSm.getSmDefaultMsgId()+ "#CommandId "+deliverSm.getCommandId()+"#CommandStatus "+deliverSm.getCommandStatus()+ "#SequenceNumber "+deliverSm.getSequenceNumber()+"#Short Message>>"+smsg;
								//log1=log+ "\nDestAddrNpi          "+deliverSm.getDestAddrNpi()+ "#DestAddrTon   "+deliverSm.getDestAddrTon()+ "#EsmClass		 "+deliverSm.getEsmClass()+ "#OptionalParametes   "+deliverSm.getOptionalParametes()+ "\nRegisteredDelivery 	 "+deliverSm.getRegisteredDelivery()+ "\nServiceType     "+deliverSm.getServiceType()+ "\nSourceAddr 		 "+deliverSm.getSourceAddr()+ "\nSourceAddrNpi    "+deliverSm.getSourceAddrNpi()+ "\nSourceAddrTon 	 "+deliverSm.getSourceAddrTon()+ "\nSmDefaultMsgId 	 "+deliverSm.getSmDefaultMsgId()+ "\nCommandId "+deliverSm.getCommandId()+"\nCommandStatus	 "+deliverSm.getCommandStatus()+ "\nSequenceNumber "+deliverSm.getSequenceNumber();
								hungamalogging.log(log,"RECEIPTRECV_", dsmlogs);
								System.out.println("short message received"+smsg);
								System.out.println("printing recved message");
								System.out.println(log);
								/*stat:DELIVRD err:000 text:*/

								int e = smsg.indexOf("err:");
								int s = smsg.indexOf("stat:");
								String errmsg=smsg.substring(s,e-1);
								String errcode=smsg.substring(e,smsg.indexOf("text:")-1);
								String out=ani+"#"+errcode+"#"+errmsg;
								message_sendx1.setText(out);
								System.out.println("message setted for logging");
								producerx1.send(message_sendx1,2,9,100000000);
								System.out.println("message puhed in queue ");

							}
							catch (Exception e)
							{
								System.out.println("Failed getting delivery receipt#"+msisdn+"#"+e);
								e.printStackTrace();
								hungamalogging.log("RECEIVER#exception in processing deliveryrecipt"+log+"#"+e,"ExceptionLog_",dsmlogs);
							}
						}
						else if(MessageType.DEFAULT.containedIn(deliverSm.getEsmClass()))
						{
							System.out.println("it is a default message");
							try
							{
								String smsg1=new String(deliverSm.getShortMessage());
								System.out.println("it is a default message1");
								msisdn=new String(deliverSm.getSourceAddr());
								System.out.println("it is a default message2");
								dnis=deliverSm.getDestAddress();
								System.out.println("it is a default message3");
								log="Short Message "+smsg1+ "DestAddress "+deliverSm.getDestAddress();
								System.out.println("it is a default message4");
								log=log+ "#DestAddrNpi-"+deliverSm.getDestAddrNpi()+ "#DestAddrTon-"+deliverSm.getDestAddrTon()+ "#EsmClass-"+deliverSm.getEsmClass()+ "#OptionalParametes-"+deliverSm.getOptionalParametes()+ "#RegisteredDelivery -"+deliverSm.getRegisteredDelivery()+ "#ServiceType     "+deliverSm.getServiceType()+ "#SourceAddr 	-"+deliverSm.getSourceAddr()+ "#SourceAddrNpi    "+deliverSm.getSourceAddrNpi()+ "#SourceAddrTon -"+deliverSm.getSourceAddrTon()+ "#SmDefaultMsgId -"+deliverSm.getSmDefaultMsgId()+ "#CommandId -"+deliverSm.getCommandId()+"#CommandStatus-"+deliverSm.getCommandStatus()+ "#SequenceNumber-"+deliverSm.getSequenceNumber();
								System.out.println("it is a default message5");
								hungamalogging.log(log,"MORECV_", dsmlogs);
								System.out.println("it is a default message6");
								System.out.println(smsg1);
								System.out.println("it is a default message7");
								if (dnis.equalsIgnoreCase("38567"))
								{
									out_string = msisdn+"#"+smsg1+"#"+dnis;
									System.out.println("it is a default message8");
									System.out.println("outstring to set"+out_string);
									try
									{
										//System.out.println("message send object is"+message_sendx);
										message_sendx.setText(out_string);
										System.out.println("message setted");
										producerx.send(message_sendx,2,9,100000000);
										System.out.println("message puhed in queue ");
									}
									catch(Exception ex)
									{
										System.out.println("exception in pushing dblogger"+msisdn+"#"+ex);
										ex.printStackTrace();
										hungamalogging.log("RECEIVER#exception in processing default message"+log+"#"+ex,"ExceptionLog_",dsmlogs);
									}
								}
								else
								{
									System.out.println("MO from a diffrent DNIS"+log);
								}

							}
							catch(Exception ex)
							{
								System.out.println("Exception in sending msg to producerlogger#"+msisdn+"#"+ex);
								hungamalogging.log("RECEIVER#exception in processing MO"+log+"#"+ex,"ExceptionLog_",dsmlogs);
							}
						}
						else
						{

								System.out.println("it is a not default not delivery message");
								try
								{
									String smsg1=new String(deliverSm.getShortMessage());
									System.out.println("it is a default message9");
									msisdn=new String(deliverSm.getSourceAddr());
									System.out.println("it is a default message10");
									dnis=deliverSm.getDestAddress();
									System.out.println("it is a default message11");
									System.out.println("it is a default message");
									System.out.println("it is a default message");
									log="Short Message "+smsg1+ "DestAddress "+deliverSm.getDestAddress();
									System.out.println("it is a default message12");
									log=log+ "#DestAddrNpi-"+deliverSm.getDestAddrNpi()+ "#DestAddrTon-"+deliverSm.getDestAddrTon()+ "#EsmClass-"+deliverSm.getEsmClass()+ "#OptionalParametes-"+deliverSm.getOptionalParametes()+ "#RegisteredDelivery -"+deliverSm.getRegisteredDelivery()+ "#ServiceType     "+deliverSm.getServiceType()+ "#SourceAddr 	-"+deliverSm.getSourceAddr()+ "#SourceAddrNpi    "+deliverSm.getSourceAddrNpi()+ "#SourceAddrTon -"+deliverSm.getSourceAddrTon()+ "#SmDefaultMsgId -"+deliverSm.getSmDefaultMsgId()+ "#CommandId -"+deliverSm.getCommandId()+"#CommandStatus-"+deliverSm.getCommandStatus()+ "#SequenceNumber-"+deliverSm.getSequenceNumber();
									System.out.println("it is a default message13");
									hungamalogging.log(log,"MORECV_", dsmlogs);
									System.out.println("it is a default message14");
									System.out.println(smsg1);

								}
								catch(Exception ex)
								{
									System.out.println("Exception in else part#"+msisdn+"#"+ex);
									hungamalogging.log("RECEIVER#Exception in else part"+log+"#"+ex,"ExceptionLog_",dsmlogs);
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
				});
                System.out.println("Returning SMPP Session");
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
					System.out.println("Initiate session for the first time to "
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
						System.out.println("Schedule reconnect after " + timeInMillis
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
								System.out.println("Reconnecting attempt #" + (++attempt)
										+ "...");
								session = newSession();
							} catch (final IOException e) {
								System.out.println("Failed opening connection and bind to "
										+ remoteIpAddress + ":" + remotePort+e);
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
						System.out.println("Session closed");
						reconnectAfter(reconnectInterval);
					}
				}
			}
			/*********************************************** Void Main ************************************************/
	    public static void main(String[] args) throws IOException
	    {
	    	while(true)
	    	{
			    	try
					{
			    		gateway = new EtislatReceiverTF("10.71.128.47", 5020, new BindParameter(BindType.BIND_TRX, "provectus", "provec", null, TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
		
					}
					catch(Exception ex)
					{
						System.out.println("exception in thread main"+ex);
						sendAlert("Etislate Receiver has been exited due to some error"+ex);
						
						SLEEP(1000); continue;
						//System.exit(0);
					}
					Init_Queue();initDB();
					ExecutorService execService = Executors.newFixedThreadPool(10);
					


					        	// ==============LOGGING=============================
				        	while (true)
				        	{

			                int TPS=10;
                                       try{ TPS=Integer.parseInt(args[0]);}catch(Exception er){TPS=10; }
			                //int TPS_Count=0;

							java.util.Date dt=new java.util.Date(System.currentTimeMillis() - (int)(4.5 * 60 * 60 * 1000));
			//				Date dt = new Date(System.currentTimeMillis() - (int)(4.5 * 60 * 60 * 1000));
							if((dt.getHours()>=10 )&& dt.getHours()<=19)
							{
								 	for(int I=0;I<10;I++)
									{  
									 if(OUT_QUE.size()>0)
									 break;
									 PUSH_APP("tbl_sms_bulk");
									 PUSH_APP("tbl_sms");
									}

									if(OUT_QUE.size()==0)
									{
										SLEEP(60000);
										continue;
									}

							        for (int I = 0; I < TPS; I++)
							        {
										if(OUT_QUE.size()==0)
										{ break;}
										try{String s=OUT_QUE.get(I);}catch(Exception e){break;}
										final String in_string_temp = OUT_QUE.get(I); 
				                       
										 execService.execute(new Runnable() {
                public void run() {
										if (message_recv instanceof TextMessage)
										{

											String in_string=in_string_temp ;
											String message_id="",msisdn="",msg_to_send="",date_time="",dnis="",etype="";
											try
											{
												System.out.println(" Received message '" + in_string + "'");
												String in_msg[] = in_string.split("#");
												message_id = in_msg[0];
												msisdn = in_msg[1];
												msg_to_send = in_msg[2];
												msg_to_send=msg_to_send.replaceAll("#","%23");
												date_time = in_msg[3];
												dnis = in_msg[4];
												etype = in_msg[5];
												dnis="38567";
											}
											catch(Exception e)
											{
												System.out.println("Exception while Tokenizing data recieved from Queue ("+in_string+")  "+e);
											}

											//System.out.println("Received message>>"+in_string );
											final RegisteredDelivery registeredDelivery = new RegisteredDelivery();
											registeredDelivery.setSMSCDeliveryReceipt(SMSCDeliveryReceipt.SUCCESS_FAILURE);
											String messageId ="";
											try
											{
												messageId = gateway.submitShortMessage("CPT", TypeOfNumber.UNKNOWN , NumberingPlanIndicator.UNKNOWN,dnis , TypeOfNumber.UNKNOWN , NumberingPlanIndicator.UNKNOWN, msisdn, new ESMClass(0), (byte)0, (byte)0,null, null, new RegisteredDelivery(1), (byte)0, new GeneralDataCoding(0), (byte)0,msg_to_send.getBytes());
												System.out.println("msgid recieved befor convert"+messageId);
												in_string=in_string+"#"+messageId;
												messageId=new Integer(Integer.parseInt(messageId.toString(),16)).toString();
												in_string=in_string+"#"+messageId;
												System.out.println("\nMessage submitted msisdn="+msisdn+",message_id is " + messageId);

												hungamalogging.log(in_string,"MTSend_",dsmlogs);
											}
											catch (ResponseTimeoutException e) {
												System.out.println("rESPONSE tIMEOUT Exception"+e);
												hungamalogging.log("RECEIVER#PDUsendMT#"+in_string,"ExceptionLog_",dsmlogs);
												e.printStackTrace();
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
														System.out.println("Exception in substring ResponseTimeoutException"+p);
														ex1="-1";
												}
												String out=msisdn+"#"+ex1+"#ResponseTimeoutException";
												try {
													message_sendx2.setText(out);
												
												System.out.println("message setted for logging");
												
													producerx2.send(message_sendx2,2,9,100000000);
												} catch (JMSException e1) {
													// TODO Auto-generated catch block
													e1.printStackTrace();
													Init_Queue();
												}
												}
											catch (PDUException e)
											{
												System.out.println("PDU Send Exception"+e);
												hungamalogging.log("RECEIVER#PDUsendMT#"+in_string,"ExceptionLog_",dsmlogs);
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
														System.out.println("Exception in substring PDUException"+p);
														ex1="-1";
												}
												String out=msisdn+"#"+ex1+"#PDUException";
												try {
													message_sendx2.setText(out);
												
												System.out.println("message setted for logging");
												
													producerx2.send(message_sendx2,2,9,100000000);
												} catch (JMSException e1) {
													// TODO Auto-generated catch block
													e1.printStackTrace();
													Init_Queue();
												}
											}
											catch (InvalidResponseException e)
											{
												System.out.println("invalid Response please check"+e);
												hungamalogging.log("RECEIVER#invalid responseMT#"+in_string,"ExceptionLog_",dsmlogs);
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
														System.out.println("Exception in substring InvalidResponseException"+p);
														ex1="-1";
												}
												String out=msisdn+"#"+ex1+"#InvalidResponse";
												try {
													message_sendx2.setText(out);
												
												System.out.println("message setted for logging");
												
													producerx2.send(message_sendx2,2,9,100000000);
												} catch (JMSException e1) {
													// TODO Auto-generated catch block
													e1.printStackTrace();
													Init_Queue();
												}
											}
											catch (NegativeResponseException e)
											{
												System.out.println("NegativeResponseException in send"+e);
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
												String out=msisdn+"#"+ex1+"#NegativeResponse";
												try {
													message_sendx2.setText(out);
												
												System.out.println("message setted for logging");
												
													producerx2.send(message_sendx2,2,9,100000000);
												} catch (JMSException e1) {
													// TODO Auto-generated catch block
													e1.printStackTrace();
													Init_Queue();
												}
												System.out.println("message puhed in queue ");
												hungamalogging.log("RECEIVER#Negative responseMT#"+in_string+"#"+ex1,"ExceptionLog_",dsmlogs);
											}
											catch (IOException e)
											{
												System.out.println("IOExceptoin in send"+e);
												e.printStackTrace();
												hungamalogging.log("RECEIVER#IOExceptionMT#"+in_string,"ExceptionLog_",dsmlogs);
												sendAlert("EtislateTF Receiver has been exited due to some error"+e);
												//SLEEP(60000);
												System.exit(0);
											}
											catch (Exception e)
											{
												System.out.println("IOExceptoin in send"+e);
												e.printStackTrace();
												hungamalogging.log("RECEIVER#ExceptionMT#"+in_string,"ExceptionLog_",dsmlogs);
												sendAlert("EtislateTF Receiver There is some error"+e);
												
											}
											 UPDATE_DB(message_id,messageId);
												//SLEEP(100);
										}
                                          
										  } }); // Thread End
										 OUT_QUE.remove(I);
										

								} // End For Loop
								SLEEP(500);
                                                                System.out.println("*************** Ready to pick Next Set  ***********************");
						}
							else
							{
								SLEEP(10000);System.out.println("*************** SLEEPING OUT OF WINDOW ***********************");
							}


				   			} // while ends
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
                String _marr[]={"8588838347","8586968482","8586968481","8586967042","8587800614"};
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


		public static void Init_Queue()
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
		}
		
		
		public static void PUSH_APP(String table_name)
		{

			ResultSet rs = null;
			try {
				//String qquery ="select msgid,ani,message,date_time,status,dnis,type,operator,circle,priority from master_db.tbl_new_sms1  where (status=0 or  operator='UNIM') and type in('TXT','RNG','WAP')  order by priority limit 10";
				//System.out.println("Null Message deleted "+dbConn().createStatement().executeUpdate("delete from uninor_cricket.tbl_msg_status where message is null"));
				String qquery ="";
				if(table_name.equals("tbl_sms"))
					qquery="select msg_id,ani,message,date_time,dnis,type from etislat_hsep.tbl_sms nolock where  date_time<= now() and status=0  and message is not null and length(message)<>0  order by date_time  limit 1000";
				else
				    qquery = "select msg_id,ani,message,date_time,dnis,type from etislat_hsep.tbl_sms_bulk nolock where date(date_time)=date(now()) and  status=0  and message is not null and length(message)<>0 order by temp desc limit 1000 ";
            	rs = dbConn().createStatement().executeQuery(qquery);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(rs!=null)
			{
				try {
					while(rs.next())
					{
						try{


							int msgid = rs.getInt("msg_id");
							String ani = rs.getString("ani").trim();
							String message=rs.getString("message").trim();
							String date_time = rs.getString("date_time");
							String dnis=rs.getString("dnis").trim();
							String etype=rs.getString("type").trim();
							String log=msgid+"#"+ani+"#"+message+"#"+dnis+"#"+etype;
							hungamalogging.log(log,"Sender_", dsmlogs);
							System.out.println("Picked Mobile :"+ani+" type -> "+etype +" message "+message);
							String out_string=msgid+"#"+ani+"#"+message+"#"+date_time+"#"+dnis+"#"+etype;						
							System.out.println("Pushed String - "+out_string);
					        OUT_QUE.add(out_string);
		
							}
							catch(Exception e)
							{
					             e.printStackTrace();
							}
						}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			} // function ends
		
		public static void UPDATE_DB(String message_id,String messageId)
		{
			try
			{
				CallableStatement cstmt = dbConn().prepareCall("{call etislat_hsep.create_smslog_new(?,?,?)}");			
				cstmt.setInt(1,Integer.parseInt(message_id));
				cstmt.setString(2,messageId);
				cstmt.setString(3,"tbl_sms_bulk");
				cstmt.execute();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		
		}
		
		public static Connection dbConn()
		{
			while(true)
			{
				try
				{
					if(!conn.isClosed())
					return conn;
				}
				catch(Exception e){}
				try
				{
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn+"?autoReconnect=true", username, pwd);
					System.out.println("Database Connection established!");
					return conn;
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
		
		public static void initDB()
		{
			try
			{

			    ResourceBundle resource = ResourceBundle.getBundle("config/chargingmgr_source");
				ip=resource.getString("IP");
				dsn=resource.getString("DSN");
				username=resource.getString("USERNAME");
				pwd=resource.getString("PWD");
			    System.out.println("IP: "+ip+" DATABASE :"+dsn+" USER :"+username+" PWD:"+pwd);

			}
			catch(Exception e)
			{

				e.printStackTrace();
				//System.exit(0);
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
