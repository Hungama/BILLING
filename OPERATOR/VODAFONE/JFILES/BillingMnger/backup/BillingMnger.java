import java.util.HashMap;
import java.util.ResourceBundle;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import java.net.*;

public class BillingMnger extends Thread
{

	private static String subject = "";
	static Connection con=null;
	Thread t;
	public static Statement stmt,stmtUpdate;
	public static CallableStatement cstmt=null;
	static public String ip=null,dsn=null,username=null,pwd=null,msgqueue=null;
	public static FileAppender appender = null, err_App = null;
	public static Logger logger = null,err_log=null;
	public static File dir=null;
	public static String strdate  = "",mnthdir="";
	public static String strtime  = "",Path="";
	public static Calendar today = null;
	static HashMap<String,String> hashMap= new HashMap<String,String>(16,.5f);
//===============================================================
public BillingMnger(int thr)
{
	String mithr=""+thr;
	t= new Thread(this,mithr);
	t.start();
}
//===========================================================
public void run()
{
	try
	{
		while(true)
		{
			//fileread(Integer.parseInt(t.getName()));
			readFromDB();

		}//while ends
	}
	catch(Exception e)
	{
		System.out.println("Error  in Run "+e);
	}
}
//===========================================================
public void readFromDB()
{
	try
	{
		Statement del_stmt=null,stmt=null;
		stmt=con.createStatement();
		del_stmt=con.createStatement();
		String qquery = "select billing_ID,msisdn,event_type,amount,service_id,operator,circle,plan_id,MODE from tbl_billing_reqs nolock where status=0  limit 0,50";//and msisdn in('8699509743','9324963670')
				qquery=qquery.trim();
				//System.out.println(qquery);
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
							String out_string = null;
							String key=plan_id+"-"+service_id;
							if(hashMap.get(key)!=null)
							{
								out_string = billing_ID+"#"+msisdn+"#"+event_type+"#"+amount+"#"+service_id+"#"+operator+"#"+circle+"#"+plan_id+"#"+hashMap.get(key);
								del_stmt.executeUpdate("update tbl_billing_reqs set status=2 where billing_ID='"+billing_ID+"'");
								processCharging(out_string);

							}
							else
							{
								System.out.println("Here is else hashMap is null for " +msisdn );
								del_stmt.executeUpdate("update tbl_billing_reqs set status=-1 where billing_ID='"+billing_ID+"'");
							}

						}
					catch(Exception e)
					{
						hunLog(e.toString(),'e');
						e.printStackTrace();
						System.exit(0);
					}
				}//while ends
			}//if ends
		}//try ends
		catch(Exception e)
		{
			hunLog(e.toString(),'e');
			e.printStackTrace();
			System.exit(0);
		}
	}//func ends
