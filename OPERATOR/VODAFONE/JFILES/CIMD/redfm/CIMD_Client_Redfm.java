
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import org.apache.log4j.*;



public class CIMD_Client_Redfm extends Thread implements CIMD_Constants
{
    public static CIMD_Client_Redfm cimd;
	//===========================================================
    public Connection con		= null;
    public Connection con_del	= null;
    public Statement stmt 		= null;
    public Statement stmt_del	= null;
    public Statement stmt_cnt	= null;
    public String DB_USER 		= "";
    public String DB_PWD  		= "";
    public String DB_SERV 		= "";
    public String DB_HOST 		= "";
    static String flag	  		= "";

    boolean isConnected			= false;
    Socket socket				= null;
    BufferedInputStream in		= null;
    BufferedOutputStream out	= null;
    int packet					= 0;
    private int ch				= 0;
	boolean DEBUG				= false;
    public boolean interactive	= false;

    //===============DB Varibales=============================

    public String ip=null,dsn=null;
    public String username=null,pwd=null,msgqueue=null;

    //=================SMS Variables===========================

    public String userId		= "";
	public String passwd		= "";
	public String service		= "";
    public String host			= "";
	public int port				= 0;
    //=========================================================

	static CIMD_Functions cimd_functions = new CIMD_Functions();

	CIMD_Client_Redfm()
    {
		try
		{
			load_config();
			BufferedReader dis = new BufferedReader(new InputStreamReader(System.in));
			String input="";
			cimd_functions.PrintAndLog("[  Userid "+userId+"   Password "+passwd+" Port  "+port+" ]");
		}
		catch(Exception e)
		{
			System.err.println(e);
			return;
		}
		isConnected = false;
		packet = 1;
	}

	public void start()
    {
		cimd_functions.DEBUG = DEBUG;
        try
        {
            connect_db();
            connect();
			try{Thread.sleep(1000);}catch(Exception ex){}
            login();
            packetAdd();
			pool_process();
			try{Thread.sleep(1000);}catch(Exception ex){}
			logout();
            close();
        }
        catch(IOException e)
        {
			cimd_functions.PrintAndLog(e.toString());
			close();
		}catch(Exception e) {}
    }

	void packetAdd()
	{
		packet+=2;
		if(packet>255) packet =1;
	}
	public static void main(String args[])
    {
        cimd = new CIMD_Client_Redfm();
		cimd_functions.PrintAndLog("Application Started Successfully...........");
		cimd.start();
    }

    private boolean login()
    {
        CimdMsg mesg = new CimdMsg(LOGIN, 0);
        mesg.addParameter(USER_ID, userId.getBytes());
        mesg.addParameter(PASSWD,  passwd.getBytes());
        try
        {
            mesg.nr = cimd_functions.int2gsmInt(packet, 3);
            cimd_functions.writeMessage(mesg,out);
            cimd_functions.PrintAndLog("[ CIMD CLIENT Logged IN ]");
        }
        catch(IOException e)
        {
			cimd_functions.PrintAndLog(e.toString());
			logout();
			close();
		}
        return true;
    }

	private boolean logout()
    {
        CimdMsg mesg = new CimdMsg(LOGOUT, 0);
        try
        {
            mesg.nr = cimd_functions.int2gsmInt(packet, 3);
            cimd_functions.writeMessage(mesg,out);
            cimd_functions.PrintAndLog("logged out");
        }
        catch(IOException e)
        {
			close();
		}
        return true;
    }

    void connect() throws IOException
    {
        if(isConnected) return;
        try
        {
            cimd_functions.PrintAndLog("[ Trying to Connect To Host " + host + " On Port: " + port+" ]");
            socket = new Socket(host, port);
            socket.setTcpNoDelay(true);
            in = new BufferedInputStream(socket.getInputStream(), 1024);
            out = new BufferedOutputStream(socket.getOutputStream(), 1024);
        }
        catch(IOException e)
        {
            cimd_functions.PrintAndLog("[    Connection could not be established    ]"+e.toString());
            close();
        }
        cimd_functions.PrintAndLog("[   !!!!!Connected!!!!!    ]");
    }

