import hungamalogging.hungamalogging;
//import checkProcess.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

	public class EtislatSender extends Thread
	{
		public static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
		public Destination destination,destinationQ;
		public Session session,sessionQ;
		public MessageProducer producer,producerQ;
		public TextMessage message,messageQ;
		javax.jms.Connection connection,connectionQ;
		private static String subject = "";
		public static Connection con=null;
		public static Statement stmt,stmtUpdate;
		public static CallableStatement cstmt=null;
		public static String  ip=null,dsn=null,username=null,pwd=null;
		public static File dir=null;
		public static String strdate  = "",mnthdir="";
		public static String strtime  = "",Path="";
		public static Calendar today = null;
		public int _priority=1;
		String dsmlogs="/home/ivr/javalogs/Etislat/",msgqueue=null;;

		static HashMap<String,String> hashMap= new HashMap<String,String>(16,.5f);
		public EtislatSender()
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
					System.out.println("insert in while Database Connection established!");
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
	public void readBillType()
	{
		try
		{
			dbConn();
			String qquery = "select plan_id,s_id,type_of_plan,fall_back_seqs from tbl_plan_bank;";
			qquery=qquery.trim();
			Thread.sleep(1000);
			Statement stmt= con.createStatement();
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
						e.printStackTrace();
					}
				}
			}
			stmt.close();
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
			sendAlert("etislatsender exited pls check error"+e1);
			System.exit(0);
		}
	}
	public void run()
	{
		 String out_string=null;

		try
		{


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
			System.out.println("exception in creating queue");
			e.printStackTrace();
			sendAlert("etislatsender exited pls check error"+e);
			System.exit(0);
		}
		try
		{
			Statement stmt = con.createStatement();
			Statement stmtUpdate = con.createStatement();
			while(true)
			{
				//String qquery = "select billing_ID,msisdn,event_type,amount,service_id,operator,circle,plan_id,MODE,subservice_id,sc from master_db.tbl_billing_reqs nolock where status=0 and service_id='2121'  and event_type in ('SUB','RESUB') and (date(now()) not  in('2014-03-01') or hour(now()) not in (12,13,14,15,16,17,18,19,20,21)) order by date_time asc limit 0,500";

				 String qquery = "select billing_ID,msisdn,event_type,amount,service_id,operator,circle,plan_id,MODE,subservice_id,sc from master_db.tbl_billing_reqs nolock where status=0 and service_id='2121'  and event_type in ('SUB','RESUB') limit 0,500";
				 qquery=qquery.trim();
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
							String dnis=rs.getString("sc");
							out_string = null;
							String key=plan_id+"-"+service_id;
							if(hashMap.get(key)!=null)
							{
								out_string = billing_ID+"#"+msisdn+"#"+event_type+"#"+amount+"#"+service_id+"#"+operator+"#"+circle+"#"+plan_id+"#"+hashMap.get(key)+"#"+in_mode+"#"+dnis;
								System.out.println(out_string);
								if(event_type.equalsIgnoreCase("RESUB"))
									_priority=3;
								else
									_priority=9;
							//message.setText(out_string);
							 	if(circle==null)
							 		circle="UND";
							 	message.setText(out_string);
							 	producer.send(message,2,_priority,100000000);//2=PERSISTENT,1=NON PERSISTENT
							 	hungamalogging.log(out_string,"Sender_",dsmlogs);
							 	stmtUpdate.executeUpdate("update tbl_billing_reqs set status=2 where billing_ID='"+billing_ID+"'");
							 	Thread.sleep(250);
							}
							else
							{
								System.out.println("Here is else hashMap is null"+ msisdn);
								stmtUpdate.executeUpdate("update tbl_billing_reqs set status=-1 where billing_ID='"+billing_ID+"'");
								Thread.sleep(150);
							}

						}
						catch(Exception e)
						{
							hungamalogging.log("sender while loop #"+out_string+"#"+e,"ExceptionLog_", dsmlogs);
							try
							{
								if(con==null || con.isClosed() )
								{
									dbConn();
									stmt = con.createStatement();
									stmtUpdate = con.createStatement();
								}
								e.printStackTrace();
							}
							catch(Exception ex)
							{
								System.exit(0);
							}
						}
					}//while rs  ends
				 }//if ends
			}//while ends

		}//try ends
		catch(Exception e)
		{
			System.out.println(e.toString());
			hungamalogging.log("sender #"+out_string+"#"+e,"ExceptionLog_", dsmlogs);
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
//========================================================
	public static void main(String args[])
	{
		try
		{
			int status=CheckProcess.GetProcessList("EtislatSender",1);
			if(status>1)
			 {
					System.out.println("Process already running ......");
					System.exit(0);
			}
		}
		catch(Exception exx){exx.printStackTrace();}
		
		
		try
		{
			//check_multiple();
			//Thread.sleep(500000);
			EtislatSender sBM = new EtislatSender();
			sBM.readBillType();
			sBM.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
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

}

