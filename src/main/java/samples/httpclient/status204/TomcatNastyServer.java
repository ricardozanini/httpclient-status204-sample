package samples.httpclient.status204;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

public class TomcatNastyServer implements NastyServer {

    public TomcatNastyServer() {
    }

    @Override
    public void start() throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(PORT);

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        Tomcat.addServlet(ctx, "NoContentEndpointServlet", new NoContentEndpointServlet());
        Tomcat.addServlet(ctx, "OkEndpointServlet", new OkEndpointServlet());

        ctx.addServletMappingDecoded(NC_ENDPOINT, "NoContentEndpointServlet");
        ctx.addServletMappingDecoded(OK_ENDPOINT, "OkEndpointServlet");

        tomcat.start();
    }

    public static class NoContentEndpointServlet extends HttpServlet {

        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            ServletOutputStream out = resp.getOutputStream();
            resp.setStatus(204);
            out.write("Oow! I have a body but I shouldn't".getBytes());
            out.flush();
            out.close();
        }
    }

    public static class OkEndpointServlet extends HttpServlet {

        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            ServletOutputStream out = resp.getOutputStream();
            out.write("OK!".getBytes());
            out.flush();
            out.close();
        }
    }
}
