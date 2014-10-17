package in.mings.mingle.utils;


public class ByteArrayUtils {
	/**
	 * 16进制的byte数组
	 */
	public static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1',
			(byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
			(byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
			(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F' };

	/**
	 * 16进制基础字符
	 */
	public static final String HEX_STRING = "0123456789ABCDEF";
	
	/**
	 * 返回数组{@code raw}的16进制字符串形式
	 * @param raw
	 * @return
	 */
	public static String toHexStr(byte[] raw) {
		int len = raw.length;
		char[] hex = new char[len * 2];
		int i = 0, pos = 0;
		for (byte b : raw) {
			if (pos >= len) break;
			pos++;
			int v = b & 0xFF;
			hex[i++] = HEX_STRING.charAt(v >>> 4);
			hex[i++] = HEX_STRING.charAt(v & 0xF);
		}
		return new String(hex);
	}

	/**
	 * 返回16进制数组字符串
	 * @param raw	二进制数组
	 * @return
	 */
	public static String toHexString(byte[] raw) {
		int len = raw.length;
		byte[] hex = new byte[2 * len];
		int index = 0;
		int pos = 0;
		for (byte b : raw) {
			if (pos >= len)	break;
			pos++;
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}
		return new String(hex);
	}
	
	/**
	 * 转换成10进制
	 * @param raw
	 * @return
	 */
	public static int toInt(byte[] raw) {
		return (raw[0] << 24) | (raw[1] << 16) | (raw[2] << 8) | raw[3]; 
	}
}
