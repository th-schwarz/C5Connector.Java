package codes.thischwa.c5c;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;

import codes.thischwa.c5c.impl.ExifRemoverImpl;

public class _ExifRemover {
	private static String tmpFile = "/tmp/test.jpg";

	public static void main(String[] args) throws Exception {

		_ExifRemover er = new _ExifRemover();
		//er.testSimple();
		er.testRaw();
	}
	
	public _ExifRemover() throws Exception{
		Files.deleteIfExists(Paths.get(tmpFile));
	}

	void testExifRemover() throws Exception {
		ExifRemoverImpl er = new ExifRemoverImpl();
		er.removeExif(_ExifRemover.class.getResourceAsStream("/exif.jpg"), new FileOutputStream("/tmp/test.jpg"), "jpg");		
	}
	
	void testRaw() throws Exception {				
		new ExifRewriter().removeExifMetadata(_ExifRemover.class.getResourceAsStream("/exif.jpg"), new FileOutputStream("/tmp/test.jpg"));
	}
	
	void testSimple() throws Exception {
		BufferedImage image = ImageIO.read(_ExifRemover.class.getResourceAsStream("/exif.jpg"));
		ImageIO.write(image, "jpg", new File(tmpFile));
	}

}
