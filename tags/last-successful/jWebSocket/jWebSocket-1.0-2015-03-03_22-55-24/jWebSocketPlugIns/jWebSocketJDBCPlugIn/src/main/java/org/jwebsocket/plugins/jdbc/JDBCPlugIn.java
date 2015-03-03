//	---------------------------------------------------------------------------
//	jWebSocket - jWebSocket JDBC Plug-In (Community Edition, CE)
//	---------------------------------------------------------------------------
//	Copyright 2010-2014 Innotrade GmbH (jWebSocket.org)
//	Alexander Schulze, Germany (NRW)
//
//	Licensed under the Apache License, Version 2.0 (the "License");
//	you may not use this file except in compliance with the License.
//	You may obtain a copy of the License at
//
//	http://www.apache.org/licenses/LICENSE-2.0
//
//	Unless required by applicable law or agreed to in writing, software
//	distributed under the License is distributed on an "AS IS" BASIS,
//	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//	See the License for the specific language governing permissions and
//	limitations under the License.
//	---------------------------------------------------------------------------
package org.jwebsocket.plugins.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import org.jwebsocket.api.IBasicStorage;
import org.jwebsocket.api.PluginConfiguration;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.config.JWebSocketCommonConstants;
import org.jwebsocket.config.JWebSocketServerConstants;
import org.jwebsocket.kit.PlugInResponse;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.TokenPlugIn;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.storage.ehcache.EhCacheStorage;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.jwebsocket.util.Tools;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Alexander Schulze EhChache
 * @author Rolando Santamaria Maso
 */
public class JDBCPlugIn extends TokenPlugIn {

	private static final Logger mLog = Logging.getLogger();
	// if namespace changed updateSQL client plug-in accordingly!
	private static final String NS_JDBC
			= JWebSocketServerConstants.NS_BASE + ".plugins.jdbc";
	private final static String VERSION = "1.0.0";
	private final static String VENDOR = JWebSocketCommonConstants.VENDOR_CE;
	private final static String LABEL = "jWebSocket JDBCPlugIn";
	private final static String COPYRIGHT = JWebSocketCommonConstants.COPYRIGHT_CE;
	private final static String LICENSE = JWebSocketCommonConstants.LICENSE_CE;
	private final static String DESCRIPTION = "jWebSocket JDBCPlugIn - Community Edition";
	private IBasicStorage mCache = null;
	private final int mConnValTimeout = 300;
	private static ApplicationContext mBeanFactory;
	private static Settings mSettings;

	/**
	 *
	 * @param aConfiguration
	 */
	public JDBCPlugIn(PluginConfiguration aConfiguration) {
		super(aConfiguration);
		if (mLog.isDebugEnabled()) {
			mLog.debug("Instantiating JDBC plug-in...");
		}
		// specify default name space for JDBC plugin
		setNamespace(NS_JDBC);
		mCache = new EhCacheStorage(NS_JDBC);

		try {
			mBeanFactory = getConfigBeanFactory();
			mSettings = (Settings) mBeanFactory.getBean("settings");
			if (null != mSettings) {
				if (mLog.isInfoEnabled()) {
					mLog.info("JDBC plug-in successfully instantiated.");
				}
			} else {
				mLog.error("Database bean could not be loaded properly.");
			}
		} catch (BeansException lEx) {
			mLog.error(lEx.getClass().getSimpleName() + " at JDBC plug-in instantiation: " + lEx.getMessage());
		}
	}

	@Override
	public String getVersion() {
		return VERSION;
	}

	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getVendor() {
		return VENDOR;
	}

	@Override
	public String getCopyright() {
		return COPYRIGHT;
	}

	@Override
	public String getLicense() {
		return LICENSE;
	}

	@Override
	public String getNamespace() {
		return NS_JDBC;
	}

