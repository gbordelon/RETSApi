package org.realtor.rets.retsapi;

import org.apache.log4j.Category;
import org.realtor.rets.util.AttributeExtracter;
import org.realtor.rets.util.ResourceLocator;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 *  RETSSearchTransaction.java
 *
 *  @author jbrush
 *  @version 1.0
 */
public class RETSSearchTransaction extends RETSTransaction {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3476963630741706452L;

	static Category cat = Category.getInstance(RETSSearchTransaction.class);

    //Required Arguments
    protected static final String SEARCHTYPE = "SearchType";
    protected static final String SEARCHCLASS = "Class";
    protected static final String SEARCHQUERY = "Query";
    protected static final String SEARCHQUERYTYPE = "QueryType";

    // Optional Arguments
    protected static final String SEARCHCOUNT = "Count";
    protected static final String SEARCHFORMAT = "Format";
    protected static final String SEARCHLIMIT = "Limit";
    protected static final String SEARCHOFFSET = "Offset";
    protected static final String SEARCHSELECT = "Select";
    protected static final String SEARCHDELIMITER = "DELIMITER";
    protected static final String SEARCHRESTRICTEDINDICATOR = "RestrictedIndicator";
    protected static final String SEARCHSTANDARDNAMES = "StandardNames";
    protected static final String SEARCHPAYLOAD = "Payload";
    private String version = null;
    private String records=null;
    private Boolean countPresent=false;

    public RETSSearchTransaction() {
        super();
        setRequestType("Search");
        setSearchQueryType("DMQL");
    }

    public void setResponse(String body) {
        super.setResponse(body);

        HashMap<String, HashMap<String, String>> hm = this.getAttributeHash(body);
        processXML(hm);
    }
    
    public Boolean getCountPresent(){
    	return countPresent;
    }
    
    public void setCountPresent(Boolean present){
    	this.countPresent=present;
    }

    ///////////////////////////////////////////////////////////////////////

    /*    void processCompact(String body) {
            processCountTag(body);
            processDelimiterTag(body);
            processColumnTag(body);
            processCompactData(body);
            processMaxRowTag(body);
        } */
    void processXML(HashMap<String, HashMap<String, String>> hash) {
        if (hash == null) {
            return;
        }
        
        processCountTag( hash.get("COUNT"));
        processXMLData( hash.get("BODY"));
        processMaxRowTag( hash.get("MAXROWS"));
        processDelimiterTag( hash.get("DELIMITER"));
    }

    private HashMap<String, HashMap<String, String>> getAttributeHash(String body) {
        AttributeExtracter ae = new AttributeExtracter();
        DefaultHandler h = ae;

        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser p = spf.newSAXParser();
            ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes());

