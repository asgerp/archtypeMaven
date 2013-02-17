/*
 * Copyright 2012 Net4Care, www.net4care.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
 
package org.net4care.storage.delegate; 
 
import org.net4care.storage.*; 
import org.net4care.storage.queries.*; 
import org.net4care.utility.Constants; 
import org.net4care.utility.Net4CareException; 
import org.net4care.utility.Utility; 
 
import java.sql.*; 
 
import org.w3c.dom.Document; 
import java.util.List; 
import java.util.ArrayList; 
import org.apache.log4j.Logger; 
 
/** 
 * An XDS repository AND ObservationCache that stores stuff in a simple SQLite 
 * database. Not for production quality usage, but fine for small scale 
 * experiments. 
 *  
 * @author Net4Care. Henrik Baerbak Christensen. 
 *  
 *         The Schema is just three tables: Registry, Repository, Cache 
 *  
 *         The registry + repository mimic the XDS architecture: MetaData and a 
 *         reference to the real document are stored in registry while the 
 *         repository stores the real HL7 document (as an XML string). The two 
 *         are represented by two tables (registry,repository) where a foreign 
 *         key (xmlId) in the registry associate the primary key in the 
 *         repository. 
 *  
 *         The observation cache mimics this basic table structure, except the 
 *         tables are called registryCache and cache respectively. 
 *  
 *         Table: registry | obsID | cpr | xmlID | timestamp | codeSystem | 
 *         codes | organizationID | treatmentID 
 *  
 *         xmlID is foreign key into the Repository that contains the actual XML 
 *         document 
 *  
 *         obsID is primary key, assigned by the database cpr is the CPR of 
 *         person timestamp is the UNIX long integer timestamp (milliseconds 
 *         since Jan 01 1970) codeSystem + codes defines the types of 
 *         observations stored in the XML. codeSystem is the HL7 defined OID for 
 *         the encoding of 'codes' codes is a string, concatenating codes 
 *         delimited by "|" bar-character. At least one code must be present 
 *         Example: An observation stores both FVC and FEV1 then 
 *         codes=|FVC|FEV1| organizationID is the id of the organization 
 *         responsible for the clinical measurements being taken (stewardship in 
 *         HL7 terms) treatmentID is the id of the treatment in the EPJ system 
 *         that is the diagnosis being the reason for the measurements taken 
 *  
 *  
 *         Table: repository | xmlID | xmldoc | 
 *  
 *         xmlID is the primary key, and foreign key for Registry.xmlID xmldoc 
 *         is the actual string containing the HL7 document. 
 *  
 *         Table: registryCache Same layout as Registry except the foreign key 
 *         is named 'obsRefId' 
 *  
 *         Table: cache | obsRefID | serializedform | 
 *  
 *         obsRefID is the primary key, and foreign key for 
 *         RegistryCache.obsRefID serializedform is the string with the 
 *         observation in its serialized form (JSON or ...) 
 *  
 *         Hint: You can use the Windows executable sqlite3.exe to inspect the 
 *         db. 
 *  
 *         The JDBC driver is SqliteJDBC v056 which is a compiled version of 
 *         SQLite 3.6.14.2. Origin: http://www.zentus.com/sqlitejdbc/ 
 *  
 *         The Maven version uses this dependency 
 *  
 *         <dependency> <groupId>org.xerial</groupId> 
 *         <artifactId>sqlite-jdbc</artifactId> <version>3.6.16</version> 
 *         <scope>provided</scope> </dependency> 
 */ 
	 import com.apkc.archtype.quals.*;
	 @ArchTypeComponent(
	           patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{3}")}
	           ) 
public class SQLiteXDSRepository implements XDSRepository, ObservationCache { 
	private static final String DEFAULT_DB = "xds.db"; 
	private Connection conn; 
	private Statement stat; 
	private PreparedStatement insertIntoRepository, insertIntoRegistry, 
			insertIntoCache, insertIntoCacheRegistry; 
 
	private Logger logger = Logger.getLogger(SQLiteXDSRepository.class); 
 
