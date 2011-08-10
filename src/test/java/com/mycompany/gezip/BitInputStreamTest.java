/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.gezip;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 *
 * @author mlankenau
 */
public class BitInputStreamTest extends TestCase {
	public void test1() throws IOException {
		ByteArrayInputStream ba = new ByteArrayInputStream(new byte[] {(byte) 0xaa});
		BitInputStream stream = new BitInputStream(ba);
		int i = stream.read(2);
		Assert.assertEquals(2, i);
		i = stream.read(4);
		Assert.assertEquals(10, i);
	}

	public void test2() throws IOException {
		ByteArrayInputStream ba = new ByteArrayInputStream(new byte[] {(byte) 0xaa});
		BitInputStream stream = new BitInputStream(ba);
		stream.read(6);
		assertTrue(stream.canRead(2));
		assertFalse(stream.canRead(3));		
	}
}
