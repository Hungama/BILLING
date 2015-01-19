/*########################################################################################################################
##									PROGRAME 	: RECHARGING FM 														##
##									PURPOSE  	: ONLINE RECHARGING(AP)													##
##									ACCOUNT  	: AIRTEL (PAN INDIA)													##
##									DEVELOPED BY: TEAM AIRTEL (CELLEBRUM,PARWANOO)										##
##									DATE		: 27 JUNE,2007															##
##########################################################################################################################*/


//******* IMPORT PACKAGES *********
import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.ResourceBundle;
import java.util.Properties;


//********* THREAD RECHARGING _FM ***********************
public class Recharging_main extends Thread
{
    File mfile = new File("Check_Recharge.lck");
	public static Connection con=null, con2 = null,con3 = null,con4=null,con5=null,con6=null,con7=null;
	public static Statement stmtFM,stmtFM1,stmtFM2,stmtDel,stmtupd,stmtBGM,stmtResubInfo;
	public static FileOutputStream outfile 		= null;
	public static PrintStream outprint			= null;
	public static int grace_count=0,grace_days=0;
	public static String da=null;
	Properties properties = new Properties();
	static String FilePath = "config/Service.cfg";
	public int NO_OF_SERVER = 0;
	static String[] SUBSCRIPTION_TABLE = new String[10];
	static String[] RESUB_PROCEDURE = new String[10];
	static String[] GRACE_PERIOD = new String[10];
	static String[] PRERENEWAL_MESSAGE =new String[10];
	static String[] UNSUB_PROCEDURE = new String[10];
	static String[] DNIS_ARRY = new String[10];
	static String[] PRERENEWAL_PERIOD =new String[10];
	static String[] PRERENEWAL_PROCEDURE =new String[10];
	public String ip = null;
	public String dsn = null;
	public String username = null;
	public String pwd = null;
    public boolean prerenewal_chk=true;

	String strOutPut="",line="";
	static int count=1;
	int cnt=0;
	int cnt4=0;
	int iNewCount=0,iRetryCount=0,iUnsubCount=0;
	String strSMSText="";
//**************************************** MAKING DB CONNECTION ***********************************************************


	public void readConfiguration()
	{
		try
		{
			System.out.println("**********************************************************");
			System.out.println("**     Thread Started With The Following Configuration  **");
			System.out.println("**              File to be Read is DBCONFIG.CFG          **");

			/*ResourceBundle resource = ResourceBundle.getBundle("config/DBCONFIG");
			ip=resource.getString("IP");
			dsn=resource.getString("DSN");
			username=resource.getString("USERNAME");
			pwd=resource.getString("PWD");*/
			ip="119.82.69.210";
			dsn="master_db";
			username="root";
			pwd="root";
		    System.out.println("IP: "+ip+" DATABASE :"+dsn+" USER :"+username+" PWD:"+pwd);
			//sleep(1000*20);

		}
		catch(Exception e)
		{
			System.out.println("Exception while reading DBCONFIG.cfg");
			e.printStackTrace();

		}
	}

