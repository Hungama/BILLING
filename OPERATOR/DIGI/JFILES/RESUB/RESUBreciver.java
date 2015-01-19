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

	public class RESUBreciver extends Thread{
	public static String url_recv = ActiveMQConnection.DEFAULT_BROKER_URL+"?wireFormat.cacheEnabled=false&wireFormat.tightEncodingEnabled=false";
	public static String url_send = ActiveMQConnection.DEFAULT_BROKER_URL+"?wireFormat.cacheEnabled=false&wireFormat.tightEncodingEnabled=false";
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

	public RESUBreciver()
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
		String mode=null;
		String hun_mode=null;
		String res_code=null;
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
	               // for(int i=0;i<in_msg.length;i++)
	                //{
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
	                     mode=in_msg[10];
	                     hun_mode=mode;

	                     if("TIVR".equalsIgnoreCase(mode))
	                     	hun_mode="IVR";
	                     else if("TUSSD".equalsIgnoreCase(mode))
	                     	hun_mode="USSD";
	                     else if("TPCN".equalsIgnoreCase(mode))
	                     	hun_mode="PCN";
	                     else if("TOBD".equalsIgnoreCase(mode)||"OBD".equalsIgnoreCase(mode)||"HUNOBD".equalsIgnoreCase(mode)||"WAP".equalsIgnoreCase(mode))
	                     	hun_mode="IVR";

	                if("DIGM".equalsIgnoreCase(operator))
	                {
						setPlan_id=plan_id;
						 String out_string="",pre_post="";
	                		 String temp[]=(chargeAmount(msisdn,hun_mode,amount)).split("#");

	                		 code = temp[0];
							 trans_id=temp[1];
							 res_code=temp[2];
							 chargeamt=temp[6];
							 pre_post="NA";
							//System.out.println("temp"+temp.length+"code ---> "+code);
							if(trans_id.equals("") || trans_id==null)
								trans_id="00";
							System.out.println("trans_id ---> "+trans_id+" res_code "+res_code);
							System.out.println("chargeamt ---> "+chargeamt);
							 if("ok".equalsIgnoreCase(code))
							 {
								 out_string = operator+"#OK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+useravailbal+"#"+chargeamt+"#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#"+trans_id;

							 }
							 else
							 {
							 	out_string = operator+"#NOK#"+billing_ID+"#"+msisdn+"#"+event_type+"#"+res_code+"#0#"+amount+"#"+pre_post+"#"+service_id+"#"+setPlan_id+"#"+trans_id;
							 }

						 hunLog(out_string,'t');
						 message_send.setText(out_string);
						 producer.send(message_send);
						 System.out.println(" Sent message '" + message_send.getText());
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
//  Appended on 27/12/11
//========================================================
public String chargeAmount(String ani, String mode,String amount)
{
	String price_code="";
		try
		{
			String retstr="",keyword="migseven",cpa_sid="Q4sv4jPncR+t32gSY3+Wrw==";
			if("1".equalsIgnoreCase(amount))
			{
			   keyword="migseven";
			   cpa_sid="Q4sv4jPncR8IMiDLcsWQCA==";
			   price_code="VAS220100";
		    }

			else if("3".equalsIgnoreCase(amount))
			{
				keyword="migthirty";
				cpa_sid="Q4sv4jPncR+t32gSY3+Wrw==";
				price_code="VAS220300";
			}
			else if("0.5".equalsIgnoreCase(amount))
			{
				keyword="migthree";
				cpa_sid="Q4sv4jPncR/7t1cUkJW2Wg==";
				price_code="VAS220050";
			}
		BillingProject gsk=new BillingProject();
		 System.out.println("inside chargeamount"+keyword+cpa_sid);
		//retstr= (gsk.ValidateAndBill(ani,"VAS220"+amount+"00",mode,keyword,cpa_sid));//VAS220100,VAS220300
		retstr= (gsk.ValidateAndBill(ani,price_code,mode,keyword,cpa_sid));//VAS220100,VAS220300

		System.out.println("retstr---> "+retstr);
		String[] temp =retstr.split("#");

		if(temp[0].equalsIgnoreCase("OK"))
		{
			return (retstr+"#"+amount);
		}
		else if(temp[0].equalsIgnoreCase("NOK") && temp[4].equalsIgnoreCase("202") && (amount.equals("3")|| amount.equals("1")))
		{

			if(amount.equals("3"))
			 {
				 amount="1";
				 cpa_sid="Q4sv4jPncR8IMiDLcsWQCA==";
				 retstr=chargeAmount(ani, mode,"1");
				  return retstr ;
			 }
			 else if(amount.equals("1"))
			 {
				 amount="0.5";
				 cpa_sid="Q4sv4jPncR/7t1cUkJW2Wg==";
				 retstr=chargeAmount(ani, mode,"0.5");
				  return retstr ;
			 }
			 else
			   return (retstr+"#"+amount);

		}
		else if(temp[0].equalsIgnoreCase("NOK") && temp[1].equalsIgnoreCase("-2"))
		{
					return (retstr+"#"+amount);
		}
		else
			return (retstr+"#"+amount);


	}
	catch(Exception e)
	{
		e.printStackTrace();
		hunLog(e.toString(),'e');


	}

return "NOK#-2#Error##Error#Error#Error";
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

			switch(file)
			{
			case'e':
				messageQ.setText("Error#"+log);
				producerQ.send(messageQ);

			break;
			case'T':
			case't':
				messageQ.setText("Reciver#"+log);
				producerQ.send(messageQ);

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
		/*dir=new File(errPath);
		if(!dir.exists())
		    dir.mkdir();

		/*err_App = new FileAppender(new PatternLayout(),errPath+"ERROR_"+strdate+".log");
		err_App.setAppend(true);

		logger = Logger.getLogger("Hun-ER-Logger");
		logger.addAppender(err_App);

		//======================TATA Appender=================
		tata_app = new FileAppender(new PatternLayout(),errPath+"DIGI_"+strdate+".log");
		tata_app.setAppend(true);
		tata_logger = Logger.getLogger("Hun-TD-Logger");
		tata_logger.addAppender(tata_app);*/

		//======================REL Appender=================
		//rel_app = new FileAppender(new PatternLayout(),errPath+"DIGI_"+strdate+".log");
		//rel_app.setAppend(true);
		//rel_logger = Logger.getLogger("Hun-RL-Logger");
		//rel_logger.addAppender(rel_app);


		/*****************************************************/
	    }
	    catch(Exception e)
	    {
			System.out.println("Error @main"+e);
		}

		RESUBreciver rBM = new RESUBreciver();
		rBM.start();
	}

}


//8595298259
//insert into master_db.tbl_billing_reqs(msisdn,event_type,date_time,amount,status,def_lang,SC,MODE,circle,operator,service_id,subservice_id,plan_id) values('9150628153','RESUB',now(),10,0,'02','54646','IVR','TNU','MTSM',1103,0,2);
