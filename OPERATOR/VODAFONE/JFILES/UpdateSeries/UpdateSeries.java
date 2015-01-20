import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateSeries
{
	static Connection conn=null;
	static Statement stmt1=null,stmt2=null;
	public  String LOGDATE_FORMAT="yyyyMMdd";
	public  String LOGTIME_FORMAT="yyyy-MM-dd HH:mm:ss";
	public  String LOG_PATH="/home/ivr/jfiles/UpdateSeries/log/";
	static int cntr=0;

	public static void main(String[] args)
	{
		UpdateSeries us=new UpdateSeries();
		dbConnection();
		us.fileRead();
	}
	public void fileRead()
	{
		String series="",operator="",circle="",num_type="";
		File f=new File("/home/ivr/jfiles/UpdateSeries/series.txt");
		System.out.println("file is ---->>>>"+f);
		if(f.exists())
		{
			try
			{
				String str="";
				FileReader fr=new FileReader(f);
				BufferedReader br =new BufferedReader(fr);
				while(true)
				{
					if((str=br.readLine())!= null)
					{
						cntr++;
						String data[]=str.split("#");
						series=data[0];
						operator=data[1];
						circle=data[2];
						num_type=data[3];
						System.out.println("the counter reached at "+cntr);
						checkNUpdate(series,operator,circle,num_type);
						Thread.sleep(50);
					}
					else
					{
						System.out.println("file is processed and going to be sleep......");
						Thread.sleep(5000);
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("exception in processing the file...."+e);
			}
		}
		else
		{
			System.out.println("File is not exist to be update....");
			System.exit(0);
		}
	}
    /************************************************* CREATING FILE IN APPEND MODE *******************************************/
	public void FILE(String Filename,String Content)
	{
		final File file = new File(Filename);
		final File parent_directory = file.getParentFile();

		if (null != parent_directory)
		{
		    parent_directory.mkdirs();
		}
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(Filename,true));
			out.write(Content+"\n");
			out.close();
		}
		catch (IOException e)
		{
			System.out.println("File I/O Error :"+e);
		}
		catch(Exception E)
		{
			System.out.println("Error :"+E);
		}
	}
    /*********************************************** DATE FORMAT FOR LOGGING DATE *****************************************/
    public  String LOGTIME()
    {
    	Date todaysDate = new java.util.Date();
    	SimpleDateFormat formatter = new SimpleDateFormat(LOGTIME_FORMAT);
    	String formattedDate = formatter.format(todaysDate);
    	return formattedDate;
    }

    /**********************************************DATE String for log_file Creation*******************************************************/
    public   String LOGDATE()
    {
	    Date todaysDate = new java.util.Date();
	    SimpleDateFormat formatter = new SimpleDateFormat(LOGDATE_FORMAT);
	    String formattedDate = formatter.format(todaysDate);
	    return formattedDate;
    }

	public void checkNUpdate(String ser,String opr,String cir,String type)
	{
		int seriesCnt=0;
		String INFO="";
		try
		{
			stmt1=conn.createStatement();
			stmt2=conn.createStatement();
			INFO=ser+"#"+opr+"#"+cir+"#"+type;
			ResultSet rs=stmt1.executeQuery("select count(*) cnt from tbl_valid_series where series="+ser);
			if(rs.next())
				seriesCnt=rs.getInt("cnt");
			if(seriesCnt>=1)
			{
				System.out.println("series "+ser+" is already updated in the table writing logs");
				FILE(LOG_PATH+"Already_Exists_"+LOGDATE()+".txt",LOGTIME()+"#"+INFO);
			}
			else
			{
				System.out.println("series "+ser+" is updating in the table ------>>>>>");
				stmt2.executeUpdate("insert into tbl_valid_series values('"+ser+"','"+opr+"','"+cir+"','"+type+"')");
				FILE(LOG_PATH+"Fresh_Update_"+LOGDATE()+".txt",LOGTIME()+"#"+INFO);
			}
		}
		catch (Exception e)
		{
			System.out.println("exception in checking and updating the series"+e);
			FILE(LOG_PATH+"Exception_"+LOGDATE()+".txt",LOGTIME()+"#"+INFO+"#"+e);
		}

	}
	public static void dbConnection()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
    		//conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/master_db","root","hungama");
    		conn = DriverManager.getConnection("jdbc:mysql://10.43.248.137:3306/master_db","ivr","ivr");
    		System.out.println("Database Connection established! ");
		}
		catch (Exception e)
		{
			System.out.println("exception in creating connection"+e);
		}
	}
}
