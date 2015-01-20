import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.log4j.*;
import java.util.*;
import java.io.*;


import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

	public class reciverBillingMnger extends Thread{
	public static String url_recv = ActiveMQConnection.DEFAULT_BROKER_URL;
	public static String url_send = ActiveMQConnection.DEFAULT_BROKER_URL;
	public Destination destination_recv,destination_send;
	public Session session_recv,session_send;
	public MessageConsumer consumer;
	public MessageProducer producer;
	public TextMessage message_send,message_recv;
	javax.jms.Connection connection_recv,connection_send;
    private static String subject_recv = "";
    private static String subject_send = "";
	public static Connection con=null;
	public static Statement stmt,stmtUpdate;
	public static CallableStatement cstmt=null;
/***************** logger Variable  **********/
    public static Calendar today = null;
	public static String strdate  = "",mnthdir="";
	public static String strtime  = "";
	public static String errPath="";
	public static FileAppender err_App = null,rel_app=null,tata_app=null;
	//public static Layout layout = null,tata_layout=null,rel_layout=null;
	public static Logger logger = null,tata_logger = null,rel_logger = null;
	public static File dir=null;
//=======================================================

	public reciverBillingMnger()
	{
		try
		{

		    ResourceBundle resource_source = ResourceBundle.getBundle("config/chargingmgr_source");
			String msgqueue_send=resource_source.getString("MSGQUEUE");
			ResourceBundle resource_destination = ResourceBundle.getBundle("config/chargingmgr_destination");
			String msgqueue_recv=resource_destination.getString("MSGQUEUE");
		    subject_recv = msgqueue_send;
		    subject_send = msgqueue_recv;
		}
		catch(Exception e)
		{
			hunLog(e.toString(),'e');
			e.printStackTrace();
			System.exit(0);
		}
	}
//=======================================================

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

	public String chargeBalance(String msisdn,String chargeamt,String operator,String circle,String event_type,String service_id)
	{

        String code="",appid="236";

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

							 if(service_id.equalsIgnoreCase("1203"))
							     appid="548";

							if(event_type.equalsIgnoreCase("SUB"))
							{
							   event_type="1";
							   chargingURL="http://119.82.69.210/billing/reliance_billing/reliance_billing_interface.php?action="+event_type+"&mdn="+msisdn+"&appid="+appid;
						     }
							 else if(event_type.equalsIgnoreCase("RESUB"))
							 {
							   event_type="2";
							   chargingURL="http://119.82.69.210/billing/reliance_billing/reliance_billing_interface.php?action="+event_type+"&mdn="+msisdn+"&appid="+appid;
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
//=======================================================
	public void run()
	{
		String billing_ID=null;
		String msisdn=null;
		String event_type=null;
		String amount=null;
		String service_id=null;
		String operator=null;
		String circle=null;
		String useravailbal=null;
		String code=null;
		String trans_id=null;
		String chargeamt=null;
		String fall_back_seqs=null;
		String type_of_plan=null;
		String setPlan_id=null;
		String plan_id=null;
		try
		{
			System.out.println("Active message Queue established!");
			message_send = new ActiveMQTextMessage();
			message_recv = new ActiveMQTextMessage();
			ConnectionFactory connectionFactory_recv = new ActiveMQConnectionFactory(url_recv);
			ConnectionFactory connectionFactory_send = new ActiveMQConnectionFactory(url_send);
		    connection_recv = connectionFactory_recv.createConnection();
		    connection_send = connectionFactory_send.createConnection();
		    connection_recv.start();
		    connection_send.start();
		    session_recv = connection_recv.createSession(false,Session.AUTO_ACKNOWLEDGE);
		    session_send = connection_send.createSession(false,Session.AUTO_ACKNOWLEDGE);
	        destination_recv = session_recv.createQueue(subject_recv);
	        destination_send = session_recv.createQueue(subject_send);
	        producer = session_send.createProducer(destination_send);
	        consumer = session_recv.createConsumer(destination_recv);
		}
		catch(Exception e)
		{
			hunLog(e.toString(),'e');
			e.printStackTrace();

		}
		while(true)
		{
			try
			{
				message_recv = (TextMessage) consumer.receive();
		        if (message_recv instanceof TextMessage)
		        {
	                TextMessage textMessage = (TextMessage) message_recv;
	                String in_string = textMessage.getText();
	                System.out.println(" Received message '"+ in_string + "'");
	                String in_msg[] = in_string.split("#");
	                for(int i=0;i<in_msg.length;i++)
	                {
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
	                }
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
							 String[] temp = chargeBalance(msisdn,chargeamt,operator,circle,event_type,service_id).split("#");
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
						 hunLog(out_string,'t');
						message_send.setText(out_string);
						 producer.send(message_send);
						 System.out.println(" Sent message '" + message_send.getText());
	                }
					else if("RELC".equalsIgnoreCase(operator) || "RELM".equalsIgnoreCase(operator))
					{
						 String out_string="",pre_post="NA";
						 String date_start=null,date_end=null;
						 chargeamt=amount;
						 setPlan_id=plan_id;
						 chargeamt=amount;


						 String[] temp = chargeBalance(msisdn,chargeamt,operator,circle,event_type,service_id).split(",");
						 //System.out.println("arry is "+temp[0]+"\t"+temp[1]+"\t"+temp[2]+"\t"+temp[3]);
						 if("0".equals(temp[1]))
						 {
							 trans_id   = temp[0];
							 code       = temp[1];
							 chargeamt  = temp[2];
							 date_start = temp[3];
							 date_end   = temp[4];
							 out_string = operator+"#OK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+useravailbal+"#"+chargeamt+"#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#"+trans_id+"#"+code+"#"+date_start+"#"+date_end;
						 }
						 else if("18".equals(temp[1]) || "10".equals(temp[1]))
						 {
							 trans_id   = temp[0];
							 code       = temp[1];
							 chargeamt  = temp[2];
							 date_start = temp[3];
							 date_end   = temp[4];
							 out_string = operator+"#OK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+useravailbal+"#"+"0#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#"+trans_id+"#"+code+"#"+date_start+"#"+date_end;
						 }
						 else if("17".equals(temp[1]))
						 {
							 trans_id   = temp[0];
							 code       = "17";
							 chargeamt  = "10";
							 date_start = "200620111330";
							 date_end   = "300620111330";
							 out_string = operator+"#OK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+useravailbal+"#"+chargeamt+"#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#"+trans_id+"#"+code+"#"+date_start+"#"+date_end;
						 }
						 else
						 {
							 trans_id = temp[0];
							 code = temp[1];
							 if("success".equalsIgnoreCase(code))
							   out_string = operator+"#OK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+useravailbal+"#"+chargeamt+"#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#"+trans_id+"#"+code+"#"+date_start+"#"+date_end;
							 else
							   out_string = operator+"#NOK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+useravailbal+"#0#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#"+trans_id+"#"+code+"#"+date_start+"#"+date_end;
							   //if(!("0".equals(temp[1])) || "NOK".equalsIgnoreCase(temp[1]))
						 }
							hunLog(out_string,'r');
							message_send.setText(out_string);
							producer.send(message_send);
							System.out.println(" Sent message '" + message_send.getText());
					}//else if
	            }//try
			}
			catch(Exception e)
			{
				e.printStackTrace();
				hunLog(e.toString(),'e');
			}
		}//while

	}
//========================================================
//  Appended on 07/06/11
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
//  Appended on 06/06/11
//========================================================
public static void hunLog(String log,char file)
	{

		try
		{
			Calendar mytoday = Calendar.getInstance();
			String mystrdate = formatN(""+mytoday.get(Calendar.YEAR),4) + formatN(""+(mytoday.get(Calendar.MONTH)+1),2) + formatN(""+mytoday.get(Calendar.DATE),2);
			String mystrtime = formatN(""+mytoday.get(Calendar.HOUR_OF_DAY),2)+formatN(""+mytoday.get(Calendar.MINUTE),2)+formatN(""+mytoday.get(Calendar.SECOND),2);
			System.out.println("here----"+ file+"-------"+log);
			switch(file)
			{
			case'e':
			//Error Logs
					if(strdate.equals(mystrdate))
					{
						logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
					}
					else
					{
						mnthdir=formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
						errPath="/home/ivr/javalogs/BillingMnger/reciver/"+mnthdir+"/";
						dir=new File(errPath);
						if(!dir.exists())
						dir.mkdir();

						strdate = mystrdate;
						err_App = new FileAppender(new PatternLayout(),errPath+"ERROR_"+strdate+".log");
						err_App.setAppend(true);

						logger = Logger.getLogger("Hun-ER-Logger");
						logger.addAppender(err_App);
						logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
					}
			break;
			case'T':
			case't':
			//TATA Operator Logs
					if(strdate.equals(mystrdate))
					{
						tata_logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
					}
					else
					{
						mnthdir=formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
						errPath="/home/ivr/javalogs/BillingMnger/reciver/"+mnthdir+"/";
						dir=new File(errPath);
						if(!dir.exists())
						dir.mkdir();

						strdate = mystrdate;
						tata_app = new FileAppender(new PatternLayout(),errPath+"TATA_"+strdate+".log");
						tata_app.setAppend(true);


						tata_logger = Logger.getLogger("Hun-TD-Logger");
						tata_logger.addAppender(tata_app);
						tata_logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
					}
			break;
			case'r':
			case'R':
			      if(strdate.equals(mystrdate))
					{
						rel_logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
					}
					else
					{
						mnthdir=formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
						errPath="/home/ivr/javalogs/BillingMnger/reciver/"+mnthdir+"/";
						dir=new File(errPath);
						if(!dir.exists())
						dir.mkdir();

						strdate = mystrdate;
						rel_app = new FileAppender(new PatternLayout(),errPath+"REL_"+strdate+".log");
						rel_app.setAppend(true);

						rel_logger = Logger.getLogger("Hun-RL-Logger");
						rel_logger.addAppender(rel_app);
						rel_logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
					}

			break;
		}//swtich ends
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//System.out.println("Error @hunlog"+e);
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
//========================================================

	public static void main(String args[])
	{
       try
       {
       /******************************************************/

		today = Calendar.getInstance();
		strdate = formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
		strtime = formatN(""+today.get(Calendar.HOUR_OF_DAY),2)+formatN(""+today.get(Calendar.MINUTE),2)+formatN(""+today.get(Calendar.SECOND),2);
		mnthdir=formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
		errPath="/home/ivr/javalogs/BillingMnger/reciver/"+mnthdir+"/";
		dir=new File(errPath);
		if(!dir.exists())
		    dir.mkdir();

		err_App = new FileAppender(new PatternLayout(),errPath+"ERROR_"+strdate+".log");
		err_App.setAppend(true);

		logger = Logger.getLogger("Hun-ER-Logger");
		logger.addAppender(err_App);

		//======================TATA Appender=================
		tata_app = new FileAppender(new PatternLayout(),errPath+"TATA_"+strdate+".log");
		tata_app.setAppend(true);
		tata_logger = Logger.getLogger("Hun-TD-Logger");
		tata_logger.addAppender(tata_app);

		//======================REL Appender=================
		rel_app = new FileAppender(new PatternLayout(),errPath+"REL_"+strdate+".log");
		rel_app.setAppend(true);
		rel_logger = Logger.getLogger("Hun-RL-Logger");
		rel_logger.addAppender(rel_app);


		/*****************************************************/
	    }
	    catch(Exception e)
	    {
			System.out.println("Error @main"+e);
		}

		reciverBillingMnger rBM = new reciverBillingMnger();
		rBM.start();
	}

}


