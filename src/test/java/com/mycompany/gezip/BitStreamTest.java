/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.gezip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import junit.framework.TestCase;
import static junit.framework.Assert.*;

/**
 *
 * @author mlankenau
 */
public class BitStreamTest  extends TestCase {

	private void display(byte[] data) {
		for (byte b : data) {
			System.out.print(" " + b);
		}
		System.out.println("");
	}

		
	public void testBoth() throws IOException {

		ByteArrayOutputStream bosCompressed = new ByteArrayOutputStream();
		BitOutputStream bitOut = new BitOutputStream(bosCompressed);

		bitOut.write(1, 1);
		bitOut.write((int)'a', 8);
		bitOut.flush();

		byte[] bitBytes = bosCompressed.toByteArray();
		display(bitBytes);
		ByteArrayInputStream bisCompressed = new ByteArrayInputStream(bitBytes);
		BitInputStream bitIn = new BitInputStream(bisCompressed);
		int a = bitIn.read(1);
		assertEquals(1, a);
		int b = bitIn.read(8);
		assertEquals((int)'a', b);
	}


	public void testBothWithAscii7() throws IOException {
		final int bits = 7;
		String testString = "Hallo, dies ist ein ASCII 7 String";

		ByteArrayOutputStream bosCompressed = new ByteArrayOutputStream();
		BitOutputStream bitOut = new BitOutputStream(bosCompressed);

		for (char c : testString.toCharArray()) {
			bitOut.write(c, bits);
		}
		bitOut.flush();
		
		byte[] bitBytes = bosCompressed.toByteArray();
		display(bitBytes);
		ByteArrayInputStream bisCompressed = new ByteArrayInputStream(bitBytes);
		BitInputStream bitIn = new BitInputStream(bisCompressed);

		StringBuffer buffer = new StringBuffer();
		while (bitIn.canRead(bits)) {
			char c = (char) bitIn.read(bits);
			buffer.append(c);
		}
		
		assertEquals(testString, buffer.toString());
	}


	public void testBothBeginningOfBook() throws IOException {
		ByteArrayOutputStream bosCompressed = new ByteArrayOutputStream();
		BitOutputStream bitOut = new BitOutputStream(bosCompressed);

		bitOut.write(1, 1);
		bitOut.write((int)'a', 8);
		bitOut.flush();

		byte[] bitBytes = bosCompressed.toByteArray();
		display(bitBytes);
		ByteArrayInputStream bisCompressed = new ByteArrayInputStream(bitBytes);
		BitInputStream bitIn = new BitInputStream(bisCompressed);
		int a = bitIn.read(1);
		assertEquals(1, a);
		int b = bitIn.read(8);
		assertEquals((int)'a', b);
	}
}
