/*
 * RETSConnection.java
 *
 * Created on November 16, 2001, 1:33 PM
 */
package org.realtor.rets.retsapi;

import com.aftexsw.util.bzip.CBZip2InputStream;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;

import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;


/**
 * Provides a connection to a RETS Server.
 *

 * @version 1.0
 */
public class RETSConnection {
    // log4j category
    static Category cat = Category.getInstance(RETSConnection.class);

//    static {
//        RETSConfigurator.configure();
//    }

    //Key value pairs for request header.
    private HashMap<String, String> headerHash = new HashMap<String, String>();
    private HashMap<String, Object> responseHeaderMap = new HashMap<String, Object>();
    private String serverUrl = null;
    private String errMsg = null;
    private boolean gzipCompressed = false;
    private boolean bzipCompressed = false;
    private boolean streamFlag = false;
    private long lastTransactionTime = 0;

    private String connMethod = "POST";
    private String transactionLogDirectory = null;
    private String requestId = "";
    private String sessionId = "";
    private String uaPassword = "";
    private PrintWriter log = null;
    private HttpClient client = new HttpClient();

    HashMap<String, Object> transactionContext = new HashMap<String, Object>(); // holds data across transactions
    private int connTimeoutSeconds = 60; // 60 seconds default

    /**
     * Creates new RETSConnection and changes default connection timeout
     * and sets the ServerURL.
     */
    public RETSConnection(String url, int connTimeoutSeconds) {
        this(url);
        this.connTimeoutSeconds = connTimeoutSeconds;
    }

    /**
     * Creates new RETSConnection and changes default connection timeout
     * and sets the ServerURL.
     */
    public RETSConnection(int connTimeoutSeconds) {
        this();
        this.connTimeoutSeconds = connTimeoutSeconds;
    }

    /**
     * Creates new RETSConnection and sets the ServerURL.
     */
    public RETSConnection(String url) {
        this();
        serverUrl = url;
    }

    /**
     * Create a new RETSConnection and setup some required Header fields.
     */
    public RETSConnection() {
        setRequestHeaderField("User-Agent", "Mozilla/4.0");
        setRequestHeaderField("RETS-Version", "RETS/1.0");
    }

    /**
     * Executes a transaction
     *
     * @param transaction transaction to execute
     */
    public void execute(RETSTransaction transaction) {
        execute(transaction, false);
    }

    /**
     * Executes a transaction
     *
     * @param transaction transaction to execute
     */
    public void executeStreamResponse(RETSTransaction transaction) {
        execute(transaction, true);
    }

