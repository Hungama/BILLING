//package org.etislat.hungama;

import com.ng.etms.eng.vas.masp.ws.DeactivationData;
import com.ng.etms.eng.vas.masp.ws.MaspServiceWSProxy;
import com.ng.etms.eng.vas.masp.ws.MaspServiceWS_ServiceLocator;

public class EtislatMain 
{
	DeactivationData dd=new DeactivationData();
	MaspServiceWS_ServiceLocator sl=new MaspServiceWS_ServiceLocator();
	MaspServiceWSProxy msp=new MaspServiceWSProxy();
 public EtislatMain()
 {
	 try
	 {
		 dd.setMsisdn("7838102430");
		 dd.setServiceCode("EPL");
		 dd.setShortCode("38567");
		 dd.setVendorCode("Provectus");
		 
		 msp.setEndpoint("http://10.161.6.10:8280/masp-ejb/MaspServiceWS/MaspServiceWS");
	 	 String response =  msp.deactivateMaspSubscriberService(dd);
                 System.out.println("**************"+response);
              
		
	 }
	 catch (Exception e)
	 {
		e.printStackTrace();
	}
 }
	
	public static void main(String[] args) 
	{
		new EtislatMain();

	}

}