	private void loadProperties(String fileName) throws IOException
		{
			System.out.println("Reading configuration file "+fileName+"...");
			FileInputStream propsFile = new FileInputStream(fileName);
			properties.load(propsFile);
			propsFile.close();
			System.out.println("Setting default parameters...");
			try
			{
				NO_OF_SERVER=getIntProperty("Total_Services",NO_OF_SERVER);
				System.out.println("NO_OF_SERVER ==" + NO_OF_SERVER);
				for(int i=1;i<=NO_OF_SERVER;i++)
				{
					SUBSCRIPTION_TABLE[i] = properties.getProperty("SUBSCRIPTION_TABLE["+i+"]");
					System.out.println("SUBSCRIPTION_TABLE["+i+"]= "+SUBSCRIPTION_TABLE[i]);
					RESUB_PROCEDURE[i] = properties.getProperty("RESUB_PROCEDURE["+i+"]");
					System.out.println("RESUB_PROCEDURE["+i+"]= "+RESUB_PROCEDURE[i]);
					UNSUB_PROCEDURE[i] = properties.getProperty("UNSUB_PROCEDURE["+i+"]");
					System.out.println("UNSUB_PROCEDURE["+i+"]= "+UNSUB_PROCEDURE[i]);
					DNIS_ARRY[i] = properties.getProperty("DNIS_ARRY["+i+"]");
					System.out.println("DNIS_ARRY["+i+"]= "+DNIS_ARRY[i]);
					GRACE_PERIOD[i] = properties.getProperty("GRACE_PERIOD["+i+"]");
					System.out.println("GRACE_PERIOD["+i+"]= "+GRACE_PERIOD[i]);
					PRERENEWAL_MESSAGE[i] = properties.getProperty("PRERENEWAL_MESSAGE["+i+"]");
					System.out.println("PRERENEWAL_MESSAGE["+i+"]= "+PRERENEWAL_MESSAGE[i]);
					PRERENEWAL_PERIOD[i] = properties.getProperty("PRERENEWAL_PERIOD["+i+"]");
					System.out.println("PRERENEWAL_PERIOD["+i+"]= "+PRERENEWAL_PERIOD[i]);
					PRERENEWAL_PROCEDURE[i] = properties.getProperty("PRERENEWAL_PROCEDURE["+i+"]");
					System.out.println("PRERENEWAL_PROCEDURE["+i+"]= "+PRERENEWAL_PROCEDURE[i]);
				}
			}
			catch (Exception e)
			{
			   System.out.println("Exception in properties.");
			}
		}

		private int getIntProperty(String propName, int defaultValue)
		{
			return Integer.parseInt(properties.getProperty(propName,Integer.toString(defaultValue)));
		}


	public Recharging_main()
	{
			try
			{


				readConfiguration();
				System.out.println("Initiallizing DB");
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn, username, pwd);
				con2 = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn, username, pwd);
				con3 = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn, username, pwd);
				con4 = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn, username, pwd);
				con5 = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn, username, pwd);
				con6 = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn, username, pwd);
				con7 = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn, username, pwd);
				stmtFM = con.createStatement();
				stmtFM1 = con2.createStatement();
				stmtFM2 = con3.createStatement();
                stmtBGM = con5.createStatement();
				stmtDel = con2.createStatement();
				stmtupd = con2.createStatement();
				stmtResubInfo = con6.createStatement();
				System.out.println("Database Connection established!");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}
//****************************************MAIN FUNCTION ********************************************************
	public static void main(String args[])
	{
		Recharging_main eng_recharge = new Recharging_main();
		eng_recharge.start();
	}
//****************************************RUN METHOD FOR STARTING THREAD ****************************************
public void run()
{
	try
	{
		    //getDateTime();
		    System.out.println("checking another instance");
		    check_multiple();
		    loadProperties(FilePath);
			for(int i=1;i<=NO_OF_SERVER;i++)
			{
				Recharging_main(i);
			}
			mfile.delete();
		    /*while(true)
		    {
				if((new java.util.Date()).getHours()>=17 && (new java.util.Date()).getMinutes()>30)
				{
					System.out.println("Got TIME==>"+(new java.util.Date()).getHours()+":"+(new java.util.Date()).getMinutes());
					System.out.println("System is going to exit");
					mfile.delete();
					sleep(1000);
					System.exit(0);
				}
				int sleep_min = getBillingCount();
				if(sleep_min<=0)
				{
					prerenewal_chk = prerenewal_chk();
					System.out.println("prerenewal_chk"+prerenewal_chk);
					if(!prerenewal_chk)
					{
						System.out.println("System is going to retry number in 10 min");
						sleep(10*60*1000);
						FilePath = "config/Retry_Service.cfg";
					}
					System.out.println("FilePath==>"+FilePath);
					loadProperties(FilePath);
					for(int i=1;i<=NO_OF_SERVER;i++)
					{
						Recharging_main(i);
					}
					if(FilePath.equalsIgnoreCase("config/Retry_Service.cfg"))
					{
						System.out.println("Numbers are Retried");
						System.out.println("System is going to exit");
						mfile.delete();
						System.exit(0);
					}
				}
				else
				{
					Calendar now = Calendar.getInstance();
					if(sleep_min>=60)
					{
						sleep_min = 60;
					}
					now.add(Calendar.MINUTE,sleep_min);
					System.out.println("Going to sleep for :"+sleep_min+" mins");
					System.out.println("Next Check @ : " + now.get(Calendar.HOUR_OF_DAY)
										+ ":"
										+ now.get(Calendar.MINUTE)
										+ ":"
					+ now.get(Calendar.SECOND));
					sleep(sleep_min*60*1000);
				}
			}*/

	}
	catch(Exception e)
	{
		e.printStackTrace();
	}

}