	/** 
	 * Create the SQLite based XDS registry and repository. 
	 *  
	 * @param pathAndNameOfDatabase 
	 *            the filename of the database. You may either give a) the empty 
	 *            string in which case SQLite creates a temporary in-memory 
	 *            database b) just give a filename like 'test.db' in which case 
	 *            the database is stored in the 'current directory' or c) the 
	 *            full path and file name (use unix path separators even on 
	 *            windows like "D:/tmp/xds.db". 
	 */ 
	public SQLiteXDSRepository(String pathAndNameOfDatabase) 
			throws Net4CareException { 
		String className = "org.sqlite.JDBC"; 
		String url = "jdbc:sqlite:" + pathAndNameOfDatabase; 
		try { 
			Class.forName(className); 
			conn = DriverManager.getConnection(url); 
			stat = conn.createStatement(); 
		} catch (ClassNotFoundException e) { 
			logger.error("Error. The class name " + className 
					+ " was not found", e); 
			throw new Net4CareException(Constants.ERROR_INTERNAL_NET4CARE, 
					"Error. The class name " + className + " was not found"); 
		} catch (SQLException e) { 
			logger.error("Error in accessing the database (url: \"" + url 
					+ "\").", e); 
			throw new Net4CareException(Constants.ERROR_DATABASE_CONNECTION, 
					"Error in accessing the database (url: \"" + url + "\")."); 
		} 
 
	} 
 
	/** 
	 * Create the SQLite standard XDS and observation cache Data stored in 
	 * DEFAULT_DB filename. 
	 *  
	 * @throws Net4CareException 
	 */ 
	public SQLiteXDSRepository() throws Net4CareException { 
		this(SQLiteXDSRepository.DEFAULT_DB); 
	} 
 
	/** 
	 * Connect to the database. If the tables do not exist they are created. 
	 *  
	 */ 
	@Override 
	public void connect() throws Net4CareException { 
		try { 
			stat.executeUpdate("create table if not exists registry (obsID integer primary key" 
					+ ", cpr varchar(12), xmlID integer" 
					+ ", timestamp integer, codeSystem varchar(32)" 
					+ ", codes text not null" 
					+ ", organizationID text, treatmentID text" 
					+ ", foreign key(xmlID) references repository(xmlID) );"); 
			stat.executeUpdate("create table if not exists repository (xmlID integer primary key, xmldoc text);"); 
 
			stat.executeUpdate("create table if not exists registryCache (obsID integer primary key" 
					+ ", cpr varchar(12), obsRefID integer" 
					+ ", timestamp integer, codeSystem varchar(32)" 
					+ ", codes text not null" 
					+ ", organizationID text, treatmentID text" 
					+ ", foreign key(obsRefID) references cache(obsRefID) );"); 
			stat.executeUpdate("create table if not exists cache (obsRefID integer primary key, serializedform text);"); 
 
			// XDS insert operations... 
			insertIntoRepository = conn 
					.prepareStatement("insert into repository values(null,?);"); 
			insertIntoRegistry = conn 
					.prepareStatement("insert into registry values (null, ?, last_insert_rowid(), ?,?, ?, ?, ?);"); 
 
			// Cache insert operations... 
			insertIntoCache = conn 
					.prepareStatement("INSERT INTO cache VALUES(null,?);"); 
			insertIntoCacheRegistry = conn 
					.prepareStatement("INSERT INTO registryCache VALUES (null, ?, last_insert_rowid(), ?,?, ?, ?, ?);"); 
 
		} catch (SQLException e) { 
			logger.error("Error in opening database.", e); 
			throw new Net4CareException(Constants.ERROR_DATABASE_CONNECTION, 
					"Error in opening database."); 
		} 
 
	} 
 
	@Override 
	public void disconnect() throws Net4CareException { 
		try { 
			conn.close(); 
		} catch (SQLException e) { 
			logger.error("Error in closing database.", e); 
			throw new Net4CareException(Constants.ERROR_DATABASE_CONNECTION, 
					"Error in closing database."); 
		} 
	} 
 
	@Override 
	public synchronized void provideAndRegisterDocument(RegistryEntry metadata, 
			Document xmlDocument) throws Net4CareException { 
		String asString = org.net4care.utility.Utility 
				.convertXMLDocumentToString(xmlDocument); 
 
		try { 
			insertIntoRepository.setString(1, asString); 
 
			prepareRegistryRowInsert(insertIntoRegistry, metadata); 
 
			insertIntoRepository.addBatch(); 
			insertIntoRegistry.addBatch(); 
			// Group the two into a transaction 
			conn.setAutoCommit(false); 
			insertIntoRepository.executeBatch(); 
			insertIntoRegistry.executeBatch(); 
			conn.setAutoCommit(true); 
 
		} catch (SQLException e) { 
			logger.error( 
					"Error in inserting XML document in SQLite registry and repository.", 
					e); 
			throw new Net4CareException(Constants.ERROR_DATABASE_CONNECTION, 
					"Error in closing database."); 
		} 
	} 
 
