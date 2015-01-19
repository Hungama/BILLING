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

//import org.apache.log4j.*;
import java.util.*;
import java.io.*;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class SendSMS
{
	public static void main(String  gsk [])
	{
		SendSMS ob=new SendSMS();

		ob.sendSMS("0169697284","VAS220300","IVR","6666817","123456","migseven","hi testing","");

	}
  //===========================================================
  //===========================================================
  public String sendSMS(String ani, String pricecode,String mode,String transid,String refid,String kiword,String msg,String cid)
  {
	  try
	  {
		//  System.out.println("sendSMS "+msg.length());
		  String mmscurl="http://192.100.86.203:8001/cxf/services/SDPServices/wsdl";
		  String out_string="";

			SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection connection = soapConnFactory.createConnection();
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage message = messageFactory.createMessage();
						message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING,"UTF-8");
						message.setProperty(SOAPMessage.WRITE_XML_DECLARATION,"true");

			MimeHeaders hd = message.getMimeHeaders();
						hd.addHeader("User-Agent","Jakarta Commons-HttpClient/3.1");
						hd.addHeader("Host", "59.161.254.19:20634");



			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope env=soapPart.getEnvelope();

						 env.addNamespaceDeclaration("xmlns","http://www.w3.org/2003/05/soap-envelope" );
						 env.addNamespaceDeclaration("xmlns","http://xsd.gateway.sdp.digi.com");
						 //env.setPrefix("soap");


			SOAPHeader hdr  = env.getHeader();

					   //hdr.setPrefix("soap");
			SOAPBody   body =  env.getBody();
					   body.removeNamespaceDeclaration(body.getPrefix());
					   body.setPrefix("xsd");

			SOAPElement bdy=body.addChildElement("SmsMt");
						   bdy.removeNamespaceDeclaration(body.getPrefix());
						   bdy.setPrefix("xsd");

			SOAPElement lgn=bdy.addChildElement("login_name");
							lgn.addTextNode("M7ygaSWeMHQ=");
			SOAPElement sid=bdy.addChildElement("service_id");
							sid.addTextNode(cid);
			SOAPElement cp_id=bdy.addChildElement("cp_id");
							cp_id.addTextNode("17302");
			SOAPElement price_code=bdy.addChildElement("price_code");
							price_code.addTextNode("VAS220000");
			SOAPElement charge_party=bdy.addChildElement("charge_party");
							charge_party.addTextNode("D");
			SOAPElement source_mobtel=bdy.addChildElement("source_mobtel");
							source_mobtel.addTextNode(ani);
			SOAPElement destination_mobtel=bdy.addChildElement("destination_mobtel");
							destination_mobtel.addTextNode(ani);
			SOAPElement sub_id=bdy.addChildElement("sub_id");
			SOAPElement sender_name=bdy.addChildElement("sender_name");
							sender_name.addTextNode("DiGi");
			SOAPElement keyword=bdy.addChildElement("keyword");
							keyword.addTextNode(kiword);
			SOAPElement interactive_session_ind=bdy.addChildElement("interactive_session_ind");
							interactive_session_ind.addTextNode("N");
			SOAPElement interactive_term_session=bdy.addChildElement("interactive_term_session");
							interactive_term_session.addTextNode("N");
			SOAPElement status=bdy.addChildElement("status");
							status.addTextNode("1");
			SOAPElement transaction_id=bdy.addChildElement("transaction_id");
			SOAPElement ref_id=bdy.addChildElement("ref_id");
			SOAPElement short_code_suffix_ind=bdy.addChildElement("short_code_suffix_ind");
							short_code_suffix_ind.addTextNode("N");
			SOAPElement short_code_suffix=bdy.addChildElement("short_code_suffix");
			SOAPElement notification_ind=bdy.addChildElement("notification_ind");
							notification_ind.addTextNode("3");
			SOAPElement response_url=bdy.addChildElement("response_url");
							response_url.addTextNode("http://172.16.56.43:8001/DigiSmsMt/SmsMt?method=SmsMt");

			//======================================================================================//
			SOAPElement sms_contents=bdy.addChildElement("sms_contents");
			SOAPElement sms_content=sms_contents.addChildElement("sms_content");

			SOAPElement content=sms_content.addChildElement("content");

							if(msg.length()>159)
								msg=msg.substring(0,159);
							content.addTextNode(msg);
			SOAPElement ucp_data_coding_id=sms_content.addChildElement("ucp_data_coding_id");

			SOAPElement ucp_msg_class=sms_content.addChildElement("ucp_msg_class");

			SOAPElement ucp_msg_type=sms_content.addChildElement("ucp_msg_type");
								ucp_msg_type.addTextNode("3");
				message.saveChanges();

			//============================================================
			URL destination = new URL(mmscurl);
			URLConnection connect=destination.openConnection();
			HttpURLConnection httpConn 	= (HttpURLConnection) connect;
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);

			OutputStream out 		= httpConn.getOutputStream();
		//ystem.out.println("Request is:"+message);
			message.writeTo(out);
			String str1=out.toString();
			//System.out.print("=========\t"+str1);

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
		//	System.out.print(str);
			return str;

	  }
	  catch(Exception e)
	  {
		  System.out.println("Error @ sendSMS "+e);
		  return "ERROR";
	  }
  }//sendSMS ends
}//class ends
