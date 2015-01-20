import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.StringTokenizer;

public class HourlyAlert extends Thread{
	public static Connection con_source=null;
	public static Statement stmt_source,stmt_destination;
	public String sIP=null,sDSN=null,sUSR=null,sPWD=null;
	public static CallableStatement cstmt=null;
	public static int day=0;
	public void readDBCONFIG()
	{
		try
		{
			System.out.println("**********************************************************");
			System.out.println("**     Thread Started With The Following Configuration  **");
			System.out.println("**              File to be Read is DB.CFG          **");

			//ResourceBundle resource = ResourceBundle.getBundle("config/cdrmoverDBCONFIG");
			sIP="10.43.248.137";
			sDSN="vodafone_hungama";
			sUSR="team_user";
			sPWD="teamuser@voda#123";

			System.out.println("**SOURCE IP is  ["+sIP+"] **  DSN is ["+sDSN+"] Usr is ["+sUSR+"] Pwd is ["+sPWD+"]\t**");
			System.out.println("**********************************************************");

		}
		catch(Exception e)
		{
			System.out.println("Exception while reading DB.cfg");
			e.printStackTrace();

		}
	}

	public HourlyAlert()
	{
		try
		{
			readDBCONFIG();
			System.out.println("Initiallizing DB");
			Class.forName("com.mysql.jdbc.Driver");
			con_source = DriverManager.getConnection("jdbc:mysql://"+sIP+"/"+sDSN, sUSR, sPWD);
			System.out.println("Database Connection established!");
			stmt_source = con_source.createStatement();
			System.out.println("DB UP");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void run()
	{
		try{
				String FILEDATE = "";
				String OPERATOR = "";
				int D_TCALL_COUNT = 0;
				int D_TFCALL_COUNT = 0;
				int I_TCALL_COUNT = 0;
				int I_TFCALL_COUNT = 0;
				int U_TCALL_COUNT = 0;
				int U_TFCALL_COUNT = 0;
				int MR_TCALL_COUNT = 0;
				int MR_TFCALL_COUNT = 0;
				int MD_TCALL_COUNT = 0;
				int MD_TFCALL_COUNT = 0;
				int MI_TCALL_COUNT = 0;
				int MI_TFCALL_COUNT = 0;
				int MU_TCALL_COUNT = 0;
				int MU_TFCALL_COUNT = 0;
				int CR_TCALL_COUNT = 0;
				int CR_TFCALL_COUNT = 0;
				int CD_TCALL_COUNT = 0;
				int CD_TFCALL_COUNT = 0;
				int CI_TCALL_COUNT = 0;
				int CI_TFCALL_COUNT = 0;
				int CU_TCALL_COUNT = 0;
				int CU_TFCALL_COUNT = 0;
				int MODR_NL_CALL_COUNT = 0;
				int MODR_L_CALL_COUNT = 0;
				int MODD_NL_CALL_COUNT = 0;
				int MODD_L_CALL_COUNT = 0;
				int MODI_NL_CALL_COUNT = 0;
				int MODI_L_CALL_COUNT = 0;
				int MODU_NL_CALL_COUNT = 0;
				int MODU_L_CALL_COUNT = 0;

				String query_date = "select date_format(adddate(now(),0),'%Y%m%d') as 'FILEDATE'";
	            ResultSet Rsdate = stmt_source.executeQuery(query_date);
	            while(Rsdate.next())
	            {
	                    FILEDATE = Rsdate.getString("FILEDATE");
	            }
			    String fpath = "/home/Hungama_call_logs/54646/54646_calllog_"+FILEDATE+".txt";
			    File f1 = new File(fpath);
			    if (f1.exists())
				{
					String line="";
					int[] R_TCALL_ARRAY = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
					int[] R_TFCALL_ARRAY = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
					String STATUS[] = new String[24];
					String DATE[] = new String[24];
					String TIME[] = new String[24];
					String DURATION[] = new String[24];
					String ANI[] = new String[24];
					String CDURATION[] = new String[24];
					String DNIS[] = new String[24];
					String REALDNIS[] = new String[24];
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fpath)));
					while ((line=br.readLine())!=null)
					{
						try
						{
							line=line.replaceAll("##","#0#");
							line=line.replaceAll("###","#0#0#");
							line=line.trim();
							//System.out.println(line);
							StringTokenizer st = new StringTokenizer(line,"#");
							String MACHINE = st.nextToken().trim();
							String ARR[] = MACHINE.split("_");

							//System.out.println(ARR[2]);
							if("APD".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[0] = st.nextToken().trim();
								 DATE[0] = st.nextToken().trim();
								 TIME[0] = st.nextToken().trim();
								 DURATION[0] = st.nextToken().trim();
								 ANI[0] = st.nextToken().trim();
								 CDURATION[0] = st.nextToken().trim();
								 DNIS[0] = st.nextToken().trim();
								 REALDNIS[0] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[0]))
									R_TCALL_ARRAY[0]++;
								else
									R_TFCALL_ARRAY[0]++;
							}
							if("ASM".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[1] = st.nextToken().trim();
								 DATE[1] = st.nextToken().trim();
								 TIME[1] = st.nextToken().trim();
								 DURATION[1] = st.nextToken().trim();
								 ANI[1] = st.nextToken().trim();
								 CDURATION[1] = st.nextToken().trim();
								 DNIS[1] = st.nextToken().trim();
								 REALDNIS[1] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[1]))
									R_TCALL_ARRAY[1]++;
								else
									R_TFCALL_ARRAY[1]++;
							}
							if("BIH".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[2] = st.nextToken().trim();
								 DATE[2] = st.nextToken().trim();
								 TIME[2] = st.nextToken().trim();
								 DURATION[2] = st.nextToken().trim();
								 ANI[2] = st.nextToken().trim();
								 CDURATION[2] = st.nextToken().trim();
								 DNIS[2] = st.nextToken().trim();
								 REALDNIS[2] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[2]))
									R_TCALL_ARRAY[2]++;
								else
									R_TFCALL_ARRAY[2]++;
							}
							if("CHN".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[3] = st.nextToken().trim();
								 DATE[3] = st.nextToken().trim();
								 TIME[3] = st.nextToken().trim();
								 DURATION[3] = st.nextToken().trim();
								 ANI[3] = st.nextToken().trim();
								 CDURATION[3] = st.nextToken().trim();
								 DNIS[3] = st.nextToken().trim();
								 REALDNIS[3] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[3]))
									R_TCALL_ARRAY[3]++;
								else
									R_TFCALL_ARRAY[3]++;
							}
							if("DEL".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[4] = st.nextToken().trim();
								 DATE[4] = st.nextToken().trim();
								 TIME[4] = st.nextToken().trim();
								 DURATION[4] = st.nextToken().trim();
								 ANI[4] = st.nextToken().trim();
								 CDURATION[4] = st.nextToken().trim();
								 DNIS[4] = st.nextToken().trim();
								 REALDNIS[4] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[4]))
									R_TCALL_ARRAY[4]++;
								else
									R_TFCALL_ARRAY[4]++;
							}
							if("GUJ".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[5] = st.nextToken().trim();
								 DATE[5] = st.nextToken().trim();
								 TIME[5] = st.nextToken().trim();
								 DURATION[5] = st.nextToken().trim();
								 ANI[5] = st.nextToken().trim();
								 CDURATION[5] = st.nextToken().trim();
								 DNIS[5] = st.nextToken().trim();
								 REALDNIS[5] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[5]))
									R_TCALL_ARRAY[5]++;
								else
									R_TFCALL_ARRAY[5]++;
							}
							if("HAY".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[6] = st.nextToken().trim();
								 DATE[6] = st.nextToken().trim();
								 TIME[6] = st.nextToken().trim();
								 DURATION[6] = st.nextToken().trim();
								 ANI[6] = st.nextToken().trim();
								 CDURATION[6] = st.nextToken().trim();
								 DNIS[6] = st.nextToken().trim();
								 REALDNIS[6] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[6]))
									R_TCALL_ARRAY[6]++;
								else
									R_TFCALL_ARRAY[6]++;
							}
							if("JNK".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[7] = st.nextToken().trim();
								 DATE[7] = st.nextToken().trim();
								 TIME[7] = st.nextToken().trim();
								 DURATION[7] = st.nextToken().trim();
								 ANI[7] = st.nextToken().trim();
								 CDURATION[7] = st.nextToken().trim();
								 DNIS[7] = st.nextToken().trim();
								 REALDNIS[7] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[7]))
									R_TCALL_ARRAY[7]++;
								else
									R_TFCALL_ARRAY[7]++;
							}
							if("KAR".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[8] = st.nextToken().trim();
								 DATE[8] = st.nextToken().trim();
								 TIME[8] = st.nextToken().trim();
								 DURATION[8] = st.nextToken().trim();
								 ANI[8] = st.nextToken().trim();
								 CDURATION[8] = st.nextToken().trim();
								 DNIS[8] = st.nextToken().trim();
								 REALDNIS[8] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[8]))
									R_TCALL_ARRAY[8]++;
								else
									R_TFCALL_ARRAY[8]++;
							}
							if("KOL".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[9] = st.nextToken().trim();
								 DATE[9] = st.nextToken().trim();
								 TIME[9] = st.nextToken().trim();
								 DURATION[9] = st.nextToken().trim();
								 ANI[9] = st.nextToken().trim();
								 CDURATION[9] = st.nextToken().trim();
								 DNIS[9] = st.nextToken().trim();
								 REALDNIS[9] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[9]))
									R_TCALL_ARRAY[9]++;
								else
									R_TFCALL_ARRAY[9]++;
							}
							if("MAH".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[10] = st.nextToken().trim();
								 DATE[10] = st.nextToken().trim();
								 TIME[10] = st.nextToken().trim();
								 DURATION[10] = st.nextToken().trim();
								 ANI[10] = st.nextToken().trim();
								 CDURATION[10] = st.nextToken().trim();
								 DNIS[10] = st.nextToken().trim();
								 REALDNIS[10] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[10]))
									R_TCALL_ARRAY[10]++;
								else
									R_TFCALL_ARRAY[10]++;
							}
							if("MPD".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[11] = st.nextToken().trim();
								 DATE[11] = st.nextToken().trim();
								 TIME[11] = st.nextToken().trim();
								 DURATION[11] = st.nextToken().trim();
								 ANI[11] = st.nextToken().trim();
								 CDURATION[11] = st.nextToken().trim();
								 DNIS[11] = st.nextToken().trim();
								 REALDNIS[11] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[11]))
									R_TCALL_ARRAY[11]++;
								else
									R_TFCALL_ARRAY[11]++;
							}
							if("MUM".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[12] = st.nextToken().trim();
								 DATE[12] = st.nextToken().trim();
								 TIME[12] = st.nextToken().trim();
								 DURATION[12] = st.nextToken().trim();
								 ANI[12] = st.nextToken().trim();
								 CDURATION[12] = st.nextToken().trim();
								 DNIS[12] = st.nextToken().trim();
								 REALDNIS[12] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[12]))
									R_TCALL_ARRAY[12]++;
								else
									R_TFCALL_ARRAY[12]++;
							}
							if("NES".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[13] = st.nextToken().trim();
								 DATE[13] = st.nextToken().trim();
								 TIME[13] = st.nextToken().trim();
								 DURATION[13] = st.nextToken().trim();
								 ANI[13] = st.nextToken().trim();
								 CDURATION[13] = st.nextToken().trim();
								 DNIS[13] = st.nextToken().trim();
								 REALDNIS[13] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[13]))
									R_TCALL_ARRAY[13]++;
								else
									R_TFCALL_ARRAY[13]++;
							}
							if("ORI".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[14] = st.nextToken().trim();
								 DATE[14] = st.nextToken().trim();
								 TIME[14] = st.nextToken().trim();
								 DURATION[14] = st.nextToken().trim();
								 ANI[14] = st.nextToken().trim();
								 CDURATION[14] = st.nextToken().trim();
								 DNIS[14] = st.nextToken().trim();
								 REALDNIS[14] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[14]))
									R_TCALL_ARRAY[14]++;
								else
									R_TFCALL_ARRAY[14]++;
							}
							if("PUB".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[15] = st.nextToken().trim();
								 DATE[15] = st.nextToken().trim();
								 TIME[15] = st.nextToken().trim();
								 DURATION[15] = st.nextToken().trim();
								 ANI[15] = st.nextToken().trim();
								 CDURATION[15] = st.nextToken().trim();
								 DNIS[15] = st.nextToken().trim();
								 REALDNIS[15] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[15]))
									R_TCALL_ARRAY[15]++;
								else
									R_TFCALL_ARRAY[15]++;
							}
							if("RAJ".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[16] = st.nextToken().trim();
								 DATE[16] = st.nextToken().trim();
								 TIME[16] = st.nextToken().trim();
								 DURATION[16] = st.nextToken().trim();
								 ANI[16] = st.nextToken().trim();
								 CDURATION[16] = st.nextToken().trim();
								 DNIS[16] = st.nextToken().trim();
								 REALDNIS[16]= st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[16]))
									R_TCALL_ARRAY[16]++;
								else
									R_TFCALL_ARRAY[16]++;
							}
							if("TNU".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[17] = st.nextToken().trim();
								 DATE[17] = st.nextToken().trim();
								 TIME[17] = st.nextToken().trim();
								 DURATION[17] = st.nextToken().trim();
								 ANI[17] = st.nextToken().trim();
								 CDURATION[17] = st.nextToken().trim();
								 DNIS[17] = st.nextToken().trim();
								 REALDNIS[17] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[17]))
									R_TCALL_ARRAY[17]++;
								else
									R_TFCALL_ARRAY[17]++;
							}
							if("UPE".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[18] = st.nextToken().trim();
								 DATE[18] = st.nextToken().trim();
								 TIME[18] = st.nextToken().trim();
								 DURATION[18] = st.nextToken().trim();
								 ANI[18] = st.nextToken().trim();
								 CDURATION[18] = st.nextToken().trim();
								 DNIS[18] = st.nextToken().trim();
								 REALDNIS[18] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[18]))
									R_TCALL_ARRAY[18]++;
								else
									R_TFCALL_ARRAY[18]++;
							}
							if("UPW".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[19] = st.nextToken().trim();
								 DATE[19] = st.nextToken().trim();
								 TIME[19] = st.nextToken().trim();
								 DURATION[19] = st.nextToken().trim();
								 ANI[19]  = st.nextToken().trim();
								 CDURATION[19] = st.nextToken().trim();
								 DNIS[19] = st.nextToken().trim();
								 REALDNIS[19] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[19]))
									R_TCALL_ARRAY[19]++;
								else
									R_TFCALL_ARRAY[19]++;
							}
							if("WBL".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[20] = st.nextToken().trim();
								 DATE[20] = st.nextToken().trim();
								 TIME[20] = st.nextToken().trim();
								 DURATION[20] = st.nextToken().trim();
								 ANI[20] = st.nextToken().trim();
								 CDURATION[20] = st.nextToken().trim();
								 DNIS[20] = st.nextToken().trim();
								 REALDNIS[20] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[20]))
									R_TCALL_ARRAY[20]++;
								else
									R_TFCALL_ARRAY[20]++;
							}
							if("HPD".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[21] = st.nextToken().trim();
								 DATE[21] = st.nextToken().trim();
								 TIME[21] = st.nextToken().trim();
								 DURATION[21] = st.nextToken().trim();
								 ANI[21] = st.nextToken().trim();
								 CDURATION[21] = st.nextToken().trim();
								 DNIS[21] = st.nextToken().trim();
								 REALDNIS[21] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[21]))
									R_TCALL_ARRAY[21]++;
								else
									R_TFCALL_ARRAY[21]++;
							}
							if("KER".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[22] = st.nextToken().trim();
								 DATE[22] = st.nextToken().trim();
								 TIME[22] = st.nextToken().trim();
								 DURATION[22] = st.nextToken().trim();
								 ANI[22] = st.nextToken().trim();
								 CDURATION[22] = st.nextToken().trim();
								 DNIS[22] = st.nextToken().trim();
								 REALDNIS[22] = st.nextToken().trim();
								if("54646".equalsIgnoreCase(REALDNIS[22]))
									R_TCALL_ARRAY[22]++;
								else
									R_TFCALL_ARRAY[22]++;
							}
						}catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					for(int i=0;i<23;i++)
					{
						R_TCALL_ARRAY[23] = R_TCALL_ARRAY[23] + R_TCALL_ARRAY[i];
						R_TFCALL_ARRAY[23] = R_TFCALL_ARRAY[23] + R_TFCALL_ARRAY[i];
					}
					for(int i=0;i<24;i++)
					{
						String CIRCLE = getCIRCLE(i);
//						if("PAN".equalsIgnoreCase(CIRCLE)||"GUJ".equalsIgnoreCase(CIRCLE))
//						{
							System.out.println("Hungama RELserver TCALL_COUNT:"+R_TCALL_ARRAY[i]+"TFCALL_COUNT:"+R_TFCALL_ARRAY[i]+"CIRCLE:"+getCIRCLE(i));
							cstmt = con_source.prepareCall("{call vodafone_hungama.JBOX_HOURLYALERT(?,?,?)}");
							cstmt.setInt(1, R_TCALL_ARRAY[i]);
							cstmt.setInt(2, R_TFCALL_ARRAY[i]);
							cstmt.setString(3, getCIRCLE(i));
							cstmt.execute();
							cstmt.close();
//						}
					}

				}
