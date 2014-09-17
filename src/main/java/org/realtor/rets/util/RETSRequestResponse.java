/**
 *        RETSRequestResponse.java
 *
 *        @author        jbrush
 *        @version
 */
package org.realtor.rets.util;

import org.apache.log4j.*;

import java.util.*;


///////////////////////////////////////////////////////////////////////
public class RETSRequestResponse implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6631033455597937267L;
	static Category cat = Category.getInstance(RETSRequestResponse.class);
    private HashMap<String, String> req = null;
    private HashMap<String, String> resp = null;

    public RETSRequestResponse() {
        req = new HashMap<String, String>();
        resp = new HashMap<String, String>();
    }

    ///////////////////////////////////////////////////////////////////////
    public void setRequestVariable(String key, String value) {
        req.put(key, value);
    }

    public String getRequestVariable(String key) {
        return (String) req.get(key);
    }

    public Map<String, String> getRequestMap() {
        return (Map<String, String>) req;
    }

    public void addToRequestMap(Map< String, String> m) {
        req.putAll(m);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setResponseVariable(String key, String value) {
        resp.put(key, value);
    }

    public String getResponseVariable(String key) {
        return (String) resp.get(key);
    }

    public Map<String, String> getResponseMap() {
        return (Map<String, String>) resp;
    }

    public void addToResponseMap(Map< String, String> m) {
        resp.putAll( m);
    }

    ///////////////////////////////////////////////////////////////////////
}
