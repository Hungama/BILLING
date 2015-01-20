import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
public class CIMD_Functions implements CIMD_Constants
{
	int ch;
	public boolean DEBUG;
	BufferedInputStream in=null;
	BufferedOutputStream out=null;

	private int scan() throws IOException
    {
        int c = ch;
        ch = in.read();
        return c;
    }

	CimdMsg readMessage(BufferedInputStream bis) throws IOException
	{
		this.in = bis;
		if (in == null)
		{
		    throw new IOException("Not connected");
		}
		try
		{
		    ch = 0;
		    loop: while (ch != -1)
		    {
				if (scan() != START_OF_TEXT)
				{
			    	continue loop;
				}
				int op = scan() << 8;
				op |= scan();
				if (ch != COLON)
				{
			    	continue loop;
				}
				scan();
				int pn = scan() << 16;
				pn |= scan() << 8;
				pn |= scan();
				CimdMsg msg = new CimdMsg(op, pn);
				if (ch != TAB)
				{
			    	continue loop;
				}
				scan();
				while (ch != END_OF_TEXT)
				{
			    	int par = scan() << 16;
			    	par |= scan() << 8;
			    	if (ch == END_OF_TEXT)
			    	{
						break;
			    	}
			    	par |= scan();
			    	if (ch != COLON)
			    	{
						continue loop;
			    	}
			    	scan();
			    	byte[] buf = new byte[MAX_DATA_LENGTH];
			    	int bufSize = 0;
			    	int val = 0;
			    	while ((val = scan()) != TAB)
			    	{
						if (bufSize == MAX_DATA_LENGTH)
						{
				    		continue loop;
						}
						buf[bufSize++] = (byte)val;
			    	}
			    	byte[] newBuf = new byte[bufSize];
			    	System.arraycopy(buf, 0, newBuf, 0, bufSize);
			    	msg.addParameter(par, newBuf);
				}
				return msg;
		    } throw new IOException("End of stream reached");
		}
		catch (IOException e)
		{
		    throw e;
		}
    }

    void writeMessage(CimdMsg mesg, BufferedOutputStream bus) throws IOException
    {
		out = bus;
        if(out == null) throw new IOException("Not connected");
        try
        {
			writeMsg(mesg, out);
        }
        catch(IOException e)
        {
            throw e;
        }
    }

	public void writeMsg(CimdMsg mesg, BufferedOutputStream out) throws IOException
	{

            out.write(START_OF_TEXT);
            out.write(mesg.op >> 8);
            out.write(mesg.op);
            out.write(COLON);
            out.write(mesg.nr >> 16);
            out.write(mesg.nr >> 8);
            out.write(mesg.nr);
            out.write(TAB);
            Parameter params[] = mesg.getParameters();
            for(int i = 0; i < params.length; i++)
            {
                out.write(params[i].id >> 16);
                out.write(params[i].id >> 8);
                out.write(params[i].id);
                out.write(COLON);
                out.write(params[i].data);
                out.write(TAB);
            }
            out.write(END_OF_TEXT);
            System.out.println(END_OF_TEXT);
            out.flush();
	}

	public static final int int2gsmInt(int val, int size)
    {
        if(size <= 0) throw new IllegalArgumentException("size > 0");
        int v = 0;
        int def = 1;
        int shift = 0;
        for(; size > 0; size--)
        {
            v |= int2gsmInt((val / def) % 10) << shift;
            def *= 10;
            shift += 8;
        }
        return v;
    }
    public static int int2gsmInt(int val)
    {
        if(val < 0 || val > 9) throw new IllegalArgumentException("val between 0 and 9: " + val);
        else return val + 48;
    }

	public static final int gsmInt2int(int val)
	{
		if (val < ASCII_ZERO || val > ASCII_NINE)
		{
		    throw new IllegalArgumentException("val between 0x30 and 0x39: " + val);
		}
		return val - ASCII_ZERO;
    }

	public static final int gsmInt2int(int val, int size)
	{
		if (size <= 0)
		{
		    throw new IllegalArgumentException("size > 0");
		}
		int v     = 0;
		int mult  = 1;
		int shift = 0;
		for ( ; size > 0 ; size--)
		{
		    v += (gsmInt2int((val >> shift) & 0xFF)) * mult;
		    mult *= 10;
		    shift += 8;
		}
		return v;
    }
    public static int PrintAndLog(String Buff)
	{
		try
		{
			Calendar today 	= Calendar.getInstance();
			String ALERTS	= "CIMD";
			String strlogfile = ""+formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
			String strdate = formatN(""+today.get(Calendar.YEAR),4) + formatN(""+(today.get(Calendar.MONTH)+1),2) + formatN(""+today.get(Calendar.DATE),2);
			String strtime = formatN(""+today.get(Calendar.HOUR_OF_DAY),2)+formatN(""+today.get(Calendar.MINUTE),2)+formatN(""+today.get(Calendar.SECOND),2);
			Buff = "["+ALERTS+" "+strdate+" "+strtime +"]--> "+Buff;
			System.out.println(Buff);
			FileOutputStream outfile = new FileOutputStream("/home/ivr/javalogs/CIMD/VH1/" +ALERTS+"_"+ strlogfile + ".log",true);
			PrintStream outprint = new PrintStream(outfile);
			outprint.println(Buff);
			outprint.close();
			outfile.close();
			return 1;
		}
		catch(Exception e)
		{
			System.out.println(e);
			return 0;
		}
	}
	public static String formatN(String str, int x)
	{
		int len;
		String ret_str="";
		len = str.length();
		if (len >= x)
			ret_str = str;
		else
		{
			for(int i=0; i<x-len; i++)
				ret_str = ret_str + "0";
			ret_str = ret_str + str;
		}
		return ret_str;
	}
}
