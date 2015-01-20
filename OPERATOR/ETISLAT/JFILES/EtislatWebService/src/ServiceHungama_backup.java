import com.ng.etms.eng.vas.masp.ws.*;
import java.rmi.Remote;
import org.apache.axis.client.Stub;


public class ServiceHungama_backup {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ServiceHungama_backup _obj = new ServiceHungama_backup();
		_obj.process();
		// TODO Auto-generated method stub

	}

	public void process()
	{
		
		
		try
		{
		
		
			DeactivationData _obj = new DeactivationData();
			_obj.setMsisdn("2348093588212");
			_obj.setServiceCode("STOP EPL");
			_obj.setShortCode("38567");
			_obj.setVendorCode("PROVECTUS");
		
 	       /* MaspEventSummaryData _objdata = new MaspEventSummaryData();
		_objdata.setBillingCd("33567");
		_objdata.setChannel("SMS");
		_objdata.setEventId(1l);
		_objdata.setEventTs("2014-05-16 11:03:16");
		_objdata.setMessageText("hi testing");
		_objdata.setMsisdn("2348093588212");
		_objdata.setServiceCd("EPL");
		_objdata.setShortCode("38567");
		_objdata.setStatus("A");
		_objdata.setVendorCd("PROVECTUS");
		_objdata.setEventType("4");*/
		
		MaspServiceWS_ServiceLocator locator= new MaspServiceWS_ServiceLocator();
 		Remote remote = locator.getPort(MaspServiceWS_PortType.class);
 	    System.out.println(locator.getMaspServiceWSPortAddress());
 	    Stub axisPort = (Stub)remote;
 	    MaspServiceWS_PortType service=(MaspServiceWSSoapBindingStub) axisPort;
	   // MaspEventResponseData response = service.createMaspEventInboundSummary(_objdata);
 	   String response1 = service.deactivateMaspSubscriberService(_obj);
 	   //System.out.println("Response   getCode" + response.getCode());
 	  //System.out.println("Response   getDescription" + response.getDescription()); 	   
 	   System.out.println("Response   " + response1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception at the time of service invoked");
		}
 	   
		
	}
}
