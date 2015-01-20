import hungamalogging.hungamalogging;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
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
import java.math.BigInteger;
//import java.text.*;
public class EtislatReceiverTF_TRANS extends Thread implements  Gateway
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
	public static int exitCounter=0;
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
	EtislatReceiverTF_TRANS globobj =null;
    static int temp=0;

		// =======================================================
		public EtislatReceiverTF_TRANS(final String remoteIpAddress,final int remotePort, final BindParameter bindParam) throws IOException
		{
			EtislatReceiverTF_TRANS.remoteIpAddress = remoteIpAddress;
			EtislatReceiverTF_TRANS.remotePort = remotePort;
			EtislatReceiverTF_TRANS.bindParam = bindParam;
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
	        public static void check_multiple()
        	{
                	 try
                 	{
                        	 File mfile = new File("Check_Recharge.lck");
                         	if(mfile.exists())
                         	{
                                	 System.out.println(" WARNING !!! ANOTHER PROGRAM IS RUNNING !!!!!");
                                	 System.exit(0);
                        	 }
                        	 else
                         	{
                                	 mfile.createNewFile();
                        	 }
                	 }
               		  catch(Exception me)
                	 {
                        	 System.out.println("Exception occur in check_multiple function"+me);
                	 }
        	 }

			/*********************************************** Void Main ************************************************/
			public static void main(String[] args) throws IOException
			{
					/*try
					{
						check_multiple();
					}
					catch(Exception eee)
					{
						eee.printStackTrace();
						System.out.println("exception in checking multiple instance"+eee);
					}*/
					int status=CheckProcess.GetProcessList("EtislatReceiverTF_TRANS",1);
               				if(status>1)
                			{
			                        System.out.println("Process already running ......");
                        			System.exit(0);
               				}
					gateway = new EtislatReceiverTF_TRANS("10.71.128.47", 5020, new BindParameter(BindType.BIND_TX, "provectus", "provec", null, TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
/*
					System.out.println("exception in thread main"+ex);
					sendAlert("Etislate Receiver has been exited due to some error"+ex);

					SLEEP(1000); continue;
*/						//System.exit(0);
					Init_Queue();initDB();
					try
					{
						dbConn().createStatement().executeUpdate("update etislat_hsep.tbl_sms_bulk set status=0 where status=2");
					    dbConn().createStatement().executeUpdate("update etislat_hsep.tbl_sms set status=0 where status=2");
					}
					catch(Exception e)
					{
						System.out.println("Excption while updating previous records "+e);
						e.printStackTrace();
					}
					ExecutorService execService = Executors.newFixedThreadPool(40);
					System.out.println("******************** Starting Apps ******************************");

					int TPS=10;
					try{ TPS=Integer.parseInt(args[0]);}catch(Exception er){TPS=20; }
					 try{ temp=Integer.parseInt(args[1]);}catch(Exception er){temp=0; }

				// ==============LOGGING=============================
				while (true)
				{
					//int TPS_Count=0;
					exitCounter++;
					if(exitCounter>10000 && OUT_QUE.size()==0)
						System.exit(0);
					java.util.Date dt=new java.util.Date(System.currentTimeMillis() - (int)(4.5 * 60 * 60 * 1000));
		//							Date dt = new Date(System.currentTimeMillis() - (int)(4.5 * 60 * 60 * 1000));
					//if((dt.getHours()>=10 )&& dt.getHours()<=19)
		//			{
					System.out.println("******************** Correct Timing ************************");
					//	for(int I=0;I<10;I++)
					//	{
							//if(!((dt.getHours()>=10 )&& dt.getHours()<=19))
							//	break;
							//if(OUT_QUE.size()>0)
							//	break;
								PUSH_APP("tbl_sms");
							if(((dt.getHours()>=7 )&& dt.getHours()<=19))
								PUSH_APP("tbl_sms_bulk");
						        else
							System.out.println("*************************************************** OUT of Window No Promotional SMS Send *********************************");
					//	}
					int qsize= OUT_QUE.size();
						if(OUT_QUE.size()==0)
						{
							SLEEP(1000);
							continue;
						}
			//				int qsize= OUT_QUE.size();
						for (int I = 0; I <qsize; I++)
						{
					//		if(!((dt.getHours()>=10 )&& dt.getHours()<=19))
					//			break;
							if(OUT_QUE.size()==0)
							{ break;}
							try{String s=OUT_QUE.get(0);}catch(Exception e){break;}
							final String in_string_temp = OUT_QUE.get(0);
							OUT_QUE.remove(0); SLEEP(1000/TPS);
							execService.execute(new Runnable()
							{
								public void run()
								{
										//System.out.println("=============HI i m in run method===========");
										String in_string=in_string_temp ;
										String message_id="",table_name="",msisdn="",msg_to_send="",date_time="",dnis="",etype="";
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
											table_name=in_msg[6];
											//dnis="38567";
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
//											messageId = gateway.submitShortMessage("CPT", TypeOfNumber.UNKNOWN , NumberingPlanIndicator.UNKNOWN,dnis , TypeOfNumber.UNKNOWN , NumberingPlanIndicator.UNKNOWN, msisdn, new ESMClass(0), (byte)0, (byte)0,null, null, new RegisteredDelivery(1), (byte)0, new GeneralDataCoding(0), (byte)0,msg_to_send.getBytes());

 OptionalParameter opr1[]= new OptionalParameter[1];
 opr1[0] = new OptionalParameter.COctetString((short)0x0424,hexStringToByteArray(toHex(msg_to_send)));
//System.out.println("convert into byte Array  "+opr1[0]);
messageId = gateway.submitShortMessage("CPT", TypeOfNumber.UNKNOWN , NumberingPlanIndicator.UNKNOWN,dnis , TypeOfNumber.UNKNOWN , NumberingPlanIndicator.UNKNOWN, msisdn, new ESMClass(0), (byte)0, (byte)0,null, "000001000000000R", new RegisteredDelivery(0), (byte)0, new GeneralDataCoding(8), (byte)0,"".getBytes(),opr1);
											System.out.println("msgid recieved befor convert"+messageId);
											in_string=in_string+"#"+messageId;
										//	messageId=new Integer(Integer.parseInt(messageId.toString(),16)).toString();
messageId=new Long(Long.parseLong(messageId.toString(),16)).toString();
	in_string=in_string+"#"+messageId;
											System.out.println("\nMessage submitted msisdn="+msisdn+",message_id is " + messageId);
											hungamalogging.log(in_string,"MTSend_",dsmlogs);
										}
										catch (ResponseTimeoutException e)
										{
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
											String out=msisdn+"#"+ex1+"#ResponseTimeoutException#"+message_id;
											try
											{
													message_sendx2.setText(out);
													System.out.println("message setted for logging");
													producerx2.send(message_sendx2,2,9,100000000);
											}
											catch (JMSException e1)
											{
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
									String out=msisdn+"#"+ex1+"#PDUException#"+message_id;
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
									String out=msisdn+"#"+ex1+"#InvalidResponse#"+message_id;
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
									String ex2=null;
									try
									{
									ex1=e.toString();
									ex1=ex1.substring(0,ex1.indexOf("found")-1);
									ex1=ex1.substring(ex1.lastIndexOf(" ")+1,ex1.length());
									ex1=ex1.trim();
									ex2=e.getCommandStatus()+"";
									}
									catch(Exception p)
									{
									System.out.println("Exception in substring negative respone"+p);
									ex1="-1";
									}
									String out=msisdn+"#"+ex1+"#NegativeResponse#"+message_id;
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
									hungamalogging.log("RECEIVER#Negative responseMT#"+in_string+"#"+ex2,"NRExceptionLog_",dsmlogs);
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
										UPDATE_DB(message_id,messageId,table_name);
										//SLEEP(100);
									}

					} ); // Thread End
			} // End For Loop

			System.out.println("*************** Ready to pick Next Set  ***********************");
		//	}
			/*else
			{
			SLEEP(10000);System.out.println("*************** SLEEPING OUT OF WINDOW ***********************");
			}*/
			} // while ends
	    }

private static byte[] hexStringToByteArray(final String ENCODED) {
   String encoded=ENCODED;
   if ((encoded.length() % 2) != 0)
        encoded=encoded+"0";
        //throw new IllegalArgumentException("Input string must contain an even number of characters");

    final byte result[] = new byte[encoded.length()/2];
    final char enc[] = encoded.toCharArray();
    for (int i = 0; i < enc.length; i += 2) {
        StringBuilder curr = new StringBuilder(2);
        curr.append(enc[i]).append(enc[i + 1]);
		//System.out.println("value of curr is ----------->>>>>>>>>"+curr);
        result[i/2] = (byte) Integer.parseInt(curr.toString().replaceAll("-", ""), 16);
        //System.out.println("value of result is ----------->>>>>>>>>"+result[i/2]);
    }
    return result;
}

public static String toHex(String str) {
	StringBuffer ostr = new StringBuffer();
    try {

		for(int i=0; i<str.length(); i++)
		{
			String ch = ""+str.charAt(i);

			ostr.append(String.format("%04x", new BigInteger(ch.getBytes("UTF-8"))));
		}
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return ostr.toString();
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
                String _marr[]={"8588838347","8586968481","8586967042","8587800614","9811795244","8586968485"};
				//String _marr[]={"1234567890"};
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
					qquery="SELECT msg_id,ani,message,date_time,dnis,TYPE FROM etislat_hsep.tbl_sms nolock WHERE date_time<= NOW() AND STATUS=0 AND message IS NOT NULL AND LENGTH(message)<>0 ORDER BY date_time LIMIT 1000";
					//qquery="select msg_id,ani,message,date_time,dnis,type from etislat_hsep.tbl_sms nolock where  date_time<= now() and status=0  and message is not null and length(message)<>0 and (date(now()) not  in('2014-03-01') or hour(now()) not in (12,13,14,15,16,17,18,19,20,21)) order by date_time  limit 1000";
				else
					qquery="SELECT msg_id,ani,message,date_time,dnis,TYPE FROM etislat_hsep.tbl_sms_bulk nolock WHERE STATUS=0 AND message IS NOT NULL AND LENGTH(message)<>0 AND TIME(NOW())>'11:30' AND DATE(date_time)=DATE(NOW()) ORDER BY date_time ASC,msg_id DESC LIMIT 500";
//qquery = "select msg_id,ani,message,date_time,dnis,type from etislat_hsep.tbl_sms_bulk nolock where status=0  and message is not null and length(message)<>0 and time(now())>'12:30' and date_time<= now() and date(date_time)=date(now()) and (date(now()) not  in('2014-03-01') or hour(now()) not in (12,13,14,15,16,17,18,19,20,21))  order by date_time asc,msg_id desc limit 5000 ";

//				    qquery = "select msg_id,ani,message,date_time,dnis,type from etislat_hsep.tbl_sms_bulk nolock where date(date_time)=date(subtime(now(),'04:30:00')) and date_time<= now() and  status=0  and message is not null and length(message)<>0 and time(now())>'14:30' order by temp desc limit 1000 ";
            	System.out.println("Running QUery "+qquery);
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
							String log="ETIS_SENDER#"+msgid+"#"+ani+"#"+message+"#"+date_time+"#"+dnis+"#"+etype+"#ETIS#ETIS#0#0";
							//System.out.println("Picked Mobile :"+ani+" type -> "+etype +" message "+message);
							String out_string=msgid+"#"+ani+"#"+message+"#"+date_time+"#"+dnis+"#"+etype+"#"+table_name;
						System.out.println("Pushed String - "+out_string);
							int dndcount=0;
								if(table_name.equalsIgnoreCase("tbl_sms_bulk") && checkDND(ani)>0)
								{
									hungamalogging.log(log+"#DND","Sender_", dsmlogs);
									dbConn().createStatement().executeUpdate("delete from etislat_hsep."+table_name+" where  msg_id="+msgid);
								}
								else
								{
									hungamalogging.log(log,"Sender_", dsmlogs);
									OUT_QUE.add(out_string);
							        dbConn().createStatement().executeUpdate("update etislat_hsep."+table_name+" set status=2 where  msg_id="+msgid);
								}
						//		Thread.sleep(5000);
							}
							catch(Exception e)
							{
					             e.printStackTrace();
							}
						}
					System.out.println("***************Completed QUery *********************");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			} // function ends

		public static int checkDND(String msisdn)
		{
			int count=0;
			try {
//				PreparedStatement pstmt=dbConn().prepareStatement("select count(1) cnt  from tbl_etislat_dnd where ani=case when (length(msisdn)=13)then msisdn else concat('234',msisdn) end");

   PreparedStatement pstmt=dbConn().prepareStatement("select count(1) cnt  from etislat_hsep.tbl_etislat_dnd where ani=case when (length(?)=13)then ? else concat('234',?) end");

				pstmt.setString(1, msisdn+"");
				 pstmt.setString(2, msisdn+"");
				 pstmt.setString(3, msisdn+"");

				ResultSet rs=pstmt.executeQuery();
				while(rs.next())
					count=rs.getInt("cnt");
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return count;
		}
		public static void UPDATE_DB(String message_id,String messageId,String table_name)
		{
			try
			{
				CallableStatement cstmt = dbConn().prepareCall("{call etislat_hsep.create_smslog_new(?,?,?)}");
				cstmt.setInt(1,Integer.parseInt(message_id));
				cstmt.setString(2,messageId);
				cstmt.setString(3,table_name);
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