//#################################################### Billing Count #####################################################################

	public int getBillingCount()
	{
		try
		{
			return 0;
			/*String STR;
			int first_cnt=0,second_cnt=0;
			STR = "select count(1) from tbl_billing_reqs nolock where date(date_time)=date(now()) and event_type='RESUB'";
			ResultSet rsMobile = stmtFM.executeQuery(STR);
			System.out.println(STR);
			while(rsMobile .next())
			{
				first_cnt = rsMobile.getInt(1);
			}
			rsMobile.close();
			sleep(1000*60);
			rsMobile = stmtFM.executeQuery(STR);
			System.out.println(STR);
			while(rsMobile.next())
			{
				second_cnt = rsMobile.getInt(1);
			}
			rsMobile.close();
			return second_cnt/(first_cnt-second_cnt);*/
		}catch(Exception e)
		{
			System.out.println(e);
			return 0;
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

	public  void Recharging_main(int server)
	{
		try
			{

				cnt=1;
				String STR;
				STR = "select ani,datediff(now(),renew_date) as 'dayleft' from "+SUBSCRIPTION_TABLE[server]+" nolock where renew_date<=addtime(now(),10000) and status in(1,11)";
				ResultSet rsMobile = stmtFM.executeQuery(STR);
				System.out.println(STR);
                while(rsMobile .next())
				{
					String mobile = rsMobile .getString("ani").trim();
					grace_days=rsMobile.getInt("dayleft");
					System.out.println("Picked Mobile for Recharge:"+mobile+" COUNT TILL THIS NUMBER  "+cnt+" days:"+grace_days);
					cnt++;
					if(grace_days>Integer.parseInt(GRACE_PERIOD[server]))
					{
						System.out.println("Unsubscribing This Number ");
						CallableStatement cstmtfm = null;
						String reas="Insufficient Balance";
						da=retrnDate();
						cstmtfm = con4.prepareCall("{call "+UNSUB_PROCEDURE[server]+"(?,?)}");
						cstmtfm.setString(1,mobile);
						cstmtfm.setString(2,reas);
						cstmtfm.execute();
						cstmtfm.close();
						//log_unsub(SUBSCRIPTION_TABLE[server],mobile,reas);
						iUnsubCount++;

					}
					else
					{
						System.out.println("Sending request for resubscription  "+mobile);
						CallableStatement cstmtfm = null;
						cstmtfm = con4.prepareCall("{call "+RESUB_PROCEDURE[server]+"(?)}");
						cstmtfm.setString(1,mobile);
						cstmtfm.execute();
						cstmtfm.close();
					}
					//******** cheking the biiling code in the file ***********


				}//end of while loop
               	//resub_info(server);
				//strSMSText="AirtelUPW Resub\n New Try ="+iNewCount+"\n"+"Retry ="+iRetryCount+"\n"+"Unsub="+iUnsubCount+"\n"+"Completed at"+(new java.util.Date());
				//updateAlert("RESUB",strSMSText);

			}//end of catch
			catch(Exception e)
			{
				log("logging error "+e+" for number \n");
				e.printStackTrace();
				System.out.println("Error:"+e);
				//strSMSText="Airtel UPW Resub Exception\n"+(e.toString().substring(0,120));
				//updateAlert("RESUB",strSMSText);

			}
	}

	/************ INSERTS MESSAGE CONTENT AND ID FOR ALERT TO BE SEND ************/
		public boolean updateAlert(String alertid, String alert_mesg)
		{

				try
				{

				ResultSet rs=stmtBGM.executeQuery("select count(1) from dbo.tbl_alert_content where alert_id='"+alertid+"'");
				rs.next();

					if(rs.getInt(1) > 0)
					{

						stmtBGM.executeUpdate("update dbo.tbl_alert_content set alert_message= '"+alert_mesg+"'     where alert_id='"+alertid+"'");
						System.out.println(">>Updated");
					}
					else
					{
					stmtBGM.executeUpdate(" insert into dbo.tbl_alert_content(alert_message, alert_id) values('"+alert_mesg+"','"+ alertid+"')" );
					System.out.println(">>inserted");
					}
				}
				catch(Exception e)
				{
				e.printStackTrace();
				return false;
				}

				return true;
	}
//********************************************** Sub retry ***************************************************************************


 //*********************************************  Resub Info Send SMS METHOD *********************************************************
public void resub_info(int server)
				{
						 try
						 {
							//if(!prerenewal_chk)
							//	return;
							int balance=0;
							String InfoMessage=null;
							String[] InfoMessage1=new String[2];
							String mobile="";
							String DNIS="";
							String resub_infoQuery="";
							DNIS=DNIS_ARRY[server];
							if("NA".equals(PRERENEWAL_MESSAGE[server]))
							{
								System.out.println("No Prerenewal Messages for "+DNIS);
								return;
							}
							System.out.println("Sending request for PreRenewal Message "+PRERENEWAL_MESSAGE[server]+" DNIS "+DNIS);
							CallableStatement cstmtInfo = null;
							cstmtInfo = con7.prepareCall("{call "+PRERENEWAL_PROCEDURE[server]+"(?,?,?)}");
							cstmtInfo.setString(1,PRERENEWAL_MESSAGE[server]);
							cstmtInfo.setInt(2,Integer.parseInt(PRERENEWAL_PERIOD[server]));
							cstmtInfo.setString(3,DNIS_ARRY[server]);
							cstmtInfo.execute();
							cstmtInfo.close();
					 }
					 catch(Exception e)
						{
							 System.out.println(e);
						}
       			}

     //*************************************************  Prerenewal Check ***************************************************
	 			public boolean prerenewal_chk()
	 			{
	 					 try
	 					 {
	 						 Calendar today = Calendar.getInstance();
	 						 String strprerenewalfile = "prerenewal_"+formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
	 						 File prefile = new File(strprerenewalfile+".lock");
	 						 if(prefile.exists())
	 						 {
	 							 System.out.println(" PRERENEWAL MESSAGE ARE ALREADY FIRED FOR TODAY !!!!!");
	 							 return false;
	 						 }
	 						 else
	 						 {
	 							 today.add(Calendar.DATE, -1);
	 							 String strprerenewaloldfile = "prerenewal_"+formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
	 							 File oldfile = new File(strprerenewaloldfile+".lock");
	 							 oldfile.delete();
	 							 prefile.createNewFile();
	 							 return true;
	 						 }
	 					 }
	 					 catch(Exception me)
	 					 {
	 						 System.out.println("Exception occur in check_multiple function"+me);
	 						 return false;
	 					 }
	 		}


	//*********************************************  LOG METHOD *********************************************************
			public void log(String str)
			{
				try
				{
					Calendar today = Calendar.getInstance();
					String strlogfile = "log_"+formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
					FileOutputStream outfile = new FileOutputStream("./log/" + strlogfile + ".txt",true);
					PrintStream outprint = new PrintStream(outfile);
					outprint.print(str);
					outprint.close();
					outfile.close();
				}
				catch(Exception ee)
				{
					System.out.println("Exception:" + ee.toString());
					ee.printStackTrace();
					strSMSText="Airtel UPW  Resub Exception\n"+(ee.toString().substring(0,120));
					updateAlert("RESUB",strSMSText);
				}
			}



	//************************************** UNSUB LOG METHOD(maintains unsubscription logs) ****************************
		public void log_unsub(String service,String str,String res)
				{
					try	{
							Calendar today = Calendar.getInstance();
							String strlogfile = service+"log_Unsub"+formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
							str=str+"#"+res;
							System.out.println(str);
							FileOutputStream outfile = new FileOutputStream("./UnSubscription_Log/" + strlogfile + ".txt",true);
							PrintStream outprint = new PrintStream(outfile);
							outprint.println(str);
							outprint.close();
							outfile.close();
					}
					catch(Exception e)
					{
						error(e);
					}
			}
	//*******************************************************************************************************************
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
				updateAlert("RESUB",strSMSText);
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
