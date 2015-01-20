package com.hungama.kannel;
import java.util.HashMap;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

public class Logger
{
	private static org.apache.log4j.Logger AppLogger = null;
	private static org.apache.log4j.Logger Exception = null;
	private static org.apache.log4j.Logger INFOLOGS = null;
	private static String SPACE = " ";
	public static final int app= 10;
	public static final int error= 11;
	public static final int infoLogs= 18;
	private static HashMap additional;
	private static org.apache.log4j.Logger generic = null;

	public synchronized static void init(String log4jfile,int additionalAppenders)
	{
		if (log4jfile != null && !log4jfile.trim().equals(""))
			PropertyConfigurator.configureAndWatch(log4jfile);
		Exception = org.apache.log4j.Logger.getLogger("Exception");
		AppLogger = org.apache.log4j.Logger.getLogger("AppLogger");
		INFOLOGS = org.apache.log4j.Logger.getLogger("INFOLOGS");
		System.out.println(" Logger class :=>"+log4jfile);
		if(additionalAppenders > 0)
		{
			if(additional == null)
			additional = new HashMap();
			String key=null;
			for(int i=1;i<=additionalAppenders;i++)
			{
				key="GenericLogger"+i;
				if(!additional.containsKey(key))
				additional.put(key,org.apache.log4j.Logger.getLogger(key));
			}
		}
	}
	public Logger(){}

	public static void log(String opr, String text, int level)
	{
		//System.out.println("msisdn:=> "+msisdn+" text:=> "+text+" level:=> "+level);
		if (level == error)
		{Exception.error(opr + SPACE + text);}
		else if (level == app)
		{AppLogger.info(opr + SPACE + text);}
		else if (level == infoLogs)
		{INFOLOGS.info(text);}
		else
		{
            generic = (org.apache.log4j.Logger)additional.get("GenericLogger"+level);
            if(generic!=null)
                generic.debug(opr+SPACE+text);
			else
				System.out.println(opr + SPACE+ "Logger:log() Logger not found " + level);
		}
	}
}
