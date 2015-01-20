import java.sql.*;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.text.SimpleDateFormat;
public class unsub extends Thread
{
	public static Connection con=null;
	public static Statement stmtFM,stmtFM1,stmtFM2;//,stmtFM3,stmtFM4,stmtDel,stmtupd,stmtBGM,stmtResubInfo;
	public	String DSN="master_db";
	public	String USR="billing";
	public	String PWD="billing@voda#123";
	public	String IP="10.43.248.137";
	String msisdn;
	String Date_Time;
	String Mode;
	String Service_id;
	String Status;
	ResultSet rsFile;
	ResultSet rs;
	ResultSet rs1;
	SimpleDateFormat formatter;
	SimpleDateFormat formatter1;
	Date today;
	String result;
	Date today1;
	String result1;
	String[] optmode={"321_IVR","321_SMS","321_USSD","CRM","EURO","FREEIBD","IVR","OBD","OBDCALLBACK","RECO","RET","SDE","SM","SMS","USSD197","197","RETAIL197","VOICE","USSD"};

	public void log(String msisdn1,String Date_Time1,String Mode1,String Service_id1,String response2)
	{
		try
		{
			formatter = new SimpleDateFormat("yyyyMMdd");
			today = new Date();
			result = formatter.format(today);

			formatter1 = new SimpleDateFormat("yyyyMMdd:HHmmss");
			today1 = new Date();
			result1 = formatter1.format(today1);
			FileOutputStream fos=null;
			fos = new FileOutputStream("unsublog/"+result+".txt",true);
			PrintWriter pw= new PrintWriter(fos);

			pw.println(msisdn1+"#"+Date_Time1+"#"+Mode1+"#"+Service_id1+"#"+result1+"#Response---->:"+response2);
			pw.close();
			fos.close();
			System.out.println(msisdn1+"#"+Date_Time1+"#"+Mode1+"#"+Service_id1+"#"+result1+"#Response---->:"+response2);

		}
		catch(Exception e2)
		{
			System.out.println("*****log function Exception*****");
			e2.printStackTrace();
		}
	}
	private Connection dbConn()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		    con = DriverManager.getConnection("jdbc:mysql://"+IP+"/"+DSN, USR, PWD);
			System.out.println("Database Connection established!");
			return con;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try {
				Thread.sleep(10000);
			}
			catch (InterruptedException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return con;
	}
	public static void main(String args[])
	{
		unsub bulk = new unsub();
		bulk.start();
	}

//===================================================================
	public void run()
	{
		try
		{
			con=dbConn();
			stmtFM1 = con.createStatement();
			stmtFM2 = con.createStatement();

			//stmtFM = con.createStatement();
			//stmtFM4 = con.createStatement();
			//stmtFM2 = con.createStatement();
			//stmtBGM = con.createStatement();
			while(true)
			{
				

				String selectFile ="select * from master_db.tbl_unsub where status=0 and (date(now()) not in ('2013-06-19') or hour(now()) not in(0,1,2,3,4,5))  limit 0,2000";

					rsFile = stmtFM1.executeQuery(selectFile);
					while(rsFile.next())
					{
						try
						{       String line1="",response1="",event_id="";
							int Plan_id=0;

							msisdn = rsFile.getString("Msisdn");
							Date_Time = rsFile.getString("Date_Time");
							Mode = rsFile.getString("Mode");
							int index = Arrays.binarySearch(optmode,Mode.toUpperCase());
							if(index<0)
								Mode="IVR";
							Service_id = rsFile.getString("Service_id");
							Status = rsFile.getString("Status");
							Plan_id=rsFile.getInt("plan_id");
							System.out.println("###################### PICKED NEW NUMBER ####################");

						 if(Service_id.equals("1301"))
						  {
							if(Plan_id==6)
	                                                        event_id="HNG_MUSICULM";
							else
								event_id="HNG_MUSICULD";
						  }
						 else if(Service_id.equals("1302"))
							event_id="HNG_ENTRMNTPORTAL";
						 else if(Service_id.equals("1307"))
							event_id="HNG_VH1MUSIC";
						 else if(Service_id.equals("1310"))
							event_id="HNG_REDFM";

						 String unsubURL="http://10.43.248.137/VodafoneBilling/vodafoneMSGBilling.php?msisdn="+msisdn+"&req=dct&reqMode="+Mode+"&eventType="+event_id;
						 System.out.println(unsubURL+ "  ---- service_id="+Service_id+" ---plan ID="+Plan_id);
							//Unsub URL
							try
							{

							URL url1=new URL(unsubURL);
							HttpURLConnection unsubObj = (HttpURLConnection)url1.openConnection();
							System.out.println("~~~~~~~~~~~~~~~~ \t"+unsubObj.getResponseCode());
							BufferedReader in1 = new BufferedReader(new InputStreamReader(unsubObj.getInputStream()));
							System.out.println("*******************START*************************");
							while ((line1= in1.readLine()) != null)
							{
								response1 = response1 + line1;
								System.out.println("responce-->"+response1);
							}
							System.out.println("*******************END***********************");
							in1.close();
							unsubObj.disconnect();
							sleep(50);
							}
							catch(Exception ex)
							{
								 ex.printStackTrace();
                                                                 System.out.println("Exception While Calling "+unsubURL);
                                                                 response1="NOK#URL EXCEPTION";
							}
							log(msisdn,Date_Time,Mode,Service_id,response1);

                                    String LogQuery="insert into master_db.tbl_unsub_log select *,'"+response1+"',now() from master_db.tbl_unsub where Msisdn="+msisdn;
				    String deleteQuery="delete from master_db.tbl_unsub where Msisdn="+msisdn;
	   			    stmtFM2.executeUpdate(LogQuery);
				    stmtFM2.executeUpdate(deleteQuery);
							sleep(50);
							System.out.println("  ");
						}
						catch(Exception e)
						{
							e.printStackTrace();
							System.out.println("Error under run "+ e);
                                                         sleep(50);

						}
					}//while ends  File
                             sleep(1000);
			}//true ends
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
	}
}

