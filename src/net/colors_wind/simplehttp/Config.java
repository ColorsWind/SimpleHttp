package net.colors_wind.simplehttp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.yaml.snakeyaml.Yaml;

import lombok.SneakyThrows;
import lombok.ToString;
import lombok.val;

@ToString
public class Config {
	
	public final Map<String, String> accessType = new ConcurrentHashMap<>();
	public final File webRoot;
	public final long maxFileSize;
	public final int maxWidthImg;
	public final int maxHeightImg;
	public final int threadPoolSize;
	public final int listenPort;
	public final boolean debug;
	
	@SuppressWarnings("unchecked")
	@SneakyThrows
	public Config() {
		val file = new File("./config.yml");
		if (!file.exists()) {
			saveResouce("config.yml", file);
		}
		val yaml = new Yaml();
		val map = (Map<String,Object>)yaml.load(new FileInputStream(file));
		accessType.putAll((Map<String,String>) map.get("AccessType"));
		webRoot = new File((String) map.get("WebRoot"));
		this.maxFileSize = (Integer)map.get("MaxFileSize");
		val imageProcess = (Map<String, Integer>)map.get("ImageProcess");
		this.maxWidthImg = imageProcess.get("MaxWidth");
		this.maxHeightImg = imageProcess.get("MaxHeight");
		this.threadPoolSize = (Integer) map.get("ThreadPoolSize");
		this.listenPort = (Integer) map.get("ListenPort");
		this.debug = (Boolean) map.get("Debug");
	}
	
	
	@SneakyThrows
	public InputStream getResource(String name) {
		URL url = Config.class.getClassLoader().getResource(name);
		if (url == null) {
			return null;
		}
		URLConnection connection = url.openConnection();
		connection.setUseCaches(false);
		return connection.getInputStream();
	}

	@SneakyThrows
	public boolean saveResouce(String name, File target) {
		if (!target.exists()) {
			BufferedInputStream in = new BufferedInputStream(getResource(name));
			FileOutputStream out = new FileOutputStream(target);
			byte[] buff = new byte[1024];
			int len;
			while ((len = in.read(buff)) != -1) {
				out.write(buff, 0, len);
			}
			out.flush();
			out.close();
			in.close();
		}
		return true;
	}

	public File getServerFile(URI uri) {
		return new File(webRoot, uri.getPath().substring(1));
	}
	
	public static String getExtensionName(File file) {
		String name = file.getName();
		int dot = name.lastIndexOf(".");
		if (dot < 0 || dot == name.length()) {
			return "";
		}
		return name.substring(dot + 1).toLowerCase();
	}
	


}
