//package mypack;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
//import org.apache.*;
//import org.apache.commons.lang.StringUtils;

public class Voda_Alert_Demo {

	public static   String DATE_FORMAT="yyyy-MM-dd";
	static String line="";
	public static void main(String[] args) {

		String pre_date=YesterdayDate();
	//	File file=new File("d:/localhost_access_log."+pre_date+".txt");
		File file=new File("/usr/local/apache-tomcat-6.0.35/logs/localhost_access_log."+pre_date+".txt");

		File f=new File("/home/ivr/jfiles/Alert_System/recorecord.csv");
		//File f=new File("d:/recorecord.csv");
		try
		{
			
		FileWriter out=new FileWriter(f);	
		BufferedReader reader=new BufferedReader(new FileReader(file));

		while((line=reader.readLine())!=null)
		{
				if(line.length()>70)
	     		{
//		System.out.println("done");
		   String action="";	
		   String status="";
			String tnsid="";
			String	str[]=line.split("&");
//			System.out.println("done2");
			String msisdn=str[0].substring(str[0].length()-10);
//			System.out.println("done1");
					if(line.contains("Update"))
					{
						action="Update";
						
						str=line.split("&");
			        	        String str1[]=str[4].split("=");
			                        status=str1[1];
						str1=str[3].split("=");
			                        tnsid=str1[1];
						  if(!status.equals("GRACE"))
						  {
						out.write(msisdn + "," + "RESUB"+"," + status+","+tnsid+"\n");	
					  }
					}
					else
					{
						action="SubManagerServlet";
						str=line.split("&");
			        	String str1[]=str[5].split("=");
			        	status=str1[1];
					str1=str[3].split("=");
			        	tnsid=str1[1];
					
					str1=str[4].split("=");
			        	String action1=str1[1];
			        	 if(status.equals("SUCCESS")&&action1.equals("ACT"))
                         		{
		                               out.write(msisdn + "," + "SUB"+"," + status+","+tnsid+"\n");
                		         }                            
					}

        }
		}
		System.out.println("Successfully inserted.");
		out.close();
		reader.close();
		}catch(Exception ex)
		{
			System.out.println("Exception in Reading file"+" "+line +" "+ex);
		}
	}
	public static String YesterdayDate()
	{
	    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	    Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, -1); 
	    return dateFormat.format(cal.getTime());
	}
}