	@Override
	public void processToken(PlugInResponse aResponse, WebSocketConnector aConnector, Token aToken) {
		String lType = aToken.getType();

		if (lType != null) {
			if (lType.equals("querySQL")) {
				// run single native query sql command (select)
				// with returning a result set
				querySQL(aConnector, aToken);
			} else if (lType.equals("updateSQL")) {
				// run single native updateSQL sql command (updateSQL, delete, insert)
				// w/o returning a result set, but number of affected rows
				updateSQL(aConnector, aToken);
			} else if (lType.equals("execSQL")) {
				// run single native updateSQL sql command (updateSQL, delete, insert)
				// w/o returning a result set, but number of affected rows
				execSQL(aConnector, aToken);
			} else if (lType.equals("getNextSeqVal")) {
				// get the next value of a sequence
				getNextSeqVal(aConnector, aToken);
			} else if (lType.equals("select")) {
				// abstract select from database
				select(aConnector, aToken);
				// abstract updateSQL from database
			} else if (lType.equals("update")) {
				update(aConnector, aToken);
				// abstract delete from database
			} else if (lType.equals("delete")) {
				delete(aConnector, aToken);
				// abstract insert into database
			} else if (lType.equals("insert")) {
				insert(aConnector, aToken);
				// abstract start transaction
			} else if (lType.equals("startTA")) {
				startTA(aConnector, aToken);
				// abstract commit transaction
			} else if (lType.equals("commit")) {
				commit(aConnector, aToken);
				// abstract rollback transaction
			} else if (lType.equals("rollback")) {
				rollback(aConnector, aToken);
			} else if (lType.equals("getSecure")) {
				getSecure(aConnector, aToken);
				// run multiple abstract updateSQL sql commands (updateSQL, delete, insert)
			} else if (lType.equals("postSecure")) {
				postSecure(aConnector, aToken);
				// run multiple native query sql commands (select)
			} else if (lType.equals("getSQL")) {
				getSQL(aConnector, aToken);
				// run multiple native updateSQL sql commands (updateSQL, delete, insert)
			} else if (lType.equals("postSQL")) {
				postSQL(aConnector, aToken);
			}
		}
	}

	/**
	 *
	 * @param aToken
	 * @return
	 */
	public NativeAccess getNativeAccess(Token aToken) {
		// connection's alias
		String lAlias = (null == aToken ? null : aToken.getString("alias"));

		if (null == lAlias) {
			return mSettings.getNativeAccess();
		} else {
			return mSettings.getNativeAccess(lAlias);
		}
	}

	/**
	 *
	 * @param aToken
	 * @return
	 */
	public DataSource getNativeDataSource(Token aToken) {
		return getNativeAccess(aToken).getDataSource();
	}

	/**
	 * Get the default native data source
	 *
	 * @return
	 */
	public Class<JDBCTools> getJDBCTools() {
		return JDBCTools.class;
	}

	@Override
	public Token invoke(WebSocketConnector aConnector, Token aToken) {
		String lType = aToken.getType();
		String lNS = aToken.getNS();

		if (lType != null && getNamespace().equals(lNS)) {
			if (lType.equals("getNextSeqVal")) {
				return getNextSeqVal(aToken);
			} else if (lType.equals("updateSQL")) {
				return updateSQL(aToken);
			} else if (lType.equals("execSQL")) {
				return execSQL(aToken);
			} else if (lType.equals("querySQL")) {
				return query(aToken);
			}
		}
		return null;
	}

	private void querySQL(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'querySQL'...");
		}

		// check if user is allowed to run 'querySQL' command
		if (!hasAuthority(aConnector, NS_JDBC + ".querySQL")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			return;
		}

