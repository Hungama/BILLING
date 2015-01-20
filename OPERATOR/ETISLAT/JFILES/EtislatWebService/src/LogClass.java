//package com.etislatweb.service;

import java.io.File;
import java.text.SimpleDateFormat;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LogClass
{
	public  static FileAppender err_App = null;
	public  static Logger err_log = null;
	public  static File dir=null;
	
	public static SimpleDateFormat folderDateFormat=new SimpleDateFormat("yyyyMM");
	public static SimpleDateFormat fileDateFormat=new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat DateTimeFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	protected static void hunLog(String log,String fileName )
	{
		try
		{	
			dir=new File(ServiceHungama.Path+folderDateFormat.format(new java.util.Date())+"/");
			if(!dir.exists())
			  dir.mkdirs();
			
			err_App = new FileAppender(new PatternLayout(),ServiceHungama.Path+folderDateFormat.format(new java.util.Date())+"/"+fileName+fileDateFormat.format(new java.util.Date())+".log");
				
			err_App.setAppend(true);
			err_log = Logger.getLogger("HunLogger");
			err_log.addAppender(err_App);
			err_log.info("#"+DateTimeFormat.format(new java.util.Date())+"#"+log);
			err_log.removeAllAppenders();
			err_App.close();
		}
		catch(Exception e)
		{
			System.out.println(e+log);
			e.printStackTrace();
		}
	}
}