//==========================================================
public void processCharging(String in_string)
{
	String billing_ID=null;
	String msisdn=null;
	String event_type=null;
	String amount=null;
	String service_id=null;
	String operator=null;
	String circle=null;
	String useravailbal="NA";
	String code=null;
	String trans_id=null;
	String chargeamt=null;
	String fall_back_seqs=null;
	String type_of_plan=null;
	String setPlan_id=null;
	String plan_id=null;
	String res_code=null,res_text=null;
	try
	{
		String in_msg[] = in_string.split("#");
		billing_ID = in_msg[0];
		msisdn = in_msg[1];
		event_type = in_msg[2];
		amount = in_msg[3];
		service_id = in_msg[4];
		operator = in_msg[5];
		circle = in_msg[6];
		plan_id = in_msg[7];
		type_of_plan = in_msg[8];
		fall_back_seqs = in_msg[9];
		 if("TATM".equalsIgnoreCase(operator))
			{
				String out_string="",pre_post="";
				useravailbal=checkBalence(msisdn,operator);
				System.out.println(" Balance='"+useravailbal+"'");
				pre_post = pre_post(useravailbal);
				chargeamt=getAmt(type_of_plan,"4",operator,amount,useravailbal,fall_back_seqs);
				setPlan_id=plan_id;
				System.out.println("useravailbal"+ useravailbal);
				if(Integer.parseInt(chargeamt)== -2 || Integer.parseInt(chargeamt)== -3)
					out_string = operator+"#NOK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+useravailbal+"#0#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#nok#lowbalance";
				 if(Integer.parseInt(chargeamt) <= 0 && (!"postpaid".equalsIgnoreCase(pre_post))) // low balenced
					 out_string = operator+"#NOK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+useravailbal+"#0#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#nok#lowbalance";
				 else
				 {
					 String[] temp = chargeBalance(msisdn,chargeamt,operator,circle,event_type).split("#");
					 trans_id = temp[0];
					 code = temp[1];


					 if("ok".equalsIgnoreCase(code))
					 {
						 out_string = operator+"#OK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+useravailbal+"#"+chargeamt+"#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#"+trans_id+"#"+code;
					 }
					 else
					 {
						 out_string = operator+"#NOK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+useravailbal+"#0#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#"+trans_id+"#"+code;
					 }
				 }
				 hunLog(out_string,'s');
				 callProc(out_string);

			}
	}
	catch(Exception e)
	{
		hunLog(e.toString(),'e');
		e.printStackTrace();
	}
}
//===============================================================
//=======================================================
public void callProc(String in_string)
{
	String operator=null;
	String status=null;
	String billing_ID=null;
	String msisdn=null;
	String event_type=null;
	String amount=null;
	String service_id=null;
	String avl_amt=null;
	String chr_amt=null;
	String trans_id=null;
	String pre_post=null;
	Connection con1=null;
	String plan_id = null;
	String response = null;
	String date_start=null,date_end=null;
	try
	{
		String in_msg[] = in_string.split("#");
		operator=in_msg[0];
		System.out.println("calling Procedure  for Operator -"+operator);
		if("TATM".equalsIgnoreCase(operator))
		{
				status = in_msg[1];
				billing_ID = in_msg[2];
				msisdn = in_msg[3];
				event_type = in_msg[4];
				avl_amt = in_msg[5];
				chr_amt = in_msg[6];
				amount = in_msg[7];
				pre_post = in_msg[8];
				service_id = in_msg[9];
				plan_id = in_msg[10];
				trans_id = in_msg[11];
				response = in_msg[12];
			if("ok".equalsIgnoreCase(status))
			{
				cstmt = con1.prepareCall("{call BILLING_"+event_type.toUpperCase()+"_"+status.toUpperCase()+"(?,?,?,?,?,?,?,?,?,?)}");
				cstmt.setString(1, billing_ID);
				cstmt.setString(2, msisdn);
				cstmt.setString(3, event_type);
				cstmt.setString(4, avl_amt);
				cstmt.setString(5, chr_amt);
				cstmt.setString(6, amount);
				cstmt.setString(7, pre_post);
				cstmt.setString(8, service_id);
				cstmt.setString(9, plan_id);
				cstmt.setString(10, trans_id);
				cstmt.execute();
				cstmt.close();
			}
			else
			{
				cstmt = con1.prepareCall("{call BILLING_"+event_type.toUpperCase()+"_"+status.toUpperCase()+"(?,?,?,?,?,?,?,?,?,?)}");
				cstmt.setString(1, billing_ID);
				cstmt.setString(2, msisdn);
				cstmt.setString(3, event_type);
				cstmt.setString(4, avl_amt);
				cstmt.setString(5, response);
				cstmt.setString(6, amount);
				cstmt.setString(7, pre_post);
				cstmt.setString(8, service_id);
				cstmt.setString(9, plan_id);
				cstmt.setString(10, trans_id);
				cstmt.execute();
				cstmt.close();
				System.out.println(" response '"+ response + "'");
			}
		}
		else if("RELC".equalsIgnoreCase(operator) || "RELM".equalsIgnoreCase(operator))
		{
			String sub_top=null;
			status     = in_msg[1];
			billing_ID = in_msg[2];
			msisdn     = in_msg[3];
			event_type = in_msg[4];
			avl_amt    = in_msg[5];
			chr_amt    = in_msg[6];
			amount     = in_msg[7];
			pre_post   = in_msg[8];
			service_id = in_msg[9];
			plan_id    = in_msg[10];
			trans_id   = in_msg[11];
			response   = in_msg[12];
			date_start = in_msg[13];
			date_end   = in_msg[14];
			if("ok".equalsIgnoreCase(status))
			{	sub_top=event_type;
				if(event_type.equalsIgnoreCase("topup"))
					{
						sub_top="topup";
						event_type="SUB";
					}

				System.out.println("calling proc:REL_BILLING_"+event_type.toUpperCase()+"_"+status.toUpperCase());
				cstmt = con1.prepareCall("{call REL_BILLING_"+event_type.toUpperCase()+"_"+status.toUpperCase()+"(?,?,?,?,?,?,?,?,?,?,?,?)}");
				cstmt.setString(1, billing_ID);
				cstmt.setString(2, msisdn);
				cstmt.setString(3, sub_top);
				cstmt.setString(4, response);
				cstmt.setString(5, chr_amt);
				cstmt.setString(6, amount);
				cstmt.setString(7, pre_post);
				cstmt.setString(8, service_id);
				cstmt.setString(9, plan_id);
				cstmt.setString(10, trans_id);
				cstmt.setString(11, date_start);
				cstmt.setString(12, date_end);
				cstmt.execute();
				cstmt.close();
			}
			else
			{
				System.out.println("calling proc:REL_BILLING_"+event_type.toUpperCase()+"_"+status.toUpperCase());
				cstmt = con1.prepareCall("{call REL_BILLING_"+event_type.toUpperCase()+"_"+status.toUpperCase()+"(?,?,?,?,?,?,?,?,?,?,?,?)}");
				cstmt.setString(1, billing_ID);
				cstmt.setString(2, msisdn);
				cstmt.setString(3, event_type);
				cstmt.setString(4, response);
				cstmt.setString(5, chr_amt);
				cstmt.setString(6, amount);
				cstmt.setString(7, pre_post);
				cstmt.setString(8, service_id);
				cstmt.setString(9, plan_id);
				cstmt.setString(10, trans_id);
				cstmt.setString(11, date_start);
				cstmt.setString(12, date_end);
				cstmt.execute();
				cstmt.close();

			}//else ends
		}//if operator ends
	}//try ends
	catch(Exception e)
	{
		hunLog(e.toString(),'e');
		e.printStackTrace();
	}
}//fun ends
//===============================================================


