package com.runescape.util;

import com.grinder.client.util.Log;
import com.runescape.io.Buffer;
import com.runescape.io.packets.outgoing.PacketBuilder;
import com.runescape.sign.SignLink;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 13/02/2020
 */
public class OSTextInput {

    public static Huffman huffman;
    public static final char[] cp1252AsciiExtension = new char[]{'€', '\u0000', '‚', 'ƒ', '„', '…', '†', '‡', 'ˆ', '‰', 'Š', '‹', 'Œ', '\u0000', 'Ž', '\u0000', '\u0000', '‘', '’', '“', '”', '•', '–', '—', '˜', '™', 'š', '›', 'œ', '\u0000', 'ž', 'Ÿ'};

    static {
        final byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(SignLink.findcachedir(),"huffman.dat"));
            huffman = new Huffman(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            Log.error("Could not load huffman file", e);
        }
    }

    public static int writeToStream(Buffer buffer, String string) {
        int onset = buffer.index;
        byte[] bytes = convertStringToByteArray(string);
        buffer.writeSmartByteShort(bytes.length);
        buffer.index += huffman.write(bytes, 0, bytes.length, buffer.array, buffer.index);
        return buffer.index - onset;
    }
    public static int writeToStream(PacketBuilder buffer, String string) {
        int onset = buffer.getPosition();
        byte[] bytes = convertStringToByteArray(string);
        buffer.writeSmartByteShort(bytes.length);
        buffer.skip(huffman.write(bytes, 0, bytes.length, buffer.getBuffer(), buffer.getPosition()));
        return buffer.getPosition() - onset;
    }
    public static String applyHuffmanEncoding(Buffer buffer) {

        String string;

        try {
            int length = buffer.readUnsignedShortSmart();
            if(length > 32767) {
                length = 32767;
            }

            byte[] bytes = new byte[length];
            buffer.index += huffman.read(buffer.array, buffer.index, bytes, 0, length);
            string = decodeStringCp1252(bytes, 0, length);
        } catch (Exception e) {
            string = "Cabbage";
            Log.error("Could not apply huffman encoding to buffer", e);
        }

        return string;
    }
    public static String decodeStringCp1252(byte[] bytes, int offset, int length) {
        char[] chars = new char[length];
        int charCount = 0;

        for(int i = 0; i < length; ++i) {
            int charCode = bytes[i + offset] & 255;
            if(charCode != 0) {
                if(charCode >= 128 && charCode < 160) {
                    char asciiChar = cp1252AsciiExtension[charCode - 128];
                    if(asciiChar == 0)
                        asciiChar = '?';
                    charCode = asciiChar;
                }
                chars[charCount++] = (char)charCode;
            }
        }

        return new String(chars, 0, charCount);
    }
    public static byte[] convertStringToByteArray(CharSequence var0) {
        int var1 = var0.length();
        byte[] var2 = new byte[var1];

        for(int var3 = 0; var3 < var1; ++var3) {
            char var4 = var0.charAt(var3);
            if(var4 > 0 && var4 < 128 || var4 >= 160 && var4 <= 255) {
                var2[var3] = (byte)var4;
            } else if(var4 == 8364) {
                var2[var3] = -128;
            } else if(var4 == 8218) {
                var2[var3] = -126;
            } else if(var4 == 402) {
                var2[var3] = -125;
            } else if(var4 == 8222) {
                var2[var3] = -124;
            } else if(var4 == 8230) {
                var2[var3] = -123;
            } else if(var4 == 8224) {
                var2[var3] = -122;
            } else if(var4 == 8225) {
                var2[var3] = -121;
            } else if(var4 == 710) {
                var2[var3] = -120;
            } else if(var4 == 8240) {
                var2[var3] = -119;
            } else if(var4 == 352) {
                var2[var3] = -118;
            } else if(var4 == 8249) {
                var2[var3] = -117;
            } else if(var4 == 338) {
                var2[var3] = -116;
            } else if(var4 == 381) {
                var2[var3] = -114;
            } else if(var4 == 8216) {
                var2[var3] = -111;
            } else if(var4 == 8217) {
                var2[var3] = -110;
            } else if(var4 == 8220) {
                var2[var3] = -109;
            } else if(var4 == 8221) {
                var2[var3] = -108;
            } else if(var4 == 8226) {
                var2[var3] = -107;
            } else if(var4 == 8211) {
                var2[var3] = -106;
            } else if(var4 == 8212) {
                var2[var3] = -105;
            } else if(var4 == 732) {
                var2[var3] = -104;
            } else if(var4 == 8482) {
                var2[var3] = -103;
            } else if(var4 == 353) {
                var2[var3] = -102;
            } else if(var4 == 8250) {
                var2[var3] = -101;
            } else if(var4 == 339) {
                var2[var3] = -100;
            } else if(var4 == 382) {
                var2[var3] = -98;
            } else if(var4 == 376) {
                var2[var3] = -97;
            } else {
                var2[var3] = 63;
            }
        }

        return var2;
    }
    public static int encodeStringCp1252(CharSequence var0, int var1, int var2, byte[] var3, int var4) {
        int var5 = var2 - var1;

        for(int var6 = 0; var6 < var5; ++var6) {
            char var7 = var0.charAt(var6 + var1);
            if(var7 > 0 && var7 < 128 || var7 >= 160 && var7 <= 255) {
                var3[var6 + var4] = (byte)var7;
            } else if(var7 == 8364) {
                var3[var6 + var4] = -128;
            } else if(var7 == 8218) {
                var3[var6 + var4] = -126;
            } else if(var7 == 402) {
                var3[var6 + var4] = -125;
            } else if(var7 == 8222) {
                var3[var6 + var4] = -124;
            } else if(var7 == 8230) {
                var3[var6 + var4] = -123;
            } else if(var7 == 8224) {
                var3[var6 + var4] = -122;
            } else if(var7 == 8225) {
                var3[var6 + var4] = -121;
            } else if(var7 == 710) {
                var3[var6 + var4] = -120;
            } else if(var7 == 8240) {
                var3[var6 + var4] = -119;
            } else if(var7 == 352) {
                var3[var6 + var4] = -118;
            } else if(var7 == 8249) {
                var3[var6 + var4] = -117;
            } else if(var7 == 338) {
                var3[var6 + var4] = -116;
            } else if(var7 == 381) {
                var3[var6 + var4] = -114;
            } else if(var7 == 8216) {
                var3[var6 + var4] = -111;
            } else if(var7 == 8217) {
                var3[var6 + var4] = -110;
            } else if(var7 == 8220) {
                var3[var6 + var4] = -109;
            } else if(var7 == 8221) {
                var3[var6 + var4] = -108;
            } else if(var7 == 8226) {
                var3[var6 + var4] = -107;
            } else if(var7 == 8211) {
                var3[var6 + var4] = -106;
            } else if(var7 == 8212) {
                var3[var6 + var4] = -105;
            } else if(var7 == 732) {
                var3[var6 + var4] = -104;
            } else if(var7 == 8482) {
                var3[var6 + var4] = -103;
            } else if(var7 == 353) {
                var3[var6 + var4] = -102;
            } else if(var7 == 8250) {
                var3[var6 + var4] = -101;
            } else if(var7 == 339) {
                var3[var6 + var4] = -100;
            } else if(var7 == 382) {
                var3[var6 + var4] = -98;
            } else if(var7 == 376) {
                var3[var6 + var4] = -97;
            } else {
                var3[var6 + var4] = 63;
            }
        }

        return var5;
    }
    public static String capitalize(String var0) {
        int var1 = var0.length();
        char[] var2 = new char[var1];
        byte var3 = 2;

        for(int var4 = 0; var4 < var1; ++var4) {
            char var5 = var0.charAt(var4);
            if(var3 == 0) {
                var5 = Character.toLowerCase(var5);
            } else if(var3 == 2 || Character.isUpperCase(var5)) {
                char var6;
                if(var5 != 181 && var5 != 402) {
                    var6 = Character.toTitleCase(var5);
                } else {
                    var6 = var5;
                }

                var5 = var6;
            }

            if(Character.isLetter(var5)) {
                var3 = 0;
            } else if(var5 != '.' && var5 != '?' && var5 != '!') {
                if(Character.isSpaceChar(var5)) {
                    if(var3 != 2) {
                        var3 = 1;
                    }
                } else {
                    var3 = 1;
                }
            } else {
                var3 = 2;
            }

            var2[var4] = var5;
        }

        return new String(var2);
    }

    public static String escapeBrackets(String var0) {
        int var1 = var0.length();
        int var2 = 0;

        for(int var3 = 0; var3 < var1; ++var3) {
            char var4 = var0.charAt(var3);
            if(var4 == '<' || var4 == '>') {
                var2 += 3;
            }
        }

        StringBuilder var6 = new StringBuilder(var1 + var2);

        for(int var7 = 0; var7 < var1; ++var7) {
            char var5 = var0.charAt(var7);
            if(var5 == '<') {
                var6.append("<lt>");
            } else if(var5 == '>') {
                var6.append("<gt>");
            } else {
                var6.append(var5);
            }
        }

        return var6.toString();
    }

}