            p.parse(bais, h, "file:/" + ResourceLocator.locate("dummy.dtd"));
        } catch (Exception e) {
            cat.warn(e, e);

            return null;
        }

        return ae.getHash();
    }

    void processCountTag(HashMap<String, String> hash) {
        if (hash == null) {
            return;
        }
        setCountPresent(true);
        String records = hash.get("Records");
        if ( records == null) {
            records = hash.get("records");
        }
        if ( records == null) {
            records = hash.get("RECORDS");
        }
        setRecords( records);
        
    }

    void processDelimiterTag(HashMap<String, String> hash) {
        if (hash == null) {
            return;
        }

        String delim = hash.get("value");

        if (delim == null) {
            delim = hash.get("VALUE");
        }

        if (delim == null) {
            delim = hash.get("Value");
        }

        setSearchDelimiter(delim);
    }

    void processColumnTag(HashMap<String, String> hash) {
    }

    void processCompactData(HashMap<String, String> hash) {
    }

    void processXMLData(HashMap<String, String> hash) {
    }

    void processMaxRowTag(HashMap<String, String> hash) {
        if (hash == null) {
            setResponseVariable("MAXROWS", "true");
        }

        //  else
        //    setResponseVariable("MAXROWS", "false");
    }

    ///////////////////////////////////////////////////////////////////////
    public void setSearchType(String searchType) {
        setRequestVariable(SEARCHTYPE, searchType);
    }

    public String getSearchType() {
        return getRequestVariable(SEARCHTYPE);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setSearchClass(String searchClass) {
        setRequestVariable(SEARCHCLASS, searchClass);
    }

    public String getSearchClass() {
        return getRequestVariable(SEARCHCLASS);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setSearchQuery(String searchQuery) {
        setRequestVariable(SEARCHQUERY, searchQuery);
    }

    public String getSearchQuery() {
        return getRequestVariable(SEARCHQUERY);
    }
    
    ///////////////////////////////////////////////////////////////////////
    public void setSearchPayload(String searchPayload) {
        setRequestVariable(SEARCHPAYLOAD, searchPayload);
    }

    public String getSearchPayload() {
        return getRequestVariable(SEARCHPAYLOAD);
    }
    

    ///////////////////////////////////////////////////////////////////////
    public void setPayload(String searchPayload) {
        setRequestVariable(SEARCHPAYLOAD, searchPayload);
    }

    public String getPayload() {
        return getRequestVariable(SEARCHPAYLOAD);
    }

    public void setQuery(String searchQuery) {
        setRequestVariable(SEARCHQUERY, searchQuery);
    }

    public String getQuery() {
        return getRequestVariable(SEARCHQUERY);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setSearchQueryType(String searchQueryType) {
        setRequestVariable(SEARCHQUERYTYPE, searchQueryType);
    }

    public String getSearchQueryType() {
        return getRequestVariable(SEARCHQUERYTYPE);
    }

    public void setQueryType(String searchQueryType) {
        setRequestVariable(SEARCHQUERYTYPE, searchQueryType);
    }

    public String getQueryType() {
        return getRequestVariable(SEARCHQUERYTYPE);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setSearchCount(String value) {
        setRequestVariable(SEARCHCOUNT, value);
    }

    public String getSearchCount() {
        return getRequestVariable(SEARCHCOUNT);
    }

    public void setCount(String value) {
        setRequestVariable(SEARCHCOUNT, value);
    }

    public String getCount() {
        return getRequestVariable(SEARCHCOUNT);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setSearchFormat(String value) {
        setRequestVariable(SEARCHFORMAT, value);
    }

    public String getSearchFormat() {
        return getRequestVariable(SEARCHFORMAT);
    }

    public void setFormat(String value) {
        setRequestVariable(SEARCHFORMAT, value);
    }

    public String getFormat() {
        return getRequestVariable(SEARCHFORMAT);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setSearchLimit(String value) {
        setRequestVariable(SEARCHLIMIT, value);
    }

    public String getSearchLimit() {
        return getRequestVariable(SEARCHLIMIT);
    }

    public void setLimit(String value) {
        setRequestVariable(SEARCHLIMIT, value);
    }

    public String getLimit() {
        return getRequestVariable(SEARCHLIMIT);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setSearchOffset(String value) {
        setRequestVariable(SEARCHOFFSET, value);
    }

    public String getSearchOffset() {
        return getRequestVariable(SEARCHOFFSET);
    }

    public void setOffset(String value) {
        setRequestVariable(SEARCHOFFSET, value);
    }

    public String getOffset() {
        return getRequestVariable(SEARCHOFFSET);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setSearchSelect(String value) {
        setRequestVariable(SEARCHSELECT, value);
    }

    public String getSearchSelect() {
        return getRequestVariable(SEARCHSELECT);
    }

    public void setSelect(String value) {
        setRequestVariable(SEARCHSELECT, value);
    }

    public String getSelect() {
        return getRequestVariable(SEARCHSELECT);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setSearchDelimiter(String value) {
        setRequestVariable(SEARCHDELIMITER, value);
    }

    public String getSearchDelimiter() {
        return getRequestVariable(SEARCHDELIMITER);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setSearchRestrictedIndicator(String value) {
        setRequestVariable(SEARCHRESTRICTEDINDICATOR, value);
    }

    public String getSearchRestrictedIndicator() {
        return getRequestVariable(SEARCHRESTRICTEDINDICATOR);
    }

    ///////////////////////////////////////////////////////////////////////
    public void setSearchStandardNames(String value) {
        setRequestVariable(SEARCHSTANDARDNAMES, value);
    }

    public String getSearchStandardNames() {
        return getRequestVariable(SEARCHSTANDARDNAMES);
    }

    public void setStandardNames(String value) {
        setRequestVariable(SEARCHSTANDARDNAMES, value);
    }

    public String getStandardNames() {
        return getRequestVariable(SEARCHSTANDARDNAMES);
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
    public void setRecords(String records) {
        this.records = records;
    }
    
    public String getRecords() {
        return records;
    }

    public String getVersion() {
        return version;
    }
}