	void close()
    {
        try
        {
            in = null;
            out = null;
            if(socket != null)
            {
                socket.close();
                socket = null;
            }
        }
        catch(IOException e){cimd_functions.PrintAndLog(e.toString());}
    }

	public void pool_process() throws IOException
	{


		try
		{
			int iCount=0;
			int ctr=0;
			int dnd_count=0;
			String input;
			BufferedReader keyboardInput = new BufferedReader(new InputStreamReader(System.in));
			String source="54646",dest="7838666171",msg="",rowid="",circle,date_time,status,iType;
			while(true)
			{


				try
				{

					String str="select count(*) cnt  from master_db.tbl_sms where flag=2";
					int cnt=0;
					ResultSet rs1=stmt_cnt.executeQuery(str);
					while(rs1.next())
					  {
						cnt= rs1.getInt("cnt");
					  }
					if(cnt>0)
					{
							String qquery = "select msgid,ani,message,date_time,dnis,flag,circle,type,status from tbl_sms nolock where status=0  and message is not null  and flag=2 limit  200";
					   	           qquery=qquery.trim();
							ResultSet rs= stmt.executeQuery(qquery);
							while(rs.next())
							{

								rowid   	= rs.getString("msgid");
								dest    	= rs.getString("ani");
								source  	= rs.getString("dnis");
								msg     	= rs.getString("message");
								circle    	= rs.getString("circle");
								date_time   = rs.getString("date_time");
								status   	= rs.getString("status");
								iType   	= rs.getString("type");

								cimd_functions.PrintAndLog( "#91"+dest+"#"+source+"#"+msg+"#0");
								iSubmit("91"+dest, source,msg,"0");

								log("#91"+dest+"#"+source+"#"+msg+"#0","REDFM");
								String l="SMSSenderREDFM#"+rowid+"#"+dest+"#"+msg+"#"+date_time+"#"+source+"#"+iType+"#VODM#"+circle+"#"+status+"#NODND";
								log(l,"SMSSender");
								 stmt_del.executeUpdate("delete from tbl_sms where msgid = '"+rowid+"' and ani='"+dest+"' and flag=2");
								 sleep(1000);
								 ctr=0;

							}//while ends


				 	}
				 	else
				 	{

						cimd_functions.PrintAndLog("No records to Process. Sleeping !!! Total Time Elapsed [ "+(ctr*5)+" ]");
						try
						{
							if(ctr>1)
							{
								int retcode = IsAlive();
								cimd_functions.PrintAndLog(" IsAlive() response "+retcode);
								if(retcode<1)
								{
									cimd_functions.PrintAndLog("Not able to get IsAlive() response");
									reConnect();
								}
								ctr=0;
							}
							Thread.sleep(30000);ctr++;
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}

					}//else ends

				}//try ends
				catch(Exception ep)
				{
					System.out.println("got exception in process 3"+ep);
					ep.printStackTrace();
					reConnect();
					continue;
				}
			}//while ends
		}
		catch(Exception e)
		{
			System.out.println("got exception in process last"+e);
			e.printStackTrace();
			reConnect();
		}
	}
	public int IsAlive()
	{
		cimd_functions.PrintAndLog("Sending Enquire Request.......");
		CimdMsg sm = new CimdMsg(this.ALIVE,cimd_functions.int2gsmInt(packet, 3));
		try
		{
			cimd_functions.writeMessage(sm,out);
			CimdMsg cm = cimd_functions.readMessage(in);
			cimd_functions.PrintAndLog(cm.toString());


			packetAdd();
			checkNack("IsAlive#"+cm.toString());
			cimd_functions.PrintAndLog("Enquire Request Done.......");
		}
		catch(IOException e)
		{
			if(e.toString().trim().equals("java.io.IOException: Not connected"))
			{
				isConnected = false;
				cimd_functions.PrintAndLog("Client Not Connected with Server.");
				return -1;
			}
			close();

		}
		return 1;
	}
	public void iSubmit(String dest,String source,String msg,String flag) throws Exception
	{
		try
		{


				if (msg.length()<=160)
				{
					CimdMsg sm = new CimdMsg(this.SUBMIT,cimd_functions.int2gsmInt(packet, 3));
					sm.addParameter(this.DEST_ADDRESS,dest.getBytes());
					sm.addParameter(this.USER_DATA,msg.getBytes());
					cimd_functions.writeMessage(sm,out);
					CimdMsg cm = cimd_functions.readMessage(in);
					cimd_functions.PrintAndLog(cm.toString());
					packetAdd();
					checkNack("Submit#"+cm.toString());
				}
				else
				{
					int tot_msgs;
					if (msg.length()%153 == 0)
						tot_msgs = msg.length()/153;
					else
						tot_msgs = msg.length()/153 + 1;
					String udh = "050003000" + tot_msgs + "0";

					CimdMsg[] cimd_msg = new CimdMsg[tot_msgs];
					CimdMsg cm;
					cimd_functions.PrintAndLog("Tot msgs = " + tot_msgs);
					for (int i=0;i<tot_msgs; i++)
					{
						cimd_msg[i] = new CimdMsg(this.SUBMIT,cimd_functions.int2gsmInt(packet, 3));
						cimd_msg[i].addParameter(this.DEST_ADDRESS,dest.getBytes());

						cimd_functions.PrintAndLog(udh+(i+1));
						cimd_msg[i].addParameter(this.USER_DATA_HEADER,(udh + (i+1)).getBytes());
						if (i == tot_msgs-1)
						{
							cimd_functions.PrintAndLog(msg.substring(i*153));
							cimd_msg[i].addParameter(this.USER_DATA,msg.substring(i*153).getBytes());
						}
						else
						{
							cimd_functions.PrintAndLog(msg.substring(i*153,i*153+153));
							cimd_msg[i].addParameter(this.USER_DATA,msg.substring(i*153,i*153+153).getBytes());
						}


						cimd_functions.writeMessage(cimd_msg[i],out);
						cm = cimd_functions.readMessage(in);
						cimd_functions.PrintAndLog(cm.toString());
						packetAdd();
					}
				}

		}
		catch(Exception ex)
		{
			throw ex;
		}
	}

