import java.io.*;
//import java.sql.*;
import java.util.*;
import java.net.*;
import java.util.Date;


import javax.xml.soap.MimeHeaders;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.*;
import java.util.*;
import java.io.*;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;



public class BillingProject
{

    public static String filepath="./response/";
	//===========================================================
	//===========================================================
	public String ValidateAndBill(String ani,String pricecode,String mode,String kiword,String cpa_sid)
	{
		System.out.println("ValidateAndBill");
		String mmscurl="http://192.100.86.206:8001/billing/services/SDPValidateBill/wsdl";
		String out_string="";

		try
		{
			SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection connection = soapConnFactory.createConnection();
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage message = messageFactory.createMessage();
			message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING,"UTF-8");
			message.setProperty(SOAPMessage.WRITE_XML_DECLARATION,"true");

			MimeHeaders hd = message.getMimeHeaders();
				//hd.addHeader("SOAPAction","\"AmountCharging#chargeAmount\"");
				hd.addHeader("User-Agent","Jakarta Commons-HttpClient/3.1");
				hd.addHeader("Host", "59.161.254.19:20634");

			SOAPPart soapPart = message.getSOAPPart();

			SOAPEnvelope env=soapPart.getEnvelope();
					env.addNamespaceDeclaration("xmlns","http://www.w3.org/2003/05/soap-envelope" );
					env.addNamespaceDeclaration("xmlns","http://xsd.gateway.sdp.digi.com");

			SOAPHeader hdr=env.getHeader();
		    SOAPBody body = env.getBody();
					 body.removeNamespaceDeclaration(body.getPrefix());
					 body.setPrefix("xsd");
			SOAPElement bdy=body.addChildElement("ValidateAndBill");
							bdy.removeNamespaceDeclaration(body.getPrefix());
					 		bdy.setPrefix("xsd");
				SOAPElement lgn=bdy.addChildElement("login_name");
							lgn.addTextNode("M7ygaSWeMHQ=");
				SOAPElement sid=bdy.addChildElement("service_id");
							//sid.addTextNode("Q4sv4jPncR8IMiDLcsWQCA==");
							sid.addTextNode(cpa_sid);
				SOAPElement cpid=bdy.addChildElement("cp_id");
							cpid.addTextNode("17302");
				SOAPElement prcd=bdy.addChildElement("price_code");
							prcd.addTextNode(pricecode);
				SOAPElement chpt=bdy.addChildElement("charge_party");
							chpt.addTextNode("S");
				SOAPElement srmo=bdy.addChildElement("source_mobtel");
							srmo.addTextNode(ani);
				SOAPElement dtmo=bdy.addChildElement("destination_mobtel");
							dtmo.addTextNode(ani);
				SOAPElement subid=bdy.addChildElement("sub_id");
							subid.addTextNode("");
				SOAPElement keywrd=bdy.addChildElement("keyword");
							keywrd.addTextNode(kiword);//migthirty-"migseven"
				SOAPElement dly_chnl=bdy.addChildElement("delivery_channel");
							dly_chnl.addTextNode(mode);
				SOAPElement status=bdy.addChildElement("status");
							status.addTextNode("4");
				SOAPElement refid=bdy.addChildElement("ref_id");
							refid.addTextNode("");
		   SOAPElement vbndls=body.addChildElement("variable_bundles");
		   SOAPElement vbndl=vbndls.addChildElement("variable_bundle");
						SOAPElement cp_id=vbndl.addChildElement("cp_id");
									cp_id.addTextNode("17302");
						SOAPElement serid=vbndl.addChildElement("service_id");
									serid.addTextNode(cpa_sid);
						SOAPElement psplit= vbndl.addChildElement("price_split");
									psplit.addTextNode(pricecode);
		  /* SOAPElement aoi=body.addChildElement("array_of_info");
						SOAPElement name=aoi.addChildElement("name");
									name.addTextNode("hungama");
						SOAPElement val  =aoi.addChildElement("value");
									val.addTextNode("hung");*/





						message.saveChanges();

		  //============================================================
			URL destination = new URL(mmscurl);
			URLConnection connect=destination.openConnection();
			HttpURLConnection httpConn 	= (HttpURLConnection) connect;
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);

			OutputStream out 		= httpConn.getOutputStream();
			System.out.println("Request is:"+message);
			message.writeTo(out);
			String str1=out.toString();
			System.out.print("=========\t"+str1);

