import java.io.*;
import java.util.*;

public class CimdMsg
{

    private Parameter[] params = new Parameter[10];
    private int size;
    int nr;
    int op;
    public CimdMsg(int op, int nr)
    {
		this.op = op;
		this.nr = nr;
    }

    void addParameter(int par, byte[] content)
    {
		Parameter p = new Parameter(par, content);

		if (size == params.length)
		{
		    Parameter[] newBuf = new Parameter[size * 2];
		    System.arraycopy(params, 0, newBuf, 0, size);
		    params = newBuf;
		}

		params[size++] = p;
    }

	Parameter[] getParameters()
	{
		Parameter[] newBuf = new Parameter[size];
		System.arraycopy(params, 0, newBuf, 0, size);
		return newBuf;
    }

    int getParameterSize()
    {
		return size;
    }
	static int ASCII_ZERO = 48;
	static int	ASCII_NINE = 57;

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


    public String toString()
    {
		String str = "Cimd_SERVER RESPONSE [nr:" + gsmInt2int(nr, 3) +",op: " + gsmInt2int(op, 2) + ", params: [";
		for (int i = 0 ; i < size ; i++)
		{
			if (i != 0)	str += ", ";
		    str += params[i].toString();
		}
		return str + "]";
    }
 }
