package samples.httpclient.status204;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String LOCALHOST = "http://localhost";

    public static void main(String[] args) throws Exception {
        NastyServer server = new TomcatNastyServer();
        server.start();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet httpGetOK = new HttpGet(String.format("%s:%s%s", LOCALHOST, NastyServer.PORT, NastyServer.OK_ENDPOINT));
            final HttpGet httpGetNC = new HttpGet(String.format("%s:%s%s", LOCALHOST, NastyServer.PORT, NastyServer.NC_ENDPOINT));

            performRequest(httpClient, httpGetOK);
            performRequest(httpClient, httpGetNC);
            performRequest(httpClient, httpGetNC);
        }
    }

    private static void performRequest(CloseableHttpClient httpClient, HttpGet httpGet) throws IOException {

        LOGGER.info("Request {} :: Begin", httpGet.getURI());
        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            LOGGER.info(response.getStatusLine().toString());
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        LOGGER.info("Request {} :: Finished", httpGet.getURI());
    }

}
