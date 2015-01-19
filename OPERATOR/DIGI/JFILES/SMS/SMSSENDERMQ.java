 /*########################################################################################################################
##									PROGRAME 	: CRBT RNG REQUEST READER 												##
##									PURPOSE  	: ONLINE RECHARGING														##
##									ACCOUNT  	: HUNGAMA 																##
##									DEVELOPED BY: TECHNICAL TEAM														##
##									DATE		: 25th April 2011														##
##########################################################################################################################*/


//******* IMPORT PACKAGES *********
import java.sql.*;
import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.Date;
import java.util.ResourceBundle;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//======================================
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;



//********* THREAD RECHARGING _FM ***********************
public class SMSSENDERMQ extends Thread
{
    File mfile = new File("Check_Recharge.lck");
    //public static DBConnection con6300			= null;
	//public static Connection con=null, con2 = null,con3 = null,con4=null,con5=null,con6=null,con7=null;
	public static Statement stmtFM,stmtFM1,stmtFM2,stmtDel,stmtupd,stmtBGM,stmtResubInfo;
	public static FileOutputStream outfile 		= null;
	public static PrintStream outprint			= null;
	public static int grace_count=0,grace_days=0;
	public static String da=null;
	Properties properties = new Properties();
	static String FilePath = "./Service.cfg";
	public int NO_OF_SERVER = 0;
	static String[] SUBSCRIPTION_TABLE = new String[10];
	static String[] RESUB_PROCEDURE = new String[10];
	static String[] GRACE_PERIOD = new String[10];
	static String[] PRERENEWAL_MESSAGE =new String[10];
	static String[] UNSUB_PROCEDURE = new String[10];
	static String[] DNIS_ARRY = new String[10];
	static String[] PRERENEWAL_PERIOD =new String[10];
	static String[] PRERENEWAL_PROCEDURE =new String[10];
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


	public SMSSENDERMQ()
	{
		try
		{
			System.out.println("**********************************************************");
			System.out.println("**     Thread Started With The Following Configuration  **");
			System.out.println("**              File to be Read is dbConfig.CFG          **");

			ResourceBundle resource = ResourceBundle.getBundle("config/dbConfig");
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
			}
		}
	}
//****************************************MAIN FUNCTION ********************************************************
	public static void main(String args[])
	{
		SMSSENDERMQ SMSSENDER = new SMSSENDERMQ();
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
			stmtFM = con_readBillType.createStatement();
			stmtBGM = con_readBillType.createStatement();
			try
			{
				ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
				connection = connectionFactory.createConnection();
				connection.start();
				session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
				destination = session.createQueue("SMSQ");
				producer = session.createProducer(destination);
				messageQ = new ActiveMQTextMessage();
				System.out.println("SMSQ Queue Connetion");
				//=================================================
				connection_log = connectionFactory.createConnection();
				connection_log.start();
				session_log = connection_log.createSession(false,Session.AUTO_ACKNOWLEDGE);
				destination_log = session.createQueue("HUNLOG");
				producer_log = session_log.createProducer(destination_log);
				message_log = new ActiveMQTextMessage();
				System.out.println("HUNLOG Queue Connetion");
			}
			catch(Exception e2)
			{
				System.out.println("Exception under Queue Connetion");
				System.exit(0);

			}

		    while(true)
		    {
		    	try
		    	{

		    		cnt=1;
					String STR,ani,message,dnis,date_time,response_time,orignal_msg,smsURL,amount,msg_type;
					String rndPIN="",txtMSG="";
					int msgid=0,status;
					stmtBGM.executeUpdate("delete from tbl_new_sms where message is null");
					 STR = "select msgid,ani,message,date_time,dnis,amount,type,status from tbl_new_sms where status=0 and message is not null order by date_time asc";
					ResultSet rs = stmtFM.executeQuery(STR);
	                while(rs .next())
					{
						msgid = rs.getInt("msgid");
						ani = rs.getString("ani").trim();
						message=rs.getString("message").trim();
						orignal_msg = message;

						date_time = rs.getString("date_time");
						dnis=rs.getString("dnis").trim();
						amount=rs.getString("amount").trim();
						msg_type=rs.getString("type");
						status = rs.getInt("status");
						System.out.println("Picked Mobile :"+ani+" message "+orignal_msg+ " amount "+amount);

						log("SMSSender#"+msgid+"#"+ani+"#"+orignal_msg+"#"+date_time+"#"+dnis+"#"+msg_type+"#DIGM#DIG#"+status+"#NoDND#");

						messageQ.setText(msgid+"#"+ani+"#"+message+"#"+date_time+"#"+dnis+"#"+amount);
						producer.send(messageQ,2,9,10000000);
						stmtBGM.executeUpdate("delete from tbl_new_sms where msgid='"+msgid+"'");
						sleep(30);

					}

						//rs.close();
						sleep(20);
		    	}
		    	catch(Exception e)
		    	{
		    		try
					{
						if(e.toString().startsWith("com.mysql.jdbc.CommunicationsException:"))
						{
							System.out.println("DB Connectivity Failure!!! Retries to connect DB");
							Thread.sleep(10000);
							Connection con2 =dbConn();
							stmtFM = con2.createStatement();
							stmtBGM = con2.createStatement();
						}

					}catch(Exception e1)
					{
						e1.printStackTrace();
						System.exit(0);
					}
		    	}
			}

	}
	catch(Exception e)
	{
		e.printStackTrace();
	}

}

