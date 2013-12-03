package fi.nls.oskari.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;

/**
 * Implements HTTP request and response methods
 */
public class HttpHelper {

    private static final Logger log = LogFactory.getLogger(HttpHelper.class);
    
    /**
     * Basic HTTP GET method
     * 
     * @param url
     * @return response body
     */
    public static String getRequest(String url, String cookies) {
		HttpRequest request;
		String response = null;
		try {
			request = HttpRequest.get(url)
					.acceptGzipEncoding().uncompress(true)
					.trustAllCerts()
					.trustAllHosts();
            if(cookies != null) {
                request.getConnection().setRequestProperty("Cookie", cookies);
            }
            if(request.ok() || request.code() == 304)
				response = request.body();
			else {
				handleHTTPError("GET", url, request.code());
			}
		} catch (HttpRequestException e) {
			handleHTTPRequestFail(url, e);
		} catch (Exception e) {
			handleHTTPRequestFail(url, e);
		}
		return response;
    }
    
    /**
     * HTTP GET method with optional basic authentication and contentType definition
     * 
     * @param url
     * @param contentType
     * @param username
     * @param password
     * @return response body
     */
    public static BufferedInputStream getRequestStream(String url, String contentType, String username, String password) {
		HttpRequest request = getRequest(url, contentType, username, password);
		if(request != null) {
			return request.buffer();
		}
		return null;
    }

    /**
     * HTTP GET method with optional basic authentication and contentType definition
     * 
     * @param url
     * @param contentType
     * @param username
     * @param password
     * @return response body
     */
    public static BufferedReader getRequestReader(String url, String contentType, String username, String password) {
		HttpRequest request = getRequest(url, contentType, username, password);
		if(request != null) {
			return request.bufferedReader();
		}
		return null;
    }
    
    /**
     * HTTP GET method with optional basic authentication and contentType definition
     * 
     * @param url
     * @param contentType
     * @param username
     * @param password
     * @return response body
     */
    public static HttpRequest getRequest(String url, String contentType, String username, String password) {
		HttpRequest request;
		try {
			
			HttpRequest.keepAlive(false);
			if(username != "" && username != null) {
				request = HttpRequest.get(url)
						.basic(username, password)
						.accept(contentType)
						.connectTimeout(30)
						.acceptGzipEncoding().uncompress(true)
						.trustAllCerts()
						.trustAllHosts();
			} else {
				request = HttpRequest.get(url)
						.contentType(contentType)
						.connectTimeout(30)
						.acceptGzipEncoding().uncompress(true)
						.trustAllCerts()
						.trustAllHosts();
			}
			if(request.ok() || request.code() == 304)
				return request;
			else {
				handleHTTPError("GET", url, request.code());
			}
			
		} catch (HttpRequestException e) {
			handleHTTPRequestFail(url, e);
		} catch (Exception e) {
			handleHTTPRequestFail(url, e);
		}
		return null;
    }
    
    /**
     * HTTP POST method with optional basic authentication and contentType definition
     * 
     * @param url
     * @param contentType
     * @param username
     * @param password
     * @return response body
     */
    public static BufferedReader postRequestReader(String url, String contentType, String data, String username, String password) {
		HttpRequest request;
		BufferedReader response = null;
		try {
			
			HttpRequest.keepAlive(false);
			if(username != "" && username != null) {
				request = HttpRequest.post(url)
						.basic(username, password)
						.contentType(contentType)
						.connectTimeout(30)
						.acceptGzipEncoding().uncompress(true)
						.trustAllCerts()
						.trustAllHosts()
						.send(data);
			} else {
				request = HttpRequest.post(url)
						.contentType(contentType)
						.connectTimeout(30)
						.acceptGzipEncoding().uncompress(true)
						.trustAllCerts()
						.trustAllHosts()
						.send(data);
			}
			if(request.ok() || request.code() == 304)
				response = request.bufferedReader();
			else {
				handleHTTPError("POST", url, request.code());
			}
			
		} catch (HttpRequestException e) {
			handleHTTPRequestFail(url, e);
		} catch (Exception e) {
			handleHTTPRequestFail(url, e);
		}
		return response;
    }
    
    /**
     * Handles HTTP error logging for HTTP request methods
     * 
     * @param type
     * @param url
     * @param code
     */
    private static void handleHTTPError(String type, String url, int code) {
        log.warn("HTTP "+ type + " request error (" + code + ") when requesting: " + url);
    }

    /**
     * Handles Exceptions logging for HTTP request methods
     * 
     * @param url
     * @param e
     */
    private static void handleHTTPRequestFail(String url, Exception e) {
        log.warn(e, "HTTP request failed when requesting: " + url);
    }
}
