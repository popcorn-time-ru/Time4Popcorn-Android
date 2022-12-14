package eu.sesma.castania.castserver;

import java.util.Map;

import eu.sesma.castania.castserver.NanoHTTPD.Response;

/**
 * @author Paul S. Hawke (paul.hawke@gmail.com) On: 9/15/13 at 2:52 PM
 */
public class InternalRewrite extends Response {
    private final String uri;
    private final Map<String, String> headers;

    public InternalRewrite(final Map<String, String> headers, final String uri) {
        super(null);
        this.headers = headers;
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}