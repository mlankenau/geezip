/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.gezip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import sun.audio.AudioDataStream;

/**
 *
 * @author mlankenau
 */
public class Engine {

	final int frameAdrSize = 19;
	final int frameSize = 1 << frameAdrSize;

	final int lengthSize = 4;
	final int maxLength = 1 << lengthSize;

	public static class Word {
		public boolean ref = false;
		public int b = 0;
		public int	start;
		public int length;

		public Word(int b) {
			ref = false;
			this.b = b;
		}

		public Word(int start, int length) {
			this.start = start;
			this.length = length;
			ref = true;
		}
		
		public boolean equals(Object o) {
			if  (!(o instanceof Word)) return false;
			Word other = (Word) o;
			return other.ref == ref && other.start == start && other.length == length && other.b == b;
		}

		public String toString() {
			if (ref)
				return String.format("(%d, %d)", start, length);
			else
				return String.format("(%x)", b);
		}


	}

	public Word searchInFrame(int[] frame, int framePos, int[] source, int sourcePos, int sourceSize) {
		Word bestWord = null;

		
		for (int i=0; i<framePos-1; i++) {
			int j = 0;
			while (j < maxLength-1 && i+j<framePos && sourcePos+j < sourceSize && frame[i+j] == source[sourcePos+j]) {
				j++;
			}
			if (j < 2) continue; // one matching byte doesn't help us
			if (bestWord != null && bestWord.length >= j) continue;  // we have a better one
			bestWord = new Word(i, j);
		}


		return bestWord;
	}


	protected void encode(Word word, BitOutputStream bos) throws IOException {
		if (!word.ref) {
			bos.write(0, 1);
			bos.write(word.b, 8);
		} else {
			bos.write(1, 1);
			bos.write(word.start, frameAdrSize);
			bos.write(word.length, lengthSize);
		}
	}

	public static int readStream(InputStream stream, int[] buffer, int length) throws IOException {
		int nread = 0;
		for (int i=0; i<length; i++) {
			int b = stream.read();
			if (b == -1) break;
			buffer[i] = b;
			nread++;
		}
		return nread;
	}

	public static void writeStream(OutputStream stream, int[] buffer, int length) throws IOException {
		for (int i=0; i<length; i++)
			stream.write(buffer[i]);
	}


	public void compress(InputStream inputStream, OutputStream outputStream) throws IOException {
		BitOutputStream bos = new BitOutputStream(outputStream);
		int[] frame = new int[frameSize];
		int[] source = new int[frameSize];
		int sourceSize = 0;
		int framePos = 0;
		int sourcePos = 0;

		while (true) {

			sourceSize = readStream(inputStream, source, frameSize);
			sourcePos = 0;
			framePos = 0;

			if (sourceSize == 0) break;

			while (sourcePos < sourceSize) {
				// search occourence in frame
				Word searchResult = searchInFrame(frame, framePos, source, sourcePos, sourceSize);
				if (searchResult != null) {
					encode(searchResult, bos);
					for (int i=0; i<searchResult.length; i++) {
						frame[framePos] = source[sourcePos];
						sourcePos++;
						framePos++;
					}
				} else {
					searchResult = new Word(source[sourcePos]);
					encode(searchResult, bos);
					frame[framePos] = source[sourcePos];
					framePos++;
					sourcePos++;
				}
			}			
		}
		bos.flush();
	}


	protected Word dencode(BitInputStream bis) throws IOException {
		if (!bis.canRead(1))  return null;
		int isRef = bis.read(1);
		if (isRef == 0) {
			if (!bis.canRead(8)) return null;
			int b = bis.read(8);
			return new Word((int)b);
		} else {
			if (!bis.canRead(frameAdrSize)) return null;
			int start = bis.read(frameAdrSize);
			if (!bis.canRead(lengthSize)) return null;
			int length = bis.read(lengthSize);
			return new Word(start, length);
		}		
	}


	public void decompress(InputStream inputStream, OutputStream outputStream) throws IOException {
		BitInputStream bitInputStream = new BitInputStream(inputStream);

		int[] frame = new int[frameSize];
		int framePos = 0;
		
		while (true) {
			Word word = dencode(bitInputStream);
			if (word == null) break;

			if (!word.ref) {
				//System.out.print(""+((char)word.b));
				frame[framePos] = word.b;
				framePos++;
			} else {
				for (int i=word.start; i<word.start+word.length; i++)
				{
					//System.out.print(""+((char)frame[i]));
					frame[framePos] = frame[i];
					framePos++;
				}
			}

			if (framePos >= frameSize) {

				writeStream(outputStream, frame, framePos);
				framePos = 0;
			}
		}

		writeStream(outputStream, frame, framePos);
	}

	public byte[] compress(InputStream inputStream) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		compress(inputStream, out);
		return out.toByteArray();
	}

	public byte[] compress(byte[] in) throws IOException {
		ByteArrayInputStream inStream = new ByteArrayInputStream(in);
		return compress(inStream);
	}


	public byte[] decompress(InputStream inputStream) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		decompress(inputStream, out);
		return out.toByteArray();
	}

	public byte[] decompress(byte[] in) throws IOException {
		ByteArrayInputStream inStream = new ByteArrayInputStream(in);
		return decompress(inStream);
	}

}