public String checkBalence(String msisdn,String operator)
	{

        String bal="";

       	 try
		 {
			if("TATM".equalsIgnoreCase(operator))
			{
				try
				{
					//http://119.82.69.210/parleyx/getBalance.php?tel=918699509743&spid=000022
					String balcheckURL = "http://119.82.69.210/parleyx/getBalance.php?tel=91";
					String spid ="000022";
					//System.out.println("MDN>>"+msisdn);
					balcheckURL=balcheckURL+msisdn+"&spid="+spid;
					URL balchk = new URL(balcheckURL);
					HttpURLConnection balchkconn = (HttpURLConnection)balchk.openConnection();
					String response ="";
					if(balchkconn.getResponseCode()== HttpURLConnection.HTTP_OK){

						//RESPONSE IS GOOD
						BufferedReader in = new BufferedReader(new InputStreamReader(balchkconn.getInputStream()));
						String line="";
						//System.out.println("*******************START*************************");
						while ((line= in.readLine()) != null)
						{
							// parse incoming lines for your data
							//System.out.println(line);
							response = response + line;
						}
						in.close();
						balchkconn.disconnect();
						//System.out.println("*******************END***************************");
						if(response.indexOf("Error")>=0)
							bal = "-100";
						else
						{
							String [] resultArr = response.split("\\.");
							bal = resultArr[0];
						}
					}
					else
					{
						response = "Its Not HTTP_OK"+balchkconn.getResponseCode();
						bal = "-400";
					}
				}catch(ConnectException e){
					e.printStackTrace();
					bal = "-500";
				}
				catch(Exception e)
				{
					e.printStackTrace();
					bal = "-300";
				}
			}
			else if(operator.equalsIgnoreCase("RELC") || operator.equalsIgnoreCase("RELM"))
				{
					bal = "100";
				}
			else if("uninor".equalsIgnoreCase(operator))
			{
				bal ="9900";
			}
		}
		catch(Exception e)
		{
			hunLog(e.toString(),'e');
			e.printStackTrace();
		}
		//System.out.println("user balence after charging "+ bal);
		return 	bal;
	}
