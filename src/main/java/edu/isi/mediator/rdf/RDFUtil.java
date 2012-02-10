// __COPYRIGHT_START__
//
// Copyright 2009 University of Southern California. All Rights Reserved.
//
// __COPYRIGHT_END__

package edu.isi.mediator.rdf;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.isi.mediator.gav.main.MediatorException;
import edu.isi.mediator.gav.util.MediatorLogger;

public class RDFUtil {

	private static final MediatorLogger logger = MediatorLogger.getLogger(RDFUtil.class.getName());

	/**
	 * Write namespaces to output.
	 */
	static public void setNamespace(Map<String,String> sourceNamespaces, Map<String,String> ontologyNamespaces, 
			PrintWriter outWriter) throws MediatorException{
		Iterator<String> keys1 = ontologyNamespaces.keySet().iterator();
		while(keys1.hasNext()){
			String key = keys1.next();
			String prefix = "@prefix " + key + ": <" + ontologyNamespaces.get(key) + "> .";
			outWriter.println(prefix);
		}
		Iterator<String> keys2 = sourceNamespaces.keySet().iterator();
		while(keys2.hasNext()){
			String key = keys2.next();
			String prefix = "@prefix " + key + ": <" + sourceNamespaces.get(key) + "> .";
			outWriter.println(prefix);
		}
		//outWriter.println();
	}


	/**
	 * Return the prefix for a given namespace IF the map contains only one entry.
	 * @param namespaces
	 * 		map containing namespaces
	 * @return
	 * 		the prefix for a given namespace IF the map contains only one entry
	 * 		null otherwise
	 */
	static public String getPrefix(Map<String,String> namespaces){
		Set<Entry<String,String>> entries = namespaces.entrySet();
		if(entries.size()==1){
			//return the unique key
			Iterator<Entry<String,String>> it = entries.iterator();
			Entry<String,String> e = it.next();
			return (String)e.getKey();
		}
		else{
			return null;
		}
	}

	/**
	 * Returns the table name from a given URI.
	 * Table name appears as: DBNAME.TableName/ or /TableName/
	 * @param uri
	 * 		the uri (it is a URI generated by D2R)
	 * @return
	 * @throws MediatorException
	 */
	static public String getTableName(String uri) throws MediatorException{
		
		String tokens[] = uri.split("/");
		
		if(tokens.length<2)
			throw new MediatorException("The URI must contain a table name either as DBNAME.TableName/ or /TableName/.");
		
		String tableName = tokens[tokens.length-2];
		
		logger.debug("TableName=" + tableName);
		int ind = tableName.indexOf(".");

		if(ind>0){
			tableName = tableName.substring(ind+1);
		}
		logger.debug("TableName=" + tableName);
		
		return tableName;
	}

	/**
	 * Returns the table name from a given URI.
	 * Table name appears as: DBNAME.TableName/
	 * @param uri
	 * 		the uri (it is a URI generated by D2R)
	 * @return
	 * @throws MediatorException
	 */
	static public String getTableNameOracle(String uri) throws MediatorException{
		
		//the "." is the start of the table name
		int ind1 = uri.lastIndexOf(".");
		//System.out.println("getTableName:" + uri + " ind=" + ind1);
		if(ind1<0){
			throw new MediatorException("The URI must contain a table name that starts with \".\"");
		}
		int ind2 = uri.indexOf("/", ind1);
		if(ind2<0){
			throw new MediatorException("The subject URI must contain a table name that starts with \".\" and ends with \"/\". /DBName.TableName/");
		}
		String tableName = uri.substring(ind1+1, ind2);
		//System.out.println("TableName:" + tableName);
		
		return tableName;
	}

	/**
	 * Returns the database name from a given URI.
	 * database name appears as: /DBNAME.TableName/
	 * @param uri
	 * 		the uri (it is a URI generated by D2R)
	 * @return
	 * @throws MediatorException
	 */
	static public String getDatabaseName(String uri) throws MediatorException{
		
		String tokens[] = uri.split("/");
		
		if(tokens.length<2)
			throw new MediatorException("The URI must contain a table name either as DBNAME.TableName/ or /TableName/.");
		
		String dbName = tokens[tokens.length-2];
		
		logger.debug("TableName=" + dbName);
		int ind = dbName.indexOf(".");

		if(ind>0){
			dbName = dbName.substring(0, ind) + ".";
		}
		else dbName = "";
		
		logger.debug("Database name=" + dbName);
		
		return dbName;

	}

	/**
	 * Returns the database name from a given URI.
	 * database name appears as: /DBNAME.TableName/
	 * @param uri
	 * 		the uri (it is a URI generated by D2R)
	 * @return
	 * @throws MediatorException
	 */
	static public String getDatabaseNameOracle(String uri) throws MediatorException{
		
		//the "." is the start of the table name
		int ind1 = uri.lastIndexOf(".");
		//System.out.println("getTableName:" + uri + " ind=" + ind1);
		if(ind1<0){
			throw new MediatorException("The URI must contain a table name that starts with \".\"");
		}
		//set the database name
		int ind3 = uri.lastIndexOf("/", ind1);
		return uri.substring(ind3+1,ind1);
	}

	/**
	 * Returns a generic symbol.
	 * @param uniqueId
	 * 		uniqueid - usually the time
	 * @param rowId
	 * @param value
	 * @return
	 * 		a generic symbol.
	 */
	static public String gensym(String uniqueId, int rowId, String value){
		return uniqueId + "r" + String.valueOf(rowId) + "_" + value;
	}
	
	static public String escapeQuote(String s){
		s = s.replaceAll("\"", "\\\\\"");
		s = s.replaceAll("\r\n", " ");
		return "\"" + s + "\"";
	}
	

}
