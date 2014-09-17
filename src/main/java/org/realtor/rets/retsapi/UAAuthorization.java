/*
* UAAuthorization.java
*
* Created on April 1, 2008
*/
package org.realtor.rets.retsapi;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;

import java.security.MessageDigest;
import java.math.BigInteger;
/**
*  Computers UA header for RETS 1.7
*
*
* @author  pobrien
* @version 1.0
*/
public class UAAuthorization extends java.lang.Object {

	String version="";
	String product="";
	String password="";
	String sessionId="";
	String requestId="";

	public UAAuthorization(String version, String product, String password, String sessionId, String requestId) {
		this.version = version;
		this.product = product;
		this.password = password;
		this.sessionId = sessionId;
		this.requestId = requestId;
    }

    public String getString() {
	    String a1 = MD5( product+":"+ password);
	    return "Digest "+MD5( a1+":"+requestId+":"+sessionId+":"+version);
	}


	    public String MD5(String s){

			try
			{
				MessageDigest m=MessageDigest.getInstance("MD5");
	       		m.update(s.getBytes(),0,s.length());
	       		return String.format("%032x", new BigInteger(1,m.digest()));
	   		}
	   		catch (Exception e)
	   		{
			   return "";
	   		}
	    }

}
