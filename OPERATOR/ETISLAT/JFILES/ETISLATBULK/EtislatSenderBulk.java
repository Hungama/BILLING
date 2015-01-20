//hiiiii stopped
//******* IMPORT PACKAGES *********
import hungamalogging.hungamalogging;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.ResourceBundle;
import java.sql.CallableStatement;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

//import com.mysql.jdbc.CallableStatement;
//********* THREAD RECHARGING _FM ***********************
public class EtislatSenderBulk extends Thread
{
    File mfile = new File("Check_Rechargeblk.lck");
    String dsmlogs="/home/ivr/javalogs/EtislatTF";
	public static Statement stmtFM,stmtFM1,stmtFM2,stmtDel,stmtupd,stmtBGM,stmtResubInfo;


	public static FileOutputStream outfile 		= null;
	public static PrintStream outprint			= null;
	public static int grace_count=0,grace_days=0;
	public static String da=null;
	Properties properties = new Properties();
	public int NO_OF_SERVER = 0;
	public	String DSN="";
	public	String USR="";
	public	String PWD="";
	public	String IP="";
    public boolean prerenewal_chk=true;
  	String strOutPut="",line="";
	static int count=1;
	int cnt=0;
	int cnt4=0;
	int iNewCount=0,iRetryCount=0,iUnsubCount=0;
	String strSMSText="";
	//================================================
	public static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	public Destination destination,destination_log;
	public Session session,session_log;
	public MessageConsumer consumer;
	public MessageProducer producer,producer_log;
	public TextMessage messageQ,message_log;
	javax.jms.Connection connection,connection_log;

//**************************************** MAKING DB CONNECTION ***********************************************************
	public EtislatSenderBulk()
	{
		try
		{
			System.out.println("**********************************************************");
			System.out.println("**     Thread Started With The Following Configuration  **");
			System.out.println("**              File to be Read is dbConfig.CFG          **");

			ResourceBundle resource = ResourceBundle.getBundle("config/chargingmgr_source");
			IP=resource.getString("IP");
			DSN=resource.getString("DSN");
			USR=resource.getString("USERNAME");
			PWD=resource.getString("PWD");
			System.out.println("** IP is  ["+IP+"] **  DSN is ["+DSN+"] Usr is ["+USR+"] Pwd is ["+PWD+"]\t**");
			System.out.println("**********************************************************");
			//sleep(1000*20);
		}
		catch(Exception e)
		{
			System.out.println("Exception while reading Recharge.cfg");
			e.printStackTrace();
			System.exit(0);
		}
	}


	private Connection dbConn()
	{
		while(true)
		{
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
			    Connection con = DriverManager.getConnection("jdbc:mysql://"+IP+"/"+DSN, USR, PWD);
				System.out.println("Database Connection established!");
				return con;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);
			}
		}
	}

//****************************************MAIN FUNCTION ********************************************************
	public static void main(String args[])
	{
		EtislatSenderBulk SMSSENDER = new EtislatSenderBulk();
		SMSSENDER.start();
	}
//****************************************RUN METHOD FOR STARTING THREAD ****************************************
	public void run()
	{
		try
		{
		    System.out.println("checking another instance");
		    check_multiple();
		    Connection con_readBillType = dbConn();
			System.out.println("connection is"+con_readBillType);
			stmtFM = con_readBillType.createStatement();
			CallableStatement cstmt = con_readBillType.prepareCall("{call etislat_hsep.create_smslog(?,?)}");
			try
			{
				ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
				connection = connectionFactory.createConnection();
				connection.start();
				session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
				destination = session.createQueue("ETISLATSENDQUEUETF");
				producer = session.createProducer(destination);
				messageQ = new ActiveMQTextMessage();
				System.out.println("SMSSEND Queue Connetion");
				//=================================================
			}
			catch(Exception e2)
			{
				System.out.println("Exception under Queue Connetion");
				hungamalogging.log("Sender#Creatingqueue#"+e2,"ExceptionLog_",dsmlogs);
				System.exit(0);
			}
		    while(true)
		    {
				String log=null;
		    	try
		    	{
		    		cnt=1;
					String STR,ani,message,dnis,date_time,etype;
					int msgid=0;
					STR = "select msg_id,ani,message,date_time,dnis,type from etislat_hsep.tbl_sms_bulk nolock where  date_time<= now() and status=0  and message is not null and length(message)<>0  order by date_time  limit 200 ";
					ResultSet rs = stmtFM.executeQuery(STR);
	                while(rs .next())
					{
						msgid = rs.getInt("msg_id");
						ani = rs.getString("ani").trim();
						message=rs.getString("message").trim();
						date_time = rs.getString("date_time");
						dnis=rs.getString("dnis").trim();
						etype=rs.getString("type").trim();
						log=msgid+"#"+ani+"#"+message+"#"+dnis+"#"+etype;
						hungamalogging.log(log,"Sender_", dsmlogs);
						System.out.println("Picked Mobile :"+ani+" type -> "+etype +" message "+message);
						messageQ.setText(msgid+"#"+ani+"#"+message+"#"+date_time+"#"+dnis+"#"+etype);
						producer.send(messageQ);
						cstmt.setInt(1,msgid);
						cstmt.setString(2,"tbl_sms_bulk");
						cstmt.execute();
						sleep(50);
					}
						sleep(30);
		    	}
		    	catch(Exception e)
		    	{
						log=e.toString();
						e.printStackTrace();
		    			System.out.println("Exception in sending message"+e);
		    			hungamalogging.log("sending mtmessge to recv#"+log,"ExceptionLog_", dsmlogs);
						sendAlert("Exception in etislat sender closed"+e);
		    			System.exit(0);

		    	}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			sendAlert("Exception in etislat sender closed"+e);
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

//#################################################### check dnd status METHOD  ############################################################

	 public void check_multiple()
	 {
		 try
		 {
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
//****************************************************  CODE END *********************************************************
