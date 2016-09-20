package com.androidlib.net;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

/**
 * Created by zhangliang on 16/9/20.
 */

public class SerializableCookie implements Cookie, Externalizable {
	private static final int COMMENT     = 0x04;
	private static final int COMMENT_URL = 0x08;
	private static final int DOMAIN      = 0x20;
	private static final int EXPIRY_DATE = 0x10;
	private static final int NAME        = 0x01;
	private static final int PATH        = 0x40;
	private static final int PORTS       = 0x80;
	private static final int VALUE       = 0x02;
	private static final long serialVersionUID = -5879746417217519616L;

	private transient Cookie cookie;
	private transient int    nullMask    = 0;

	public SerializableCookie()
	{
		super();
	}

	public SerializableCookie(final Cookie cookie)
	{
		super();
		this.cookie = cookie;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		nullMask = in.readInt();
		String name = null;
		String value = null;
		String comment = null;
		Date expiryDate = null;
		String domain = null;
		String path = null;
		int[] ports = null;
		boolean isSecure = false;
		int version = 0;

		if ((nullMask & SerializableCookie.NAME) == 0)
		{
			name = in.readUTF();
		}

		if ((nullMask & SerializableCookie.VALUE) == 0)
		{
			value = in.readUTF();
		}

		if ((nullMask & SerializableCookie.COMMENT) == 0)
		{
			comment = in.readUTF();
		}

		if ((nullMask & SerializableCookie.COMMENT_URL) == 0)
		{
			in.readUTF();
		}

		if ((nullMask & SerializableCookie.EXPIRY_DATE) == 0)
		{
			expiryDate = new Date(in.readLong());
		}

		in.readBoolean();

		if ((nullMask & SerializableCookie.DOMAIN) == 0)
		{
			domain = in.readUTF();
		}

		if ((nullMask & SerializableCookie.PATH) == 0)
		{
			path = in.readUTF();
		}

		if ((nullMask & SerializableCookie.PORTS) == 0)
		{
			final int len = in.readInt();
			ports = new int[len];
			for (int i = 0; i < len; i++)
			{
				ports[i] = in.readInt();
			}
		}

		isSecure = in.readBoolean();
		version = in.readInt();
		final BasicClientCookie bc = new BasicClientCookie(name, value);
		bc.setComment(comment);
		bc.setDomain(domain);
		bc.setExpiryDate(expiryDate);
		bc.setPath(path);
		bc.setSecure(isSecure);
		bc.setVersion(version);
		cookie = bc;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		nullMask |= (getName() == null) ? SerializableCookie.NAME : 0;
		nullMask |= (getValue() == null) ? SerializableCookie.VALUE : 0;
		nullMask |= (getComment() == null) ? SerializableCookie.COMMENT : 0;
		nullMask |= (getCommentURL() == null) ? SerializableCookie.COMMENT_URL : 0;
		nullMask |= (getExpiryDate() == null) ? SerializableCookie.EXPIRY_DATE : 0;
		nullMask |= (getDomain() == null) ? SerializableCookie.DOMAIN : 0;
		nullMask |= (getPath() == null) ? SerializableCookie.PATH : 0;
		nullMask |= (getPorts() == null) ? SerializableCookie.PORTS : 0;

		out.writeInt(nullMask);

		if ((nullMask & SerializableCookie.NAME) == 0)
		{
			out.writeUTF(getName());
		}

		if ((nullMask & SerializableCookie.VALUE) == 0)
		{
			out.writeUTF(getValue());
		}

		if ((nullMask & SerializableCookie.COMMENT) == 0)
		{
			out.writeUTF(getComment());
		}

		if ((nullMask & SerializableCookie.COMMENT_URL) == 0)
		{
			out.writeUTF(getCommentURL());
		}

		if ((nullMask & SerializableCookie.EXPIRY_DATE) == 0)
		{
			out.writeLong(getExpiryDate().getTime());
		}

		out.writeBoolean(isPersistent());

		if ((nullMask & SerializableCookie.DOMAIN) == 0)
		{
			out.writeUTF(getDomain());
		}

		if ((nullMask & SerializableCookie.PATH) == 0)
		{
			out.writeUTF(getPath());
		}

		if ((nullMask & SerializableCookie.PORTS) == 0)
		{
			if (getPorts() != null)
			{
				out.writeInt(getPorts().length);
				for (final int p : getPorts())
				{
					out.writeInt(p);
				}
			}
		}

		out.writeBoolean(isSecure());
		out.writeInt(getVersion());
	}

	@Override
	public String getName() {
		return cookie.getName();
	}

	@Override
	public String getValue() {
		return cookie.getValue();
	}

	@Override
	public String getComment() {
		return this.cookie.getComment();
	}

	@Override
	public String getCommentURL() {
		return cookie.getCommentURL();
	}

	@Override
	public Date getExpiryDate() {
		return cookie.getExpiryDate();
	}

	@Override
	public boolean isPersistent() {
		return cookie.isPersistent();
	}

	@Override
	public String getDomain() {
		return cookie.getDomain();
	}

	@Override
	public String getPath() {
		return cookie.getPath();
	}

	@Override
	public int[] getPorts() {
		return cookie.getPorts();
	}

	@Override
	public boolean isSecure() {
		return cookie.isSecure();
	}

	@Override
	public int getVersion() {
		return cookie.getVersion();
	}

	@Override
	public boolean isExpired(Date date) {
		return cookie.isExpired(date);
	}

	@Override
	public String toString() {
		if (cookie == null)
		{
			return "null";
		}
		else
		{
			return cookie.toString();
		}
	}
}
