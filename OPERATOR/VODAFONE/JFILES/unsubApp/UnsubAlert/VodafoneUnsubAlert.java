//package mypack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VodafoneUnsubAlert {
	public static String DATE_FORMAT = "yyyyMMdd";
	public static Connection con = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String date = TodayDate();
		String date1 = TodayMonth();
		HashMap map=new HashMap();
		//HashMap<String, String> map = new HashMap<String, String>();
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://10.43.248.137/test","ivr", "ivr");
			PreparedStatement st = con.prepareStatement("delete from test.vodaunsubcount");
			st.execute();
			System.out.println("After Delete old Records");
			//File file = new File("d:/" + date + ".txt");
			 File file = new File("/home/ivr/jfiles/unsubApp/unsublog/"+date+".txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] str = line.split("#");
				String str1[]=str[5].split(":");
				String key = str[2]+"#"+str1[1];
				if (map.containsKey(key)) {
					int i=Integer.parseInt(String.valueOf(map.get(key)));
					map.put(key, String.valueOf(++i));
				} else {
					map.put(key, "1");
				}
			}
			Date dt = new Date();
			Iterator itr = map.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry pairs = (Map.Entry) itr.next();
				System.out.println(pairs.getKey()+"="+pairs.getValue());
				st = con.prepareStatement("insert into test.vodaunsubcount (umode,response,counts) values(?,?,?)");
				String str[]=String.valueOf(pairs.getKey()).split("#");
				st.setString(1, str[0]);
				st.setString(2, str[1]);
				st.setString(3, String.valueOf(pairs.getValue()));
				st.executeUpdate();
			}
			con.close();
			System.out.println("After insert Records");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Exception in SQl Dataase" + e);
			try {
				if (!con.isClosed())
					con.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println("Exception in SQl Connection Closing" + e1);
			}
		}
	}

	public static String TodayDate() {
		Date todaysDate = new java.util.Date();
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		String formattedDate = formatter.format(todaysDate);
		return formattedDate;
	}

	public static String TodayMonth() {
		Date todaysDate = new java.util.Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		String formattedDate = formatter.format(todaysDate);
		return formattedDate;
	}
}
