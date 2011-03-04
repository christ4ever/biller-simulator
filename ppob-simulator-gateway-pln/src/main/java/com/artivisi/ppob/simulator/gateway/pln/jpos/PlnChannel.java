package com.artivisi.ppob.simulator.gateway.pln.jpos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;

import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;

public class PlnChannel  extends BaseChannel {
	private static final int MAX_MSG_LENGTH = 4096;
	private static final int MSG_TRAILER = 255;

	@Override
	protected void sendMessageTrailler(ISOMsg m, int len) throws IOException {
		serverOut.write(MSG_TRAILER);
	}

	@Override
	protected byte[] streamReceive() throws IOException {
		byte[] buf = new byte[MAX_MSG_LENGTH];
		int len = 0;
		for (len = 0; len < MAX_MSG_LENGTH; len++) {
			int data = serverIn.read();
			if (data == MSG_TRAILER) {
				break;
			}
			buf[len] = (byte) data;
		}
		if (len == MAX_MSG_LENGTH) {
			throw new IOException("packet too long");
		}

		byte[] d = new byte[len];
		System.arraycopy(buf, 0, d, 0, len);
		return d;
	}

	@Override
	public ISOMsg receive() throws IOException, ISOException {
		try {
			return super.receive();
		} catch (SocketTimeoutException err) {
			// tidak dapat response sampai timeout, gpp return null saja
			return null;
		}
	}

	// constructor default override
	public PlnChannel(ISOPackager p, ServerSocket serverSocket)
			throws IOException {
		super(p, serverSocket);
	}

	public PlnChannel(ISOPackager p) throws IOException {
		super(p);
	}

	public PlnChannel(String host, int port, ISOPackager p) {
		super(host, port, p);
	}

	public PlnChannel() {
	}
}