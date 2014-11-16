/*
 * C5Connector.Java - The Java backend for the filemanager of corefive.
 * It's a bridge between the filemanager and a storage backend and 
 * works like a transparent VFS or proxy.
 * Copyright (C) Thilo Schwarz
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package codes.thischwa.c5c.requestcycle;

import java.net.URL;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.PropertiesLoader;
import codes.thischwa.c5c.exception.C5CException;
import codes.thischwa.c5c.util.StringUtils;
import codes.thischwa.jii.IDimensionProvider;

/**
 * This container maintenance objects per request. It relies on {@link ThreadLocal}.<br/>
 * Provided Objects:<ul>
 * <li>The {@link Locale}: It is grabbed from the query string of the referrer. That's the location set by the filemanager.</li>
 * <li> The {@link Context}.</li>
 * </ul>
 * <i>Hint:</i> The implementation of the {@link IDimensionProvider} will be initialized 
 * and provided for each request because there isn't any thread-save one.
 */
public class RequestData {
	private static Logger logger = LoggerFactory.getLogger(RequestData.class);
	
	private static ThreadLocal<Context> context = new ThreadLocal<>();

	private static ThreadLocal<Locale> locale = new ThreadLocal<>();

	/**
	 * Initializes the current request cycle.
	 * 
	 * @param req
	 *            current user request instance
	 */
	public static void beginRequest(final HttpServletRequest req) {
		if (req == null)
			throw new NullPointerException("the request cannot be null");
		
		// init the context
		try {
			RequestData.context.set(new Context(req));
		} catch (C5CException e) {
			throw new RuntimeException("Couldn't initialize the context.", e);
		}		

		String referer = req.getHeader("referer");
		if(StringUtils.isNullOrEmptyOrBlank(referer))
			locale.set(req.getLocale());
		try {
			URL url = new URL(referer);
			Map<String, String> params = StringUtils.divideAndDecodeQueryString(url.getQuery());
			String langCode = params.get("langCode");
			if(StringUtils.isNullOrEmptyOrBlank(langCode)) {
				logger.warn("Couldn't analyse the locale from the referer to use, take the default one.");
				locale.set(PropertiesLoader.getDefaultLocale());
			} else {
				locale.set(new Locale(langCode.toLowerCase()));
			}
		} catch (Exception e) {
			logger.warn("Couldn't analyse the locale to use, take the default one.");
			locale.set(PropertiesLoader.getDefaultLocale());
		}
	}

	/**
	 * Gets the context.
	 * 
	 * @return the context
	 */
	public static Context getContext() {
		return context.get();
	}
		
	/**
	 * Gets the locale.
	 *
	 * @return the locale
	 */
	public static Locale getLocale() {
		return locale.get();
	}

	/**
	 * Terminates the current request cycle. <br />
	 * <strong>Important: To prevent memory leaks, make sure that this method is called at the end of the current request cycle!</strong>
	 */
	public static void endRequest() {
		context.remove();
		locale.remove();
	}
}
