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

	public class VODMreciver extends Thread
	{
//		HNG_ENTRMNTPORTAL_T_30
//		HNG_ENTRMNTPORTAL_T_10
//	HNG_ENTRMNTPORTAL_T_3
	public static String url_recv = ActiveMQConnection.DEFAULT_BROKER_URL;
	public static String url_send = ActiveMQConnection.DEFAULT_BROKER_URL;
	public Destination destination_recv,destination_send,destinationQ;
	public Session session_recv,session_send,sessionQ;
	public MessageConsumer consumer;
	public MessageProducer producer,producerQ;
	public TextMessage message_send,message_recv,messageQ;
	javax.jms.Connection connection_recv,connection_send,connectionQ;
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

	public VODMreciver()
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

//=========================================
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
		String descp=null;
		//String top_resp=null;
		String chargeamt=null;
		String fall_back_seqs=null;
		String type_of_plan=null;
		String setPlan_id=null;
		String plan_id=null;
		String in_mode=null;
		try
		{
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
	        System.out.println("Active message Queue established!");
	      //==============LOGGING=============================
			messageQ = new ActiveMQTextMessage();
			connectionQ = connectionFactory_send.createConnection();
			connectionQ.start();
			sessionQ = connectionQ.createSession(false,Session.AUTO_ACKNOWLEDGE);
			destinationQ = sessionQ.createQueue("HUNLOG");
			producerQ = sessionQ.createProducer(destinationQ);
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
	                     in_mode=in_msg[10];
	                }
	                if("VODM".equalsIgnoreCase(operator))
	                {
						 String out_string="",pre_post="NA";
						 useravailbal="NA";
						 setPlan_id=plan_id;

						if("SUB".equalsIgnoreCase(event_type)|| "RESUB".equalsIgnoreCase(event_type))
						{

							 String[] temp = chargeBalance(msisdn,operator,circle,event_type,service_id,in_mode,amount).split("#");
							 code 	  = temp[0];
							 trans_id = temp[1];
							 descp	  = temp[2];


							 if("ok".equalsIgnoreCase(code))
							 {
								 out_string = operator+"#OK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+useravailbal+"#"+chargeamt+"#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#"+trans_id+"#"+descp+"#"+in_mode;
								 hunLog(out_string,'t');
							 }
							 else
							 {
								 out_string = operator+"#NOK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+useravailbal+"#0#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#"+trans_id+"#"+descp+"#"+in_mode;
								 hunLog(out_string,'t');
								 message_send.setText(out_string);
								 producer.send(message_send);
							 	 System.out.println(" Sent message '" + message_send.getText());
							 }
						 }
						 else
						 {

							//useravailbal=checkBalence(msisdn,operator);
							//pre_post = pre_post(useravailbal);
							//chargeamt=getAmt(type_of_plan,"4",operator,amount,useravailbal,fall_back_seqs);
							 String[] temp = chargeBalance(msisdn,operator,circle,event_type,service_id,in_mode,amount).split("#");
							 descp=temp[0];
							 code = temp[1];
							 trans_id = temp[2];
							 chargeamt  = temp[3];
							 pre_post="NA";

							if("ok".equalsIgnoreCase(code))
							 {
								 out_string = operator+"#OK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+useravailbal+"#"+chargeamt+"#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#"+trans_id+"#"+descp+"#"+in_mode;
							 }
							 else
							 {
								 out_string = operator+"#NOK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+useravailbal+"#0#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#"+trans_id+"#"+descp+"#"+in_mode;
							 }


							 hunLog(out_string,'t');
							 message_send.setText(out_string);
							 producer.send(message_send);
							 System.out.println(" Sent message '" + message_send.getText());
						 }
	                }
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


	public String chargeBalance(String msisdn,String operator,String circle,String event_type,String service_id,String mode,String amount)
	{

        String code="";

      	 try
		 {
				    String chargingURL="";
					if("VODM".equalsIgnoreCase(operator))
				    {
				    	 try
				    	 {
							 String response="",event_id="";
							 if(service_id.equals("1302"))
								event_id="HNG_ENTRMNTPORTAL";
							 else if(service_id.equals("1307"))
								event_id="HNG_VH1MUSIC";
							 else if(service_id.equals("1310"))
									 	event_id="HNG_REDFM";


								if("SUB".equalsIgnoreCase(event_type)|| "RESUB".equalsIgnoreCase(event_type))
								{
									mode=mode.toUpperCase();
									if(mode.indexOf("9XM")>-1 || mode.indexOf("9XT")>-1 || mode.indexOf("MTV")>-1 || mode.indexOf("RFM")>-1||mode.indexOf("9xm")>-1 || mode.indexOf("9xM")>-1  ||mode.indexOf("9Xt")>-1 ||mode.indexOf("9xT")>-1|| mode.indexOf("RFM")>-1 )
									{
										mode=mode.substring(0,mode.indexOf("-"));//changes done as per mail by Yogesh Khaushik on 07-02-2012
									}
									else if(mode.equalsIgnoreCase("NETB")||mode.equalsIgnoreCase("NET-MS"))
										mode="NET";
									else if(mode.equalsIgnoreCase("OBD-MS") || mode.equalsIgnoreCase("OBD-LBR") || mode.equalsIgnoreCase("OBD-MPMC"))
										mode="OBD";
									else if(mode.equalsIgnoreCase("IVR-MS") || mode.equalsIgnoreCase("IVR-LBR") || mode.equalsIgnoreCase("IVR-MPMC")||mode.equalsIgnoreCase("AA_WEB"))
 										mode="IVR";
									else if(mode.equalsIgnoreCase("USSD-MS"))
										mode="USSD";

									 chargingURL="http://10.43.248.137/VodafoneBilling/vodafoneMSGBilling.php?msisdn="+msisdn+"&req=act&reqMode="+mode+"&eventType="+event_id;


								  }
								  else if("TOPUP".equalsIgnoreCase(event_type))
								  {
									  response="";
									  event_id=event_id+"_T_"+amount;
									  chargingURL="http://10.43.248.137/VodafoneBilling/vodafoneOCGBilling.php?msisdn="+msisdn+"&Tcharge="+amount+"&eventType="+event_id;
								  }
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
				    	 }
				    	 catch(Exception e)
				    	 {
							 hunLog(e.toString(),'e');
				    		 e.printStackTrace();
				    		 code = "NOK#NOK";
				    	 }
				    }

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
	}//========================================================

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
public void hunLog(String log,char file)
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
				messageQ.setText("Error#"+log);
				producerQ.send(messageQ);
			//Error Logs
				/*	if(strdate.equals(mystrdate))
					{
						logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
					}
					else
					{
						mnthdir=formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
						errPath="../reciver/"+mnthdir+"/";
						dir=new File(errPath);
						if(!dir.exists())
						dir.mkdir();

						strdate = mystrdate;
						err_App = new FileAppender(new PatternLayout(),errPath+"ERROR_"+strdate+".log");
						err_App.setAppend(true);

						logger = Logger.getLogger("Hun-ER-Logger");
						logger.addAppender(err_App);
						logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
					}*/
			break;
			case'T':
			case't':
				messageQ.setText("Reciver#VODM#"+log);
				producerQ.send(messageQ);
			//TATA Operator Logs
					/*if(strdate.equals(mystrdate))
					{
						tata_logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
					}
					else
					{
						mnthdir=formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2);
						errPath="../reciver/"+mnthdir+"/";
						dir=new File(errPath);
						if(!dir.exists())
						dir.mkdir();

						strdate = mystrdate;
						tata_app = new FileAppender(new PatternLayout(),errPath+"VODM_"+strdate+".log");
						tata_app.setAppend(true);


						tata_logger = Logger.getLogger("Hun-TD-Logger");
						tata_logger.addAppender(tata_app);
						tata_logger.info("#"+mystrdate+"#"+mystrtime+"#"+log);
					}*/
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
public String checkBalence(String msisdn,String operator)
	{

        String bal="";

       	 try
		 {
				    if("VODM".equalsIgnoreCase(operator))
				    {
				    	try
				    	{

								String balcheckURL ="http://10.22.8.43:80/BalValExt/SMPeriodicBalanceCheck?uid=HUNGAMA&pwd=1Hun@tst&msisdn=91"+msisdn+"&imsi=12345"+msisdn+"&eventid=&tCharge=&reqid=&wapurl=&cSize=&device=&wapnode=&reqtype=ivr&inid=54646&indata=&invoiceid=";
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

		}
		catch(Exception e)
		{
			hunLog(e.toString(),'e');
			e.printStackTrace();
		}
		//System.out.println("user balence after charging "+ bal);
		return 	bal;
	}
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
		/*errPath="../reciver/"+mnthdir+"/";
		dir=new File(errPath);
		if(!dir.exists())
		    dir.mkdirs();

		err_App = new FileAppender(new PatternLayout(),errPath+"ERROR_"+strdate+".log");
		err_App.setAppend(true);

		logger = Logger.getLogger("Hun-ER-Logger");
		logger.addAppender(err_App);

		//======================TATA Appender=================
		tata_app = new FileAppender(new PatternLayout(),errPath+"VODM_"+strdate+".log");
		tata_app.setAppend(true);
		tata_logger = Logger.getLogger("Hun-TD-Logger");
		tata_logger.addAppender(tata_app);*/



		/*****************************************************/
	    }
	    catch(Exception e)
	    {
			System.out.println("Error @main"+e);
		}

		VODMreciver rBM = new VODMreciver();
		rBM.start();
	}

}


