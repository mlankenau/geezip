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
import java.util.ArrayList;
import java.util.List;
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

		Word result = Engine.searchInFrame(frame, frame.length, source, 0, source.length);
		assertNull(result);
	}


	public void testSearchPositive() {
		int[] frame = new int[] {1, 2};
		int[] source = new int[] {1, 2, 3};

		Word result = Engine.searchInFrame(frame, frame.length, source, 0, source.length);
		assertNotNull(result);
		assertEquals(new Word(0, 2), result);
	}


	public void testSearchPositive2() {
		int[] frame = new int[] {1, 2, 0, 1, 2, 3};
		int[] source = new int[] {1, 2, 3};

		Word result = Engine.searchInFrame(frame, frame.length, source, 0, source.length);
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

		ByteArrayOutputStream bosCompressed = new ByteArrayOutputStream();

		Engine e = new Engine();
		e.compress(bookInputStream, bosCompressed);

		System.out.println("size of gutenberg compressed: " + bosCompressed.toByteArray().length);

		ByteArrayInputStream bisCompressed = new ByteArrayInputStream(bosCompressed.toByteArray());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		e.decompress(bisCompressed, bos);
		byte[] result = bos.toByteArray();
		

//		for(int i=0; i<result.length; i++) {
//			System.out.print("" + ((char) result[i]));
//		}
		
		//assertEquals(testdata.length, result.length);
//		for (int i=0; i< testdata.length; i++) {
//			assertEquals(testdata[i], result[i]);
//		}
	}


	

	private void display(byte[] data) {
		for (byte b : data) {
			System.out.print(" " + b);
		}
		System.out.println("");
	}
}
