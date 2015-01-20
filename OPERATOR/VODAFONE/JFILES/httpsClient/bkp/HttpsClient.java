import java.net.URL;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

public class HttpsClient
{
  public static void main(String[] args)
  throws Exception
  {
    String httpsURL = "https://10.43.248.137/submanager/Update?msisdn=917838102430&service_id=HNG_ENTRMNTPORTAL&class_vod=ENTRMNTPORTAL&txnid=3542312061&charging_mode=10DAYS&CIRCLE_ID=13";
    URL myurl = new URL(httpsURL);
    HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
    InputStream ins = con.getInputStream();
    InputStreamReader isr = new InputStreamReader(ins);
    BufferedReader in = new BufferedReader(isr);

    String inputLine;

    while ((inputLine = in.readLine()) != null)
    {
      System.out.println(inputLine);
    }

    in.close();
  }
}