		// run query
		Token lResToken = query(aToken);
		// send response to requester
		lServer.sendToken(aConnector, lResToken);
	}

	private Token mCheckDataSource(Token aToken) {
		TokenServer lServer = getServer();
		Token lResToken;
		if (null == getNativeAccess(aToken)) {
			String lAlias = aToken.getString("alias");
			lResToken = lServer.createErrorToken(aToken,
					-1, "No database connection for alias '"
					+ (null == lAlias ? "default" : lAlias) + "' available.");
		} else {
			lResToken = lServer.createResponse(aToken);
		}
		return lResToken;
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 */
	private Token query(Token aToken) {
		Token lResToken = mCheckDataSource(aToken);
		if (0 != lResToken.getInteger("code")) {
			return lResToken;
		}

		// load SQL query string
		String lSQL = aToken.getString("sql");
		String lAlias = aToken.getString("alias");

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'query' for alias '"
					+ (null == lAlias ? "default" : lAlias) + "', SQL '"
					+ (null == lSQL ? "[no query passed]" : lSQL.replace("\n", "\\n").replace("\r", "\\r"))
					+ "'...");
		}

		// load SQL script
		List<Object> lScript = aToken.getList("script");
		// load expiration, default is no cache (expiration = 0)
		Integer lExpiration = aToken.getInteger("expiration", 0);

		Token lSQLResponse;
		List<String> lDetails = new FastList<String>();
		List<Map> lResultSets = new FastList<Map>();
		lResToken.setList("details", lDetails);
		lResToken.setList("resultSets", lResultSets);

		if (lScript != null) {
			for (Object lSQLn : lScript) {
				lSQLResponse = getNativeAccess(aToken).query((String) lSQLn);
				Map<String, Object> lResultSet = new FastMap<String, Object>();
				lResultSet.put("colcount", lSQLResponse.getInteger("colcount", -1));
				lResultSet.put("rowcount", lSQLResponse.getInteger("rowcount", -1));
				lResultSet.put("columns", lSQLResponse.getList("columns"));
				lResultSet.put("data", lSQLResponse.getList("data"));
				lResultSets.add(lResultSet);
			}
		}
		if (lSQL != null) {
			lSQLResponse = getNativeAccess(aToken).query(lSQL);
			Integer lCode = lSQLResponse.getInteger("code");
			if (0 == lCode) {
				lResToken.setInteger("colcount", lSQLResponse.getInteger("colcount", -1));
				lResToken.setInteger("rowcount", lSQLResponse.getInteger("rowcount", -1));
				lResToken.setList("columns", lSQLResponse.getList("columns"));
				lResToken.setList("data", lSQLResponse.getList("data"));
			} else {
				lResToken.setInteger("code", lCode);
				lResToken.setString("msg", lSQLResponse.getString("msg"));
			}
		}
		// send response to requester
		return lResToken;
	}

	private Token getNextSeqVal(Token aToken) {
		Token lResToken = mCheckDataSource(aToken);
		if (0 != lResToken.getInteger("code")) {
			return lResToken;
		}

		String lSequence = aToken.getString("sequence");
		Integer lCount = aToken.getInteger("count", 1);

		if (lSequence != null) {
			Map<String, String> lVars = new FastMap<String, String>();
			lVars.put("sequence", lSequence);
			List<Integer> lValues = new FastList<Integer>();
			String lErrMsg = null;
			for (int lValIdx = 0; lValIdx < lCount; lValIdx++) {
				String lQuery = Tools.expandVars(getNativeAccess(aToken).getSelectSequenceSQL(),
						lVars, Tools.EXPAND_CASE_SENSITIVE);
				Token lPKToken = getNativeAccess(aToken).query(lQuery);
				if (0 == lPKToken.getInteger("code")) {
					Number lNextSeqVal;
					List lRows = lPKToken.getList("data");
					if (lRows != null) {
						List lFields = (List) lRows.get(0);
						if (lFields != null) {
							lNextSeqVal = (Number) lFields.get(0);
							lValues.add(lNextSeqVal.intValue());
						}
					}
				} else {
					lErrMsg = lPKToken.getString("msg");
					break;
				}
			}
			if (null == lErrMsg && lValues.size() > 0) {
				lResToken.setInteger("code", 0);
				lResToken.setList("values", lValues);
			} else {
				lResToken.setInteger("code", -1);
				lResToken.setString("msg", "value could not be obtained: " + lErrMsg);
			}
		} else {
			lResToken.setInteger("code", -1);
			lResToken.setString("msg", "no sequence given");
		}
		return lResToken;
	}

	private void getNextSeqVal(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();
		// send response to requester
		lServer.sendToken(aConnector, getNextSeqVal(aToken));
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 */
	private Token updateSQL(Token aToken) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'updateSQL'...");
		}
		Token lResToken = mCheckDataSource(aToken);
		if (0 != lResToken.getInteger("code")) {
			return lResToken;
		}
		TokenServer lServer = getServer();
		// load SQL string
		String lSQL = aToken.getString("sql");
		List<Object> lScript = aToken.getList("script");

		Token lSQLResult;
		List<String> lDetails = new FastList<String>();
		List<Integer> lRowsAffected = new FastList<Integer>();
		lResToken.setList("details", lDetails);
		lResToken.setList("rowsAffected", lRowsAffected);

		// first execute SQL script if such passed
		if (lScript != null) {
			for (Object lSQLn : lScript) {
				lSQLResult = getNativeAccess(aToken).update((String) lSQLn);
				if (lSQLResult.getInteger("code", 0) != 0) {
					lResToken.setInteger("code", -1);
					lResToken.setString("msg", "Update error. Please refer to 'details' field.");
					lDetails.add(lSQLn + ": " + lSQLResult + "\n");
				}
				lRowsAffected.add(lSQLResult.getInteger("rowsAffected", -1));
			}
		}
		// then execute single SQL if such passed
		if (lSQL != null) {
			lSQLResult = getNativeAccess(aToken).update(lSQL);
			if (lSQLResult.getInteger("code", 0) != 0) {
				lResToken.setInteger("code", -1);
				lResToken.setString("msg", "Update error. Please refer to 'details' field.");
				lDetails.add(lSQL + ": " + lSQLResult + "\n");
			}
			lRowsAffected.add(lSQLResult.getInteger("rowsAffected", -1));
		}

		return lResToken;
	}

	private void updateSQL(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		// check if user is allowed to run 'updateSQL' command
		if (!hasAuthority(aConnector, NS_JDBC + ".updateSQL")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			return;
		}

		Token lResToken = updateSQL(aToken);

		// send response to requester
		lServer.sendToken(aConnector, lResToken);
	}

	private Token execSQL(Token aToken) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'execSQL'...");
		}
		Token lResToken = mCheckDataSource(aToken);
		if (0 != lResToken.getInteger("code")) {
			return lResToken;
		}

		TokenServer lServer = getServer();
		// load SQL string
		String lSQL = aToken.getString("sql");
		Token lExecToken = getNativeAccess(aToken).exec(lSQL);
		lServer.setResponseFields(aToken, lExecToken);
		return lExecToken;
	}

	private void execSQL(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		// check if user is allowed to run 'execSQL' command
		if (!hasAuthority(aConnector, NS_JDBC + ".execSQL")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			return;
		}

		// send response to requester
		lServer.sendToken(aConnector, execSQL(aToken));
	}

	// field names may be of type string only!
	private String validateFieldsString(List<Object> aFields) {
		if (aFields == null) {
			return null;
		}
		StringBuilder lRes = new StringBuilder();
		int lCnt = aFields.size();
		int lIdx = 0;
		for (Object lField : aFields) {
			lRes.append((String) lField);
			lIdx++;
			if (lIdx < lCnt) {
				lRes.append(",");
			}
		}
		return lRes.toString();
	}

	// values may be of variable types!
	private String validateValuesString(List aValues) {
		if (aValues == null) {
			return null;
		}
		StringBuilder lRes = new StringBuilder();
		int lCnt = aValues.size();
		int lIdx = 0;
		for (Object lValue : aValues) {
			lRes.append(JDBCTools.valueToString(lValue));
			lIdx++;
			if (lIdx < lCnt) {
				lRes.append(",");
			}
		}
		return lRes.toString();
	}

	// table names may be of type string only!
	private String validateTablesString(List<Object> aTables) {
		if (aTables == null) {
			return null;
		}
		StringBuilder lRes = new StringBuilder();
		int lCnt = aTables.size();
		int lIdx = 0;
		for (Object lTable : aTables) {
			lRes.append((String) lTable);
			lIdx++;
			if (lIdx < lCnt) {
				lRes.append(",");
			}
		}
		return lRes.toString();
	}

	private String validateOrdersString(List<Object> aOrders) {
		if (aOrders == null) {
			return null;
		}
		StringBuilder lRes = new StringBuilder();
		int lCnt = aOrders.size();
		int lIdx = 0;
		for (Object lOrder : aOrders) {
			lRes.append((String) lOrder);
			lIdx++;
			if (lIdx < lCnt) {
				lRes.append(",");
			}
		}
		return lRes.toString();
	}

	/**
	 *
	 *
	 * @param aConnector
	 * @param aToken
	 */
	private void select(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'select'...");
		}

		// check if user is allowed to run 'select' command
		if (!hasAuthority(aConnector, NS_JDBC + ".select")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			return;
		}

		// obtain required parameters for query
		List<Object> lTables = aToken.getList("tables");
		List<Object> lFields = aToken.getList("fields");
		List<Object> lOrders = aToken.getList("orders");

		if (lTables == null || lTables.size() <= 0) {
			lServer.sendToken(aConnector,
					lServer.createErrorToken(aToken, -1,
							"No tables passed for JDBC select."));
			return;
		}
		if (lFields == null || lFields.size() <= 0) {
			lServer.sendToken(aConnector,
					lServer.createErrorToken(aToken, -1,
							"No fields passed for JDBC select."));
			return;
		}

		String lTablesStr = validateTablesString(lTables);
		String lFieldsStr = validateFieldsString(lFields);
		String lOrdersStr = validateOrdersString(lOrders);

		String lWhere = aToken.getString("where");
		String lGroup = aToken.getString("group");
		String lHaving = aToken.getString("having");

		// load expiration, default is no cache (expiration = 0)
		Integer lExpiration = aToken.getInteger("expiration", 0);

		// build SQL string
		String lSQL
				= "select "
				+ lFieldsStr
				+ " from "
				+ lTablesStr;

		// add where condition
		if (lWhere != null && lWhere.length() > 0) {
			lSQL += " where " + lWhere;
		}
		// add order options
		if (lOrdersStr != null && lOrdersStr.length() > 0) {
			lSQL += " order by " + lOrdersStr;
		}

		Token lQueryToken = TokenFactory.createToken();
		lQueryToken.setString("sql", lSQL);
		Token lResponse = query(lQueryToken);
		lServer.setResponseFields(aToken, lResponse);

		// send response to requester
		lServer.sendToken(aConnector, lResponse);
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 */
	private void update(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'update'...");
		}

		// check if user is allowed to run 'update' command
		if (!hasAuthority(aConnector, NS_JDBC + ".update")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			return;
		}

		String lTable = aToken.getString("table");
		List lFields = aToken.getList("fields");
		List lValues = aToken.getList("values");
		String lWhere = aToken.getString("where");

		if (lTable == null || lTable.length() <= 0) {
			lServer.sendToken(aConnector,
					lServer.createErrorToken(aToken, -1,
							"No table passed for JDBC update."));
			return;
		}
		if (lFields == null || lFields.size() <= 0) {
			lServer.sendToken(aConnector,
					lServer.createErrorToken(aToken, -1,
							"No fields passed for JDBC update."));
			return;
		}
		if (lValues == null || lValues.size() <= 0) {
			lServer.sendToken(aConnector,
					lServer.createErrorToken(aToken, -1,
							"No values passed for JDBC update."));
			return;
		}
		if (lFields.size() != lValues.size()) {
			lServer.sendToken(aConnector,
					lServer.createErrorToken(aToken, -1,
							"Number of values doe not match number of fields in JDBC update."));
			return;
		}

		StringBuilder lSetStr = new StringBuilder();

		int lIdx = 0;
		int lCnt = lFields.size();
		for (Object lField : lFields) {
			lSetStr.append((String) lField);
			lSetStr.append("=");
			lSetStr.append(JDBCTools.valueToString(lValues.get(lIdx)));
			lIdx++;
			if (lIdx < lCnt) {
				lSetStr.append(",");
			}
		}

		String lSQL = "update"
				+ " " + lTable
				+ " set"
				+ " " + lSetStr.toString();
		if (lWhere != null) {
			lSQL += " where"
					+ " " + lWhere;
		}

		Token lUpdateToken = TokenFactory.createToken();
		lUpdateToken.setString("sql", lSQL);
		Token lResponse = updateSQL(lUpdateToken);
		lServer.setResponseFields(aToken, lResponse);

		// send response to requester
		lServer.sendToken(aConnector, lResponse);
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 */
	private void insert(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'insert'...");
		}

		// check if user is allowed to run 'insert' command
		if (!hasAuthority(aConnector, NS_JDBC + ".insert")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			return;
		}

		String lTable = aToken.getString("table");
		List lFields = aToken.getList("fields");
		List lValues = aToken.getList("values");

		String lFieldsStr = validateFieldsString(lFields);
		String lValuesStr = validateValuesString(lValues);

		if (lTable == null || lTable.length() <= 0) {
			lServer.sendToken(aConnector,
					lServer.createErrorToken(aToken, -1,
							"No table passed for JDBC insert."));
			return;
		}
		if (lFields == null || lFields.size() <= 0) {
			lServer.sendToken(aConnector,
					lServer.createErrorToken(aToken, -1,
							"No fields passed for JDBC insert."));
			return;
		}
		if (lValues == null || lValues.size() <= 0) {
			lServer.sendToken(aConnector,
					lServer.createErrorToken(aToken, -1,
							"No values passed for JDBC insert."));
			return;
		}
		if (lFields.size() != lValues.size()) {
			lServer.sendToken(aConnector,
					lServer.createErrorToken(aToken, -1,
							"Number of values doe not match number of fields in JDBC insert."));
			return;
		}

		String lSQL = "insert into"
				+ " " + lTable
				+ " (" + lFieldsStr + ")"
				+ " values"
				+ " (" + lValuesStr + ")";

		Token lInsertToken = TokenFactory.createToken();
		lInsertToken.setString("sql", lSQL);
		Token lResponse = updateSQL(lInsertToken);
		lServer.setResponseFields(aToken, lResponse);

		// send response to requester
		lServer.sendToken(aConnector, lResponse);
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 */
	private void delete(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'delete'...");
		}

		// check if user is allowed to run 'delete' command
		if (!hasAuthority(aConnector, NS_JDBC + ".delete")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			return;
		}

		String lTable = aToken.getString("table");
		String lWhere = aToken.getString("where");

		if (lTable == null || lTable.length() <= 0) {
			lServer.sendToken(aConnector,
					lServer.createErrorToken(aToken, -1,
							"No table passed for JDBC delete."));
			return;
		}

		String lSQL = "delete from"
				+ " " + lTable;
		if (lWhere != null) {
			lSQL += " where"
					+ " " + lWhere;
		}

		Token lInsertToken = TokenFactory.createToken();
		lInsertToken.setString("sql", lSQL);
		Token lResponse = updateSQL(lInsertToken);
		lServer.setResponseFields(aToken, lResponse);

		// send response to requester
		lServer.sendToken(aConnector, lResponse);
	}

	/*
	 private Token mQuerySQL(String aSQL, int aExpiration) {
	 String lHash = "hash";
	 Token lResponse = null;

	 // check cache before running the query.
	 if (aExpiration > 0) {
	 lHash = Tools.getMD5(aSQL);
	 lResponse = (Token) mCache.get(lHash);
	 if (lResponse != null) {
	 lResponse.setBoolean("isCached", true);
	 return lResponse;
	 }
	 }

	 // if to be cached, put it to cache
	 // don't save isCached flag
	 if (aExpiration > 0) {
	 mCache.put(lHash, lResponse);
	 }
	 lResponse.setBoolean("isCached", false);

	 return lResponse;
	 }
	 */
	/**
	 *
	 * @param aConnector
	 * @param aToken
	 */
	private void startTA(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'startTA'...");
		}

		// check if user is allowed to run 'transactions' command
		if (!hasAuthority(aConnector, NS_JDBC + ".transactions")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			// return;
		}

		/*
		 * String lSQL = aToken.getString("sql"); Token lResponse =
		 * mExecSQL(lSQL);
		 *
		 * // send response to requester lServer.sendToken(aConnector,
		 * lResponse);
		 */
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 */
	private void rollback(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'rollback'...");
		}

		// check if user is allowed to run 'transactions' command
		if (!hasAuthority(aConnector, NS_JDBC + ".transactions")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			// return;
		}

		/*
		 * String lSQL = aToken.getString("sql"); Token lResponse =
		 * mExecSQL(lSQL);
		 *
		 * // send response to requester lServer.sendToken(aConnector,
		 * lResponse);
		 */
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 */
	private void commit(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'commit'...");
		}

		// check if user is allowed to run 'transactions' command
		if (!hasAuthority(aConnector, NS_JDBC + ".transactions")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			// return;
		}

		/*
		 * String lSQL = aToken.getString("sql"); Token lResponse =
		 * mExecSQL(lSQL);
		 *
		 * // send response to requester lServer.sendToken(aConnector,
		 * lResponse);
		 */
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 */
	private void getSecure(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'getSecure'...");
		}

		// check if user is allowed to run 'getSecure' command
		if (!hasAuthority(aConnector, NS_JDBC + ".getSecure")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			// return;
		}

		/*
		 * String lSQL = aToken.getString("sql"); Token lResponse =
		 * mExecSQL(lSQL);
		 *
		 * // send response to requester lServer.sendToken(aConnector,
		 * lResponse);
		 */
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 */
	private void postSecure(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'postSecure'...");
		}

		// check if user is allowed to run 'postSecure' command
		if (!hasAuthority(aConnector, NS_JDBC + ".postSecure")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			// return;
		}

		/*
		 * String lSQL = aToken.getString("sql"); Token lResponse =
		 * mExecSQL(lSQL);
		 *
		 * // send response to requester lServer.sendToken(aConnector,
		 * lResponse);
		 */
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 */
	private void getSQL(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'getSQL'...");
		}

		// check if user is allowed to run 'getSQL' command
		if (!hasAuthority(aConnector, NS_JDBC + ".getSQL")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			// return;
		}

		/*
		 * String lSQL = aToken.getString("sql"); Token lResponse =
		 * mExecSQL(lSQL);
		 *
		 * // send response to requester lServer.sendToken(aConnector,
		 * lResponse);
		 */
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 */
	private void postSQL(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'postSQL'...");
		}

		// check if user is allowed to run 'postSQL' command
		if (!hasAuthority(aConnector, NS_JDBC + ".postSQL")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			// return;
		}

		/*
		 * String lSQL = aToken.getString("sql"); Token lResponse =
		 * mExecSQL(lSQL);
		 *
		 * // send response to requester lServer.sendToken(aConnector,
		 * lResponse);
		 */
	}

	private void getDBInfo(WebSocketConnector aConnector, Token aToken) {

		Token lResponse = createResponse(aToken);

		List<Map<String, Object>> lResDrivers = new FastList<Map<String, Object>>();
		Enumeration<Driver> lDrivers = DriverManager.getDrivers();
		Driver lDriver;
		while (lDrivers.hasMoreElements()) {
			lDriver = lDrivers.nextElement();
			Map<String, Object> lDriverInfo = new FastMap<String, Object>();
			lDriverInfo.put("majorVersion", lDriver.getMajorVersion());
			lDriverInfo.put("minorVersion", lDriver.getMinorVersion());
			lDriverInfo.put("simpleName", lDriver.getClass().getSimpleName());
			lDriverInfo.put("className", lDriver.getClass().getName());
			lDriverInfo.put("isJdbcCompliant", lDriver.jdbcCompliant());
			lResDrivers.add(lDriverInfo);
		}
		lResponse.setList("drivers", lResDrivers);

		Connection lConn = null;
		try {
			DataSource lDataSource = getNativeDataSource(aToken);
			Map<String, Object> lConnInfo = new FastMap<String, Object>();

			lConn = lDataSource.getConnection();
			DatabaseMetaData lMetaData = lConn.getMetaData();
			lConnInfo.put("serverName", lMetaData.getDatabaseProductName());
			lConnInfo.put("serverVersion", lMetaData.getDatabaseProductVersion());
			lConnInfo.put("driverName", lMetaData.getDriverName());
			lConnInfo.put("driverVersion", lMetaData.getDriverVersion());
			lConnInfo.put("majorJdbcVersion", lMetaData.getJDBCMajorVersion());
			lConnInfo.put("minorJdbcVersion", lMetaData.getJDBCMinorVersion());
			/*
			 * System.out.println("Server name: " +
			 * meta.getDatabaseProductName());
			 * System.out.println("Server version: " +
			 * meta.getDatabaseProductVersion());
			 * System.out.println("Driver name: " +
			 * meta.getDriverName()); System.out.println("Driver
			 * version: " + meta.getDriverVersion());
			 * System.out.println("JDBC major version: " +
			 * meta.getJDBCMajorVersion()); System.out.println("JDBC
			 * minor version: " + meta.getJDBCMinorVersion());
			 */
			lResponse.setMap("currentConnection", lConnInfo);
		} catch (SQLException lEx) {
		}
		try {
			if (lConn != null) {
				lConn.commit();
				lConn.close();
			}
		} catch (SQLException lEx) {
		}
	}
}
