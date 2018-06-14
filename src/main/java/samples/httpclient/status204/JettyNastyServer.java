package samples.httpclient.status204;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jetty also rip off the body from the response.
 */
public final class JettyNastyServer implements NastyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JettyNastyServer.class);

    public void start() throws Exception {
        Server server = new Server(NastyServer.PORT);
        server.setHandler(new NastyHandler());
        server.start();
        LOGGER.info("Server has been started");
    }

    private static class NastyHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setContentType("text/html; charset=utf-8");
            PrintWriter out = response.getWriter();
            if (target.contains(NastyServer.OK_ENDPOINT)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.println("Ok");
            } else {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                out.println("This response shouldn't have body");
            }

            baseRequest.setHandled(true);
        }

    }

}
