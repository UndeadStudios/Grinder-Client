package com.runescape.util;

import com.runescape.io.Buffer;
import com.runescape.io.packets.outgoing.PacketBuilder;

public final class TextInput {

	public static String processText(String s) {
		stream.index = 0;
		writeToStream(s, stream);
		int offset = stream.index;
		stream.index = 0;
		String text = readFromStream(offset, stream);
		return text;
	}

	public static String readFromStream(int length, Buffer stream) {
		int pointer = 0;
		int l = -1;
		for (int c = 0; c < length; c++) {
			int encodedLetter = stream.readUnsignedByte();
			int letter = encodedLetter >> 4 & 0xf;
			if (l == -1) {
				if (letter < 13)
					characterList[pointer++] = validChars[letter];
				else
					l = letter;
			} else {
				characterList[pointer++] = validChars[((l << 4) + letter) - 195];
				l = -1;
			}
			letter = encodedLetter & 0xf;
			if (l == -1) {
				if (letter < 13)
					characterList[pointer++] = validChars[letter];
				else
					l = letter;
			} else {
				characterList[pointer++] = validChars[((l << 4) + letter) - 195];
				l = -1;
			}
		}

		boolean endOfSentence = true;
		for (int c = 0; c < pointer; c++) {
			char character = characterList[c];
			if (endOfSentence && character >= 'a' && character <= 'z') {
				characterList[c] += '\uFFE0';
				endOfSentence = false;
			}
			if (character == '.' || character == '!' || character == '?')
				endOfSentence = true;
		}
		return new String(characterList, 0, pointer);
	}

	public static void writeToStream(String text, Buffer stream) {
		if (text.length() > 80)
			text = text.substring(0, 80);
		text = text.toLowerCase();
		int i = -1;
		for (int c = 0; c < text.length(); c++) {
			char character = text.charAt(c);
			int characterCode = 0;
			for (int l = 0; l < validChars.length; l++) {
				if (character != validChars[l])
					continue;
				characterCode = l;
				break;
			}

			if (characterCode > 12)
				characterCode += 195;
			if (i == -1) {
				if (characterCode < 13)
					i = characterCode;
				else
					stream.writeByte(characterCode);
			} else if (characterCode < 13) {
				stream.writeByte((i << 4) + characterCode);
				i = -1;
			} else {
				stream.writeByte((i << 4) + (characterCode >> 4));
				i = characterCode & 0xf;
			}
		}
		if (i != -1)
			stream.writeByte(i << 4);
	}
	public static void writeToStream(String text, PacketBuilder stream) {
		if (text.length() > 80)
			text = text.substring(0, 80);
		text = text.toLowerCase();
		int i = -1;
		for (int c = 0; c < text.length(); c++) {
			char character = text.charAt(c);
			int characterCode = 0;
			for (int l = 0; l < validChars.length; l++) {
				if (character != validChars[l])
					continue;
				characterCode = l;
				break;
			}

			if (characterCode > 12)
				characterCode += 195;
			if (i == -1) {
				if (characterCode < 13)
					i = characterCode;
				else
					stream.putByte(characterCode);
			} else if (characterCode < 13) {
				stream.putByte((i << 4) + characterCode);
				i = -1;
			} else {
				stream.putByte((i << 4) + (characterCode >> 4));
				i = characterCode & 0xf;
			}
		}
		if (i != -1)
			stream.putByte(i << 4);
	}

	private static final char[] characterList = new char[100];
	private static final Buffer stream = new Buffer(new byte[100]);
	private static final char[] validChars = { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm',
			'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=',
			'\243', '$', '%', '"', '[', ']' };

}
