/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.gezip;

import com.mycompany.gezip.Engine.Word;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import static junit.framework.Assert.*;
import junit.framework.TestCase;

/**
 *
 * @author mlankenau
 */    
public class EngineTest extends TestCase {
	public void testSearchNegative() {
		int[] frame = new int[] {};
		int[] source = new int[] {1, 2, 3};

		Word result = new Engine().searchInFrame(frame, frame.length, source, 0, source.length);
		assertNull(result);
	}


	public void testSearchPositive() {
		int[] frame = new int[] {1, 2};
		int[] source = new int[] {1, 2, 3};

		Word result = new Engine().searchInFrame(frame, frame.length, source, 0, source.length);
		assertNotNull(result);
		assertEquals(new Word(0, 2), result);
	}


	public void testSearchPositive2() {
		int[] frame = new int[] {1, 2, 0, 1, 2, 3};
		int[] source = new int[] {1, 2, 3};

		Word result = new Engine().searchInFrame(frame, frame.length, source, 0, source.length);
		assertNotNull(result);
		assertEquals(new Word(3, 3), result);
	}

	public void testCycle1() throws IOException {
		byte[] testdata = new byte[] { 1, 2, 3, 1, 2, 1, 2, 3 };
		ByteArrayInputStream bis = new ByteArrayInputStream(testdata);
		ByteArrayOutputStream bosCompressed = new ByteArrayOutputStream();

		Engine e = new Engine();
		e.compress(bis, bosCompressed);		

		display(bosCompressed.toByteArray());
		ByteArrayInputStream bisCompressed = new ByteArrayInputStream(bosCompressed.toByteArray());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		e.decompress(bisCompressed, bos);
		byte[] result = bos.toByteArray();
		//display(result);

		assertEquals(testdata.length, result.length);
		for (int i=0; i< testdata.length; i++) {
			assertEquals(testdata[i], result[i]);
		}
	}

	public void testCycleBook() throws IOException {
		InputStream bookInputStream = getClass().getClassLoader().getResourceAsStream("project_gutenberg.txt");

		//ByteArrayOutputStream bosCompressed = new ByteArrayOutputStream();

		Engine e = new Engine();

		Date start = new Date();
		byte[] compressedData = e.compress(bookInputStream);
		long timeCompress = new Date().getTime() - start.getTime();

		start = new Date();
		byte[] decompressed = e.decompress(compressedData);
		long timeDecompress = new Date().getTime() - start.getTime();

		float ratio = ((float) compressedData.length) / ((float)decompressed.length);
		System.out.println("size of gutenberg compressed: " + compressedData.length);
		System.out.println("size of gutenberg decompressed: " +decompressed.length);
		System.out.println("ratio: " + (ratio*100.f));
		System.out.println("time compress: " +timeCompress);
		System.out.println("time decompress: " +timeDecompress);

		bookInputStream.close();
		bookInputStream = getClass().getClassLoader().getResourceAsStream("project_gutenberg.txt");
		for(int i=0; i<decompressed.length; i++) {
			byte[] bytes = new byte[1];
			bookInputStream.read(bytes, 0, 1);
			assertEquals("at position " + i, bytes[0], decompressed[i]);
		}
	}


	private void display(byte[] data) {
		for (byte b : data) {
			System.out.print(" " + b);
		}
		System.out.println("");
	}
}
