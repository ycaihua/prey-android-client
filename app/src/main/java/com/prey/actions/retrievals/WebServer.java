package com.prey.actions.retrievals;

/**
 * Created by oso on 28-10-15.
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.prefs.PreferenceChangeEvent;

import org.apache.http.HttpException;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import android.content.Context;

import com.prey.PreyLogger;

public class WebServer {

    public static boolean RUNNING = false;
    public static int serverPort = 8080;

    private static final String ALL_PATTERN = "*";
    private static final String EXCEL_PATTERN = "/*.xls";
    private static final String HOME_PATTERN = "/home.html";

    private Context context = null;

    private BasicHttpProcessor httpproc = null;
    private BasicHttpContext httpContext = null;
    private HttpService httpService = null;
    private HttpRequestHandlerRegistry registry = null;

    public WebServer(Context context) {
        PreyLogger.i("create WebServer");
        this.setContext(context);

        httpproc = new BasicHttpProcessor();
        httpContext = new BasicHttpContext();

        httpproc.addInterceptor(new ResponseDate());
        httpproc.addInterceptor(new ResponseServer());
        httpproc.addInterceptor(new ResponseContent());
        httpproc.addInterceptor(new ResponseConnControl());

        httpService = new HttpService(httpproc,
                new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());

        registry = new HttpRequestHandlerRegistry();

        registry.register(HOME_PATTERN, new HomeCommandHandler(context));

        httpService.setHandlerResolver(registry);
    }

    private ServerSocket serverSocket;

    public void runServer() {
        try {
            PreyLogger.i("runServer");
            serverSocket = new ServerSocket(serverPort);

            serverSocket.setReuseAddress(true);

            while (RUNNING) {
                try {

                    final Socket socket = serverSocket.accept();

                    DefaultHttpServerConnection serverConnection = new DefaultHttpServerConnection();

                    serverConnection.bind(socket, new BasicHttpParams());

                    httpService.handleRequest(serverConnection, httpContext);

                    serverConnection.shutdown();
                } catch (Exception e) {

                    PreyLogger.e("error RUNNING:" + e.getMessage(), e);
                }
            }

            serverSocket.close();
        } catch (Exception e) {
            PreyLogger.e("error runServer:" + e.getMessage(), e);
        }

        RUNNING = false;
    }

    public synchronized void startServer() {
        RUNNING = true;
        runServer();
    }

    public synchronized void stopServer() {
        RUNNING = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                PreyLogger.e("error runServer:" + e.getMessage(), e);
            }
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
