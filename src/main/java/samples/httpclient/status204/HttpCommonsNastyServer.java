package samples.httpclient.status204;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.http.ConnectionClosedException;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Embedded HTTP/1.1 file server based on a classic (blocking) I/O model. <br/>
 * This server implementation just rip off the body from the response if the status code is 204.
 * 
 * @see <a href="https://hc.apache.org/httpcomponents-core-ga/examples.html">HttpComponents Samples</a>
 */
public class HttpCommonsNastyServer implements NastyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpCommonsNastyServer.class);

    public void start() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        ExecutorCompletionService<Void> completionService = new ExecutorCompletionService<Void>(executorService);
        Future<Void> future = completionService.submit(new ServerCallable());
        while (future.isDone()) {
            // waiting
        }
        LOGGER.info("Server has been started");
    }

    static class StdErrorExceptionLogger implements ExceptionLogger {
        @Override
        public void log(final Exception ex) {
            if (ex instanceof SocketTimeoutException) {
                LOGGER.info("Connection timed out");
            } else if (ex instanceof ConnectionClosedException) {
                LOGGER.error("Ops, error during closing the connection", ex);
            } else {
                LOGGER.error("General error", ex);
            }
        }
    }

    private static class ServerCallable implements Callable<Void> {
        public Void call() throws Exception {
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(15000).setTcpNoDelay(true).build();

            final HttpServer server = ServerBootstrap.bootstrap().setListenerPort(PORT).setServerInfo("NastyServer/1.1").setSocketConfig(socketConfig).setExceptionLogger(new StdErrorExceptionLogger())
                .registerHandler("/200", new HttpRequestHandler() {
                    @Override
                    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
                        LOGGER.info("Handling request 200");
                        response.setStatusCode(HttpStatus.SC_OK);
                        response.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.toString());
                        response.setEntity(new StringEntity("OK to have a body"));
                        LOGGER.info("Returning response {}", response);
                    }
                }).registerHandler("/204", new HttpRequestHandler() {
                    // forces a 204 with body o.O
                    @Override
                    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
                        LOGGER.info("Handling request 204");
                        response.setStatusCode(HttpStatus.SC_NO_CONTENT);
                        response.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.toString());
                        response.setEntity(new StringEntity("This response shouldn't have a body"));
                        LOGGER.info("Returning response {}", response);
                    }
                }).create();

            LOGGER.info("Server starting at port {}", PORT);
            server.start();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    server.shutdown(5, TimeUnit.SECONDS);
                }
            });
            return null;
        };
    }

}