	/** 
	 * The insertion of a registry row is identical for both cache and XDS, and 
	 * defined by this method. 
	 *  
	 * @param insertIntoRegistry 
	 *            the prepared statement to add values to 
	 * @param metadata 
	 *            the metadata structure that contains the data 
	 * @throws SQLException 
	 */ 
	private void prepareRegistryRowInsert(PreparedStatement insertIntoRegistry, 
			RegistryEntry metadata) throws SQLException { 
		insertIntoRegistry.setString(1, metadata.getCpr()); 
		insertIntoRegistry.setLong(2, metadata.getTimestamp()); 
		insertIntoRegistry.setString(3, metadata.getCodeSystem()); 
 
		String concatenatedCodes = Utility 
				.convertCodeListToBarSeparatedString(metadata 
						.getCodesOfValuesMeasured()); 
		insertIntoRegistry.setString(4, concatenatedCodes); 
		insertIntoRegistry.setString(5, metadata.getOrganizationId()); 
		insertIntoRegistry.setString(6, metadata.getTreatmentId()); 
	} 
 
	@Override 
	public synchronized List<Document> retrieveDocumentSet(XDSQuery query) 
			throws Net4CareException { 
		List<String> xmlset; 
		List<Document> docset = new ArrayList<Document>(); 
		xmlset = retrieveDocumentSetAsXMLString(query); 
		for (String xml : xmlset) { 
			docset.add(Utility.convertXMLStringToDocument(xml)); 
		} 
 
		return docset; 
	} 
 
	@Override 
	public synchronized List<String> retrieveDocumentSetAsXMLString(XDSQuery query) 
			throws Net4CareException { 
		List<String> returnValue = new ArrayList<String>(); 
		returnValue = executeStringRetrieveFromReferencedTable(query, 
				"registry", "repository", "xmlID", "xmldoc"); 
		return returnValue; 
	} 
 
	@Override 
	public void utterlyEmptyAllContentsOfTheDatabase() throws Net4CareException { 
		try { 
			dropAllTables(); 
			connect(); 
		} catch (SQLException e) { 
			logger.error("Error in cleaning content of database.", e); 
			throw new Net4CareException(Constants.ERROR_DATABASE_CONNECTION, 
					"Error in cleaning content of database."); 
		} 
	} 
 
	private void dropAllTables() throws SQLException { 
		stat.executeUpdate("drop table if exists registry;"); 
		stat.executeUpdate("drop table if exists repository;"); 
		stat.executeUpdate("drop table if exists registryCache;"); 
		stat.executeUpdate("drop table if exists cache;"); 
	} 
 
	@Override 
	public synchronized void provideAndRegisterObservation( 
			RegistryEntry metadata, String serializedFormOfObservation) 
			throws Net4CareException { 
		try { 
			insertIntoCache.setString(1, serializedFormOfObservation); 
 
			prepareRegistryRowInsert(insertIntoCacheRegistry, metadata); 
			insertIntoCache.addBatch(); 
			insertIntoCacheRegistry.addBatch(); 
			// Commit as a transaction 
			conn.setAutoCommit(false); 
			insertIntoCache.executeBatch(); 
			insertIntoCacheRegistry.executeBatch(); 
			conn.setAutoCommit(true); 
		} catch (SQLException e) { 
			logger.error( 
					"Error in inserting observation string in SQLite observation cache.", 
					e); 
			throw new Net4CareException(Constants.ERROR_DATABASE_CONNECTION, 
					"Error in inserting observation string in SQLite observation cache."); 
		} 
	} 
 
	@Override 
	public synchronized List<String> retrieveObservationSet(XDSQuery query) 
			throws Net4CareException { 
		List<String> returnValue = executeStringRetrieveFromReferencedTable( 
				query, "registryCache", "cache", "obsRefID", "serializedform"); 
		return returnValue; 
	} 
 
