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

public class HUNLOG extends Thread
{
	public static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	public Destination destination,destinationQ;
	public Session session,sessionQ;
	public MessageConsumer consumer;
	public TextMessage message,messageQ;
	javax.jms.Connection connection,connectionQ;


	public void run()
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
			String plan_id = null;
			String response = null;
			String date_start=null,date_end=null;
			String sub_top=null;
			try
			{

				message = new ActiveMQTextMessage();
				ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			    connection = connectionFactory.createConnection();
			    connection.start();
			    session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
		        destination = session.createQueue("HUNLOG");
		        consumer = session.createConsumer(destination);
		        System.out.println("Active message Queue established!");


			}
			catch(Exception e)
			{

				e.printStackTrace();
			}

			while(true)
			{
				try
				{
					message = (TextMessage) consumer.receive();
					if (message instanceof TextMessage)
					{
						TextMessage textMessage = (TextMessage) message;
						String in_string = textMessage.getText();



						String in_msg[] = in_string.split("#");
						operator=in_msg[0];
						if("ERROR".equalsIgnoreCase(operator))
						{
						  hunLog(in_string,"Error");
						}
						else if("Logger".equalsIgnoreCase(operator))
						{
						  hunLog(in_string,"Logger");
						}
						else if("Reciver".equalsIgnoreCase(operator))
						{
						  hunLog(in_string,"Reciver");
						}
						else if("Sender".equalsIgnoreCase(operator))
						{
						  hunLog(in_string,"Sender");
						}
						else if("SMS".equalsIgnoreCase(operator))
						{
							 hunLog(trim(in_string),"SMS");
						}
						else
						{
						  hunLog(in_string,operator);
						}



					}
				}//try ends
				catch(Exception e)
				{

					e.printStackTrace();
				}
			}//while ends

		}//run ends
//==============================================
//==============================================
public String trim(String in_string)
{
	String retstr;
	try
	{
	   	in_string=in_string.substring(in_string.indexOf("#")+1,in_string.length());
	}
	catch(Exception e)
	{
		System.out.println("Error @ trim "+ e);
	}
	return in_string;
}


public void hunLog(String log,String filename)
		{

			try
			{

				//System.out.println("writing log under"+filename+"\t"+log);
				Calendar mytoday = Calendar.getInstance();
				String mystrdate = formatN(""+mytoday.get(Calendar.YEAR),4) + formatN(""+(mytoday.get(Calendar.MONTH)+1),2) + formatN(""+mytoday.get(Calendar.DATE),2);
				String mystrtime = formatN(""+mytoday.get(Calendar.HOUR_OF_DAY),2)+formatN(""+mytoday.get(Calendar.MINUTE),2)+formatN(""+mytoday.get(Calendar.SECOND),2);
				String hdate=""+formatN(""+mytoday.get(Calendar.YEAR),4) + formatN(""+(mytoday.get(Calendar.MONTH)+1),2);
				String path="/home/ivr/javalogs/BillingMnger/"+filename+"/"+hdate+"/";
				//System.out.println("path ---> "+path );
				File dir=new File(path);
				if(!dir.exists())
				dir.mkdirs();

				FileOutputStream fos= new FileOutputStream(path+mystrdate+".log",true);
				PrintStream ps=new PrintStream(fos);
				ps.println(mystrtime+"#"+log);
				ps.close();
				fos.close();


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
	public static void main(String gsk[])
	{
		HUNLOG ob=new HUNLOG();
		ob.start();
	}


}//class ends