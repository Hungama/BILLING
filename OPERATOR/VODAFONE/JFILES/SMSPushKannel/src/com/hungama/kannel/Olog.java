//package com.hungama.kannel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import org.apache.log4j.Logger;

public class Olog
{
	private static final Logger oLog = Logger.getLogger(Olog.class);

	public static void log(String path,String data,int flag)
	{
		String filename="",foldername="",_fName="",datee="",data1="";
		data1=data;
		Date dt=new Date();
		SimpleDateFormat fname=new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat fdir=new SimpleDateFormat("yyyyMM");
		if(flag==0)
			filename=fname.format(dt)+".txt";
		else
			filename=fname.format(dt)+"_error.txt";
		foldername=fdir.format(dt);
		SimpleDateFormat iDate=new SimpleDateFormat("yyyyMMdd#HH:mm:ss");
		datee=iDate.format(dt);
		data1="#"+datee+"#"+data1;
		_fName="/home/ivr/javalogs/"+path+"/"+foldername+""+"/"+filename;
		//_fName="d:/ivr/javalogs/"+path+"/"+foldername+""+"/"+filename;

		File dir=new File("/home/ivr/javalogs/"+path+"/"+foldername+""+"/");
		//File dir=new File("d:/ivr/javalogs/"+path+"/"+foldername+""+"/");
		if(!dir.exists())
			dir.mkdirs();
		try
		{
			BufferedWriter out =new BufferedWriter(new FileWriter(_fName,true));
			//out.newLine();			
			out.write(data1+"\n");
			out.flush();
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

