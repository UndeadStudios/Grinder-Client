package com.runescape.io.packets.outgoing;

import com.runescape.util.OSTextInput;

public class PacketBuilder {

	private final int opcode;
	private int position;
	private byte[] buffer;

	public PacketBuilder(int opcode) {
		this.opcode = opcode;
		this.position = 0;
		this.buffer = new byte[2000];
	}

	public PacketBuilder putByte(int i) {
		buffer[position++] = (byte) i;
		return this;
	}

	public void putSignedBytePlus(int value){
		buffer[position++] = (byte)(value + 128);
	}

	public void putTriByte(int var1) {
		this.buffer[position++] = (byte)(var1 >> 16);
		this.buffer[position++] = (byte)(var1 >> 8);
		this.buffer[position++] = (byte)var1;
	}

	public void putReverseTriByte(int var1) {
		this.buffer[position++] = (byte)var1;
		this.buffer[position++] = (byte)(var1 >> 8);
		this.buffer[position++] = (byte)(var1 >> 16);
	}

    public void putBytes(int i) {
        buffer[position - i - 1] = (byte) i;
    }

	public PacketBuilder putSignedByteMin(int j) {
		buffer[position++] = (byte) (128 - j);
		return this;
	}

	public PacketBuilder writeReverseDataA(int i, byte[] data, int j) {
		for (int k = (i + j) - 1; k >= i; k--)
			buffer[position++] = (byte) (data[k] + 128);
		return this;
	}

	public PacketBuilder putLong(long value) {
		try {
			buffer[position++] = (byte) (value >> 56);
			buffer[position++] = (byte) (value >> 48);
			buffer[position++] = (byte) (value >> 40);
			buffer[position++] = (byte) (value >> 32);
			buffer[position++] = (byte) (value >> 24);
			buffer[position++] = (byte) (value >> 16);
			buffer[position++] = (byte) (value >> 8);
			buffer[position++] = (byte) value;
		} catch (RuntimeException runtimeexception) {
			throw new RuntimeException();
		}
		return this;
	}

	public PacketBuilder putShort(int value) {
		buffer[position++] = (byte) (value >> 8);
		buffer[position++] = (byte) value;
		return this;
	}

	public PacketBuilder putUnsignedWordBigEndian(int value) {
		buffer[position++] = (byte) value;
		buffer[position++] = (byte) (value >> 8);
		return this;
	}

	public PacketBuilder putString(String s) {
		System.arraycopy(s.getBytes(), 0, buffer, position, s.length());
		position += s.length();
		buffer[position++] = 10;
		return this;
	}
	public PacketBuilder writeStringCp1252NullTerminated(String var1) {
		int var2 = var1.indexOf(0);
		if(var2 >= 0) {
			throw new IllegalArgumentException("");
		} else {
			this.position += OSTextInput.encodeStringCp1252(var1, 0, var1.length(), this.buffer, this.position);
			this.buffer[++this.position - 1] = 0;
		}
		return this;
	}
	public PacketBuilder putVariableSizeByte(int size) {
		buffer[position - size - 1] = (byte) size;
		return this;
	}

	public PacketBuilder putBytes(byte[] tmp, int len, int off) {
		for (int i = off; i < off + len; i++) {
			buffer[position++] = tmp[i];
		}
		return this;
	}

	public PacketBuilder putDWordBigEndian(int value) {
		buffer[position++] = (byte) (value >> 16);
		buffer[position++] = (byte) (value >> 8);
		buffer[position++] = (byte) value;
		return this;
	}

	public PacketBuilder putInt(int i) {
		buffer[position++] = (byte) (i >> 24);
		buffer[position++] = (byte) (i >> 16);
		buffer[position++] = (byte) (i >> 8);
		buffer[position++] = (byte) i;
		return this;
	}

	public PacketBuilder putUnsignedWordA(int value) {
		buffer[position++] = (byte) (value >> 8);
		buffer[position++] = (byte) (value + 128);
		return this;
	}

	public PacketBuilder putSignedWordBigEndian(int j) {
		buffer[position++] = (byte) (j + 128);
		buffer[position++] = (byte) (j >> 8);
		return this;
	}

	public PacketBuilder putByteC(int i) {
		buffer[position++] = (byte) -i;
		return this;
	}

	public void skip(int length){
		position+=length;
	}

	public int getPosition() {
		return position;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public int getOpcode() {
		return opcode;
	}

	public void writeSmartByteShort(int value) {
		if(value >= 0 && value < 128) {
			this.putByte(value);
		} else if(value >= 0 && value < 32768) {
			this.putShort(value + 32768);
		} else {
			throw new IllegalArgumentException();
		}
	}
}
