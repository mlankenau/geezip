/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.gezip;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStream;
import junit.framework.TestCase;
import static org.mockito.Mockito.*;

/**
 *
 * @author mlankenau
 */
public class BitOutputStreamTest  extends TestCase {
	
	public void test1() throws IOException {
		OutputStream stream = mock(OutputStream.class);		
		BitOutputStream bos = new BitOutputStream(stream);
		bos.write(0x88, 8);
		verify(stream).write(0x88);
	}

	public void test2() throws IOException {
		OutputStream stream = mock(OutputStream.class);
		BitOutputStream bos = new BitOutputStream(stream);
		bos.write(0x08, 4);
		bos.write(0x08, 4);
		verify(stream).write(0x88);
	}

	public void test3() throws IOException {
		OutputStream stream = mock(OutputStream.class);
		BitOutputStream bos = new BitOutputStream(stream);
		bos.write(0x01ff, 9);		
		verify(stream).write(0xff);
		bos.write(0x00, 7);
		verify(stream).write(0x80);
	}
	
}
