import java.io.*;
import java.util.*;
public class CheckProcess
{

public static void main (String s[])
{
	    int status=CheckProcess.GetProcessList("CheckProcess",1);
        if(status>1)
        {
        	System.out.println("Kindly check Process already Running "+status);
        	System.exit(0);
        }
        //System.out.println("Please wait, to run Process..............");
}
public static int GetProcessList(String processName,int i)
{
		HashMap<String,Integer> processMap =new HashMap<String,Integer>();
        BufferedReader input=null;
        try
        {
                String line;
                Process p = Runtime.getRuntime().exec("jps -v");
                input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((line = input.readLine()) != null)
                {
                	if(line.contains("jps") || line.contains("Jps") || line.contains("JPS"))continue;
                	else if((line.substring(line.indexOf(" ")+1,line.length())).trim().equalsIgnoreCase("jar")) continue;
                            int indx=line.indexOf(" ");
                            if(processMap.containsKey(line.substring(indx+1,line.length())))
	                                processMap.put(line.substring(indx+1,line.length()),processMap.get(line.substring(indx+1,line.length()))+1);
	                        else
	                                processMap.put(line.substring(indx+1,line.length()),1);
                }
                input.close();
                try
                {
                        InputStream is = p.getInputStream();
                        InputStream es = p.getErrorStream();
                        OutputStream os = p.getOutputStream();
                        is.close();
                        es.close();
                        os.close();
                }
                catch(Exception e1)
                {
                        e1.printStackTrace();
                }
                //System.out.println("Hii process name is " +processName);
        }
        catch(Exception err)
        {
                err.printStackTrace();try{input.close(); }catch(Exception e){}
        }
        //System.out.println("Hii process name is " +processName +" and count is  "+processMap.get(processName));
        if(processMap.get(processName)==null)
        	return 0;
        else
        	return processMap.get(processName);

}
        public static Map<String, Integer> GetProcessList(String processName)
        {
        	HashMap<String,Integer> processMap =new HashMap<String,Integer>();
        	BufferedReader input=null;
                try
                {
                        String line;
                        Process p = Runtime.getRuntime().exec("jps -v");
                        input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
                        while ((line = input.readLine()) != null)
                        {
                        	if(line.contains("jps") || line.contains("Jps") || line.contains("JPS"))continue;
                        	else if((line.substring(line.indexOf(" ")+1,line.length())).trim().equalsIgnoreCase("jar")) continue;
                                int indx=line.indexOf(" ");
                                if(processMap.containsKey(line.substring(indx+1,line.length())))
                                        processMap.put(line.substring(indx+1,line.length()),processMap.get(line.substring(indx+1,line.length()))+1);
                                else
                                        processMap.put(line.substring(indx+1,line.length()),1);
                        }
                        input.close();
 /*                       for (String key : processMap.keySet()) {
                           System.out.println("key: " + key + " value: " + processMap.get(key));
                        }
*/
                        try
                        {
                                InputStream is = p.getInputStream();
                                InputStream es = p.getErrorStream();
                                OutputStream os = p.getOutputStream();
                                is.close();
                                es.close();
                                os.close();
                        }
                        catch(Exception e1)
                        {
                                e1.printStackTrace();
                        }
                }
                catch(Exception err)
                {
                        err.printStackTrace();try{input.close(); }catch(Exception e){}
                }

/*                if(processMap.containsKey(processName) && processMap.get(processName)>1)
                {
                	System.out.println("Process already Running");
                }
*/                return processMap;
        }

        public static String GetProcessList(String processName,String s)
        {
        	HashMap<String,Integer> processMap =new HashMap<String,Integer>();
        	String return_value="PROCESS_NOT_RUNNING";
        	BufferedReader input=null;
                try
                {
                        String line;
                        Process p = Runtime.getRuntime().exec("jps -v");
                        input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
                        while ((line = input.readLine()) != null)
                        {
                        	if(line.contains("jps") || line.contains("Jps") || line.contains("JPS"))continue;
                        	else if((line.substring(line.indexOf(" ")+1,line.length())).trim().equalsIgnoreCase("jar")) continue;
                                int indx=line.indexOf(" ");
                                if(processMap.containsKey(line.substring(indx+1,line.length())))
                                        processMap.put(line.substring(indx+1,line.length()),processMap.get(line.substring(indx+1,line.length()))+1);
                                else
                                        processMap.put(line.substring(indx+1,line.length()),1);
                        }
                        input.close();
 /*                       for (String key : processMap.keySet()) {
                           System.out.println("key: " + key + " value: " + processMap.get(key));
                        }
*/
                        try
                        {
                                InputStream is = p.getInputStream();
                                InputStream es = p.getErrorStream();
                                OutputStream os = p.getOutputStream();
                                is.close();
                                es.close();
                                os.close();
                        }
                        catch(Exception e1)
                        {
                                e1.printStackTrace();
                        }
                }
                catch(Exception err)
                {
                        err.printStackTrace();try{input.close(); }catch(Exception e){}
                }

                if(processMap.containsKey(processName) && processMap.get(processName)>1)
                {
                	return_value="PROCESS_ALREADY_RUNNING";
                }
                return return_value;
        }
}