/*				fpath = "/home/Hungama_call_logs/mtv/mtv_calllog_"+FILEDATE+".txt";
				File f3 = new File(fpath);
				if (f3.exists())
				{
					String line="";
					int[] R_TCALL_ARRAY = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
					int[] R_TFCALL_ARRAY = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
					String STATUS[] = new String[24];
					String DATE[] = new String[24];
					String TIME[] = new String[24];
					String DURATION[] = new String[24];
					String ANI[] = new String[24];
					String CDURATION[] = new String[24];
					String DNIS[] = new String[24];
					String REALDNIS[] = new String[24];
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fpath)));
					while ((line=br.readLine())!=null)
					{
						try
						{
							line=line.trim();
							//System.out.println(line);
							StringTokenizer st = new StringTokenizer(line,"#");
							String MACHINE = st.nextToken().trim();
							String ARR[] = MACHINE.split("_");
							if("APD".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[0] = st.nextToken().trim();
								 DATE[0] = st.nextToken().trim();
								 TIME[0] = st.nextToken().trim();
								 DURATION[0] = st.nextToken().trim();
								 ANI[0] = st.nextToken().trim();
								 CDURATION[0] = st.nextToken().trim();
								 DNIS[0] = st.nextToken().trim();
								 REALDNIS[0] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[0]))
									R_TCALL_ARRAY[0]++;
								else
									R_TFCALL_ARRAY[0]++;
							}
							if("ASM".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[1] = st.nextToken().trim();
								 DATE[1] = st.nextToken().trim();
								 TIME[1] = st.nextToken().trim();
								 DURATION[1] = st.nextToken().trim();
								 ANI[1] = st.nextToken().trim();
								 CDURATION[1] = st.nextToken().trim();
								 DNIS[1] = st.nextToken().trim();
								 REALDNIS[1] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[1]))
									R_TCALL_ARRAY[1]++;
								else
									R_TFCALL_ARRAY[1]++;
							}
							if("BIH".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[2] = st.nextToken().trim();
								 DATE[2] = st.nextToken().trim();
								 TIME[2] = st.nextToken().trim();
								 DURATION[2] = st.nextToken().trim();
								 ANI[2] = st.nextToken().trim();
								 CDURATION[2] = st.nextToken().trim();
								 DNIS[2] = st.nextToken().trim();
								 REALDNIS[2] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[2]))
									R_TCALL_ARRAY[2]++;
								else
									R_TFCALL_ARRAY[2]++;
							}
							if("CHN".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[3] = st.nextToken().trim();
								 DATE[3] = st.nextToken().trim();
								 TIME[3] = st.nextToken().trim();
								 DURATION[3] = st.nextToken().trim();
								 ANI[3] = st.nextToken().trim();
								 CDURATION[3] = st.nextToken().trim();
								 DNIS[3] = st.nextToken().trim();
								 REALDNIS[3] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[3]))
									R_TCALL_ARRAY[3]++;
								else
									R_TFCALL_ARRAY[3]++;
							}
							if("DEL".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[4] = st.nextToken().trim();
								 DATE[4] = st.nextToken().trim();
								 TIME[4] = st.nextToken().trim();
								 DURATION[4] = st.nextToken().trim();
								 ANI[4] = st.nextToken().trim();
								 CDURATION[4] = st.nextToken().trim();
								 DNIS[4] = st.nextToken().trim();
								 REALDNIS[4] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[4]))
									R_TCALL_ARRAY[4]++;
								else
									R_TFCALL_ARRAY[4]++;
							}
							if("GUJ".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[5] = st.nextToken().trim();
								 DATE[5] = st.nextToken().trim();
								 TIME[5] = st.nextToken().trim();
								 DURATION[5] = st.nextToken().trim();
								 ANI[5] = st.nextToken().trim();
								 CDURATION[5] = st.nextToken().trim();
								 DNIS[5] = st.nextToken().trim();
								 REALDNIS[5] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[5]))
									R_TCALL_ARRAY[5]++;
								else
									R_TFCALL_ARRAY[5]++;
							}
							if("HAY".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[6] = st.nextToken().trim();
								 DATE[6] = st.nextToken().trim();
								 TIME[6] = st.nextToken().trim();
								 DURATION[6] = st.nextToken().trim();
								 ANI[6] = st.nextToken().trim();
								 CDURATION[6] = st.nextToken().trim();
								 DNIS[6] = st.nextToken().trim();
								 REALDNIS[6] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[6]))
									R_TCALL_ARRAY[6]++;
								else
									R_TFCALL_ARRAY[6]++;
							}
							if("JNK".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[7] = st.nextToken().trim();
								 DATE[7] = st.nextToken().trim();
								 TIME[7] = st.nextToken().trim();
								 DURATION[7] = st.nextToken().trim();
								 ANI[7] = st.nextToken().trim();
								 CDURATION[7] = st.nextToken().trim();
								 DNIS[7] = st.nextToken().trim();
								 REALDNIS[7] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[7]))
									R_TCALL_ARRAY[7]++;
								else
									R_TFCALL_ARRAY[7]++;
							}
							if("KAR".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[8] = st.nextToken().trim();
								 DATE[8] = st.nextToken().trim();
								 TIME[8] = st.nextToken().trim();
								 DURATION[8] = st.nextToken().trim();
								 ANI[8] = st.nextToken().trim();
								 CDURATION[8] = st.nextToken().trim();
								 DNIS[8] = st.nextToken().trim();
								 REALDNIS[8] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[8]))
									R_TCALL_ARRAY[8]++;
								else
									R_TFCALL_ARRAY[8]++;
							}
							if("KOL".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[9] = st.nextToken().trim();
								 DATE[9] = st.nextToken().trim();
								 TIME[9] = st.nextToken().trim();
								 DURATION[9] = st.nextToken().trim();
								 ANI[9] = st.nextToken().trim();
								 CDURATION[9] = st.nextToken().trim();
								 DNIS[9] = st.nextToken().trim();
								 REALDNIS[9] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[9]))
									R_TCALL_ARRAY[9]++;
								else
									R_TFCALL_ARRAY[9]++;
							}
							if("MAH".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[10] = st.nextToken().trim();
								 DATE[10] = st.nextToken().trim();
								 TIME[10] = st.nextToken().trim();
								 DURATION[10] = st.nextToken().trim();
								 ANI[10] = st.nextToken().trim();
								 CDURATION[10] = st.nextToken().trim();
								 DNIS[10] = st.nextToken().trim();
								 REALDNIS[10] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[10]))
									R_TCALL_ARRAY[10]++;
								else
									R_TFCALL_ARRAY[10]++;
							}
							if("MPD".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[11] = st.nextToken().trim();
								 DATE[11] = st.nextToken().trim();
								 TIME[11] = st.nextToken().trim();
								 DURATION[11] = st.nextToken().trim();
								 ANI[11] = st.nextToken().trim();
								 CDURATION[11] = st.nextToken().trim();
								 DNIS[11] = st.nextToken().trim();
								 REALDNIS[11] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[11]))
									R_TCALL_ARRAY[11]++;
								else
									R_TFCALL_ARRAY[11]++;
							}
							if("MUM".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[12] = st.nextToken().trim();
								 DATE[12] = st.nextToken().trim();
								 TIME[12] = st.nextToken().trim();
								 DURATION[12] = st.nextToken().trim();
								 ANI[12] = st.nextToken().trim();
								 CDURATION[12] = st.nextToken().trim();
								 DNIS[12] = st.nextToken().trim();
								 REALDNIS[12] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[12]))
									R_TCALL_ARRAY[12]++;
								else
									R_TFCALL_ARRAY[12]++;
							}
							if("NES".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[13] = st.nextToken().trim();
								 DATE[13] = st.nextToken().trim();
								 TIME[13] = st.nextToken().trim();
								 DURATION[13] = st.nextToken().trim();
								 ANI[13] = st.nextToken().trim();
								 CDURATION[13] = st.nextToken().trim();
								 DNIS[13] = st.nextToken().trim();
								 REALDNIS[13] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[13]))
									R_TCALL_ARRAY[13]++;
								else
									R_TFCALL_ARRAY[13]++;
							}
							if("ORI".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[14] = st.nextToken().trim();
								 DATE[14] = st.nextToken().trim();
								 TIME[14] = st.nextToken().trim();
								 DURATION[14] = st.nextToken().trim();
								 ANI[14] = st.nextToken().trim();
								 CDURATION[14] = st.nextToken().trim();
								 DNIS[14] = st.nextToken().trim();
								 REALDNIS[14] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[14]))
									R_TCALL_ARRAY[14]++;
								else
									R_TFCALL_ARRAY[14]++;
							}
							if("PUB".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[15] = st.nextToken().trim();
								 DATE[15] = st.nextToken().trim();
								 TIME[15] = st.nextToken().trim();
								 DURATION[15] = st.nextToken().trim();
								 ANI[15] = st.nextToken().trim();
								 CDURATION[15] = st.nextToken().trim();
								 DNIS[15] = st.nextToken().trim();
								 REALDNIS[15] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[15]))
									R_TCALL_ARRAY[15]++;
								else
									R_TFCALL_ARRAY[15]++;
							}
							if("RAJ".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[16] = st.nextToken().trim();
								 DATE[16] = st.nextToken().trim();
								 TIME[16] = st.nextToken().trim();
								 DURATION[16] = st.nextToken().trim();
								 ANI[16] = st.nextToken().trim();
								 CDURATION[16] = st.nextToken().trim();
								 DNIS[16] = st.nextToken().trim();
								 REALDNIS[16]= st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[16]))
									R_TCALL_ARRAY[16]++;
								else
									R_TFCALL_ARRAY[16]++;
							}
							if("TNU".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[17] = st.nextToken().trim();
								 DATE[17] = st.nextToken().trim();
								 TIME[17] = st.nextToken().trim();
								 DURATION[17] = st.nextToken().trim();
								 ANI[17] = st.nextToken().trim();
								 CDURATION[17] = st.nextToken().trim();
								 DNIS[17] = st.nextToken().trim();
								 REALDNIS[17] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[17]))
									R_TCALL_ARRAY[17]++;
								else
									R_TFCALL_ARRAY[17]++;
							}
							if("UPE".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[18] = st.nextToken().trim();
								 DATE[18] = st.nextToken().trim();
								 TIME[18] = st.nextToken().trim();
								 DURATION[18] = st.nextToken().trim();
								 ANI[18] = st.nextToken().trim();
								 CDURATION[18] = st.nextToken().trim();
								 DNIS[18] = st.nextToken().trim();
								 REALDNIS[18] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[18]))
									R_TCALL_ARRAY[18]++;
								else
									R_TFCALL_ARRAY[18]++;
							}
							if("UPW".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[19] = st.nextToken().trim();
								 DATE[19] = st.nextToken().trim();
								 TIME[19] = st.nextToken().trim();
								 DURATION[19] = st.nextToken().trim();
								 ANI[19]  = st.nextToken().trim();
								 CDURATION[19] = st.nextToken().trim();
								 DNIS[19] = st.nextToken().trim();
								 REALDNIS[19] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[19]))
									R_TCALL_ARRAY[19]++;
								else
									R_TFCALL_ARRAY[19]++;
							}
							if("WBL".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[20] = st.nextToken().trim();
								 DATE[20] = st.nextToken().trim();
								 TIME[20] = st.nextToken().trim();
								 DURATION[20] = st.nextToken().trim();
								 ANI[20] = st.nextToken().trim();
								 CDURATION[20] = st.nextToken().trim();
								 DNIS[20] = st.nextToken().trim();
								 REALDNIS[20] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[20]))
									R_TCALL_ARRAY[20]++;
								else
									R_TFCALL_ARRAY[20]++;
							}
							if("HPD".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[21] = st.nextToken().trim();
								 DATE[21] = st.nextToken().trim();
								 TIME[21] = st.nextToken().trim();
								 DURATION[21] = st.nextToken().trim();
								 ANI[21] = st.nextToken().trim();
								 CDURATION[21] = st.nextToken().trim();
								 DNIS[21] = st.nextToken().trim();
								 REALDNIS[21] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[21]))
									R_TCALL_ARRAY[21]++;
								else
									R_TFCALL_ARRAY[21]++;
							}
							if("KER".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[22] = st.nextToken().trim();
								 DATE[22] = st.nextToken().trim();
								 TIME[22] = st.nextToken().trim();
								 DURATION[22] = st.nextToken().trim();
								 ANI[22] = st.nextToken().trim();
								 CDURATION[22] = st.nextToken().trim();
								 DNIS[22] = st.nextToken().trim();
								 REALDNIS[22] = st.nextToken().trim();
								if("546461".equalsIgnoreCase(REALDNIS[22]))
									R_TCALL_ARRAY[22]++;
								else
									R_TFCALL_ARRAY[22]++;
							}

						}catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					for(int i=0;i<23;i++)
					{
						R_TCALL_ARRAY[23] = R_TCALL_ARRAY[23] + R_TCALL_ARRAY[i];
						R_TFCALL_ARRAY[23] = R_TFCALL_ARRAY[23] + R_TFCALL_ARRAY[i];
					}
					for(int i=0;i<24;i++)
					{
						String CIRCLE = getCIRCLE(i);
						if("PAN".equalsIgnoreCase(CIRCLE))
						{
							System.out.println("Hungama RELserver TCALL_COUNT:"+R_TCALL_ARRAY[i]+"TFCALL_COUNT:"+R_TFCALL_ARRAY[i]+"CIRCLE:"+getCIRCLE(i));
							cstmt = con_source.prepareCall("{call mts_mtv.MTV_HOURLYALERT(?,?,?)}");
							cstmt.setInt(1, R_TCALL_ARRAY[i]);
							cstmt.setInt(2, R_TFCALL_ARRAY[i]);
							cstmt.setString(3, getCIRCLE(i));
							cstmt.execute();
							cstmt.close();
						}
					}
				}
				fpath = "/home/Hungama_call_logs/MOD/MOD_calllog_"+FILEDATE+".txt";
				File f7 = new File(fpath);
				if (f7.exists())
				{
					String line="";
					int[] R_TCALL_ARRAY = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
					int[] R_TFCALL_ARRAY = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
					String STATUS[] = new String[24];
					String DATE[] = new String[24];
					String TIME[] = new String[24];
					String DURATION[] = new String[24];
					String ANI[] = new String[24];
					String CDURATION[] = new String[24];
					String DNIS[] = new String[24];
					String REALDNIS[] = new String[24];
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fpath)));
					while ((line=br.readLine())!=null)
					{
						try
						{
							line=line.trim();
							StringTokenizer st = new StringTokenizer(line,"#");
							String MACHINE = st.nextToken().trim();
							String ARR[] = MACHINE.split("_");
							if("APD".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[0] = st.nextToken().trim();
								 DATE[0] = st.nextToken().trim();
								 TIME[0] = st.nextToken().trim();
								 DURATION[0] = st.nextToken().trim();
								 ANI[0] = st.nextToken().trim();
								 CDURATION[0] = st.nextToken().trim();
								 DNIS[0] = st.nextToken().trim();
								 REALDNIS[0] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[0]))
									R_TCALL_ARRAY[0]++;
								else
									R_TFCALL_ARRAY[0]++;
							}
							if("ASM".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[1] = st.nextToken().trim();
								 DATE[1] = st.nextToken().trim();
								 TIME[1] = st.nextToken().trim();
								 DURATION[1] = st.nextToken().trim();
								 ANI[1] = st.nextToken().trim();
								 CDURATION[1] = st.nextToken().trim();
								 DNIS[1] = st.nextToken().trim();
								 REALDNIS[1] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[1]))
									R_TCALL_ARRAY[1]++;
								else
									R_TFCALL_ARRAY[1]++;
							}
							if("BIH".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[2] = st.nextToken().trim();
								 DATE[2] = st.nextToken().trim();
								 TIME[2] = st.nextToken().trim();
								 DURATION[2] = st.nextToken().trim();
								 ANI[2] = st.nextToken().trim();
								 CDURATION[2] = st.nextToken().trim();
								 DNIS[2] = st.nextToken().trim();
								 REALDNIS[2] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[2]))
									R_TCALL_ARRAY[2]++;
								else
									R_TFCALL_ARRAY[2]++;
							}
							if("CHN".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[3] = st.nextToken().trim();
								 DATE[3] = st.nextToken().trim();
								 TIME[3] = st.nextToken().trim();
								 DURATION[3] = st.nextToken().trim();
								 ANI[3] = st.nextToken().trim();
								 CDURATION[3] = st.nextToken().trim();
								 DNIS[3] = st.nextToken().trim();
								 REALDNIS[3] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[3]))
									R_TCALL_ARRAY[3]++;
								else
									R_TFCALL_ARRAY[3]++;
							}
							if("DEL".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[4] = st.nextToken().trim();
								 DATE[4] = st.nextToken().trim();
								 TIME[4] = st.nextToken().trim();
								 DURATION[4] = st.nextToken().trim();
								 ANI[4] = st.nextToken().trim();
								 CDURATION[4] = st.nextToken().trim();
								 DNIS[4] = st.nextToken().trim();
								 REALDNIS[4] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[4]))
									R_TCALL_ARRAY[4]++;
								else
									R_TFCALL_ARRAY[4]++;
							}
							if("GUJ".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[5] = st.nextToken().trim();
								 DATE[5] = st.nextToken().trim();
								 TIME[5] = st.nextToken().trim();
								 DURATION[5] = st.nextToken().trim();
								 ANI[5] = st.nextToken().trim();
								 CDURATION[5] = st.nextToken().trim();
								 DNIS[5] = st.nextToken().trim();
								 REALDNIS[5] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[5]))
									R_TCALL_ARRAY[5]++;
								else
									R_TFCALL_ARRAY[5]++;
							}
							if("HAY".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[6] = st.nextToken().trim();
								 DATE[6] = st.nextToken().trim();
								 TIME[6] = st.nextToken().trim();
								 DURATION[6] = st.nextToken().trim();
								 ANI[6] = st.nextToken().trim();
								 CDURATION[6] = st.nextToken().trim();
								 DNIS[6] = st.nextToken().trim();
								 REALDNIS[6] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[6]))
									R_TCALL_ARRAY[6]++;
								else
									R_TFCALL_ARRAY[6]++;
							}
							if("JNK".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[7] = st.nextToken().trim();
								 DATE[7] = st.nextToken().trim();
								 TIME[7] = st.nextToken().trim();
								 DURATION[7] = st.nextToken().trim();
								 ANI[7] = st.nextToken().trim();
								 CDURATION[7] = st.nextToken().trim();
								 DNIS[7] = st.nextToken().trim();
								 REALDNIS[7] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[7]))
									R_TCALL_ARRAY[7]++;
								else
									R_TFCALL_ARRAY[7]++;
							}
							if("KAR".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[8] = st.nextToken().trim();
								 DATE[8] = st.nextToken().trim();
								 TIME[8] = st.nextToken().trim();
								 DURATION[8] = st.nextToken().trim();
								 ANI[8] = st.nextToken().trim();
								 CDURATION[8] = st.nextToken().trim();
								 DNIS[8] = st.nextToken().trim();
								 REALDNIS[8] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[8]))
									R_TCALL_ARRAY[8]++;
								else
									R_TFCALL_ARRAY[8]++;
							}
							if("KOL".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[9] = st.nextToken().trim();
								 DATE[9] = st.nextToken().trim();
								 TIME[9] = st.nextToken().trim();
								 DURATION[9] = st.nextToken().trim();
								 ANI[9] = st.nextToken().trim();
								 CDURATION[9] = st.nextToken().trim();
								 DNIS[9] = st.nextToken().trim();
								 REALDNIS[9] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[9]))
									R_TCALL_ARRAY[9]++;
								else
									R_TFCALL_ARRAY[9]++;
							}
							if("MAH".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[10] = st.nextToken().trim();
								 DATE[10] = st.nextToken().trim();
								 TIME[10] = st.nextToken().trim();
								 DURATION[10] = st.nextToken().trim();
								 ANI[10] = st.nextToken().trim();
								 CDURATION[10] = st.nextToken().trim();
								 DNIS[10] = st.nextToken().trim();
								 REALDNIS[10] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[10]))
									R_TCALL_ARRAY[10]++;
								else
									R_TFCALL_ARRAY[10]++;
							}
							if("MPD".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[11] = st.nextToken().trim();
								 DATE[11] = st.nextToken().trim();
								 TIME[11] = st.nextToken().trim();
								 DURATION[11] = st.nextToken().trim();
								 ANI[11] = st.nextToken().trim();
								 CDURATION[11] = st.nextToken().trim();
								 DNIS[11] = st.nextToken().trim();
								 REALDNIS[11] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[11]))
									R_TCALL_ARRAY[11]++;
								else
									R_TFCALL_ARRAY[11]++;
							}
							if("MUM".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[12] = st.nextToken().trim();
								 DATE[12] = st.nextToken().trim();
								 TIME[12] = st.nextToken().trim();
								 DURATION[12] = st.nextToken().trim();
								 ANI[12] = st.nextToken().trim();
								 CDURATION[12] = st.nextToken().trim();
								 DNIS[12] = st.nextToken().trim();
								 REALDNIS[12] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[12]))
									R_TCALL_ARRAY[12]++;
								else
									R_TFCALL_ARRAY[12]++;
							}
							if("NES".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[13] = st.nextToken().trim();
								 DATE[13] = st.nextToken().trim();
								 TIME[13] = st.nextToken().trim();
								 DURATION[13] = st.nextToken().trim();
								 ANI[13] = st.nextToken().trim();
								 CDURATION[13] = st.nextToken().trim();
								 DNIS[13] = st.nextToken().trim();
								 REALDNIS[13] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[13]))
									R_TCALL_ARRAY[13]++;
								else
									R_TFCALL_ARRAY[13]++;
							}
							if("ORI".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[14] = st.nextToken().trim();
								 DATE[14] = st.nextToken().trim();
								 TIME[14] = st.nextToken().trim();
								 DURATION[14] = st.nextToken().trim();
								 ANI[14] = st.nextToken().trim();
								 CDURATION[14] = st.nextToken().trim();
								 DNIS[14] = st.nextToken().trim();
								 REALDNIS[14] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[14]))
									R_TCALL_ARRAY[14]++;
								else
									R_TFCALL_ARRAY[14]++;
							}
							if("PUB".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[15] = st.nextToken().trim();
								 DATE[15] = st.nextToken().trim();
								 TIME[15] = st.nextToken().trim();
								 DURATION[15] = st.nextToken().trim();
								 ANI[15] = st.nextToken().trim();
								 CDURATION[15] = st.nextToken().trim();
								 DNIS[15] = st.nextToken().trim();
								 REALDNIS[15] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[15]))
									R_TCALL_ARRAY[15]++;
								else
									R_TFCALL_ARRAY[15]++;
							}
							if("RAJ".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[16] = st.nextToken().trim();
								 DATE[16] = st.nextToken().trim();
								 TIME[16] = st.nextToken().trim();
								 DURATION[16] = st.nextToken().trim();
								 ANI[16] = st.nextToken().trim();
								 CDURATION[16] = st.nextToken().trim();
								 DNIS[16] = st.nextToken().trim();
								 REALDNIS[16]= st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[16]))
									R_TCALL_ARRAY[16]++;
								else
									R_TFCALL_ARRAY[16]++;
							}
							if("TNU".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[17] = st.nextToken().trim();
								 DATE[17] = st.nextToken().trim();
								 TIME[17] = st.nextToken().trim();
								 DURATION[17] = st.nextToken().trim();
								 ANI[17] = st.nextToken().trim();
								 CDURATION[17] = st.nextToken().trim();
								 DNIS[17] = st.nextToken().trim();
								 REALDNIS[17] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[17]))
									R_TCALL_ARRAY[17]++;
								else
									R_TFCALL_ARRAY[17]++;
							}
							if("UPE".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[18] = st.nextToken().trim();
								 DATE[18] = st.nextToken().trim();
								 TIME[18] = st.nextToken().trim();
								 DURATION[18] = st.nextToken().trim();
								 ANI[18] = st.nextToken().trim();
								 CDURATION[18] = st.nextToken().trim();
								 DNIS[18] = st.nextToken().trim();
								 REALDNIS[18] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[18]))
									R_TCALL_ARRAY[18]++;
								else
									R_TFCALL_ARRAY[18]++;
							}
							if("UPW".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[19] = st.nextToken().trim();
								 DATE[19] = st.nextToken().trim();
								 TIME[19] = st.nextToken().trim();
								 DURATION[19] = st.nextToken().trim();
								 ANI[19]  = st.nextToken().trim();
								 CDURATION[19] = st.nextToken().trim();
								 DNIS[19] = st.nextToken().trim();
								 REALDNIS[19] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[19]))
									R_TCALL_ARRAY[19]++;
								else
									R_TFCALL_ARRAY[19]++;
							}
							if("WBL".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[20] = st.nextToken().trim();
								 DATE[20] = st.nextToken().trim();
								 TIME[20] = st.nextToken().trim();
								 DURATION[20] = st.nextToken().trim();
								 ANI[20] = st.nextToken().trim();
								 CDURATION[20] = st.nextToken().trim();
								 DNIS[20] = st.nextToken().trim();
								 REALDNIS[20] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[20]))
									R_TCALL_ARRAY[20]++;
								else
									R_TFCALL_ARRAY[20]++;
							}
							if("HPD".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[21] = st.nextToken().trim();
								 DATE[21] = st.nextToken().trim();
								 TIME[21] = st.nextToken().trim();
								 DURATION[21] = st.nextToken().trim();
								 ANI[21] = st.nextToken().trim();
								 CDURATION[21] = st.nextToken().trim();
								 DNIS[21] = st.nextToken().trim();
								 REALDNIS[21] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[21]))
									R_TCALL_ARRAY[21]++;
								else
									R_TFCALL_ARRAY[21]++;
							}
							if("KER".equalsIgnoreCase(ARR[2]))
							{
								 STATUS[22] = st.nextToken().trim();
								 DATE[22] = st.nextToken().trim();
								 TIME[22] = st.nextToken().trim();
								 DURATION[22] = st.nextToken().trim();
								 ANI[22] = st.nextToken().trim();
								 CDURATION[22] = st.nextToken().trim();
								 DNIS[22] = st.nextToken().trim();
								 REALDNIS[22] = st.nextToken().trim();
								if("-1".equalsIgnoreCase(STATUS[22]))
									R_TCALL_ARRAY[22]++;
								else
									R_TFCALL_ARRAY[22]++;
							}

						}catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					for(int i=0;i<23;i++)
					{
						R_TCALL_ARRAY[23] = R_TCALL_ARRAY[23] + R_TCALL_ARRAY[i];
						R_TFCALL_ARRAY[23] = R_TFCALL_ARRAY[23] + R_TFCALL_ARRAY[i];
					}
					for(int i=0;i<24;i++)
					{
						String CIRCLE = getCIRCLE(i);
						if("PAN".equalsIgnoreCase(CIRCLE)|| "GUJ".equalsIgnoreCase(CIRCLE))
						{
							System.out.println("Hungama RELserver TCALL_COUNT:"+R_TCALL_ARRAY[i]+"TFCALL_COUNT:"+R_TFCALL_ARRAY[i]+"CIRCLE:"+getCIRCLE(i));
							cstmt = con_source.prepareCall("{call mts_radio.RADIO_HOURLYALERT(?,?,?)}");
							cstmt.setInt(1, R_TCALL_ARRAY[i]);
							cstmt.setInt(2, R_TFCALL_ARRAY[i]);
							cstmt.setString(3, getCIRCLE(i));
							cstmt.execute();
							cstmt.close();
						}
					}

				}

*/
		}catch(Exception e)
{
			e.printStackTrace();
			System.out.println("Error  "+e);

		}
	}
	public String getCIRCLE(int index)
	{
		String c;
		if(index==0)
			c = "APD";
		else if(index==1)
			c= "ASM";
		else if(index==2)
			c= "BIH";
		else if(index==3)
			c = "CHN";
		else if(index==4)
			c = "DEL";
		else if(index==5)
			c = "GUJ";
		else if(index==6)
			c = "HAY";
		else if(index==7)
			c = "JNK";
		else if(index==8)
			c = "KAR";
		else if(index==9)
			c = "KOL";
		else if(index==10)
			c = "MAH";
		else if(index==11)
			c = "MPD";
		else if(index==12)
			c = "MUM";
		else if(index==13)
			c = "NES";
		else if(index==14)
			c = "ORI";
		else if(index==15)
			c = "PUB";
		else if(index==16)
			c = "RAJ";
		else if(index==17)
			c = "TNU";
		else if(index==18)
			c = "UPE";
		else if(index==19)
			c = "UPW";
		else if(index==20)
			c = "WBL";
		else if(index==21)
			c = "HPD";
		else if(index==22)
			c = "KER";
		else if(index==23)
			c = "PAN";
		else
			c = "OTH";
		return c;

	}
	public float score(String Duration)
	{
		float rat=0;
		if(Integer.parseInt(Duration) < 30 )
			rat=-1;
        else if(Integer.parseInt(Duration) > 30 && Integer.parseInt(Duration) < 60 )
        	rat=1;
        else if(Integer.parseInt(Duration) > 60 && Integer.parseInt(Duration) < 90 )
        	rat=2;
        else if(Integer.parseInt(Duration) > 90 && Integer.parseInt(Duration) < 120 )
        	rat=3;
        else if(Integer.parseInt(Duration) > 120 && Integer.parseInt(Duration) < 150 )
        	rat=4;
        else if(Integer.parseInt(Duration) > 150)
        	rat=5;
		return rat;
	}
	public void dbImport(String query)
	{
		try{
				ResultSet Rss = stmt_source.executeQuery(query);
				while(Rss.next())
				{
					String MSISDN = Rss.getString("MSISDN");
					String MODE = Rss.getString("MODE");
					String CHARGINGAMOUNT = Rss.getString("CHARGING AMOUNT");
					String CIRCLE = Rss.getString("CIRCLE");
					String CHARGINGREASON = Rss.getString("CHARGING REASON");
					String DATE = Rss.getString("DATE");
					String TYPE = Rss.getString("TYPE");
					String USER_TYPE = Rss.getString("USER_TYPE");
					String SERVICE = Rss.getString("SERVICE");
					String DATE1 = Rss.getString("DATE1");
					String TIME1 = Rss.getString("TIME1");
					System.out.println(MSISDN);
					stmt_destination.executeUpdate("insert into tatadocomoactivation_combo values('"+MSISDN+"','"+MODE+"','"+CHARGINGAMOUNT+"','"+CIRCLE+"','"+CHARGINGREASON+"','"+DATE+"','"+TYPE+"','"+USER_TYPE+"','"+SERVICE+"','"+DATE1+"','"+TIME1+"')");
				}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String arg[])
	{
		if(arg.length>0)
			day = Integer.parseInt(arg[0]);
		else
			day = 1;
		HourlyAlert c = new HourlyAlert();
		c.start();

	}

}
