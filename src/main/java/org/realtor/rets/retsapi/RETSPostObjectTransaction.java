package org.realtor.rets.retsapi;

import org.apache.log4j.Category;

import java.util.Iterator;
import java.util.Map;

/**
 *        RETSPostObjectTransaction.java
 *
 *        @author        pobrien
 *        @version 1.0
 */
public class RETSPostObjectTransaction extends RETSTransaction {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4622198659901766798L;
	private String updateAction;
	private String contentType;
	private String contentLength;
	private String type;
	private String resource;
	private String resourceID;
	private String objectID;
	private String orderHint;
	private String UID;
	private String uploadFile;
	
	static Category cat = Category.getInstance(RETSPostObjectTransaction.class);

    /**
     *
     */
    public RETSPostObjectTransaction() {
        super();
        setRequestType("PostObject");
        
    }

    /**
     *  Sets the response body for the transaction.
     *
     *  @param body body of the transaction
     */
    public void setResponse(String body) {
        super.setResponse(body);
        cat.debug("Setting response as " + body);
        setKeyValuePairs(body);
    }
    public String getUpdateAction() {
        return updateAction;
    }

    public void setUpdateAction(String updateAction) {
        this.updateAction = updateAction;
    }
    
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public String getContentLength() {
        return contentLength;
    }

    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }
    
    public String getType() {
        return type;
    }


    public void setResource(String resource) {
        this.resource = resource;
    }
    

    public String getResource() {
        return resource;
    }
    
    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }
    

    public String getResourceID() {
        return resourceID;
    }
    
    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }
    

    public String getObjectID() {
        return objectID;
    }
    
    public void setOrderHint(String orderHint) {
        this.orderHint = orderHint;
    }
    

    public String getOrderHint() {
        return orderHint;
    }
    
    public void setUid(String UID) {
        this.UID = UID;
    }
    public String getUID() {
        return UID;
    }
    
    public void setDelimiter(String str) {
        cat.debug("set Delimiter=" + str);
        setRequestVariable("Delimiter", str);
    }
    public String getDelimiter() {
	    return getRequestVariable("Delimiter");
    }

	public void setUploadFile(String str){
		this.uploadFile = str;
    }
	public String getUploadFile(){
		return uploadFile;
	}
	
    public void setWarningResponse(String str) {
	        cat.debug("set WarningResponse=" + str);
	        setRequestVariable("WarningResponse", str);
    }

    

    public void setWarningResponseValues(Map<String, ?> m) {
	        // convert to a string and feed to setWarningResponse()....
	        StringBuffer warning = new StringBuffer("(");
	        Iterator<String> iter = m.keySet().iterator();
			// delimiter is a 2 digit HEX value
	        char delim = (char) Integer.parseInt(getDelimiter().trim(), 16);

	        while (iter.hasNext()) {
	            String name = iter.next();
	            Object val = m.get(name);
	            String value = "";

	            if (val instanceof String) {
	                value = (String) val;
	            } else {
	                String[] arr = (String[]) val;
	                value = arr[0];
	            }

	            warning.append(name);
	            warning.append("=");
	            warning.append(value);

	            if (iter.hasNext()) {

	                warning.append(delim);
	            }
	        }

	        warning.append(")");
	        setWarningResponse(warning.toString());
    }

}