//#################################################### check_multiple METHOD  ############################################################

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


//***************************************** REcharge FM METHOD ** *************************************************



	//*********************************************  LOG METHOD *********************************************************
			public void log(String str)
			{
				try
				{
					Calendar today = Calendar.getInstance();
					String strlogfile = "log_"+formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
					message_log.setText("SMS#"+str);
					producer_log.send(message_log);
				}
				catch(Exception ee)
				{
					System.out.println("Exception:" + ee.toString());
					ee.printStackTrace();

				}
			}
	//****************************************  FORMATn METHOD **********************************************************
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

	//*******************************************************************************************************************

	//*****************************************  RETURN DATE METHOD () ***************************************************


		public static String retrnDate()
			{
				String a1="",a2="",a3="",a4="",a5="",a6="",dd="";
				Calendar cal=Calendar.getInstance();
				a1 =""+cal.get(1);
				a2 =""+(cal.get(2) + 1);
				a3 =""+cal.get(5);
				a4=""+cal.getTime().getHours();
				a5=""+cal.getTime().getMinutes();
				a6=""+cal.getTime().getSeconds();
				if(a2.length() == 1)
				{	a2 = "0" + a2;	}
				if(a3.length() == 1)
				{	a3 = "0" + a3;	}
				if(a4.length() == 1)
				{	a4 = "0" + a4;	}
				if(a5.length() == 1)
				{	a5 = "0" + a5;	}
				if(a6.length() == 1)
				{	a6 = "0" + a6;	}
				dd=a1+"-"+a2+"-"+ a3+" " +a4+":"+a5+":"+a6;
				return dd;
			}
	//*******************************************************************************************************************

	//*******************************************************************************************************************

		public void error(Exception e)
		{
			try	{
				Calendar today = Calendar.getInstance();
				String strerrorfile = "err_"+formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
				FileOutputStream outfile = new FileOutputStream("./error/" + strerrorfile + ".txt",true);
				PrintStream outprint = new PrintStream(outfile);
				outprint.println("Exception:" +e.toString());
				outprint.close();
				outfile.close();
				System.out.println(e.toString());
				e.printStackTrace();
				strSMSText="Airtel UPW Resub Exception\n"+(e.toString().substring(0,120));
				System.exit(0);
			}
			catch(Exception ee)
			{
				System.out.println("Exception:" + ee.toString());
				ee.printStackTrace();
			}
		}

		//*******************************************************************************************************************



	//*****************************************  GET DATETIME METHOD () ***************************************************

	public String getDateTime()
	{
			ResultSet rsMobile=null;
			try
			{
				String current_date=null;
				rsMobile = stmtFM.executeQuery("select convert(varchar,getdate(),008)");
				while(rsMobile .next())
				{
					current_date = rsMobile .getString(1);
				}
				return current_date;

			}catch(Exception e)
			{
				System.out.println(e);
				return "00:00:00";
			}
			finally
			{
				try
				{
					rsMobile.close();
				}catch(Exception e)
				{
					System.out.println(e);
				}
			}
   	}

}
//****************************************************  CODE END *********************************************************
