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
package de.thischwa.c5c.image;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.util.ThumbnailatorUtils;

import org.imgscalr.Scalr;

public class _TestImageManipulation {

	public static void main(String[] args) throws Exception {
		thumb();
		
		System.out.println("--------------");
		
		sclr();
	}
	
	static void thumb() throws Exception {
		System.out.println("supported formats: ");
		for(String format: ThumbnailatorUtils.getSupportedOutputFormats())
			System.out.print(format + ",");
		System.out.println();
		
		File newPic = new File("/tmp", "pic_resized.png");
		if(newPic.exists())
			newPic.delete();
		OutputStream out = new BufferedOutputStream(new FileOutputStream(newPic));
		
		long ms = System.currentTimeMillis();
		Thumbnails.of(_TestImageManipulation.class.getResourceAsStream("pic.png"))
			.size(60, 30)
			.toOutputStream(out);
		out.flush();
		out.close();
		System.out.println("t-time: " + (System.currentTimeMillis()-ms));
	}

	static void sclr() throws Exception { 
		File newPic = new File("/tmp", "pic_resized_sclr.png");
		if(newPic.exists())
			newPic.delete();
		OutputStream out = new BufferedOutputStream(new FileOutputStream(newPic));

		long ms = System.currentTimeMillis();
		BufferedImage img = ImageIO.read(_TestImageManipulation.class.getResourceAsStream("pic.png"));
		BufferedImage newImg = Scalr.resize(img, 60, 30);
		ImageIO.write(newImg, "png", out);
		img.flush();
		newImg.flush();
		out.flush();
		out.close();
		System.out.println("s-time: " + (System.currentTimeMillis()-ms));
	}
}
