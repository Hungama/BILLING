//package com.hungama.kannel;
import java.util.*;
import java.util.Date;
import java.io.*;
import java.sql.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
public class SMSPush extends Thread
{
	static Properties prop1,prop2;
	public Connection con=null;
	public Statement stmt1=null,stmt2=null,stmt3=null;
	int cnt,msgid,kqcnt,tValuecnt,diff,status;
	String ani="",message="",datetime="",dnis="",tValue="",type="",flag="",circle="";
	public static String out_string="";
	public String _threadName="";
	static
	{
		try
		{
			prop1 = new Properties();
			prop1.load(new FileInputStream("conf/loader.properties"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("DB property file is not present... please check"+ex);
		}
	}
	public SMSPush(String _threadName) {
			this._threadName=_threadName;
	}
	public Connection dbConn()
	{
		try
		{
			Class.forName(prop1.getProperty("db.driverclass"));
			con=DriverManager.getConnection(prop1.getProperty("db.url"),prop1.getProperty("db.user"),prop1.getProperty("db.password"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception in creating Connection... " +e);
			try
			{
				if(con.isClosed() || con == null)
				{
					try
					{
						Class.forName(prop1.getProperty("db.driverclass"));
						con=DriverManager.getConnection(prop1.getProperty("db.url"),prop1.getProperty("db.user"),prop1.getProperty("db.password"));
					}
					catch(Exception ee)
					{
						ee.printStackTrace();
						System.out.println("Exception in Trying to connect with database"+ee);
						System.exit(0);
					}
				}
			}
			catch(SQLException se)
			{
				se.printStackTrace();
				System.out.println("Exception in Re-creating connection..."+se);
			}
		}
		return con;
	}
	public void run()
	{
		ResultSet rs1=null,rs2=null;
		dbConn();
		while(true)
		{	
			try
			{
				String result="",k_url="",smsc_id="";
				Date d=null;
				int sec=0;
				stmt1=con.createStatement();
				stmt2=con.createStatement();
				stmt3=con.createStatement();
//				System.out.println("Thread is "+this._threadName+"# "+stmt1+"#"+stmt2+"#"+stmt3+"#"); 
				rs1=stmt1.executeQuery(prop1.getProperty(this._threadName+".Query_1"));
				while(rs1.next())
				{
					cnt=rs1.getInt("cnt");
					if(cnt>0)
					{
						if(this._threadName.equalsIgnoreCase("Kannel"))
						{
							d=new Date();
							sec=d.getSeconds();
							if(sec>=1 && sec<=40)
							{
								k_url=prop1.getProperty(this._threadName+".url");
								smsc_id="vod_54646";
							}
							else if(sec>=31 && sec<=45)
							{
								k_url=prop1.getProperty(this._threadName+".url1");
								smsc_id="vod1_54646";
							}
							else if(sec>=41 && sec<=50)
							{
								k_url=prop1.getProperty(this._threadName+".url2");
								smsc_id="vod2_54646";
							}
							else
							{
								k_url=prop1.getProperty(this._threadName+".url3");
								smsc_id="vod3_54646";
							}
						}
						else
						{
							k_url=prop1.getProperty(this._threadName+".url");
							smsc_id=prop1.getProperty(this._threadName+".smsc_id");
						}
							
						kqcnt=kannelQCheck(smsc_id);
						if(kqcnt!=-1)
						{	
							tValue=prop1.getProperty("KannelQ.threshold");
							tValuecnt=Integer.parseInt(tValue);
							if(kqcnt<(Integer.parseInt(tValue)))
							{
								diff=tValuecnt-kqcnt;
								String query=prop1.getProperty(this._threadName+".Query_2")+ diff;
								System.out.println(query);
								rs2=stmt2.executeQuery(prop1.getProperty(this._threadName+".Query_2")+ diff);
								while(rs2.next())
								{
									msgid=rs2.getInt(1);
									ani=rs2.getString(2);
									if(ani.length()==12 && ani.startsWith("91"))
										ani=ani.substring(2,12);
									message=rs2.getString(3);
									datetime=rs2.getString(4);
									status=rs2.getInt(5);
									dnis=rs2.getString(6);
									flag=rs2.getString(7);
									type=rs2.getString(8);
									circle=rs2.getString(9);
									type=type.toLowerCase();
									out_string=msgid+"#"+ani+"#"+message+"#"+datetime+"#"+status+"#"+dnis+"#"+flag+"#"+type+"#"+circle+"#";									
									result=insertKQ(k_url,ani,dnis,message,type);									
									if("0: Accepted for delivery".equalsIgnoreCase(result) || "3: Queued for later delivery".equalsIgnoreCase(result))
									{
										try
										{
											out_string=result+"#"+msgid+"#"+ani+"#"+message+"#"+datetime+"#"+status+"#"+dnis+"#"+flag+"#"+type+"#"+circle+"#Success#"+smsc_id;
											Olog.log("VODA_PUSH", out_string, 0);
										//	stmt3.executeUpdate("insert into tbl_sms_log select * from tbl_sms where msgid='"+msgid+"'");
											stmt3.executeUpdate("delete from tbl_sms where msgid='"+msgid+"'");
										}
										catch(Exception ee)
										{
											System.out.println("Exception in successfull insertion and deletion in table"+ee);
										}
									}
									else
									{
										out_string=result+"#"+msgid+"#"+ani+"#"+message+"#"+datetime+"#"+status+"#"+dnis+"#"+flag+"#"+type+"#"+circle+"#Fail#"+smsc_id;
										Olog.log("VODA_PUSH", out_string, 0);
									}
								}
							}						
							else
							{
								Thread.sleep(10000);
							}
						}					
						else
						{
							System.out.println("waiting for right response....");
							Thread.sleep(3000);
						}
					}				
					else
					{
						System.out.println("waiting for the count in the table.......");
					}
				}	
				try{rs1.close();rs2.close();} catch(Exception ee){}
				Thread.sleep(4000);					
			}		
			catch(Exception exx)
			{
				exx.printStackTrace();
				out_string=out_string+"#"+exx.toString();
				Olog.log("VODA_ERROR", out_string, 1);
				System.out.println("exception in run method...."+exx);
			}
		}
	}
	public String insertKQ(String k_url,String ani, String sc, String msg,String type)
	{
		String ln="",res="";
		try
		{			
			if("RNT".equalsIgnoreCase(type))
			{  
				k_url = k_url.replace("'<MSISDN>'", ani).replace("'<SC>'", sc).replace("'<MSG>'", SMConvert(msg));
			    k_url = k_url+"&udh="+SMConvert("06050415810000");
			}
			else
				k_url=k_url.replace("'<MSISDN>'", ani).replace("'<SC>'", sc).replace("'<MSG>'", URLEncoder.encode(msg,"UTF-8"));
			System.out.println("URL called:- "+k_url);
			URL kurlreq = new URL(k_url);
			HttpURLConnection kurlconn = (HttpURLConnection)kurlreq.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(kurlconn.getInputStream()));
			System.out.println("*******************START*************************");
			while ((ln=in.readLine()) != null)
			{
				res = res + ln;
			}
			in.close();
			kurlconn.disconnect();
		}
		catch(Exception ex2)
		{
			ex2.printStackTrace();
			System.out.println("Exception in Hitting URL for Kannel...."+ex2);
			return "-2: Error in Hitting URL";
		}
		return res;
	}
	public String SMConvert(String Data)
	{
		char[] DataArr = Data.toCharArray();
		String strData = "";
		for (int i = 0; i < DataArr.length; i = i + 2)
		{
			strData = strData + "%" + DataArr[i] + DataArr[i + 1];
			//Response.Write(DataArr[i]);
		}
		return strData;
	}
	public int kannelQCheck(String smsc_id)
	{
		String line="",response="",kannel_url="";
		try
        {
			kannel_url=prop1.getProperty("Kannel.QcheckUrl");
			kannel_url=kannel_url+smsc_id;			
			URL urlreq = new URL(kannel_url);
 System.out.println("hi......"+kannel_url);
			HttpURLConnection urlconn = (HttpURLConnection)urlreq.openConnection();
			urlconn.setConnectTimeout(2000);
			BufferedReader in = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
			System.out.println("*******************START*************************");
			while ((line=in.readLine()) != null)
			{
				System.out.println("hi......"+line);
				response = response + line;
			}
			in.close();
			urlconn.disconnect();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
		Olog.log("VODA_ERROR", e.toString(), 1);
            System.out.println("Error @ Send_err"+e);
            return -1;
        }
        return Integer.parseInt(response);
	}
	public static void main(String[] args)
	{
		int status=CheckProcess.GetProcessList("SMSPush",1);
		if(status>1)
		{	
			System.out.println("Process already running ......");
			System.exit(0);
		}
		try
		{
			String _threadString =(String) prop1.getProperty("threads.name");
			String _threadName[]=_threadString.split("#");
			for(int i=0;i<_threadName.length;i++)
			{
				SMSPush sm=new SMSPush(_threadName[i]);
				sm.start();
			}
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
			System.out.println("exception in main thread ...."+e1);
		}
	}
}

