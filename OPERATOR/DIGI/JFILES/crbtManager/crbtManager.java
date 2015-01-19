import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;


public class crbtManager extends Thread
{
	java.sql.Connection con=null;
	Statement stm=null,stm_upd=null;
	String IP="172.16.56.42",DNS="dm_radio",username="billing",password="billing";
	public crbtManager()
	{
	   start();
	}

	public void run()
	{
		//int cnt=0;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://"+IP+"/"+DNS,username,password);
			stm=con.createStatement();
			stm_upd=con.createStatement();
			System.out.println("Connection with  DB "+con);
		}
		catch(Exception e)
		{
			System.out.println("Error @ Connection "+e);
		}

		while(true)
		{
			try
			{
				int cnt=0;
				String reqid="",ani="",song_id="",ringid="";
				ResultSet rs=stm.executeQuery("select count(*)cnt from dm_radio.tbl_crbtrng_reqs where status=0");
				while(rs.next())
				cnt=rs.getInt("cnt");

				if(cnt>0)
				{
					ResultSet rs1=stm.executeQuery("select ani,req_id,songid,rngid from dm_radio.tbl_crbtrng_reqs where status=0");
					sleep(1000);
					while(rs1.next())
					{
					  ani=rs1.getString("ani");
					  reqid=rs1.getString("req_id");
					  song_id=rs1.getString("songid");
					  ringid=rs1.getString("rngid");

					  System.out.println("ani "+ani+" req_id  "+reqid+" songid "+song_id+" rngid "+ringid);
					  String rbtURL="http://172.16.56.43:8088/DigiSMS/sendSms?mdn="+ani+"&dnis=2000&msg=CT%20BUY%20"+ringid;
					  System.out.println("rbtURL ----->  "+rbtURL);

					  URL balchk = new URL(rbtURL);
						HttpURLConnection balchkconn = (HttpURLConnection)balchk.openConnection();
						String response ="";
						if(balchkconn.getResponseCode()== HttpURLConnection.HTTP_OK)
						{
							BufferedReader in = new BufferedReader(new InputStreamReader(balchkconn.getInputStream()));
	                        String line="";

							while ((line= in.readLine()) != null)
							{
								// parse incoming lines for your data
								//System.out.println(line);
								response = response + line;
							}
							System.out.println(line);	
							in.close();
							balchkconn.disconnect();

							stm_upd.executeUpdate("update dm_radio.tbl_crbtrng_reqs set status=2 where req_id="+reqid);
							sleep(50);
						}
					}
				}
			}
			catch(Exception e)
			{
				System.out.println("Error @ RUN "+e);
				e.printStackTrace();
				System.exit(0);
			}

		}
	}

	public static void main(String[] args)
	{
		crbtManager gsk=new crbtManager();

	}

}
