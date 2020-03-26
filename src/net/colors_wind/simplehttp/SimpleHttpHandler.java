package net.colors_wind.simplehttp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import static net.colors_wind.simplehttp.Main.CONFIG;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

@SuppressWarnings("restriction")
public class SimpleHttpHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		if (CONFIG.debug) {
			System.out.println(new StringBuilder(httpExchange.getRequestMethod()).append(": ")
					.append(httpExchange.getRequestURI()));
		}
		try {
			handleRequest(httpExchange);
		} catch (Exception e) {
			System.err.println(debug(httpExchange));
			e.printStackTrace();
			responeError(httpExchange, 500);
		}
	}


	public static String debug(HttpExchange httpExchange) {
		StringBuilder info = new StringBuilder();
		info.append("请求方法: ").append(httpExchange.getRequestMethod()).append("\n");
		info.append("请求URI: ").append(httpExchange.getRequestURI()).append("\n");
		info.append("Http头: ")
				.append(httpExchange.getResponseHeaders().entrySet().stream().map(
						entry -> new StringBuilder(entry.getKey()).append(": ").append(entry.getValue()).toString())
						.collect(Collectors.joining("\n")))
				.append("\n");
		info.append("远程IP: ").append(httpExchange.getRemoteAddress()).append("\n");
		return info.toString();
	}


	public void handleRequest(HttpExchange httpExchange) throws IOException {
		File file = CONFIG.getServerFile(httpExchange.getRequestURI());
		if (!file.exists()) { // file not found
			responeError(httpExchange, 404);
			return;
		}
		if (!file.isFile()) { // not file
			responeError(httpExchange, 402);
			return;
		}
		if (file.length() > CONFIG.maxFileSize) { // too large
			responeError(httpExchange, 402);
			return;
		}
		String fileType = Config.getExtensionName(file);
		String contentType = CONFIG.accessType.get(fileType);
		if (contentType == null) { // ��������ļ�����
			responeError(httpExchange, 402);
			return;
		}
		handleRequestOK(httpExchange, file, fileType, contentType);
	}

	private void handleRequestOK(HttpExchange httpExchange, File file, String fileType, String contentType)
			throws IOException {
		byte[] data;
		switch (fileType) {
		case "jpg":
		case "jpeg":
		case "git":
		case "png":
			data = requestImage(httpExchange, file, fileType);
			break;
		default:
			data = ServerIO.readFile(file);
			break;
		}
		httpExchange.sendResponseHeaders(200, data.length);
		OutputStream out = httpExchange.getResponseBody();
		out.write(data);
		out.flush();
		out.close();
	}

	private byte[] requestImage(HttpExchange httpExchange, File file, String fileType) throws IOException {
		String parameters = getRequestParameters(httpExchange);
		if (parameters.startsWith("imageMogr2/scrop/")) {
			String size = parameters.substring(17);
			StringTokenizer str = new StringTokenizer(size, "x");
			try {
				int toWidth = Integer.parseInt(str.nextToken());
				if (str.hasMoreTokens()) {
					int toHeight = Integer.parseInt(str.nextToken());
					return ServerIO.scaleImage(file, toWidth, toHeight, fileType);
				}
				return ServerIO.scaleImage(file, toWidth, fileType);
			} catch (NumberFormatException e) {
			}
		}
		return ServerIO.readFile(file);
	}



	private void responeError(HttpExchange httpExchange, int code) throws IOException {
		httpExchange.sendResponseHeaders(code, 0);
		httpExchange.getResponseHeaders().add("Content-Type:", "text/html;charset=utf-8");
		OutputStream out = httpExchange.getResponseBody();
		out.close();

	}

	private String getRequestParameters(HttpExchange httpExchange) throws IOException {
		String paramStr = "";
		if (httpExchange.getRequestMethod().equals("GET")) {
			paramStr = httpExchange.getRequestURI().getQuery();
		} else {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(httpExchange.getRequestBody(), "utf-8"));
			StringBuilder requestBodyContent = new StringBuilder();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				requestBodyContent.append(line);
			}
			paramStr = requestBodyContent.toString();
		}
		return paramStr;
	}

}