//=======================================================
public String pre_post(String getBal)
	{
		String pre_post=null;
		try
		{
			if("-1".equalsIgnoreCase(getBal))
			 {
				pre_post = "postpaid";
			 }
			 else if("-100".equalsIgnoreCase(getBal) || "-200".equalsIgnoreCase(getBal) || "-300".equalsIgnoreCase(getBal) || "-400".equalsIgnoreCase(getBal) || "-500".equalsIgnoreCase(getBal))
			 {
				 pre_post = "unknown";
			 }
			 else
			 {
				 pre_post = "prepaid";
			 }
		}catch(Exception e)
		{
			pre_post = "unknown";
			hunLog(e.toString(),'e');
			e.printStackTrace();

		}
		return pre_post;
	}

//===============================================================
//=======================================================

	public String chargeBalance(String msisdn,String chargeamt,String operator,String circle,String event_type)
	{

        String code="";

      	 try
		 {
				    String chargingURL="";
					if("TATM".equalsIgnoreCase(operator))
				    {
				    	 try
				    	 {
							 int intchargeamt =  Integer.parseInt(chargeamt)*100;
							 String response ="";
								//http://119.82.69.210/parleyx/chargeAmount.php?tel=918699509743&amount=2&contentName=54646&chargeReasonType=1
								if("SUB".equalsIgnoreCase(event_type))
								  event_type = "1";
								else if("RESUB".equalsIgnoreCase(event_type))
								  event_type = "2";
								else
								 event_type = "0";

					    	 chargingURL	= " http://119.82.69.210/parleyx/chargeAmount.php?tel=91";
					    	 chargingURL=chargingURL+msisdn+"&amount="+intchargeamt+"&contentName=Endless%20Music&chargeReasonType="+event_type;
						     URL chargrequest = new URL(chargingURL);
						     HttpURLConnection chargingconn = (HttpURLConnection)chargrequest.openConnection();
					         if(chargingconn.getResponseCode()== HttpURLConnection.HTTP_OK)
					         {
						       BufferedReader in = new BufferedReader(new InputStreamReader(chargingconn.getInputStream()));
						       String line="";
						       System.out.println("*******************START*************************");
						       while ((line=in.readLine()) != null)
						       {
						    	   System.out.println(line);
						    	   response = response + line;
						       }
						       System.out.println("*******************END*************************");
						       in.close();
						       chargingconn.disconnect();
						      // if(response.indexOf("Error")>0)
						    	 //  code = "NOK";
						       code = response;

					         }
					         else
					         {

					        	 code = "NOK#NOK";
					         }
				    	 }catch(Exception e)
				    	 {
							 hunLog(e.toString(),'e');
				    		 e.printStackTrace();
				    		 code = "NOK#NOK";
				    	 }
				    }
		else if("RELC".equalsIgnoreCase(operator) || "RELM".equalsIgnoreCase(operator))
				    {
				    	 try
				    	 {
							 String response ="";
							if(event_type.equalsIgnoreCase("SUB"))
							{
							   event_type="1";
							   chargingURL="http://119.82.69.210/billing/reliance_billing/reliance_billing_interface.php?action="+event_type+"&mdn="+msisdn+"&appid=236";
						     }
							 else if(event_type.equalsIgnoreCase("RESUB"))
							 {
							   event_type="2";
							   chargingURL="http://119.82.69.210/billing/reliance_billing/reliance_billing_interface.php?action="+event_type+"&mdn="+msisdn+"&appid=236";
						      }
							 else if(event_type.equalsIgnoreCase("TOPUP"))
							  {
								 chargingURL="http://119.82.69.210/billing/reliance_billing/reliance_top_up.php?mdn="+msisdn+"&topupprice="+chargeamt;
								  event_type="0";
							  }

							 //chargingURL	= "http://202.138.125.19:8090/cgi-bin/externalrouting/routing.cgi?type=28&appid=236&channelid=2&action=1&mdn=";
							 System.out.println(chargingURL);

							 	 URL chargrequest = new URL(chargingURL);
							 HttpURLConnection chargingconn = (HttpURLConnection)chargrequest.openConnection();
							 if(chargingconn.getResponseCode()== HttpURLConnection.HTTP_OK)
							 {
							   BufferedReader in = new BufferedReader(new InputStreamReader(chargingconn.getInputStream()));
							   String line="";
							   System.out.println("*******************START*************************");
							   while ((line=in.readLine()) != null)
							   {
								   System.out.println(line);
								   response = response + line;
							   }
							   System.out.println("*******************END*************************");
							   in.close();
							   chargingconn.disconnect();
							   code = response;
							 }
							 else
							 {
								 code = "NOK#NOK";
								 //code = "Its Not HTTP_OK"+chargingconn.getResponseCode();
							 }
						 }//try ends
						  catch(Exception e)
						 {
							 hunLog(e.toString(),'e');
							 e.printStackTrace();
							 code = "NOK#NOK";
				    	 }

				    }//ele if emds
		}
		catch(Exception e)
		{
			hunLog(e.toString(),'e');
			code = "NOK";
		}
		return 	code;
	}
