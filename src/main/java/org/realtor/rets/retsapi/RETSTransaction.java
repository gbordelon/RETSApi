/**
 *        RETSTransaction.java
 *
 *        @author        jbrush
 *        @version
 */
package org.realtor.rets.retsapi;

import org.apache.log4j.*;

import org.realtor.rets.util.*;

import java.io.*;

import java.util.*;
import java.util.regex.*;


///////////////////////////////////////////////////////////////////////

public class RETSTransaction extends RETSRequestResponse {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3454335993900788887L;
	static Category cat = Category.getInstance(RETSTransaction.class);
    private static final String STATUS = "status";
    private static final String STATUSTEXT = "statusText";
    private static final String BODY = "body";
    private static final String REQUESTTYPE = "requestType";
    protected static final String RESOURCE = "Resource";
    protected static final String CLASS_NAME = "ClassName";
    protected HashMap<String, Object> transactionContext = null;
    private HashMap<String, String> capabilityUrls = null;
    protected HashMap<String, Object> responseHeaderMap = null;
    InputStream responseStream = null;
    static final Pattern //firstStatusRE;
                firstStatusRE = Pattern.compile( "(?s).*<RETS\\s+ReplyCode=\"(.*?)\"\\s+ReplyText=\"(.*?)\".*");
    static final Pattern //secondStatusRE;
                secondStatusRE = Pattern.compile("(?s).*<RETS-STATUS\\s+ReplyCode=\"(.*?)\"\\s+ReplyText=\"(.*?)\".*");

    
    
    
    /**
     * Holds value of property compressionFormat.
     */
    private String compressionFormat = null;

    public RETSTransaction() {
        super();
/*        
        synchronized {
            try {
                firstStatusRE = Pattern.compile("<RETS\\s?ReplyCode=\"(.*?)\"\\s?ReplyText=\"(.*?)\"");
                secondStatusRE = Pattern.compile("<RETS-STATUS\\s?ReplyCode=\"(.*?)\"\\s?ReplyText=\"(.*?)\"");
            }
            catch (PatternSyntaxException e) {
                cat.error("Error compiling status REs", e);
            }
        }
*/        
    }

    public void setResource(String resource) {
        setRequestVariable(RESOURCE, resource);
    }

    public String getResource() {
        return getRequestVariable(RESOURCE);
    }

    public void setClassName(String className) {
        setRequestVariable(CLASS_NAME, className);
    }

