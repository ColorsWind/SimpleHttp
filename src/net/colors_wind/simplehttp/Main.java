package net.colors_wind.simplehttp;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

@SuppressWarnings("restriction")
public class Main {
	public static final Config CONFIG = new Config();
    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(CONFIG.listenPort), 0);
        httpServer.createContext("/", new SimpleHttpHandler());
        httpServer.setExecutor(Executors.newFixedThreadPool(CONFIG.threadPoolSize));
        httpServer.start();
        System.out.println("SimpleHttp已启用.");
    }
}