//=======================================================
//========================================================

public String getAmt(String type_of_plan,String plan_id,String operator,String amt,String bal,String fall_back_seqs)
{
	String amount=amt;
	try
	{
		switch(plan_id.charAt(0))
		{
			case'0':
					System.out.println("Fixed Charging -"+ operator);
					if (Integer.parseInt(amount)<=Integer.parseInt(bal))
					return amount;
					else
					return "-2";
			  //break;
			case'1':
					System.out.println("Flexiable Charging -"+ operator);
					String fall_back_plans[] = fall_back_seqs.split(",");
					for(int i=0;i<fall_back_plans.length;i++)
					{
						 String amount_plan[] = fall_back_plans[i].split("@");
						 if(Integer.parseInt(bal) >= Integer.parseInt(amount_plan[0]))
						 {
							 return amount_plan[1];

						 }

					}
					return "-2";
			  //break;
			case'2':
					System.out.println("Negitive Charging -"+ operator);
					return amount;
			//  break;
			case'4':
					System.out.println("Designed only for TATA DoCoMo");
					if(bal.equals("-1"))
					{
						//POST-PAID
						return amount;
					}
					else
					{
						//PRE-PAID
						int bal1=Integer.parseInt(bal);
						bal1=bal1/100;
						if(bal1>1)
						{

							if(bal1>Integer.parseInt(amt))
							{

								return amt;
							}
							else
							{
								if((bal1%2)==0)
								  bal1=bal1-2;
								else
								  bal1=bal1-1;
							}

							  if(bal1>0)
							  {
								  bal1=bal1;
								  return ""+bal1;
							  }
							  return "-2";
						  }//if bal>0 ends
						  return"-2";

					}//else ends

			//  break;
			  default:
			          	System.out.println("Unknown Charging Plan  and Model");
			          	hunLog("#"+type_of_plan+"#"+plan_id+"#"+operator+"#"+amount+"#"+bal,'e');
			  break;
		}//switch ends
	}//try ends
	catch(Exception e)
	{
		hunLog(e.toString(),'e');
		e.printStackTrace();

	}
	return"-3";
}



