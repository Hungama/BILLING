import hungamalogging.hungamalogging;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
//import org.apache.log4j.FileAppender;
//import org.apache.log4j.Logger;

public class Etislatdblogger extends Thread
{
	public static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	public Destination destination,destinationid;
	public MessageProducer producerid;
	public Session session,sessionid;
	public MessageConsumer consumer,consumerid;
	public TextMessage message,messageid;
	javax.jms.Connection connection,connectionid;
    private static String subject,subjectid = "";
	public static Connection con=null;
	public static String dsmlogs="/home/ivr/javalogs/Etislat";

	public static String ip=null,dsn=null,username=null,pwd=null;
	public String msgqueue=null;

	public Etislatdblogger(String name)
	{
		try
		{

		    ResourceBundle resource = ResourceBundle.getBundle("config/chargingmgr_destination");
			ip=resource.getString("IP");
			dsn=resource.getString("DSN");
			username=resource.getString("USERNAME");
			pwd=resource.getString("PWD");
			subject=resource.getString("MSGQUEUE");
			subjectid=resource.getString("UPDATEQUEUE");
		    System.out.println("IP: "+ip+" DATABASE :"+dsn+" USER :"+username+" PWD:"+pwd);
		    this.setName(name);
		    System.out.println("starting new Thread");
		    dbConn();
			start();


		}
		catch(Exception e)
		{
			hungamalogging.log("dblogger#"+e,"ExceptionLog_",dsmlogs);
			e.printStackTrace();
			System.exit(0);
		}
	}
	public static Connection dbConn()
	{
		try
		{
			if(con==null || con.isClosed())
			{
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn, username, pwd);
				System.out.println("Database Connection established!");
			}

		}
		catch(Exception e)
		{

			hungamalogging.log("dblogger creating connection#"+e,"ExceptionLog_",dsmlogs);
			e.printStackTrace();
			try
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e1)
			{
				// TODO Auto-generated catch block
				hungamalogging.log(e.toString(),"ExceptionLog_","/home/ivr/javalogs/Etislat");
				e1.printStackTrace();
			}
			System.exit(0);
		}

		return con;
	}
	public void run()
	{
		System.out.println("Calling to updatemethods");
		if(this.getName().equals("1"))
			UpdateMsgId();
		else
			updateBilling();

	}
	public void updateBilling()
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
		String mode=null;
		String in_string="";
		try
		{

			message = new ActiveMQTextMessage();
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		    connection = connectionFactory.createConnection();
		    connection.start();
		    session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
	        destination = session.createQueue(subject);
	        consumer = session.createConsumer(destination);
	        System.out.println("Active message Queue established!");
	    }
		catch(Exception e)
		{
			hungamalogging.log("sender creating queue#"+e,"ExceptionLog_",dsmlogs);
			e.printStackTrace();
			System.exit(0);
		}
		while(true)
		{
			try
			{
				message = (TextMessage) consumer.receive();
				 if(con==null || con.isClosed())
				dbConn();
		        if (message instanceof TextMessage)
		        {
	                TextMessage textMessage = (TextMessage) message;
	                in_string = textMessage.getText();
	                System.out.println(" DBlogger message '"+ in_string + "'");
	                hungamalogging.log(in_string,"dblogger_",dsmlogs);
	                String in_msg[] = in_string.split("#");
	                operator=in_msg[0];
	               // Etislat#NOK#443380017#2348170561151#SUB#NA#75#75#NA#2121#115#443380017#NA#SMS#lowbal'
	              // 'Etislat#NOK#443380017#2348170561151#SUB#NA#75#75#NA#2121#115#443380017#SMS#lowbal'
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
					mode   = in_msg[12];
					response       = in_msg[13];
					//mode       = in_msg[13];
					System.out.println(operator+"#"+trans_id+"#"+mode);
					Thread.sleep(150);
					if("ok".equalsIgnoreCase(status))
					{
					//	Thread.sleep(100);
					    try{billing_ID=""+Long.parseLong(billing_ID);}catch(Exception e){}
						System.out.println("calling  biiling sub ok"+billing_ID+","+msisdn+","+ event_type+","+ avl_amt+","+ chr_amt+","+ amount+","+ pre_post+","+ service_id+","+ plan_id+","+ trans_id+","+trans_id);
						CallableStatement cstmt=null;
						cstmt = con.prepareCall("{call etislat_billing_"+status.toUpperCase()+"(?,?,?,?,?,?,?,?,?,?,?)}");
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
						cstmt.setString(11, trans_id);
						cstmt.execute();
						cstmt.close();
					}
					else
					{
						System.out.println("calling  biiling sub not ok"+billing_ID+","+msisdn+","+ event_type+","+ avl_amt+","+ chr_amt+","+ amount+","+ pre_post+","+ service_id+","+ plan_id+","+ trans_id+","+trans_id);
						CallableStatement cstmt=null;
						cstmt = con.prepareCall("{call etislat_billing_"+status.toUpperCase()+"(?,?,?,?,?,?,?,?,?,?,?)}");

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
						cstmt.setString(11, trans_id);
						cstmt.execute();
						cstmt.close();
						System.out.println(" response '"+ response + "'");
					}
					System.out.println("called procedure etislat_billing_"+status.toUpperCase());

		        }//if message ends
			}//try ends
			catch(Exception e)
			{
				hungamalogging.log("Exception in calling prco updatebiling#"+e+"#"+in_string,"ExceptionLog_",dsmlogs);
				e.printStackTrace();
				try
				{
					if(con==null || con.isClosed())
					{
						dbConn();
					}
				}
				catch(Exception ex)
				{
					System.exit(0);
				}

			}
		}

	}
	public void UpdateMsgId()
	{
		String operator=null;
		String status=null;
		String billing_ID=null;
		String msisdn=null;
		String event_type=null;
		String service_id=null;
		String trans_id=null;
		String plan_id = null;
		String mode=null;
		String in_string="";

		try
		{
			messageid = new ActiveMQTextMessage();
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			connectionid = connectionFactory.createConnection();
			connectionid.start();
			sessionid = connectionid.createSession(false,Session.AUTO_ACKNOWLEDGE);
			destinationid = sessionid.createQueue(subjectid);
			consumerid = sessionid.createConsumer(destinationid);
			System.out.println("Active message Queue established!");
		}
		catch(Exception e)
		{
			hungamalogging.log("dblogger creating queue#"+e,"ExceptionLog_",dsmlogs);
			e.printStackTrace();
			System.exit(0);
		}
		while(true)
		{
			try
			{
				messageid = (TextMessage) consumerid.receive();
				if (messageid instanceof TextMessage)
				{
					TextMessage textMessage = (TextMessage) messageid;
					in_string = textMessage.getText();
					System.out.println(" DBlogger message '"+ in_string + "'");

					hungamalogging.log(in_string,"dblogger_",dsmlogs);
					String in_msg[] = in_string.split("#");
					operator=in_msg[0];
					status     = in_msg[1];
					billing_ID = in_msg[2];
					msisdn     = in_msg[3];
					event_type = in_msg[4];
					service_id = in_msg[9];
					plan_id    = in_msg[10];
					trans_id   = in_msg[11];
					mode       = in_msg[13];
					System.out.println(operator+"#"+trans_id+"#"+mode);
 if(con==null || con.isClosed())
                                dbConn();

				  if("ok".equalsIgnoreCase(status))
				  {
					  CallableStatement cstmt=null;
					  	System.out.println("calling procedure to update msgid"+ billing_ID+","+ msisdn+","+ event_type+","+ service_id+","+ plan_id+","+ trans_id);
						cstmt = con.prepareCall("{call etislat_update_billingid(?,?,?,?,?,?)}");
						cstmt.setString(1, billing_ID);
						cstmt.setString(2, msisdn);
						cstmt.setString(3, event_type);
						cstmt.setString(4, service_id);
						cstmt.setString(5, plan_id);
						cstmt.setString(6, trans_id);
						cstmt.execute();
						cstmt.close();
					}

					System.out.println("called procedure etislat_update_billingid");
				}//if message ends
			}//try ends
			catch(Exception e)
			{
				hungamalogging.log("Exception in calling prco updateid#"+e+"#","ExceptionLog_",dsmlogs);
				e.printStackTrace();
				try
				{
					if(con==null || con.isClosed())
					{
						dbConn();
					}
				}
				catch(Exception ex)
				{
					System.exit(0);
				}

			}
		}

	}
	public static void main(String args[])
	{
		try
		{

			new Etislatdblogger("1");
			new Etislatdblogger("1");
			sleep(100);
			new Etislatdblogger("2");

			sleep(100);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			hungamalogging.log(e.toString(),"ExceptionLog_","/home/ivr/javalogs/Etislat");
		}
	}
}