    /**
     * Executes a transaction
     *
     * @param transaction transaction to execute
     */
    public void execute(RETSTransaction transaction, boolean asStream) {
        java.util.Date start = new Date();
        streamFlag = asStream;

        if ( transaction instanceof RETSGetObjectTransaction ) {
            setRequestHeaderField("Accept", ((RETSGetObjectTransaction) transaction).getImageAccept());
        } else {
            setRequestHeaderField("Accept", "*/*");
        }
        

        if ((transactionLogDirectory != null) && (transactionLogDirectory.length() > 1)) {
            String transType = transaction.getClass().getName();
            int nameIdx = transType.lastIndexOf(".") + 1;
            String name = transType.substring(nameIdx);
            String outFile = transactionLogDirectory + "/" + getFileName(name) + ".txt";

            try {
                log = new PrintWriter(new FileWriter(outFile));
                log.println("<!-- RETS REQUEST -->");
            } catch (Exception e) {
                cat.error("error creating output file :" + outFile,e);
            }
        }

        String compressFmt = transaction.getCompressionFormat();

        if (compressFmt != null) {
            if (compressFmt.equalsIgnoreCase("gzip")) {
                setRequestHeaderField("Accept-Encoding", "application/gzip,gzip");
            } else if (compressFmt.equalsIgnoreCase("bzip")) {
                setRequestHeaderField("Accept-Encoding", "application/bzip,bzip");
            } else if (compressFmt.equalsIgnoreCase("none")) {
                removeRequestHeaderField("Accept-Encoding");
            }
        }

        transaction.setContext(transactionContext);

        transaction.preprocess();

        processRETSTransaction(transaction);

        transaction.postprocess();

        Date finish = new Date();
        lastTransactionTime = finish.getTime() - start.getTime();

        if (log != null) {
            try {
                log.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            log = null;
        }

        return;
    }

    public long getLastTransactionTime() {
        return lastTransactionTime;
    }

    public void setTransactionLogDirectory(String tLogDir) {
        this.transactionLogDirectory = tLogDir;
    }

    public String getTransactionLogDirectory() {
        return this.transactionLogDirectory;
    }

    private void writeToTransactionLog(String msg) {
        if (log != null) {
            try {
                this.log.println(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cat.debug(msg);
    }

    private void writeMapToTransactionLog(Map<String, String> map) {
        if (map == null) {
            return;
        }

        Iterator<String> itr = map.keySet().iterator();

        while (itr.hasNext()) {
            String key = itr.next();
            String value = "";
            Object obj = map.get(key);

            if (obj instanceof String) {
                value = (String) obj;
            } else {
                value = "{ ";

                Collection<?> c = (Collection<?>) obj;
                Iterator<?> i2 = c.iterator();

                if (i2.hasNext()) {
                    value = (String) i2.next();

                    while (i2.hasNext()) {
                        value = value + ", " + (String) i2.next();
                    }
                }

                value = value + " }";
            }

            writeToTransactionLog(key + "=" + value);
        }
    }

    String formatDate(){
    	java.util.Date now = new java.util.Date();
    	SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowFormat = format.format(now);
        return nowFormat;
    }
    
    String getFileName(String transName){
    	StringBuffer sb = new StringBuffer();
    	
    	String uaClean = this.getUserAgent().replace("/","").replace(".","");
    	System.out.println("ua "+uaClean);
    	
    	sb.append(this.getUsername());
    	sb.append("_");
    	sb.append(uaClean);
    	sb.append("_");
    	sb.append(transName);
    	sb.append("_");
    	sb.append(formatDate());
    	
    	String fileName=sb.toString();
    	return fileName;
    }
    /**
     * Returns the server's URL, this url as a base for all transactions
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * Sets the url for the connection.
     *
     * @param url Server's address ex: http://www.realtor.org/RETSServer
     */
    public void setServerUrl(String url) {
        serverUrl = url;
    }

    public void setUserAgent(String userAgent) {
    	setRequestHeaderField("User-Agent", userAgent);
    }

    public String getUserAgent() {
        return getRequestHeaderField("User-Agent");
    }

    public void setUAPassword(String uaPassword) {
	    this.uaPassword=uaPassword;
    }

    public String getUAPassword() {
	    return this.uaPassword;
    }

     public void setSessionId(String sessionId) {
		this.sessionId=sessionId;
    }

     public String getSessionId() {
		return this.sessionId;
    }

     public void setRequestId(String requestId) {
		this.requestId=requestId;
    }

     public String getRequestId() {
		return this.requestId;
    }

    public void setUAHeader(String uaDigestResponse) {
		UAAuthorization uaa = new UAAuthorization(getRetsVersion(), getUserAgent(), getUAPassword(), getSessionId(), getRequestId());
	     headerHash.put("RETS-UA-Authorization", uaa.getString());
    }

    public void setRetsVersion(String retsVersion) {
        setRequestHeaderField("RETS-Version", retsVersion);
    }

    public String getRetsVersion() {
        return getRequestHeaderField("RETS-Version");
    }

    /**
     * Key value pairs in the client request header
     *
     * @param key   field name in the request header
     * @param value value associated with the key
     */
    public void setRequestHeaderField(String key, String value) {
        headerHash.put(key, value);
    }

    public String getRequestHeaderField(String key) {
        return headerHash.get(key);
    }

    /**
     * Removes a key/value pair from the request header.
     *
     * @param key field to remove from the request header.
     */
    public void removeRequestHeaderField(String key) {
        headerHash.remove(key);
    }

    public HashMap<String, Object> getResponseHeaderMap() {
        return responseHeaderMap;
    }

    /**
     * gets the url content and returns an inputstream
     *
     * @param strURL
     * @param requestMethod
     * @param requestMap
     */
    public InputStream getURLContent(String strURL, String requestMethod, Map<String, String> requestMap) {

        HttpMethod method = null;

        cat.debug("getURLContent: URL=" + strURL);

        try {
            if (requestMethod.equalsIgnoreCase("GET")) {
                method = new GetMethod(strURL);
            } else {
                method = new PostMethod(strURL);
            }

            client.getState().setCredentials(null, null, new UsernamePasswordCredentials(getUsername(), getPassword()));
            client.getState().setCookiePolicy(CookiePolicy.COMPATIBILITY);
            client.setConnectionTimeout(connTimeoutSeconds * 1000);

            method.setDoAuthentication(true);
            //method.setFollowRedirects(true); // throws exception in httpclient 3.0

            addHeaders(method, headerHash);
            writeMapToTransactionLog(headerHash);

            // send the request parameters
            if (requestMap != null) {
                NameValuePair[] pairs = mapToNameValuePairs(requestMap);

                if (requestMethod.equalsIgnoreCase("POST")) {
                    // requestMethod is a post, so we can safely cast.
                    PostMethod post = (PostMethod) method;
                    post.setRequestBody(pairs);
                } else {
                    GetMethod get = (GetMethod) method;
                    get.setQueryString(pairs);
                }
            }

            this.writeToTransactionLog("<!-- Response from server -->");

            int responseCode = client.executeMethod(method);
            
            InputStream is = method.getResponseBodyAsStream();
            copyResponseHeaders(method);
            //method.releaseConnection(); // from bruce
            return is;
        } catch (IOException io) {
            io.printStackTrace();
            errMsg = "RETSAPI: I/O exception while processing transaction: " + io.getMessage();
            return null;
        } finally {
            if (method != null) {
                //method.releaseConnection();
            }
        }
    }

public InputStream postContent(String strURL, HashMap<String, String> postHeaders, String filePath) {

        PostMethod method = new PostMethod(strURL);
		InputStream fileIs = null;
        cat.debug("postContent: URL=" + strURL+" filePath="+filePath);

		
        try {
			File f = new File(filePath);
			fileIs = new FileInputStream(f);

            client.getState().setCredentials(null, null, new UsernamePasswordCredentials(getUsername(), getPassword()));
            client.getState().setCookiePolicy(CookiePolicy.COMPATIBILITY);
            client.setConnectionTimeout(connTimeoutSeconds * 1000);

            method.setDoAuthentication(true);
            //method.setFollowRedirects(true); // throws exception in httpclient 3.0

            addHeaders(method, headerHash);
            writeMapToTransactionLog(headerHash);
            addHeaders(method, postHeaders);
            writeMapToTransactionLog(postHeaders);

			method.setRequestBody(fileIs);
			
            this.writeToTransactionLog("<!-- Response from server -->");

            int responseCode = client.executeMethod(method);
            
            InputStream responseIs = method.getResponseBodyAsStream();
            copyResponseHeaders(method);
            //method.releaseConnection(); // from bruce
            return responseIs;
        } catch (IOException io) {
            io.printStackTrace();
            errMsg = "RETSAPI: I/O exception while processing transaction: " + io.getMessage();
            return null;
        } finally {
            if (method != null) {
                //method.releaseConnection();
            }
			
			if (fileIs != null) {
                try {
                    fileIs.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    /**
     * Changes a map into an array of name value pairs
     *
     * @param requestMap The map to change.
     * @return An array of Name value pairs, representing keys and values from the map.
     */
    private NameValuePair[] mapToNameValuePairs(Map<String, String> requestMap) {
        NameValuePair[] pairs = new NameValuePair[requestMap.size()];
        Iterator<String> iter = requestMap.keySet().iterator();
        int i = 0;

        while (iter.hasNext()) {
            String key = iter.next();
            String value = requestMap.get(key);
            NameValuePair nvp = new NameValuePair(key, value);
            pairs[i] = nvp;
            i++;
        }

        return pairs;
    }

    /**
     * Adds response headers to Http method
     *
     * @param method
     */
    private void copyResponseHeaders(HttpMethod method) {
        responseHeaderMap.clear();

        Header[] headers = method.getResponseHeaders();

        for (int i = 0; i < headers.length; i++) {
            Header current = headers[i];
            
            List<String> list = (List<String>)responseHeaderMap.get(current.getName());

            if (list == null) {
                list = new ArrayList<String>();
            }

            list.add(current.getValue());
            responseHeaderMap.put(current.getName(), list);
        }
    }

    private void addHeaders(HttpMethod method, HashMap<String, String> headers) {
        Iterator<String> keys = headers.keySet().iterator();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = headers.get(key);

            if (value instanceof String && isValidString((String) value)) {
                method.addRequestHeader(key, (String) value);
            } else if (value instanceof ArrayList) {
                ArrayList<?> list = (ArrayList<?>) value;
                StringBuffer valueList = new StringBuffer();

                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        valueList.append(";");
                    }

                    valueList.append(list.get(i));
                }

                method.addRequestHeader(key, valueList.toString());
            }
        }
    }

    /**
     * Processes a transaction, sends rets request and gets
     * the response stream from the server.  Uncompresses the
     * response stream if compression was used in the reply
     *
     * @param transaction rets transaction to process
     */
    private void processRETSTransaction(RETSTransaction transaction) {
		String action = null;
		
        try {
            serverUrl = transaction.getUrl();

            cat.debug(transaction.getRequestType() + " URL : {" + serverUrl + "}");

            if (serverUrl == null) {
                cat.error(transaction.getRequestType() + " URL is null");
                transaction.setResponseStatus("20036");
                transaction.setResponseStatusText(transaction.getRequestType() + " URL is missing. Successful login is required.");
                return; // throw exception here
            }

            String method = connMethod;

            // Action transaction requires a GET according to RETS spec
            if (transaction.getRequestType().equalsIgnoreCase("Action")) {
                method = "GET";
            }
			else if (transaction.getRequestType().equalsIgnoreCase("PostObject")) {
                method = "POST";
				action="SEND_FILE";
            }
			
            cat.debug("method: " + method);
        gzipCompressed = false;
        bzipCompressed = false;

            InputStream is;
	
			if ("SEND_FILE".equals(action)){

				HashMap<String, String> headers = new HashMap();
				headers.put("Content-type", ((RETSPostObjectTransaction) transaction).getContentType());
				headers.put("UpdateAction", ((RETSPostObjectTransaction) transaction).getUpdateAction());
				headers.put("Content-length", ((RETSPostObjectTransaction) transaction).getContentLength());
				headers.put("Type", ((RETSPostObjectTransaction) transaction).getType());
				headers.put("Resource", ((RETSPostObjectTransaction) transaction).getResource());
				headers.put("ResourceID", ((RETSPostObjectTransaction) transaction).getResourceID());
				headers.put("ObjectID", ((RETSPostObjectTransaction) transaction).getObjectID());
				headers.put("OrderHint", ((RETSPostObjectTransaction) transaction).getOrderHint());
				headers.put("UID", ((RETSPostObjectTransaction) transaction).getUID());

				is = postContent(serverUrl, headers, ((RETSPostObjectTransaction) transaction).getUploadFile());
			}
			else{
				is = getURLContent(serverUrl, method, transaction.getRequestMap());
			}
			
            if (is == null) {
                transaction.setResponseStatus("20513"); // Miscellaneous error
                transaction.setResponseStatusText(errMsg);
                transaction.setResponse(errMsg);
                errMsg = null;

                return;
            } else {
                Object compressionFmt = responseHeaderMap.get("Content-Encoding");

                if (compressionFmt != null) {
                    cat.debug("Header class : " + compressionFmt.getClass().getName());

                    if (compressionFmt.toString().equalsIgnoreCase("[gzip]")) {
                        gzipCompressed = true;
                    } else if (compressionFmt.toString().equalsIgnoreCase("[bzip]")) {
                        bzipCompressed = true;
                    }
                }

                if (gzipCompressed) {
                    is = new GZIPInputStream(is);
                } else if (bzipCompressed) {
                    is = new CBZip2InputStream(is);
                }
            }
            this.writeToTransactionLog("<!-- Obtained and Identified Response Stream -->");

            transaction.setResponseHeaderMap(this.responseHeaderMap);

            if ((transaction instanceof RETSGetObjectTransaction && (! transaction.getResponseHeader("Content-Type").startsWith("text/xml"))) || streamFlag) {
                transaction.setResponseStream(is);
            } else {
                String contents = null;
                contents = streamToString(is);
                writeToTransactionLog(contents);

                /*catch( IOException e) {
                    errMsg = "Error reading response stream: " + contents;
                    cat.error(errMsg, e);
                    transaction.setResponseStatus("20513"); // Miscellaneous error
                    transaction.setResponseStatusText(errMsg);
                    errMsg = null;
                }*/
                if (contents.length() == 0) {
                    transaction.setResponseStatus("20513"); // Miscellaneous error
                    transaction.setResponseStatusText("Empty Body");
                }

                transaction.setResponse(contents);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        String username = null; //(String)requestMap.get("username");

        if (username == null) {
            username = (String) transactionContext.get("username");
        }

        return username;
    }

    public String getPassword() {
        String password = null; //(String)requestMap.get("password");

        if (password == null) {
            password = (String) transactionContext.get("password");
        }

        return password;
    }
    public String getConnMethod() {
        return connMethod;
    }

    public void setConnMethod(String connMethod) {
        this.connMethod = connMethod;
    }


    /**
     * Removes the quotes on a string.
     *
     * @param quotedString string that might contain quotes
     */
    private static String removeQuotes(String quotedString) {
        if ((quotedString != null) && (quotedString.length() > 2)) {
            return quotedString.substring(1, quotedString.length() - 1);
        } else {
            return ""; // empty string
        }
    }

    /**
     * Checks to make sure the string passed in is a valid string parameter (not null and not zero length).
     *
     * @param value string to be validated
     */
    private boolean isValidString(String value) {
        return ((value != null) && (value.length() > 0));
    }

    private String streamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuffer sb = new StringBuffer();
            int numread = 0;
            byte[] buffer = new byte[1024 * 8]; //initialize an 8k buffer

            while ((numread = is.read(buffer)) >= 0) {
                String s = new String(buffer, 0, numread);
                sb.append(s);
            }

            return sb.toString();
        }

        return null;
    }

    /**
     * Main method for testing only!
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();

        RETSConnection rc = new RETSConnection();
        RETSLoginTransaction trans = new RETSLoginTransaction();

        try {
            Properties props = new Properties();
            props.load(new FileInputStream("/tmp/client.properties"));

            // Add the optional request parameters if they exist, are non-null and non-zero-length
            // rc.setRequestHeaderField("Authorization", (String)props.get("login.AUTHORIZATION"));
            rc.setServerUrl((String) props.getProperty("SERVER_URL"));
            trans.setUrl((String) props.getProperty("SERVER_URL"));
            trans.setUsername((String) props.getProperty("USERNAME"));
            trans.setPassword((String) props.getProperty("PASSWORD"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        rc.execute(trans);
    }

    /**
     * Build the queryString from the request map
     *
     * @param requestMap the list of request parameters
     */
    private String buildQueryString(Map<?, ?> requestMap) {
        /*if (((String)(requestMap.get("requestType"))).equalsIgnoreCase("Search")) {
            return "SearchType=Property&Class=RESI&Query=(Listing_Price%3D100000%2B)&QueryType=DMQL";
        }*/
        StringBuffer sb = new StringBuffer();
        Iterator<?> it = requestMap.keySet().iterator();

        // build query string
        while (it.hasNext()) {
            String key = (String) it.next();

            if (key.equals("requestType")) {
                //commenting out requestType because it is not a standard req parameter and may break RETS servers
                continue;
            }

            String reqStr = key + "=" + URLEncoder.encode((String) requestMap.get(key));
            cat.debug(reqStr);
            sb.append(reqStr);

            if (it.hasNext()) {
                sb.append("&");
            }
        }

        return sb.toString();
    }

}