//========================================================
//========================================================

public void hunLog(String log,char file)
	{

		try
		{
			Calendar mytoday = Calendar.getInstance();
			String mystrdate = formatN(""+mytoday.get(Calendar.YEAR),4) + formatN(""+(mytoday.get(Calendar.MONTH)+1),2) + formatN(""+mytoday.get(Calendar.DATE),2);
			String mystrtime = formatN(""+mytoday.get(Calendar.HOUR_OF_DAY),2)+formatN(""+mytoday.get(Calendar.MINUTE),2)+formatN(""+mytoday.get(Calendar.SECOND),2);
			System.out.println("Writing  Sending Logs -"+ log);
			switch(file)
			{
			case'e':
				if(strdate.equals(mystrdate))
				{
					err_log.info("#"+mystrdate+"#"+mystrtime+"#"+log);
				}
				else
				{
					mnthdir=formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
					Path="/home/ivr/javalogs/BillingMnger/Sender/"+mnthdir+"/";
					dir=new File(Path);
					if(!dir.exists())
					  dir.mkdirs();

					err_App = new FileAppender(new PatternLayout(),Path+"Error_"+strdate+".log");
					err_App.setAppend(true);
					err_log = Logger.getLogger("ErrLogger");
					err_log.addAppender(err_App);
					err_log.info("#"+mystrdate+"#"+mystrtime+"#"+log);
				 }

			break;

			case's':
				if(strdate.equals(mystrdate))
				{
					logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
				}
				else
				{
					mnthdir=formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
					Path="/home/ivr/javalogs/BillingMnger/Sender/"+mnthdir+"/";
					dir=new File(Path);
					if(!dir.exists())
					  dir.mkdirs();

					appender = new FileAppender(new PatternLayout(),Path+"Sender_"+strdate+".log");
					appender.setAppend(true);
					logger = Logger.getLogger("Logger");
					logger.addAppender(appender);
					logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
				 }

			break;
			}//swtich ends
		}
		catch(Exception e)
		{
			System.out.println("Error @hunlog"+e);
		}


	}
//========================================================
private static String formatN(String str, int x)
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
//========================================================
//===============================================================
public static void main(String args[])
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


		String connStr = "jdbc:mysql://"+ip+"/"+dsn;
		System.out.println(connStr);
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		con= DriverManager.getConnection(connStr, username, pwd);
		System.out.println("[CONNECTION WITH MYSQL  DATABASE ESTABLISHED]");



		today = Calendar.getInstance();
		strdate = formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
		strtime = formatN(""+today.get(Calendar.HOUR_OF_DAY),2)+formatN(""+today.get(Calendar.MINUTE),2)+formatN(""+today.get(Calendar.SECOND),2);
		mnthdir=formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
		Path="/home/ivr/javalogs/BillingMnger/sender/"+mnthdir+"/";
		dir=new File(Path);
		if(!dir.exists())
			dir.mkdirs();


		appender = new FileAppender(new PatternLayout(),Path+"Sender_"+strdate+".log");
		appender.setAppend(true);
		logger = Logger.getLogger("Logger");
		logger.addAppender(appender);
		//========================Error Logger================================
		err_App = new FileAppender(new PatternLayout(),Path+"Error_"+strdate+".log");
		err_App.setAppend(true);
		err_log = Logger.getLogger("ErrLogger");
		err_log.addAppender(err_App);


		System.out.println(new BillingMnger(1));




	}
	catch(Exception e)
	{
		System.out.println("Error In DB Connection "+e);
	}


}//main ends

}//class ends
