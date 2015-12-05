package main.java.com.ionsystems.infinigen.utility;

import java.lang.reflect.Array;

public class ArrayOperations {

//	public Foo[] concat(Foo[] a, Foo[] b) {
//		   int aLen = a.length;
//		   int bLen = b.length;
//		   Foo[] c= new Foo[aLen+bLen];
//		   System.arraycopy(a, 0, c, 0, aLen);
//		   System.arraycopy(b, 0, c, aLen, bLen);
//		   return c;
//		}
	
	public static <T> T[] concatenate (T[] a, T[] b) {
	    int aLen = a.length;
	    int bLen = b.length;

	    @SuppressWarnings("unchecked")
	    T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
	    System.arraycopy(a, 0, c, 0, aLen);
	    System.arraycopy(b, 0, c, aLen, bLen);

	    return c;
	}

}
