//this is a java file
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

		ob.sendSMS("0169697284","VAS220300","IVR","6666817","123456","migseven");

	}
  //===========================================================
  //===========================================================
  public String sendSMS(String ani, String pricecode,String mode,String transid,String refid,String kiword)
  {
	  try
	  {
		  System.out.println("sendSMS");
		  String mmscurl="http://192.100.86.201:8001/cxf/services/SDPServices/wsdl";
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
							//lgn.removeNamespaceDeclaration("xmlns:xsd=http://www.w3.org/2003/05/soap-envelope");
					 		//lgn.setPrefix("xsd");
							lgn.addTextNode("M7ygaSWeMHQ=");
			SOAPElement sid=bdy.addChildElement("service_id");
							//sid.removeNamespaceDeclaration(sid.getPrefix());
					 		//sid.setPrefix("xsd");
							sid.addTextNode("Q4sv4jPncR8IMiDLcsWQCA==");
			SOAPElement cp_id=bdy.addChildElement("cp_id");
							//cp_id.removeNamespaceDeclaration(cp_id.getPrefix());
					 		//cp_id.setPrefix("xsd");
							cp_id.addTextNode("7413");
			SOAPElement price_code=bdy.addChildElement("price_code");
							//price_code.removeNamespaceDeclaration(price_code.getPrefix());
					 		//price_code.setPrefix("xsd");
							price_code.addTextNode("VAS220000");
			SOAPElement charge_party=bdy.addChildElement("charge_party");
							//charge_party.removeNamespaceDeclaration(charge_party.getPrefix());
					 		//charge_party.setPrefix("xsd");
							charge_party.addTextNode("D");
			SOAPElement source_mobtel=bdy.addChildElement("source_mobtel");
							//source_mobtel.removeNamespaceDeclaration(source_mobtel.getPrefix());
					 		//source_mobtel.setPrefix("xsd");
							source_mobtel.addTextNode(ani);
			SOAPElement destination_mobtel=bdy.addChildElement("destination_mobtel");
							//destination_mobtel.removeNamespaceDeclaration(destination_mobtel.getPrefix());
					 		//destination_mobtel.setPrefix("xsd");
							destination_mobtel.addTextNode(ani);
			SOAPElement sub_id=bdy.addChildElement("sub_id");
							//sub_id.removeNamespaceDeclaration(sub_id.getPrefix());
					 		//sub_id.setPrefix("xsd");
							//sub_id.addTextNode("");
			SOAPElement sender_name=bdy.addChildElement("sender_name");
							//sender_name.removeNamespaceDeclaration(sender_name.getPrefix());
					 		//sender_name.setPrefix("xsd");
							sender_name.addTextNode("DiGi");
			SOAPElement keyword=bdy.addChildElement("keyword");
							//keyword.removeNamespaceDeclaration(keyword.getPrefix());
					 		//keyword.setPrefix("xsd");
							keyword.addTextNode(kiword);
			SOAPElement interactive_session_ind=bdy.addChildElement("interactive_session_ind");
							//interactive_session_ind.removeNamespaceDeclaration(interactive_session_ind.getPrefix());
					 		//interactive_session_ind.setPrefix("xsd");
							interactive_session_ind.addTextNode("N");
			SOAPElement interactive_term_session=bdy.addChildElement("interactive_term_session");
							//interactive_term_session.removeNamespaceDeclaration(interactive_term_session.getPrefix());
					 		//interactive_term_session.setPrefix("xsd");
							interactive_term_session.addTextNode("N");
			SOAPElement status=bdy.addChildElement("status");
							//status.removeNamespaceDeclaration(status.getPrefix());
					 		//status.setPrefix("xsd");
							status.addTextNode("1");
			SOAPElement transaction_id=bdy.addChildElement("transaction_id");
							//transaction_id.removeNamespaceDeclaration(transaction_id.getPrefix());
							//transaction_id.setPrefix("xsd");
							//transaction_id.addTextNode(transid);
			SOAPElement ref_id=bdy.addChildElement("ref_id");
							//ref_id.removeNamespaceDeclaration(ref_id.getPrefix());
							//ref_id.setPrefix("xsd");
							//ref_id.addTextNode(" ");
			SOAPElement short_code_suffix_ind=bdy.addChildElement("short_code_suffix_ind");
							//short_code_suffix_ind.removeNamespaceDeclaration(short_code_suffix_ind.getPrefix());
							//short_code_suffix_ind.setPrefix("xsd");
							short_code_suffix_ind.addTextNode("N");
			SOAPElement short_code_suffix=bdy.addChildElement("short_code_suffix");
							//short_code_suffix.removeNamespaceDeclaration(short_code_suffix.getPrefix());
							//short_code_suffix.setPrefix("xsd");
							//short_code_suffix.addTextNode(" ");
			SOAPElement notification_ind=bdy.addChildElement("notification_ind");
							//notification_ind.removeNamespaceDeclaration(notification_ind.getPrefix());
							//notification_ind.setPrefix("xsd");
							notification_ind.addTextNode("3");
			SOAPElement response_url=bdy.addChildElement("response_url");
							//response_url.removeNamespaceDeclaration(response_url.getPrefix());
							//response_url.setPrefix("xsd");
							response_url.addTextNode("http://172.28.106.6:8080/DigiSmsMt/SmsMt?method=SmsMt");

			//======================================================================================//
			SOAPElement sms_contents=bdy.addChildElement("sms_contents");
							//sms_contents.removeNamespaceDeclaration(sms_contents.getPrefix());
							//sms_contents.setPrefix("xsd");
			SOAPElement sms_content=sms_contents.addChildElement("sms_content");
							//sms_content.removeNamespaceDeclaration(sms_content.getPrefix());
							//sms_content.setPrefix("xsd");
			SOAPElement content=sms_content.addChildElement("content");
							//content.removeNamespaceDeclaration(content.getPrefix());
							//content.setPrefix("xsd");
							content.addTextNode("You are successfully subscribe to South Asian Migrant IVR Weekly Service");
			SOAPElement ucp_data_coding_id=sms_content.addChildElement("ucp_data_coding_id");
								//ucp_data_coding_id.removeNamespaceDeclaration(content.getPrefix());
								//ucp_data_coding_id.setPrefix("xsd");
								//ucp_data_coding_id.addTextNode(" ");
			SOAPElement ucp_msg_class=sms_content.addChildElement("ucp_msg_class");
								//ucp_msg_class.removeNamespaceDeclaration(content.getPrefix());
								//ucp_msg_class.setPrefix("xsd");
								//ucp_msg_class.addTextNode(" ");
			SOAPElement ucp_msg_type=sms_content.addChildElement("ucp_msg_type");
								//ucp_msg_type.removeNamespaceDeclaration(content.getPrefix());
								//ucp_msg_type.setPrefix("xsd");
								ucp_msg_type.addTextNode("3");
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
			return str;

	  }
	  catch(Exception e)
	  {
		  System.out.println("Error @ sendSMS "+e);
		  return "ERROR";
	  }
  }//sendSMS ends
}//class ends
