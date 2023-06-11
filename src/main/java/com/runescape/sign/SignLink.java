package com.runescape.sign;

import java.applet.Applet;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import com.runescape.Client;
import com.grinder.Configuration;

public final class SignLink implements Runnable {

	public static final int clientversion = 317;
	public static final int CACHE_INDEX_LENGTH = 7; 
	public static final RandomAccessFile[] indices = new RandomAccessFile[CACHE_INDEX_LENGTH];
	public static int storeid = 32;
	public static RandomAccessFile cache_dat = null;
	public static Applet mainapp = null;
	public static String dns = null;
	public static String midi = null;
	public static int midiVolume;
	public static int fadeMidi;
	public static int wavevol;
	public static boolean reporterror = true;
	public static String errorName = "";
	public static Sequencer music = null;
	public static Sequence sequence = null;
	private static boolean active;
	private static int threadLiveId;
	private static InetAddress socketAddress;
	private static int socketRequest;
	private static Socket socket = null;
	private static int threadreqpri = 1;
	private static Runnable threadreq = null;
	private static String dnsreq = null;
	private static String urlRequest = null;
	private static DataInputStream urlStream = null;
	private static int savelen;
	private static String savereq = null;
	private static byte[] savebuf = null;
	public static String vendor;

	private SignLink() {
	}

	public static void startpriv(InetAddress inetaddress) {
		threadLiveId = (int) (Math.random() * 99999999D);
		if (active) {
			try {
				Thread.sleep(500L);
			} catch (Exception _ex) {
			}
			active = false;
		}
		socketRequest = 0;
		threadreq = null;
		dnsreq = null;
		savereq = null;
		urlRequest = null;
		vendor = "Unknown";
		try {
			vendor = System.getProperty("java.vendor");
		} catch (Exception e) {
			e.printStackTrace();
		}
		socketAddress = inetaddress;
		Thread thread = new Thread(new SignLink());
		thread.setDaemon(true);
		thread.start();
		while (!active) {
			try {
				Thread.sleep(50L);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String findcachedir() {
		final File cacheDirectory = new File(Configuration.CACHE_DIRECTORY);
		
		if (!cacheDirectory.exists()) {
			cacheDirectory.mkdirs();
		}
		return Configuration.CACHE_DIRECTORY;
	}
	
	public static long getCacheSize() {
		final File cacheDirectory = new File(Configuration.CACHE_DIRECTORY);
		
		if (!cacheDirectory.exists()) {
			cacheDirectory.mkdirs();
		}

		long totalSize = 0;
		
		for (File files : Objects.requireNonNull(cacheDirectory.listFiles())) {
			totalSize += files.length();
		}
		
		return totalSize;
	}

	public static String indexLocation(int cacheIndex, int index) {
		return SignLink.findcachedir() + "index" + cacheIndex + "/" + (index != -1 ? index + ".gz" : "");
	}

	public static void setError(String error) {
		errorName = error;
	}

	@Override
	public void run() {
		active = true;
		String directory = findcachedir();
		try {

			cache_dat = new RandomAccessFile(directory + "main_file_cache.dat", "rw");
			for (int index = 0; index < CACHE_INDEX_LENGTH; index++) {
				indices[index] = new RandomAccessFile(directory + "main_file_cache.idx" + index, "rw");
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		for (int i = threadLiveId; threadLiveId == i;) {
			if (socketRequest != 0) {
				try {
					socket = new Socket(socketAddress, socketRequest);
				} catch (Exception _ex) {
					socket = null;
				}
				socketRequest = 0;
			} else if (threadreq != null) {
				Thread thread = new Thread(threadreq);
				thread.setDaemon(true);
				thread.start();
				thread.setPriority(threadreqpri);
				threadreq = null;
			} else if (dnsreq != null) {
				try {
					dns = InetAddress.getByName(dnsreq).getHostName();
				} catch (Exception _ex) {
					dns = "unknown";
				}
				dnsreq = null;
			} else if (savereq != null) {
				if (savebuf != null)
					try {
						FileOutputStream fileoutputstream = new FileOutputStream(directory + savereq);
						fileoutputstream.write(savebuf, 0, savelen);
						fileoutputstream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				savereq = null;
			} else if (urlRequest != null) {
				try {
					System.out.println("urlstream");
					urlStream = new DataInputStream((new URL(mainapp.getCodeBase(), urlRequest)).openStream());
				} catch (Exception _ex) {
					urlStream = null;
				}
				urlRequest = null;
			}
			try {
				Thread.sleep(50L);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}


}