    public String getClassName() {
        return getRequestVariable(CLASS_NAME);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setRequestType(String type) {
        setRequestVariable(REQUESTTYPE, type);
    }

    public String getRequestType() {
        return getRequestVariable(REQUESTTYPE);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setRequest(String body) {
        setRequestVariable(BODY, body);
    }

    public String getRequest() {
        return getRequestVariable(BODY);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setResponse(String body) {
        setResponseVariable(BODY, body);

        Matcher firstStatusMatcher = firstStatusRE.matcher(body);
        Matcher secondStatusMatcher = secondStatusRE.matcher(body);
        // extract values
        // cat.debug("looking for <RETS tag");
        if ((body == null) || !firstStatusMatcher.matches()) {
            return;
        }


        // extract RETS-STATUS values is any
        if (secondStatusMatcher.matches()) {
            setResponseStatus(secondStatusMatcher.group(1));
            setResponseStatusText(secondStatusMatcher.group(2));
        }
        else {
            setResponseStatus(firstStatusMatcher.group(1));
            setResponseStatusText(firstStatusMatcher.group(2));
        }
    }

    public String getResponse() {
        return getResponseVariable(BODY);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setResponseStream(InputStream is) {
        responseStream = is;
    }

    public InputStream getResponseStream() {
        return responseStream;
    }

    ///////////////////////////////////////////////////////////////////////
    public void setResponseStatus(String status) {
        setResponseVariable(STATUS, status);
    }

    public String getResponseStatus() {
        return getResponseVariable(STATUS);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setResponseStatusText(String statusText) {
        setResponseVariable(STATUSTEXT, statusText);
    }

    public String getResponseStatusText() {
        return getResponseVariable(STATUSTEXT);
    }

    ///////////////////////////////////////////////////////////////////////
    public String getUrl() {
        String url = getCapabilityUrl(getRequestType());

        cat.debug("getUrl():" + getRequestType() + " url:" + url);

        return url;
    }

    ///////////////////////////////////////////////////////////////////////
    void setKeyValuePairs(String str) {
        if (str == null) {
            return;
        }

        StringTokenizer lineTokenizer = new StringTokenizer(str, "\r\n");

        while (lineTokenizer.hasMoreTokens()) {
            String line = lineTokenizer.nextToken();

            // if tag, ignore it
            if (line.charAt(0) != '<') {
                int equalSign = line.indexOf("=");

                if (equalSign >= 0) {
                    setResponseVariable(line.substring(0, equalSign).trim(),
                            line.substring(equalSign + 1).trim());
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////
    void putCapabilityUrl(String key, String value) {
        if (capabilityUrls == null) {
            capabilityUrls = new HashMap<String, String>();
        }

        capabilityUrls.put(key, value);
    }

    public String getCapabilityUrl(String key) {
        return (String) capabilityUrls.get(key);
    }

    ///////////////////////////////////////////////////////////////////////
    public void preprocess() {
        // by default does nothing
        //subclasses can override
    }

    public void postprocess() {
        // by default does nothing
        //subclasses can override
    }

    @SuppressWarnings("unchecked")
	void setContext(HashMap<String,Object> transactionContext) {
        if (transactionContext != null) {
            this.transactionContext = transactionContext;

            capabilityUrls = (HashMap<String,String>) transactionContext.get("capabilityUrls");

            if (capabilityUrls == null) {
                capabilityUrls = new HashMap<String, String>();
                transactionContext.put("capabilityUrls", capabilityUrls);
            }
        }
    }

    public HashMap<String, Object> getTransactionContext() {
        return transactionContext;
    }

    public HashMap<String, Object> getResponseHeaderMap() {
        return responseHeaderMap;
    }

    public void setResponseHeaderMap(HashMap<String, Object> responseHeaders) {
        responseHeaderMap = responseHeaders;
    }

    /**
     * Returns the value of the response header with the specified name, or <code>null</code>
     * if the header was not returned.
     *
     * @param headerName The name of the header to be retrieved.
     */
    public String getResponseHeader(String headerName) {
        String responseString = null;
        // If we have no header map, we obviously have no headers. Also, if
        // there is no list for the header name, we don't have the
        // requested header.
        if ( headerName != null && headerName.equals("content-type") ) {
            headerName = "Content-Type";
        }
        if (responseHeaderMap != null) {
            cat.debug("RESPONSEHEADERMAP ==> " + responseHeaderMap.toString());
//            responseString = (String) responseHeaderMap.get(headerName.toLowerCase());
            cat.debug("ContentType Class is ... " + responseHeaderMap.get(headerName).getClass().getName());
            Object object = responseHeaderMap.get(headerName);
            if ( object == null ) {
                return null;
            }

            if ( object instanceof List<?> ) {
                responseString = (String)((List<?>)object).get(0);
            } 
            else {
                responseString = object.toString();
            }
        } 
        else {
            cat.debug("RESPONSEHEADERMAP ==> " + responseHeaderMap);
        }
        return responseString;
    }

    /**
     * Getter for property compressionFormat.
     *
     * @return Value of property compressionFormat.
     */
    public String getCompressionFormat() {
        return this.compressionFormat;
    }

    /**
     * Setter for property compressionFormat.
     *
     * @param compressionFormat New value of property compressionFormat.
     */
    public void setCompressionFormat(String compressionFormat) {
        this.compressionFormat = compressionFormat;
    }

    static public void log(String logMessage) {
        cat.debug(logMessage);
    }
}
