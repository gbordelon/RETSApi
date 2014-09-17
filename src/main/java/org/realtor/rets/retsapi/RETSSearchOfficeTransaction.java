package org.realtor.rets.retsapi;

import org.apache.log4j.*;


/**
 *        RETSSearchOfficeTransaction.java
 *      Performs a getOffice Transaction
 *
 *        @author        jbrush
 *        @version 1.0
 */
public class RETSSearchOfficeTransaction extends RETSSearchTransaction {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6610016057550105887L;
	static Category cat = Category.getInstance(RETSSearchOfficeTransaction.class);

    /**  Creates new a RETSSearchOfficeTransaction
     *
     */
    public RETSSearchOfficeTransaction() {
        super();
        setSearchType("Office");

        setSearchClass("Office");
//        setSearchClass("OFF");
    }
}
