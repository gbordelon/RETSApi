package org.realtor.rets.retsapi;

import org.apache.log4j.Category;

import java.util.Iterator;
import java.util.Map;

/**
 *        RETSUpdateTransaction.java
 *
 *        @author        pobrien
 *        @version 1.0
 */
public class RETSUpdateTransaction extends RETSTransaction {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4622198659901766798L;
	static Category cat = Category.getInstance(RETSUpdateTransaction.class);

    /**
     *
     */
    public RETSUpdateTransaction() {
        super();
        setRequestType("Update");
        setDelimiter("09");//default is ascii ht
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

    /**
     *  Sets the action attribute to the string passed in.
     *
     *  @param action attribute value
     */
    public void setAction(String str) {
        cat.debug("set Action=" + str);
        setRequestVariable("Action", str);
    }

    /**
     *  Sets the Resource attribute to the string passed in.
     *
     *  @param Resource attribute value
     */
    public void setResource(String str) {
        cat.debug("set Resource=" + str);
        setRequestVariable("Resource", str);
    }
    
    /**
     *  Sets the ClassName attribute to the string passed in.
     *
     *  @param ClassName attribute value
     */
    public void setClassName(String str) {
        cat.debug("set ClassName=" + str);
        setRequestVariable("ClassName", str);
    }

    /**
     *  Sets the Lock attribute to the string passed in.
     *
     *  @param Lock attribute value
     */
    public void setLock(String str) {
        cat.debug("set Lock=" + str);
        setRequestVariable("Lock", str);
    }
   
    /**
     *  Sets the LockKey attribute to the string passed in.
     *
     *  @param LockKey attribute value
     */
    public void setLockKey(String str) {
        cat.debug("set LockKey=" + str);
        setRequestVariable("LockKey", str);
    }
   
    /**
     *  Sets the Select attribute to the string passed in.
     *
     *  @param Select attribute value
     */
    public void setSelect(String str) {
        cat.debug("set Select=" + str);
        setRequestVariable("Select", str);
    }
 
    
    /**
     *  Sets the Validate attribute to the string passed in.
     *
     *  @param str Validate of the object
     */
    public void setValidate(String str) {
        cat.debug("set Validate=" + str);
        setRequestVariable("Validate", str);
    }

    /**
     *  Sets the Delimiter attribute to the string passed in.
     *
     *  @param str Delimiter attribute value
     */
    public void setDelimiter(String str) {
        cat.debug("set Delimiter=" + str);
        setRequestVariable("Delimiter", str);
    }

    public String getDelimiter() {
	    return getRequestVariable("Delimiter");
    }

    public void setRecord(String str) {
        cat.debug("set Record=" + str);
        setRequestVariable("Record", str);
    }

    public void setWarningResponse(String str) {
	        cat.debug("set WarningResponse=" + str);
	        setRequestVariable("WarningResponse", str);
    }

    public void setNewValues(Map<String, ?> m) {
        // convert to a string and feed to setRecord()....
        StringBuffer record = new StringBuffer();
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

            record.append(name);
            record.append("=");
            record.append(value);

            if (iter.hasNext()) {

                record.append(delim);
            }
        }

        setRecord(record.toString());
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

    public void setUID(String id) {
        cat.debug("UID is " + id);
        setRequestVariable("OriginalUid", id);
    }





}