	/** 
	 * The queries on HL7 documents and Observations are identical except which 
	 * table names and attributes names the perform the SELECT on. 
	 *  
	 * @param query 
	 *            The query to perform 
	 * @param external_table_name 
	 *            the name of the storage table (xmlDoc or cache) 
	 * @param key_attribute_name 
	 *            name of the attribute used as foreign key in registry and 
	 *            primary key in the storage table 
	 * @param data_attribute_name 
	 *            name of the attribute in the storage table that stores the 
	 *            actual string contents 
	 * @return List of strings that resulted from the query 
	 * @throws Net4CareException 
	 */ 
	private List<String> executeStringRetrieveFromReferencedTable( 
			XDSQuery query, String registry_table_name, 
			String external_table_name, String key_attribute_name, 
			String data_attribute_name) throws Net4CareException { 
		ResultSet rs; 
		String sqlQuery; 
		List<String> returnValue = new ArrayList<String>(); 
 
		String sqlQueryHeader = "SELECT " + registry_table_name + ".cpr, " 
				+ external_table_name + "." + key_attribute_name + ", " 
				+ external_table_name + "." + data_attribute_name + " " 
				+ "FROM " + registry_table_name + " JOIN " 
				+ external_table_name + " on " + registry_table_name + "." 
				+ key_attribute_name + " = " + external_table_name + "." 
				+ key_attribute_name + " "; 
 
		// CONVERT QUERY TO SQL SELECT STATEMENT 
		// Queries on Person and Time interval only 
		if (query.getClass() == XDSQueryPersonTimeInterval.class) { 
			XDSQueryPersonTimeInterval ptQuery = (XDSQueryPersonTimeInterval) query; 
 
			sqlQuery = sqlQueryHeader + "WHERE cpr = '" + ptQuery.getCpr() 
					+ "' " + "AND timestamp between " 
					+ ptQuery.getBeginTimeInterval() + " and " 
					+ ptQuery.getEndTimeInterval() + " " + ";"; 
			// Queries on clinical quantity type as well 
		} else if (query.getClass() == XDSQueryPersonTimeIntervalType.class) { 
			XDSQueryPersonTimeIntervalType pttQuery = (XDSQueryPersonTimeIntervalType) query; 
 
			sqlQuery = sqlQueryHeader + "WHERE cpr = '" + pttQuery.getCpr() 
					+ "' " + "AND timestamp between " 
					+ pttQuery.getBeginTimeInterval() + " and " 
					+ pttQuery.getEndTimeInterval() + " " 
					+ "AND codeSystem = '" + pttQuery.getCodeSystem() + "' "; 
			String[] observationTypes = pttQuery.getObservationTypes(); 
			for (int i = 0; i < observationTypes.length; i++) { 
				sqlQuery += "AND codes LIKE '%|" + observationTypes[i] + "|%' "; 
			} 
			sqlQuery += ";"; 
			// Queries on the id of the treatment 
		} else if (query.getClass() == XDSQueryPersonTimeIntervalTreatment.class) { 
			XDSQueryPersonTimeIntervalTreatment ptQuery = (XDSQueryPersonTimeIntervalTreatment) query; 
 
			sqlQuery = sqlQueryHeader + "WHERE cpr = '" + ptQuery.getCpr() 
					+ "' " + "AND timestamp between " 
					+ ptQuery.getBeginTimeInterval() + " and " 
					+ ptQuery.getEndTimeInterval() + " " 
					+ "AND treatmentID = '" + ptQuery.getTreatmentId() + "' " 
					+ ";"; 
 
		} else { 
			logger.error("The query of type " 
					+ query.getClass().getSimpleName() + " is not defined..."); 
			throw new Net4CareException(Constants.ERROR_INVALID_QUERYTYPE, 
					"The OBS query of type " + query.getClass().getSimpleName() 
							+ " is not defined..."); 
		} 
 
		// System.out.println( "SQLQuery = "+sqlQuery); 
 
		// AND NEXT EXECUTE QUERY AND ADD XML DOCUMENTS TO THE RETURN LIST 
		try { 
			rs = stat.executeQuery(sqlQuery); 
			while (rs.next()) { 
				returnValue.add(rs.getString(data_attribute_name)); 
			} 
			rs.close(); 
		} catch (SQLException e) { 
			logger.error("HL7 query failed (SQL=" + sqlQuery, e); 
			throw new Net4CareException(Constants.ERROR_DATABASE_CONNECTION, 
					"HL7 query failed (SQL=" + sqlQuery); 
		} 
		return returnValue; 
	} 
 
	public String toString() { 
		return "SQLiteXDSRepository and ObservationCache."; 
	} 
 
} 
