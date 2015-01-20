
class Parameter {
    byte[] data;
    int id;


	Parameter(int id, byte[] data) {
		this.id   = id;
		this.data = data;
    }

	static int ASCII_ZERO = 48;
	static int	ASCII_NINE = 57;

	public static final int gsmInt2int(int val) {
		if (val < ASCII_ZERO || val > ASCII_NINE) {
		    throw new IllegalArgumentException("val between 0x30 and 0x39: " + val);
		}
		return val - ASCII_ZERO;
    }

	public static final int gsmInt2int(int val, int size) {
		if (size <= 0) {
		    throw new IllegalArgumentException("size > 0");
		}
		int v     = 0;
		int mult  = 1;
		int shift = 0;

		for ( ; size > 0 ; size--) {
		    v += (gsmInt2int((val >> shift) & 0xFF)) * mult;
		    mult *= 10;
		    shift += 8;
		}
		return v;
    }
    /**
     * convert the parameter into a string representation
     */
    public String toString() {
	return "[" + gsmInt2int(id, 3) + ", " + new String(data) + "]";
    }
}

