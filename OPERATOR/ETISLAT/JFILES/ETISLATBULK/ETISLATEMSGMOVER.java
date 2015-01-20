import hungamalogging.hungamalogging;
import java.sql.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ETISLATEMSGMOVER extends Thread
{
	public static String  IP=null,DSN=null,USR=null,PWD=null;
	public static Connection con=null;
  	public ETISLATEMSGMOVER()
	{
		try
		{
			check_multiple();
			System.out.println("**********************************************************");
			System.out.println("**     Thread Started With The Following Configuration  **");
			System.out.println("**              File to be Read is dbConfig.CFG          **");
			ResourceBundle resource = ResourceBundle.getBundle("config/chargingmgr_source");
			IP=resource.getString("IP");
			DSN=resource.getString("DSN");
			USR=resource.getString("USERNAME");
			PWD=resource.getString("PWD");
			System.out.println("** IP is  ["+IP+"] **  DSN is ["+DSN+"] Usr is ["+USR+"] Pwd is ["+PWD+"]\t**");
		}
		catch(Exception e)
		{
			System.out.println("Exception while reading Recharge.cfg");
			e.printStackTrace();
		}
	}
	public static long getDateTime()
	{
		String s1 = new SimpleDateFormat("HH:mm:ss.ms").format(Calendar.getInstance().getTime());
		int count=(s1.substring(s1.indexOf(".")+1,s1.length())).length();
		//System.out.println(s1);
		final Pattern p = Pattern.compile("^(\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{"+count+"})$");
        final Matcher m = p.matcher(s1);
        if (m.matches())
        {
            final long hr = Long.parseLong(m.group(1)) * TimeUnit.HOURS.toMillis(1);
            final long min = Long.parseLong(m.group(2)) * TimeUnit.MINUTES.toMillis(1);
            final long sec = Long.parseLong(m.group(3)) * TimeUnit.SECONDS.toMillis(1);
            final long ms = Long.parseLong(m.group(4));
            return hr + min + sec + ms;
        }
        else
        {
            throw new IllegalArgumentException(s1 + " is not a supported interval format!");
        }
	}
	public static String timeDiff( final long l)
	{
		final long hr = TimeUnit.MILLISECONDS.toHours(l);
        final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));


        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
	}
	private Connection dbConn()
	{
		while(true)
		{
			try
			{
				if(con==null || con.isClosed())
				{
					Class.forName("com.mysql.jdbc.Driver");
					con = DriverManager.getConnection("jdbc:mysql://"+IP+"/"+DSN, USR, PWD);
					System.out.println("Database Connection established!");
				}
				return con;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
//****************************************RUN METHOD FOR STARTING THREAD ****************************************
	public void run()
	{
		try
		{
			System.out.println("checking another instance");
			dbConn();
			CallableStatement cstmt = con.prepareCall("{call etislat_hsep.send_score_alert(?,?,?)}");
			while(true)
			{
				try
				{
					cstmt.setString(1, "2121");
					cstmt.setString(2,"NA");
					cstmt.setString(3,"temp");
					long l1 = getDateTime();
					cstmt.execute();
					long l2 = getDateTime();
					System.out.println("time Taken>>>"+timeDiff(l2-l1));
					System.out.println("called procedure etislat_hsep.send_score_alert('2121',NA,temp)" );
					Thread.sleep(15000);

				}
				catch(Exception ex)
				{
					System.out.println(ex);
					if(con==null || con.isClosed())
					{
						dbConn();
						cstmt=null;

						cstmt = con.prepareCall("{call etislat_hsep.send_score_alert(?,?,?)}");
					}
					else
					{
						sendAlert("EtislateMSGMover has been exited due to some error"+ex);
						System.exit(0);
					}
					hungamalogging.log("MOVER#"+ex,"ExceptionLog_","/home/ivr/javalogs/EtislatTF");
				}
			}
		}
		catch(Exception e)
		{

			System.out.println(e);
			System.exit(0);
		}
	}
//#################################################### check_multiple METHOD  ############################################################

	 public void check_multiple()
	 {
		 File mfile = new File("Check_Recharge_mv.lck");
		 try
		 {
			 if(mfile.exists())
			 {
				 System.out.println(" WARNING !!! ANOTHER PROGRAM IS RUNNING !!!!!");
				 System.out.println("Remove Check_Recharge_mv.lck first then restart  !!!!!");

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
	//*****************************************  Main METHOD () ***************************************************

	public static void main(String args[])
	{
		ETISLATEMSGMOVER SMSSENDER = new ETISLATEMSGMOVER();
		SMSSENDER.start();
	}


}
