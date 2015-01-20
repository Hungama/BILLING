import hungamalogging.hungamalogging;
import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class EtislatdbloggerTF extends Thread
{
	public static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	public Destination destination,destinationid;
	public MessageProducer producerid;
	public Session session,sessionid;
	public MessageConsumer consumer,consumerid;
	public TextMessage message,messageid;
	javax.jms.Connection connection,connectionid;
    private static String subject = "";
	public static Connection con=null;
	public static Statement stmt,stmtUpdate;
	public String ip=null,dsn=null,username=null,pwd=null,msgqueue=null;
	public static String errPath="";
	public static ArrayList<String> keywordlist = new ArrayList<String >();
	String dsmlogs="/home/ivr/javalogs/EtislatTF";
	public static HashMap<String, String> serviceid = new HashMap<String, String>();
	public EtislatdbloggerTF()
	{
		try
		{
		    ResourceBundle resource = ResourceBundle.getBundle("config/chargingmgr_destination");
			ip=resource.getString("IP");
			dsn=resource.getString("DSN");
			username=resource.getString("USERNAME");
			pwd=resource.getString("PWD");
			subject=resource.getString("MSGQUEUE");
		    System.out.println("IP: "+ip+" DATABASE :"+dsn+" USER :"+username+" PWD:"+pwd);
		    System.out.println("starting new Thread");
			start();
		}
		catch(Exception e)
		{
			System.out.println("exceptino very first"+e);
			hungamalogging.log("dblogger creatingqueue#"+e,"ExceptionLog_",dsmlogs);
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
			}
			catch(Exception e)
			{
				System.out.println("exception inside doconn"+e);
				hungamalogging.log("dblogger making connection#"+e,"ExceptionLog_",dsmlogs);
				e.printStackTrace();
			}
		}
	}
	public void run()
	{
		System.out.println("inside run method");
		con=dbConn();
		System.out.println("Calling to updatemethods");
		updateBilling();
	}

	public void updateBilling()
	{

		String msisdn=null,dnis=null;
		String in_string="";
		String keyword;
		try
		{

			message = new ActiveMQTextMessage();
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		    connection = connectionFactory.createConnection();
		    connection.start();
		    session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
	        destination = session.createQueue(subject);
	        consumer = session.createConsumer(destination);
	        System.out.println("Active message Queue established!");
	    }
		catch(Exception e)
		{
			System.out.println("exception in run"+e);
			hungamalogging.log(e.toString(),"ExceptionLog_",dsmlogs);
			hungamalogging.log("dblogger creating queue#"+e,"ExceptionLog_",dsmlogs);
			e.printStackTrace();
		}
		while(true)
		{
			try
			{
				message = (TextMessage) consumer.receive();
				if(con==null || con.isClosed())
				{

					System.out.println("connectio  has been null");
					con=dbConn();
					System.out.println("now the connectin is"+con);
				}
		        if (message instanceof TextMessage)
		        {
	                TextMessage textMessage = (TextMessage) message;
	                in_string = textMessage.getText();
	                System.out.println(" DBlogger message '"+ in_string + "'");
	                hungamalogging.log(in_string,"dblogger_",dsmlogs);
	                String in_msg[] = in_string.split("#");
	                msisdn=in_msg[0];
	               	keyword     = in_msg[1];
					keyword=keyword.replaceAll(" ",""); //trim();
                                        keyword=keyword.replaceAll("\"","");
					keyword=keyword.toUpperCase();
					dnis=in_msg[2];
					System.out.println("dblogger kewords receive>>"+msisdn+keyword);
					if(validateKeyWord(keyword))
					{
						keyword=keyword.toUpperCase();

						try
						{
							CallableStatement cstmt=null;
							System.out.println("calling procedure MORecieved keyword");
							cstmt = con.prepareCall("{call etislat_hsep.mo_received(?,?)}");
							cstmt.setString(1, msisdn);
							cstmt.setString(2,keyword);
							cstmt.execute();
							cstmt.close();
						}
						catch(Exception ex)
						{
							System.out.println("Exception 6"+ex);
							hungamalogging.log("dblogger HELP#"+keyword+ex,"ExceptionLog_",dsmlogs);
						}


                        			if(keyword.equalsIgnoreCase("YOUTH"))
												{
													//Etislat_CheckActiveServices()
													CallableStatement cstmt=null;
													try
													{
														System.out.println("calling procedure Etislat_YOUTH");
														cstmt = con.prepareCall("{call etislat_hsep.YOUTH_Service(?,?)}");
														cstmt.setString(1, msisdn);
														cstmt.setString(2, dnis);
														cstmt.execute();
														cstmt.close();
													}
													catch(Exception ex)
													{
														System.out.println("Exception 1"+ex);
														cstmt.close();
														hungamalogging.log("dblogger HELP#"+keyword+ex,"ExceptionLog_",dsmlogs);
													}
						}
						else if(keyword.equalsIgnoreCase("HELP"))
						{
							//Etislat_CheckActiveServices()
							CallableStatement cstmt=null;
							try
							{
								System.out.println("calling procedure Etislat_HELP");
								cstmt = con.prepareCall("{call etislat_hsep.Help_Service(?,?)}");
								cstmt.setString(1, msisdn);
								cstmt.setString(2, "ALL");
								cstmt.execute();
								cstmt.close();
							}
							catch(Exception ex)
							{
								System.out.println("Exception 1"+ex);
								cstmt.close();
								hungamalogging.log("dblogger HELP#"+keyword+ex,"ExceptionLog_",dsmlogs);
							}
						}
						else if(keyword.contains("HELP"))
						{
							CallableStatement cstmt=null;
							try
							{
								String sertoHELP=keyword.substring(keyword.indexOf("HELP")+4);
								System.out.println("value of sertoHELP"+sertoHELP);
								sertoHELP=sertoHELP.trim();
								System.out.println("calling procedure HELPkeyword"+sertoHELP);
								cstmt = con.prepareCall("{call etislat_hsep.Help_Service(?,?)}");
								cstmt.setString(1, msisdn);
								cstmt.setString(2, sertoHELP);
								cstmt.execute();
								cstmt.close();
							}
							catch(Exception ex)
							{

								System.out.println("Exception 2"+ex);
								cstmt.close();
								hungamalogging.log("dblogger HELP#"+keyword+ex,"ExceptionLog_",dsmlogs);
							}

						}
						else if(keyword.trim().toLowerCase().contains("stop all")  || keyword.trim().toLowerCase().contains("stopall"))
						{
							 CallableStatement cstmt=null;
                                                        try
                                                        {
                                                                System.out.println("calling procedure ETI_STOPALL");
	                                                        cstmt = con.prepareCall("{call etislat_hsep.ETI_STOPALL(?,?)}");
                                                                cstmt.setString(1, msisdn);
								cstmt.setString(2, "SMS");
                                                                cstmt.execute();
                                                                cstmt.close();
                                                        }
                                                        catch(Exception ex)
                                                        {

                                                                System.out.println("Exception ETI_STOPALL i"+ex);
                                                                cstmt.close();
                                                                hungamalogging.log("dblogger keyword#"+keyword+"#"+ex,"ExceptionLog_",dsmlogs);
                                                        }
	
						}
						else if(keyword.equalsIgnoreCase("STOP"))
						{
							//Etislat_CheckActiveServices()
							CallableStatement cstmt=null;
							try
							{
								System.out.println("calling procedure Etislat_CheckActiveServices");
								cstmt = con.prepareCall("{call etislat_hsep.Etislat_CheckActiveServices(?)}");
								cstmt.setString(1, msisdn);
								cstmt.execute();
								cstmt.close();
							}
							catch(Exception ex)
							{

								System.out.println("Exception 3"+ex);
								cstmt.close();
								hungamalogging.log("dblogger keyword#"+keyword+"#"+ex,"ExceptionLog_",dsmlogs);
							}
						}
						else if(keyword.contains("STOP"))
						{
							CallableStatement cstmt=null;
							try
							{
								String sertostop=keyword.substring(keyword.indexOf("STOP")+4);
								System.out.println("value of sertostop"+sertostop);
								sertostop=sertostop.trim();
								if(sertostop.equalsIgnoreCase("JOKES")|| sertostop.equalsIgnoreCase("JOKE"))
								{
									System.out.println("calling procedure JOKESUNSUB");
									cstmt = con.prepareCall("{call etislat_hsep.JOKES_UNSUB(?,?)}");
								}
								else if(sertostop.equalsIgnoreCase("HNP"))
								{
									System.out.println("calling procedure HOLLYWOOD_UNSUB");
									cstmt = con.prepareCall("{call etislat_hsep.HOLLYWOOD_UNSUB(?,?)}");
								}
								else if(sertostop.equalsIgnoreCase("FNP"))
								{
									System.out.println("calling procedure FUNNEWSUNSUB");
									cstmt = con.prepareCall("{call etislat_hsep.FUNNEWS_UNSUB(?,?)}");

								}
								else if(sertostop.equalsIgnoreCase("SFP"))
								{
									System.out.println("calling procedure SFPUNSUB");
									cstmt = con.prepareCall("{call etislat_hsep.SFP_UNSUB(?,?)}");
								}
								else if(sertostop.equalsIgnoreCase("EPL"))
								{
									System.out.println("calling procedure EPLUNSUB");
									cstmt = con.prepareCall("{call etislat_hsep.EPL_UNSUB(?,?)}");

								}
								 else if(sertostop.equalsIgnoreCase("LSP"))
                                                                {
                                                                        System.out.println("calling procedure LSPUNSUB");
                                                                        cstmt = con.prepareCall("{call etislat_hsep.LSP_UNSUB(?,?)}");

                                                                }
								 else if(sertostop.equalsIgnoreCase("MOT"))
                                                                {
                                                                        System.out.println("calling procedure MOTUNSUB");
                                                                        cstmt = con.prepareCall("{call etislat_hsep.MOT_UNSUB(?,?)}");

                                                                }
								else if(sertostop.equalsIgnoreCase("CAREER"))
                                                                {
                                                                        System.out.println("calling procedure CCPUNSUB");
                                                                        cstmt = con.prepareCall("{call etislat_hsep.CCP_UNSUB(?,?)}");
                                                                }
								else if(sertostop.equalsIgnoreCase("ANXIETY"))
                                                                {
                                                                        System.out.println("calling procedure DAPUNSUB");
                                                                        cstmt = con.prepareCall("{call etislat_hsep.DAP_UNSUB(?,?)}");
                                                                }
								else if(sertostop.equalsIgnoreCase("HYGIENE"))
                                                                {
                                                                        System.out.println("calling procedure HBPUNSUB");
                                                                        cstmt = con.prepareCall("{call etislat_hsep.HBP_UNSUB(?,?)}");
                                                                }

								else if(sertostop.equalsIgnoreCase("ASTRO"))
								{
									System.out.println("calling procedure ASTROUNSUB");
									cstmt = con.prepareCall("{call etislat_hsep.ASTRO_UNSUB(?,?)}");
								}
								System.out.println("call unsub proc"+msisdn);
							//	System.out.println("value of collable"+cstmt);
								cstmt.setString(1, msisdn);
								cstmt.setString(2, "SMS");
								System.out.println("value of collable"+cstmt);
								cstmt.execute();
								cstmt.close();

							}
							catch(Exception ex)
							{
								System.out.println("Exception 4"+ex);
								hungamalogging.log("dblogger #"+keyword+"#"+ex,"ExceptionLog_",dsmlogs);
							}

						}
						else
						{
							CallableStatement cstmt=null;
							try
							{
								if(keyword.contains("ASTRO"))
								{
									System.out.println("calling procedure ASTROSUB");
									cstmt = con.prepareCall("{call etislat_hsep.ASTRO_SUB(?,?,?,?,?,?,?)}");
									String sign=keyword.substring(keyword.indexOf("ASTRO")+5);
									cstmt.setString(7, sign);

								}
								else if(keyword.equalsIgnoreCase("JOKES"))
								{
									//JOKES_SUB`(in IN_ANI VARCHAR(16),in IN_MOD VARCHAR(10),in IN_DNIS varchar(30),in IN_SID int,in IN_PID int)
									System.out.println("calling procedure JOKESSUB");
									cstmt = con.prepareCall("{call etislat_hsep.JOKES_SUB(?,?,?,?,?,?)}");

								}
								else if(keyword.equalsIgnoreCase("HNP"))
								{
									System.out.println("calling procedure HNPSUB");
									cstmt = con.prepareCall("{call etislat_hsep.HOLLYWOOD_SUB(?,?,?,?,?,?)}");
								}
								else if(keyword.equalsIgnoreCase("FNP"))
								{
									System.out.println("calling procedure FNPSUB ");
									cstmt = con.prepareCall("{call etislat_hsep.FUNNEWS_SUB(?,?,?,?,?,?)}");

								}
								else if(keyword.equalsIgnoreCase("SFP"))
								{
									System.out.println("calling procedure SFPSUB ");
									cstmt = con.prepareCall("{call etislat_hsep.SFP_SUB(?,?,?,?,?,?)}");
								}
								else if(keyword.equalsIgnoreCase("EPL"))
								{
									System.out.println("calling procedure EPLSUB ");
									cstmt = con.prepareCall("{call etislat_hsep.EPL_SUB(?,?,?,?,?,?)}");

								}
                                                                else if(keyword.equalsIgnoreCase("LSP"))
                                                                {
                                                                        System.out.println("calling procedure LSPSUB ");
                                                                        cstmt = con.prepareCall("{call etislat_hsep.LSP_SUB(?,?,?,?,?,?)}");

                                                                }
								else if(keyword.equalsIgnoreCase("MOT"))
                                                                {
                                                                        System.out.println("calling procedure MOTSUB ");
                                                                        cstmt = con.prepareCall("{call etislat_hsep.MOT_SUB(?,?,?,?,?,?)}");

                                                                }
								else if(keyword.equalsIgnoreCase("CAREER"))
                                                                {
                                                                        System.out.println("calling procedure CCPSUB ");
                                                                        cstmt = con.prepareCall("{call etislat_hsep.CCP_SUB(?,?,?,?,?,?)}");

                                                                }
								else if(keyword.equalsIgnoreCase("ANXIETY"))
                                                                {
                                                                        System.out.println("calling procedure DAPSUB ");
                                                                        cstmt = con.prepareCall("{call etislat_hsep.DAP_SUB(?,?,?,?,?,?)}");

                                                                }
								else if(keyword.equalsIgnoreCase("HYGIENE"))
                                                                {
                                                                        System.out.println("calling procedure HBPSUB ");
                                                                        cstmt = con.prepareCall("{call etislat_hsep.HBP_SUB(?,?,?,?,?,?)}");
                                                                }
								cstmt.setString(1, msisdn);
								/*if(keyword.trim().equalsIgnoreCase("HYGIENE")||keyword.trim().equalsIgnoreCase("ANXIETY")||keyword.trim().equalsIgnoreCase("CAREER"))
									cstmt.setString(2, "SMS");
								else*/
								cstmt.setString(2, "TNB");
								cstmt.setString(3, dnis);
								cstmt.setString(4, "2121");
								cstmt.setString(5, serviceid.get(keyword));
								cstmt.setInt(6,0);
                                                                System.out.println("value of collable"+cstmt);
								cstmt.execute();
								cstmt.close();
							}
							catch(Exception ex)
							{

								System.out.println("Exception 5"+ex);
								try{cstmt.close();}catch(Exception e1){}
								hungamalogging.log("dblogger HELP#"+keyword+ex,"ExceptionLog_",dsmlogs);
							}
						}

					}
					else
					{
						CallableStatement cstmt=null;
						try
						{

							System.out.println("calling procedure invalid keyword");
							cstmt = con.prepareCall("{call etislat_hsep.invalid_keyword(?,?)}");
							cstmt.setString(1, msisdn);
							cstmt.setString(2,keyword);
							cstmt.execute();
							cstmt.close();
						}
						catch(Exception ex)
						{
							System.out.println("Exception 6"+ex);
							cstmt.close();
							hungamalogging.log("dblogger HELP#"+keyword+ex,"ExceptionLog_",dsmlogs);
						}
					}


		        }//if message ends
			}//try ends
			catch(Exception e)
			{
				//cstmst.close();
				System.out.println("exception in outer try"+e);
				hungamalogging.log(""+e,"ExceptionLog_",dsmlogs);
				e.printStackTrace();
				sendAlert("EtislatdbloggerTf exited");
				System.exit(0);

			}
		}

	}
	public static void loadKeyWord()
	{

		try
		{
			System.out.println("insided load keyword");
			File f = new File("config/keywords.conf");
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String str=null;
			while ((str = br.readLine()) != null)
			{
				keywordlist.add(str.replaceAll(" ",""));
			}
			for(int x=0;x<keywordlist.size();x++)
			{
				System.out.println("keyword is"+keywordlist.get(x));
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception in loading keyword"+ex);
			System.exit(0);
		}
	}
	public static void loadserviceid()
	{
		try
		{
			File f = new File("config/ServiceId.conf");
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String str=null;
			while ((str = br.readLine()) != null)
			{
				serviceid.put(str.substring(0,str.indexOf("#")), str.substring(str.indexOf("#")+1));
			}
			br.close();
			fr.close();

		}
		catch(Exception ex)
		{
			System.out.println("Exception in loading messages"+ex);
			String dsmlogs="/home/ivr/javalogs/EtislatTF";
			hungamalogging.log("dblogger loading serviceid#"+ex,"ExceptionLog_",dsmlogs);
			System.exit(0);
		}
	}
		public static  boolean  validateKeyWord(String word)
		{
			try
			{
				if(!word.toUpperCase().startsWith("STOP"))
				{
					word=word.toUpperCase();
				}

				word=word.trim();
				//word=word.replaceAll(" ","");

				if(keywordlist.contains(word))
					return true;
				else
					return false;
			}
			catch(Exception ex)
			{
				String dsmlogs="/home/ivr/javalogs/EtislatTF";
				hungamalogging.log("dblogger validating keyword#"+ex,"ExceptionLog_",dsmlogs);
				return false;
			}
		}

	public static void main(String args[])
	{
		try
		{
			//new EtislatdbloggerTF("1");
			//sleep(100);
			loadKeyWord();
			loadserviceid();
			new EtislatdbloggerTF();

			sleep(100);
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
            String _marr[]={"8588838347","8586968481","8586967042","8587800614"};
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
