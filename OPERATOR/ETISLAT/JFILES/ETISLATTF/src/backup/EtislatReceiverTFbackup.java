import hungamalogging.hungamalogging;
import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ResourceBundle;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
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
	public Destination destination_recv, destination_send, destination_sendid,destination_sendx;
	public Session session_recv, session_send, session_sendid,session_sendx;
	public MessageConsumer consumer;
	public MessageProducer producer, producerid,producerx;
	public TextMessage message_send, message_recv,message_sendid,message_sendx;
	javax.jms.Connection connection_recv, connection_send, connection_sendid,connection_sendx;
	private static String subject_recv = "";
	private static String subject_send = "";
	public static Connection con = null;
	public static Statement stmt, stmtUpdate;
	public static CallableStatement cstmt = null;
	/***************** logger Variable **********/

	public static File dir = null;
	private SMPPSession session = null;
	private static String remoteIpAddress;
	private static int remotePort;
	private static BindParameter bindParam;
	private final long reconnectInterval = 5000L;
	public static String MID = "";
	String dsmlogs="/home/ivr/javalogs/EtislatTF";

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
			System.out.println("creatin new Session");
			session = newSession();
		}
		public EtislatReceiverTF()
		{
			try
			{
				start();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				sendAlert("EtislateReceiverTF has been exited due to some error"+e);
				System.exit(0);

			}
		}
		public void run()
		{
			String in_string=null;
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
		        	// ==============LOGGING=============================
	        	while (true)
	        	{


				java.util.Date dt=new java.util.Date(System.currentTimeMillis() - (int)(4.5 * 60 * 60 * 1000));
//				Date dt = new Date(System.currentTimeMillis() - (int)(4.5 * 60 * 60 * 1000));
				if((dt.getHours()>=10 )&& dt.getHours()<=19)
				{
		   		message_recv = (TextMessage) consumer.receive();
		        	if (message_recv instanceof TextMessage)
		        	{
		        		TextMessage textMessage = (TextMessage) message_recv;
		        		in_string = textMessage.getText();
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
		        	System.out.println("Received message>>"+in_string );
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
					catch (PDUException e)
					{
						System.out.println("PDU Send Exception"+e);
						hungamalogging.log("RECEIVER#PDUsendMT#"+in_string,"ExceptionLog_",dsmlogs);
					}
					catch (InvalidResponseException e)
					{
						System.out.println("invalid Response please check"+e);
						hungamalogging.log("RECEIVER#invalid responseMT#"+in_string,"ExceptionLog_",dsmlogs);
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
						hungamalogging.log("RECEIVER#Negative responseMT#"+in_string+"#"+ex1,"ExceptionLog_",dsmlogs);
					}
					catch (IOException e)
					{
						System.out.println("IOExceptoin in send"+e);
						e.printStackTrace();
						hungamalogging.log("RECEIVER#IOExceptionMT#"+in_string,"ExceptionLog_",dsmlogs);
						sendAlert("EtislateTF Receiver has been exited due to some error"+e);
						System.exit(0);
					}
						SLEEP(100);
				}
				else
				{
					Thread.sleep(1000);
				}

				
	   			} // while ends
			}
			catch(Exception e)
		  	{
		  		System.out.println(" ---SMPP Billing Application Stops------------ "+e);
		  		hungamalogging.log("RECEIVER#exceptonMT"+in_string,"ExceptionLog_",dsmlogs);
		  		sendAlert("Etislate Receiver has been exited due to some error"+e);
		  		System.exit(0);

			}
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
				}
				catch(Exception ex)
				{
					hungamalogging.log("RECEIVER#Exception in creating active  mq"+ex,"ExceptionLog_",dsmlogs);
					System.out.println("Exception in creatin new queue"+ex);
					sendAlert("Etislate Receiver has been exited due to some error"+ex);
					System.exit(0);
				}
				//System.out.println("inside smsssion"+message_sendx+"and "+producerx);
				final SMPPSession tmpSession = new SMPPSession(remoteIpAddress,remotePort, bindParam);
				tmpSession.setTransactionTimer(6000);
				tmpSession.addSessionStateListener(new SessionStateListenerImpl());
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
								log="#SourceAddr "+deliverSm.getSourceAddr()+"#DestAddress "+deliverSm.getDestAddress() +"#"+"DestAddrNpi "+deliverSm.getDestAddrNpi()+ "#DestAddrTon "+deliverSm.getDestAddrTon()+ "#EsmClass "+deliverSm.getEsmClass()+ "#OptionalParametes "+deliverSm.getOptionalParametes()+ "#RegisteredDelivery "+deliverSm.getRegisteredDelivery()+ "#ServiceType "+deliverSm.getServiceType()+ "SourceAddrNpi "+deliverSm.getSourceAddrNpi()+ "#SourceAddrTon "+deliverSm.getSourceAddrTon()+ "#SmDefaultMsgId "+deliverSm.getSmDefaultMsgId()+ "#CommandId "+deliverSm.getCommandId()+"#CommandStatus "+deliverSm.getCommandStatus()+ "#SequenceNumber "+deliverSm.getSequenceNumber()+"#Short Message>>"+smsg;
								//log1=log+ "\nDestAddrNpi          "+deliverSm.getDestAddrNpi()+ "#DestAddrTon   "+deliverSm.getDestAddrTon()+ "#EsmClass		 "+deliverSm.getEsmClass()+ "#OptionalParametes   "+deliverSm.getOptionalParametes()+ "\nRegisteredDelivery 	 "+deliverSm.getRegisteredDelivery()+ "\nServiceType     "+deliverSm.getServiceType()+ "\nSourceAddr 		 "+deliverSm.getSourceAddr()+ "\nSourceAddrNpi    "+deliverSm.getSourceAddrNpi()+ "\nSourceAddrTon 	 "+deliverSm.getSourceAddrTon()+ "\nSmDefaultMsgId 	 "+deliverSm.getSmDefaultMsgId()+ "\nCommandId "+deliverSm.getCommandId()+"\nCommandStatus	 "+deliverSm.getCommandStatus()+ "\nSequenceNumber "+deliverSm.getSequenceNumber();
								hungamalogging.log(log,"RECEIPTRECV_", dsmlogs);
								System.out.println("short message received"+smsg);
								System.out.println("printing recved message");
								System.out.println(log);

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
	    	try
			{
	    		gateway = new EtislatReceiverTF("10.71.128.47", 5020, new BindParameter(BindType.BIND_TRX, "provectus", "provec", null, TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
		    	for(int x=0;x<Integer.parseInt(args[0]);x++)
		    	{
		    		new EtislatReceiverTF();
		    	}
			}
			catch(Exception ex)
			{
				System.out.println("exception in thread main"+ex);
				sendAlert("Etislate Receiver has been exited due to some error"+ex);
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
