//package com.etislatweb.service;

//import com.hungama.webservice.mo.LogClass;
import com.ng.etms.eng.vas.masp.ws.*;
import java.rmi.Remote;
import java.sql.*;
import java.util.ResourceBundle;
import org.apache.axis.client.Stub;
//import org.apache.log4j.Logger;


public class ServiceHungama extends Thread
{
	public String ip=null,dsn=null,username=null,pwd=null;
	public static Connection con_select=null,con_update=null;
	public static Statement stmt,stmtUpdate;
	MaspServiceWS_ServiceLocator locator;
	Remote remote;
	Stub axisPort;
	MaspServiceWS_PortType service;
	MaspEventSummaryData _objdata;
	MaspEventResponseData response;
	String response1="",actloginfo="",deactloginfo="",outstr="";
	String billingCd="",Channel="",eventTS="",message="",msisdn="",serviceCd="",shortcode="",status="",vendorCd="";
	//static Logger logger;
	static final String Path="/home/ivr/javalogs/EtislatMaspUniPortal/";
	
	public static void main(String[] args)
	{	
		ServiceHungama _objSh = new ServiceHungama();
		_objSh.start();
		//logger=Logger.getLogger(ServiceHungama.class);
		//logger.info(message);
	}

	public ServiceHungama()
	{
		try
		{
		    ResourceBundle resource = ResourceBundle.getBundle("config/db_config");
			ip=resource.getString("IP");
			dsn=resource.getString("DSN");
			username=resource.getString("USERNAME");
			pwd=resource.getString("PWD");
		    System.out.println("IP: "+ip+" DATABASE :"+dsn+" USER :"+username+" PWD:"+pwd);
		}
		catch(Exception e)
		{
			System.out.println("exception in arguments for database connection "+e);
		}
		try
		{
			locator= new MaspServiceWS_ServiceLocator();
			remote = locator.getPort(MaspServiceWS_PortType.class);
			//System.out.println(locator.getMaspServiceWSPortAddress());
			axisPort = (Stub)remote;
			service=(MaspServiceWSSoapBindingStub) axisPort;
		}
		catch(Exception ee)
		{
			System.out.println("exception in masp service ws "+ee);
		}
	}
	public Connection dbConn()
	{
		while(true)
		{
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+dsn, username, pwd);
				System.out.println("Database Connection established!");
				return con;
			}catch(Exception e)
			{
				//hunLog(e.toString(),'e');
				LogClass.hunLog("Exception in dbConn Maethod "+e.toString(),"Error_");
				e.printStackTrace();
				try
				{
					Thread.sleep(10000);
				}
				catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}
			}
		}
	}
	public String activationProcess(String billCd,String chnl,String evtTs,String msg,String ani,String serCd,String sc,String st,String vCd,String evtType)
	{
		try
		{
			_objdata = new MaspEventSummaryData();
			_objdata.setBillingCd(billCd);
			_objdata.setChannel(chnl);
			//_objdata.setEventId(1l);
			_objdata.setEventTs(evtTs);
			_objdata.setMessageText(msg);
			_objdata.setMsisdn(ani);
			_objdata.setServiceCd(serCd);
			_objdata.setShortCode(sc);
			_objdata.setStatus(st);
			_objdata.setVendorCd(vCd);
			_objdata.setEventType(evtType);
			response = service.createMaspEventInboundSummary(_objdata);
			System.out.println("Response getCode " + response.getCode());
			System.out.println("Response getDescription " + response.getDescription());
		}
		catch(Exception ex)
		{
			//logger.error(ex.toString());
			LogClass.hunLog(ex.toString(),"Error_");
			ex.printStackTrace();			
		}
		return response.getDescription();
	}

	public String deactivationProcess(String ani,String serCd,String sc,String vCd)
	{
		try
		{
			DeactivationData _obj = new DeactivationData();
			_obj.setMsisdn(ani);
			_obj.setServiceCode(serCd);
			_obj.setShortCode(sc);
			_obj.setVendorCode(vCd);
			response1 = service.deactivateMaspSubscriberService(_obj);
			System.out.println("Response from deactivation is " + response1);
		}
		catch (Exception exx) 
		{
			//logger.info(exx.toString());
			LogClass.hunLog("Exception in deactivationProcess Method " + exx.toString(),"Error_");
			exx.printStackTrace();			
		}
		return response1;
	}		
	public void run()
	{
		try
		{
			con_select = dbConn();
			con_update = dbConn();
			stmt = con_select.createStatement();
			stmtUpdate = con_update.createStatement();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//hunLog(e.toString(),'e');
		}		
		while(true)
		{
			try
			{
				Thread.sleep(1000);
 				String qquery = "select billingCd,Channel,eventType,eventTS,message,msisdn,serviceCd,shortcode,status,vendorCd from tbl_etislat_masp_uniportal limit 1000";
				qquery=qquery.trim();
				ResultSet rs= stmt.executeQuery(qquery);
				while(rs.next())
				{
					String eventType=rs.getString("eventType");
					if(eventType.equalsIgnoreCase("4")|| eventType.equalsIgnoreCase("1"))
					{
						billingCd=rs.getString("billingCd");
						Channel=rs.getString("Channel");
						Channel="SMS";
						eventTS=rs.getString("eventTS");
						message=rs.getString("message");
						msisdn=rs.getString("msisdn");
						serviceCd=rs.getString("serviceCd");
						shortcode=rs.getString("shortcode");
						status=rs.getString("status");
						vendorCd=rs.getString("vendorCd");
						String actRes=activationProcess(billingCd,Channel,eventTS,message,msisdn,serviceCd,shortcode,status,vendorCd,eventType);
						System.out.println("response after hitting for activation request "+actRes);
						actloginfo=billingCd+"#"+Channel+"#"+eventTS+"#"+message+"#"+msisdn+"#"+serviceCd+"#"+shortcode+"#"+status+"#"+vendorCd+"#"+eventType+"#"+actRes;
						outstr=actloginfo;
						//logger.info(actloginfo);
						LogClass.hunLog("Exception in Run Method "+actloginfo,"Activation_");
					}
					else if(eventType.equalsIgnoreCase("5"))
					{
						msisdn=rs.getString("msisdn");
						serviceCd=rs.getString("serviceCd");
						shortcode=rs.getString("shortcode");
						vendorCd=rs.getString("vendorCd");
						String deactRes=deactivationProcess(msisdn,serviceCd,shortcode,vendorCd);
						deactloginfo=msisdn+"#"+serviceCd+"#"+shortcode+"#"+vendorCd+"#"+eventType+"#"+deactRes;
						outstr=deactloginfo;
						//logger.info(deactloginfo);
						LogClass.hunLog(deactloginfo,"Deactivation_");
					}
					stmtUpdate.executeUpdate("delete from tbl_etislat_masp_uniportal where msisdn='"+msisdn+"'");
                                        System.out.println("Number is deleting " + msisdn);  
				}
				System.out.println("Going to sleep for 5 seconds......");
				Thread.sleep(5000L);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				//logger.error(e.toString());
				LogClass.hunLog("Exception in Run Method in Second Catch"+outstr+"#"+e.toString(),"Error_");
				System.out.println("Exception at the time of service invoked"+e);
			}
		}
	}
}