	public void readsocket() throws IOException
	{
        CimdMsg cm = cimd_functions.readMessage(in);
		CimdMsg sm = new CimdMsg(this.DEL_MESG_RESP,cm.nr);
		cimd_functions.PrintAndLog(sm.toString());
		cimd_functions.writeMessage(sm,out);
		checkNack("ReadSocket#"+cm.toString());
	}
	public void connect_db()
	{

		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con= DriverManager.getConnection("jdbc:mysql://"+ip+":3306/"+dsn,username,pwd);
			stmt	= con.createStatement();
	    	stmt_del= con.createStatement();
	    	stmt_cnt= con.createStatement();

		}
		catch(Exception e)
		{
			cimd_functions.PrintAndLog("Database Connection Could not be established! Exiting Application....");
			System.exit(1);
		}
	}
	public void load_config()
	{
		try
		{
			 ResourceBundle resource = ResourceBundle.getBundle("config/CIMDREDFM");

			 msgqueue=resource.getString("MSGQUEUE");
			 userId=resource.getString("USERID");
			 passwd=resource.getString("PASSWD");
			 host=resource.getString("HOST");
			 port=Integer.parseInt(resource.getString("PORT"));
			 ip=resource.getString("IP");
			 dsn=resource.getString("DSN");
			 username=resource.getString("USERNAME");
			 pwd=resource.getString("PWD");


			 System.out.println(" USERID :"+userId+" PASSWD :"+passwd+" HOST:"+host);


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public int reConnect()
	{
		try
		{
			cimd_functions.PrintAndLog("Into Reconnect Function");
			Thread.sleep(1000);
			cimd_functions.PrintAndLog("Trying to Close Any Open Connection");
			cimd_functions.PrintAndLog("Logging out from CIMD server to break session...");
			logout();
			Thread.sleep(2000);
			close();

			Thread.sleep(2000);
			//connect();
			//Thread.sleep(2000);
			//close();
			cimd_functions.PrintAndLog("Trying to Connect To Host " + host + " On Port: " + port+" again." );
			Thread.sleep(3000);
			connect();

			Thread.sleep(2000);
			packet=0;

			cimd_functions.PrintAndLog("Logging to CIMD server with User Id "+userId+" and password "+passwd+" ");
			login();
			//System.exit(0);
		}
		catch(Exception e){}
		return 1;
	}


	//------------------------------------------------ LOG METHOD () ----------------------------------------------------

		public  void log(String str,String filename)
		{
			try	{
					Calendar today = Calendar.getInstance();
					String strlogfile = formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
					String strdate = formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
					String strtime = formatN(""+today.get(Calendar.HOUR_OF_DAY),2)+formatN(""+today.get(Calendar.MINUTE),2)+formatN(""+today.get(Calendar.SECOND),2);
					File f=new File("/home/ivr/javalogs/CIMD/REDFM");
					if(!f.exists())
						f.mkdirs();
					System.out.println(str);

					if(filename.equalsIgnoreCase("SMSSender"))
					{
						f=new File("/home/ivr/javalogs/BillingMnger/SMSSender/"+ strlogfile + "/");
						if(!f.exists())
							f.mkdirs();


						FileOutputStream outfile = new FileOutputStream("/home/ivr/javalogs/BillingMnger/SMSSender/"+strlogfile+"/"+ strdate + ".log",true);
						PrintStream outprint = new PrintStream(outfile);
						outprint.println(strtime + "#" + str);
						outprint.close();
						outfile.close();
						System.out.println("/home/ivr/javalogs/BillingMnger/SMSSender/"+strdate+"/"+ strdate + ".txt");
					}
					else
					{
						FileOutputStream outfile = new FileOutputStream("/home/ivr/javalogs/CIMD/REDFM/"+ strdate + ".txt",true);
						PrintStream outprint = new PrintStream(outfile);
						outprint.println("#"+strdate +"#" + strtime + "#" + str);
						outprint.close();
						outfile.close();
					}

			}
			catch(Exception e)
			{
				System.out.println("GOT Exception in REDFM LOG method as:"+e);
			}
		}

		//--------------------------------------------------------------------------------------------------------------------

		//--------------------------------------------- FORMATN METHOD () ---------------------------------------------------
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
	//------------------------------------------------------------------------------------------------------------------------
public void checkNack(String resp)
{
	try
	{
		String temp[]=resp.split("#");

		temp[1]=temp[1].substring(0,temp[1].indexOf("params"));
		temp[1]=temp[1].replaceAll(",","");
		//cimd_functions.PrintAndLog(temp[0]+" value - "+temp[1]);
		temp[1]=temp[1].trim();
		temp[1]=temp[1].substring(temp[1].indexOf("op:")+3,temp[1].length());
		temp[1]=temp[1].trim();
		//cimd_functions.PrintAndLog(temp[0]+" value - "+temp[1]);

		if(temp[1].equals("99"))
		{
			cimd_functions.PrintAndLog(temp[0]+" Nack  recieved going to Reconnect  - "+temp[1]);
			reConnect();
		}

	}
	catch(Exception e)
	{
		System.out.println("Error @ check Nack "+e);
	}
}
	//============================================================================


}


