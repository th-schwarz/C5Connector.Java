/*
 * C5Connector.Java - The Java backend for the filemanager of corefive.
 * It provides a simple object for creating an editor instance.
 * Copyright (C) Thilo Schwarz
 * 
 * == BEGIN LICENSE ==
 * 
 * Licensed under the terms of any of the following licenses at your
 * choice:
 * 
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 * 
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 * 
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * == END LICENSE ==
 */
package de.thischwa.c5c.image;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.util.ThumbnailatorUtils;

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
