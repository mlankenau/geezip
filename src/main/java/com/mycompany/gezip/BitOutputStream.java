/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.gezip;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author mlankenau
 */
public class BitOutputStream {
	OutputStream outputStream;

	long buffer = 0;
	int nbuffer = 0;
	boolean finished = false;
	
	public BitOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void write(int bits, int length) throws IOException {
		//System.out.println(String.format("writing %x (%d)", bits, length));

		if (finished) 
			throw new RuntimeException("Stream is finished");
		
		buffer = buffer << length;
		buffer = buffer | bits;
		nbuffer += length;

		while (nbuffer >= 8) {
			long temp = buffer;
			temp = temp >> (nbuffer - 8);
			temp = temp & 0xff;
			outputStream.write((int) temp);
			nbuffer -= 8;
		}
	}

	void flush() throws IOException {
		buffer <<= (8 - nbuffer);
		outputStream.write((int) buffer);
		finished = true;
	}
}
