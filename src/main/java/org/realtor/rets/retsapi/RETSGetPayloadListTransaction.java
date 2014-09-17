/**
 *        RETSGetPayloadListTransaction.java
 *
 *        @author        pobrien
 *        @version
 */
package org.realtor.rets.retsapi;


//import java.util.*;
import org.apache.log4j.*;


///////////////////////////////////////////////////////////////////////
public class RETSGetPayloadListTransaction extends RETSTransaction {
    /**
	 * 
	 */
	private static final long serialVersionUID = 9214592476498826752L;
	static Category cat = Category.getInstance(RETSGetPayloadListTransaction.class);
    String version = null;

    /**
     * constructor
     */
    public RETSGetPayloadListTransaction() {
        super();
        setRequestType("GetPayloadList");
    }

    public void setId(String str) {
        setRequestVariable("ID", str);
    }



}