			SOAPMessage reply = connection.call(message, mmscurl);

			//----------------------------Response----------------------------------

			System.out.println("\nRESPONSE:\n");
			// Create the transformer
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			// Extract the content of the reply
			Source sourceContent = reply.getSOAPPart().getContent();
			// Set the output for the transformation
			StreamResult result = new StreamResult(System.out);
			transformer.transform(sourceContent, result);
			reply.writeTo(out);
			String str=out.toString();
			System.out.print(str);
			writeXML(ani,str);
			String arr[]=(readXML(str)).split("#");
			String trans_id  = arr[0];
			String err_code  = arr[1];
			String err_desc  = arr[2];
			String err_list  = arr[3];
			String succ_list = arr[4];


			if(arr[0].equals("-2"))
			{
				 out_string = "NOK#-2#Error#Error#Error#Error";
			}
			else
			{
				if(arr[1].equals("1"))//error_code check
			   		out_string  = "OK#"+trans_id+"#"+err_code+"#"+err_desc+"#"+err_list+"#"+succ_list;
			   	else
			   		out_string  = "NOK#"+trans_id+"#"+err_code+"#"+err_desc+"#"+err_list+"#"+succ_list;

		   	}



			connection.close();
			Thread.sleep(20);
			return out_string;
		}
		catch(Exception e)
		{
			System.out.println("exception in  is ValidateAndBill:"+e);
			return "NOK#-2#Error#Error#Error#"+e.toString();
		}
	}
//============================================ WRITE XML METHOD ================================
public  void writeXML(String ani,String xmlstr)
	{
		try
		{
			Calendar mytoday = Calendar.getInstance();
			String mystrdate = formatN(""+mytoday.get(Calendar.YEAR),4) + formatN(""+(mytoday.get(Calendar.MONTH)+1),2) + formatN(""+mytoday.get(Calendar.DATE),2);
			String mystrtime = formatN(""+mytoday.get(Calendar.HOUR_OF_DAY),2)+formatN(""+mytoday.get(Calendar.MINUTE),2)+formatN(""+mytoday.get(Calendar.SECOND),2);
			String dir=formatN(""+mytoday.get(Calendar.YEAR),4) + formatN(""+(mytoday.get(Calendar.MONTH)+1),2);
			File f=new File("/home/ivr/javalogs/BillingMnger/xml/"+dir);
			if(!f.exists())
			   f.mkdirs();
			FileOutputStream outfile = new FileOutputStream("/home/ivr/javalogs/BillingMnger/xml/"+dir+"/"+mystrdate+ ".txt",true);
			PrintStream outprint = new PrintStream(outfile);
			outprint.print("#BULK#"+mystrdate+"#"+mystrtime+"#"+xmlstr);
			outprint.close();
			outfile.close();

		}
		catch(Exception e)
		{
			System.out.println("exception in writeXML is :"+e);
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
public String readXML(String str)
{
	try
	{
		String transid=str.substring(str.indexOf("<transaction_id>")+16,str.indexOf("</transaction_id>"));

		String error_code=str.substring(str.indexOf("<error_code>")+12,str.indexOf("</error_code>"));


		String error_desc=str.substring(str.indexOf("<error_desc>")+12,str.indexOf("</error_desc>"));
		String error_list="NA";
		if(error_code.equalsIgnoreCase("1"))
			 error_list=str.substring(str.indexOf("<error_list>")+12,str.indexOf("</error_list>"));

		String success_list="NA";
		if(error_code.equalsIgnoreCase("1"))
			 success_list=str.substring(str.indexOf("<success_list>")+14,str.indexOf("</success_list>"));

		System.out.println("\ntransid: "+transid+"\nerror_code:"+error_code+"\nerror_desc: "+error_desc+"\nerror_list: "+error_list+"\nsuccess_list: "+success_list);
		if(error_desc.equals("") || error_desc==null)
			error_desc="-";
		return(transid+"#"+error_code+"#"+error_desc+"#"+error_list+"#"+success_list);

	}
	catch(Exception e)
	{
		System.out.println("exception in readXML is :"+e);
		return "-2#";
	}
}

//========================================================


//================================================================================================
	public static void main(String[] args)
	{
		BillingProject gsk=new BillingProject();

		gsk.ValidateAndBill("0169697284","VAS220300","IVR","migthirty","Q4sv4jPncR+t32gSY3+Wrw==");

	}

}
