package net.colors_wind.simplehttp;

import static net.colors_wind.simplehttp.Main.CONFIG;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerIO {
	
	
	public static byte[] scaleImage(File file, int toWidth, int toHeight, String type) throws IOException {
		if (toWidth > CONFIG.maxWidthImg || toHeight > CONFIG.maxHeightImg) {
			return readFile(file);
		}
		BufferedImage src = ImageIO.read(file);
        Image image = src.getScaledInstance(toWidth, toHeight,
                Image.SCALE_DEFAULT);
        BufferedImage result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
        result.getGraphics().drawImage(image, 0, 0, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(result, type, out);
        return out.toByteArray();
	}
	
	public static byte[] scaleImage(File file, int toWidth, String type) throws IOException {  
		BufferedImage src = ImageIO.read(file);
		int width = src.getWidth();
		int height = src.getHeight();
		int toHeight = (toWidth * height) / width;
		if (toWidth > CONFIG.maxWidthImg || toHeight > CONFIG.maxHeightImg) {
			return readFile(file);
		}
        Image image = src.getScaledInstance(toWidth, toHeight,
                Image.SCALE_DEFAULT);
        BufferedImage result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
        result.getGraphics().drawImage(image, 0, 0, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(result, type, out);
        return out.toByteArray();
	}
	
	public static byte[] readFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
		byte[] b = new byte[1024];
		int n;
		while ((n = fis.read(b)) != -1) {
			bos.write(b, 0, n);
		}
		fis.close();
		byte[] data = bos.toByteArray();
		bos.close();
		return data;
	}

}
