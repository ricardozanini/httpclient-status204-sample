package samples.httpclient.status204;

public interface NastyServer {
    public static final int PORT = 8080;
    public static final String OK_ENDPOINT = "/200";
    public static final String NC_ENDPOINT = "/204";

    void start() throws Exception;

}
