/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.gezip;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author mlankenau
 */
public class BitInputStream {
	InputStream inputStream;

	long buffer = 0;
	int nbuffer = 0;

	public BitInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public static int rightBits(int length)
	{
		return (int)(0xffffffffl >> (32 - length));
	}

	public int read(int length) throws IOException {
		consume(length);

		long temp = buffer >> (nbuffer - length) ;
		temp &= rightBits(length);
		nbuffer -= length;

		//System.out.println(String.format("reading %x (%d)", temp, length));

		return (int)temp;
	}

	private void consume(int length) throws IOException {
		while (nbuffer < length) {
			int b = inputStream.read();
			if (b == -1) {
				break;
			}
			buffer = buffer << 8;
			buffer |= (b & 0xff);
			nbuffer += 8;
		}
	}

	public boolean canRead(int length) throws IOException {
		consume(length);
		return (nbuffer >= length);
	}
}
