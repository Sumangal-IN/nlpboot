package com.tcs.nlpboot;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class app2 {

	public static final String MYSQL_DB_HOST = "u3y93bv513l7zv6o.chr7pe7iynqr.eu-west-1.rds.amazonaws.com";
	public static final String MYSQL_DB_NAME = "ip83rudxmkl9e98c";
	public static final String MYSQL_DB_USERNAME = "eegxixxjams6wn5e";
	public static final String MYSQL_DB_PASSWORD = "ib05z62xqbk0uozd";

	static ArrayList<String[]> word_replace = new ArrayList<String[]>();
	static ArrayList<String[]> metadata = new ArrayList<String[]>();
	static ArrayList<String[]> table_join_conditions = new ArrayList<String[]>();
	static ArrayList<String> nv_tables = new ArrayList<String>();
	static ArrayList<String> v_tables = new ArrayList<String>();
	static ArrayList<String> tables_in_use = new ArrayList<String>();
	static ArrayList<String> columns = new ArrayList<String>();
	static ArrayList<String> primaryColumns = new ArrayList<String>();
	static ArrayList<String> projectionList = new ArrayList<String>();
	static ArrayList<String> conditionList = new ArrayList<String>();
	static ArrayList<String> orderList = new ArrayList<String>();
	static ArrayList<String> limitList = new ArrayList<String>();
	static ArrayList<String> groupList = new ArrayList<String>();
	static ArrayList<String> havingList = new ArrayList<String>();

	public static String getSQL(String input, Connection conn_mysql)
			throws Exception {
		System.out.println("Input :           " + input);
		word_replace = new ArrayList<String[]>();
		metadata = new ArrayList<String[]>();
		table_join_conditions = new ArrayList<String[]>();
		nv_tables = new ArrayList<String>();
		v_tables = new ArrayList<String>();

		Statement statement = conn_mysql.createStatement();
		ResultSet rs = statement.executeQuery("select * from word_replace");
		while (rs.next()) {
			String[] words_pair = new String[2];
			words_pair[0] = rs.getString("word");
			words_pair[1] = rs.getString("new_word");
			word_replace.add(words_pair);
		}
		rs.close();

		rs = statement.executeQuery("select * from metadata_tables");
		while (rs.next()) {
			if (rs.getInt("verb") == 1)
				v_tables.add(rs.getString("name"));
			else
				nv_tables.add(rs.getString("name"));
		}
		rs.close();
		// System.out.println("Verb tables :     " + v_tables);
		// System.out.println("Non Verb tables : " + nv_tables);
		rs = statement.executeQuery("select * from metadata_table_join");
		while (rs.next()) {
			String[] words = new String[3];
			words[0] = rs.getString("table1");
			words[1] = rs.getString("table2");
			words[2] = rs.getString("condition");
			table_join_conditions.add(words);
		}
		rs.close();

		rs = statement
				.executeQuery("select concat(metadata_tables.name,'.',metadata_column.name) from metadata_tables,metadata_column where metadata_column.primary=1 and metadata_column.table_id=metadata_tables.id");
		while (rs.next()) {
			primaryColumns.add(rs.getString(1));
		}
		rs.close();

		tables_in_use = new ArrayList<String>();
		columns = new ArrayList<String>();
		projectionList = new ArrayList<String>();
		conditionList = new ArrayList<String>();
		orderList = new ArrayList<String>();
		limitList = new ArrayList<String>();
		groupList = new ArrayList<String>();
		havingList = new ArrayList<String>();

		Pattern p = null;
		Matcher m = null;
		// convert the whole input into lower case
		input = input.toLowerCase();
		// System.out.println("after lower casing .....");
		// System.out.println(input);

		// replace words
		input = " " + input + " ";
		for (String[] words_pair : word_replace) {
			if (words_pair[1] == null)
				words_pair[1] = "";
			input = input.replaceAll(" " + words_pair[0] + " ", " "
					+ words_pair[1] + " ");
		}
		while (input.contains("  ")) {
			input = input.replaceAll("  ", " ");
		}
		input = input.trim();
		// System.out.println("after word replacement .....");
		// System.out.println(input);

		// tokenizing
		// tables in use
		String input_words[] = input.split(" ");
		Set<String> tables = new HashSet<String>();
		for (int i = 0; i < input_words.length; i++) {
			if (isVerbTable(input_words[i]) || isNonVerbTable(input_words[i])) {
				tables.add(input_words[i]);
			}
		}
		for (String table : tables) {
			tables_in_use.add(table);
		}
		System.out.println("Related tables :  " + tables_in_use);

		// columns in use
		rs = conn_mysql
				.createStatement()
				.executeQuery(
						"select concat(metadata_tables.name,'.',metadata_column.name) from "
								+ "metadata_column,metadata_tables where "
								+ "metadata_column.table_id=metadata_tables.id and metadata_tables.name in ("
								+ listToCSVWithQuote(tables_in_use) + ")");
		while (rs.next()) {
			columns.add(rs.getString(1));
		}
		rs.close();
		System.out.println("Related columns : " + columns);

		rs = conn_mysql.createStatement().executeQuery("select * from starter");
		while (rs.next()) {
			if (input.startsWith(rs.getString(2))) {
				System.out.println("Starts with : " + rs.getString(2));
				ArrayList<String> field = new ArrayList<String>();
				if (rs.getString(3).equals("nverb.primary")) {
					for (int i = 0; i < input_words.length; i++) {
						if (isNonVerbTable(input_words[i])) {
							field.add(getPrimaryColumn(input_words[i]));
							break;
						}
					}
				}
				if (rs.getString(3).equals("nverb.all")) {
					for (int i = 0; i < input_words.length; i++) {
						if (isNonVerbTable(input_words[i])) {
							field.addAll(getAllColumns(input_words[i]));
							break;
						}
					}
				}
				if (rs.getString(3).equals("column")) {
					for (int i = 0; i < input_words.length; i++) {
						if (isColumn(input_words[i])) {
							field.add(input_words[i]);
						}
					}
				}
				if (rs.getString(4) == null) {
					projectionList.addAll(field);
				} else {
					projectionList.add(rs.getString(4).replace(
							"[" + rs.getString(3) + "]", field.get(0)));
				}
				if (rs.getString(5) != null) {
					groupList.addAll(field);
				}
			}
		}
		rs.close();

		// projection list
		rs = conn_mysql
				.createStatement()
				.executeQuery(
						"select metadata_expression.id,expression,projection from metadata_expression,metadata_tables "
								+ "where metadata_expression.table_id=metadata_tables.id and projection is not null and "
								+ "metadata_tables.name in ("
								+ listToCSVWithQuote(tables_in_use) + ")");
		while (rs.next()) {
			p = Pattern.compile(rs.getString("expression"));
			m = p.matcher(input);
			if (m.find()) {
				projectionList.add(evaluateExpressions(input,
						rs.getInt("metadata_expression.id"),
						rs.getString("metadata_expression.projection"),
						conn_mysql, m.start(), m.end()));
			}
		}
		rs.close();

		// condition list
		rs = conn_mysql
				.createStatement()
				.executeQuery(
						"select metadata_expression.id,expression,metadata_expression.condition from metadata_expression,metadata_tables where metadata_expression.table_id=metadata_tables.id and metadata_expression.condition is not null and metadata_tables.name in ("
								+ listToCSVWithQuote(tables_in_use) + ")");
		while (rs.next()) {
			p = Pattern.compile(rs.getString("expression"));
			m = p.matcher(input);
			while (m.find()) {
				conditionList.add(evaluateExpressions(input,
						rs.getInt("metadata_expression.id"),
						rs.getString("metadata_expression.condition"),
						conn_mysql, m.start(), m.end()));
			}
		}
		rs.close();

		// order list
		rs = conn_mysql
				.createStatement()
				.executeQuery(
						"select metadata_expression.id,expression,metadata_expression.order from metadata_expression,metadata_tables where metadata_expression.table_id=metadata_tables.id and metadata_expression.order is not null and metadata_tables.name in ("
								+ listToCSVWithQuote(tables_in_use) + ")");
		while (rs.next()) {
			p = Pattern.compile(rs.getString("expression"));
			m = p.matcher(input);
			if (m.find()) {
				orderList.add(evaluateExpressions(input,
						rs.getInt("metadata_expression.id"),
						rs.getString("metadata_expression.order"), conn_mysql,
						m.start(), m.end()));
			}
		}
		rs.close();

		// limit list
		rs = conn_mysql
				.createStatement()
				.executeQuery(
						"select metadata_expression.id,expression,metadata_expression.limit from metadata_expression,metadata_tables where metadata_expression.table_id=metadata_tables.id and metadata_expression.limit is not null and metadata_tables.name in ("
								+ listToCSVWithQuote(tables_in_use) + ")");
		while (rs.next()) {
			p = Pattern.compile(rs.getString("expression"));
			m = p.matcher(input);
			if (m.find()) {
				limitList.add(evaluateExpressions(input,
						rs.getInt("metadata_expression.id"),
						rs.getString("metadata_expression.limit"), conn_mysql,
						m.start(), m.end()));
			}
		}
		rs.close();

		// having list
		rs = conn_mysql
				.createStatement()
				.executeQuery(
						"select metadata_expression.id,expression,metadata_expression.having from metadata_expression,metadata_tables where metadata_expression.table_id=metadata_tables.id and metadata_expression.having is not null and metadata_tables.name in ("
								+ listToCSVWithQuote(tables_in_use) + ")");
		while (rs.next()) {
			p = Pattern.compile(rs.getString("expression"));
			m = p.matcher(input);
			if (m.find()) {
				havingList.add(evaluateExpressions(input,
						rs.getInt("metadata_expression.id"),
						rs.getString("metadata_expression.having"), conn_mysql,
						m.start(), m.end()));
			}
		}
		rs.close();
		String SQL = completeQuery(projectionList, conditionList, orderList,
				limitList, groupList, havingList, tables_in_use, conn_mysql);
		return SQL;
	}

	public static String completeQuery(ArrayList<String> projectionList,
			ArrayList<String> conditionList, ArrayList<String> orderList,
			ArrayList<String> limitList, ArrayList<String> groupList,
			ArrayList<String> havingList, ArrayList<String> tables_in_use,
			Connection conn_mysql) throws Exception {
		System.out.println("Projections :     " + projectionList);
		System.out.println("Conditions :      " + conditionList);
		System.out.println("Order by :        " + orderList);
		System.out.println("Limit :           " + limitList);
		System.out.println("Group by :        " + groupList);
		System.out.println("Having :          " + havingList);
		String sql = "select ";
		if (!projectionList.isEmpty())
			sql += listToCSV(projectionList);
		else
			sql = "*";
		sql += " from ";
		if (!tables_in_use.isEmpty())
			sql += listToCSV(tables_in_use) + " ";
		conditionList.addAll(getJoinConditions(tables_in_use, conn_mysql));
		if (!conditionList.isEmpty())
			sql += "where " + listToCSV(conditionList).replace(",", " and ")
					+ " ";
		if (!groupList.isEmpty())
			sql += " group by " + listToCSV(groupList) + " ";
		if (!havingList.isEmpty())
			sql += " having " + listToCSV(havingList) + " ";
		if (!orderList.isEmpty())
			sql += " order by " + listToCSV(orderList) + " ";
		if (!limitList.isEmpty())
			sql += listToCSV(limitList);
		return sql;
	}

	public static String listToCSV(ArrayList<String> list) {
		if (list.isEmpty())
			return "";
		String csv = list.toString();
		csv = csv.substring(1, csv.length() - 1);
		return csv;
	}

	public static String listToCSVWithQuote(ArrayList<String> list) {
		if (list.isEmpty())
			return "";
		String csv = list.toString();
		csv = csv.replace("[", "'").replace("]", "'").replaceAll(",", "','")
				.replaceAll(" ", "");
		return csv;
	}

	private static ArrayList<String> getJoinConditions(
			ArrayList<String> tables, Connection conn_mysql) throws Exception {
		ArrayList<String> conditions = new ArrayList<String>();
		ResultSet rs = conn_mysql.createStatement().executeQuery(
				"SELECT * FROM metadata_table_join where table1 in ("
						+ listToCSVWithQuote(tables) + ") and table2 in ("
						+ listToCSVWithQuote(tables) + ")");
		while (rs.next()) {
			conditions.add(rs.getString("condition"));
		}
		rs.close();
		return conditions;
	}

	private static String evaluateExpressions(String input, int expr_id,
			String part_sql, Connection conn_mysql, int start, int end)
			throws Exception {
		if (part_sql.contains("[")) {
			String select_text = input.substring(start, end);
			ResultSet rs = conn_mysql.createStatement().executeQuery(
					"select * from metadata_expression_variable where expression_id="
							+ expr_id);
			Pattern p = null;
			Matcher m = null;
			while (rs.next()) {
				String value = select_text.replaceAll(
						rs.getString("exp_remove"), "");
				p = Pattern.compile(rs.getString("exp_extract"));
				m = p.matcher(value);
				int match_count = 0;
				if (m.find()) {
					match_count++;
					if (match_count == rs.getInt("occurance")) {
						value = value.substring(m.start(), m.end());
						part_sql = part_sql.replace(rs.getString("variable"),
								value);
						break;
					}
				}
			}
			rs.close();
		}
		return part_sql;
	}

	public static boolean isVerbTable(String token) {
		for (String table : v_tables)
			if (table.equals(token))
				return true;
		return false;
	}

	public static boolean isNonVerbTable(String token) {
		for (String table : nv_tables)
			if (table.equals(token))
				return true;
		return false;
	}

	public static boolean isColumn(String token) {
		for (String column : columns)
			if (column.contains("." + token))
				return true;
		return false;
	}

	public static String getPrimaryColumn(String table) {
		for (String column : primaryColumns) {
			if (column.contains(table))
				return column;
		}
		return "";
	}

	public static ArrayList<String> getAllColumns(String table) {
		ArrayList<String> all_columns = new ArrayList<String>();
		for (String column : columns) {
			if (column.contains(table))
				all_columns.add(column);
		}
		return all_columns;
	}

	public static JSONArray convert(ResultSet rs) throws Exception,
			JSONException {
		JSONArray json = new JSONArray();
		ResultSetMetaData rsmd = rs.getMetaData();

		while (rs.next()) {
			int numColumns = rsmd.getColumnCount();
			JSONObject obj = new JSONObject();

			for (int i = 1; i < numColumns + 1; i++) {
				String column_name = rsmd.getColumnName(i);

				if (rsmd.getColumnType(i) == java.sql.Types.ARRAY) {
					obj.put(column_name, rs.getArray(column_name));
				} else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT) {
					obj.put(column_name, rs.getInt(column_name));
				} else if (rsmd.getColumnType(i) == java.sql.Types.BOOLEAN) {
					obj.put(column_name, rs.getBoolean(column_name));
				} else if (rsmd.getColumnType(i) == java.sql.Types.BLOB) {
					obj.put(column_name, rs.getBlob(column_name));
				} else if (rsmd.getColumnType(i) == java.sql.Types.DOUBLE) {
					obj.put(column_name, rs.getDouble(column_name));
				} else if (rsmd.getColumnType(i) == java.sql.Types.FLOAT) {
					obj.put(column_name, rs.getFloat(column_name));
				} else if (rsmd.getColumnType(i) == java.sql.Types.INTEGER) {
					obj.put(column_name, rs.getInt(column_name));
				} else if (rsmd.getColumnType(i) == java.sql.Types.NVARCHAR) {
					obj.put(column_name, rs.getNString(column_name));
				} else if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR) {
					obj.put(column_name, rs.getString(column_name));
				} else if (rsmd.getColumnType(i) == java.sql.Types.TINYINT) {
					obj.put(column_name, rs.getInt(column_name));
				} else if (rsmd.getColumnType(i) == java.sql.Types.SMALLINT) {
					obj.put(column_name, rs.getInt(column_name));
				} else if (rsmd.getColumnType(i) == java.sql.Types.DATE) {
					obj.put(column_name, rs.getDate(column_name));
				} else if (rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP) {
					obj.put(column_name, rs.getTimestamp(column_name));
				} else {
					obj.put(column_name, rs.getObject(column_name));
				}
			}

			json.put(obj);
		}

		return json;
	}

	// public static void main(String args[]) throws Exception {
	// Class.forName("com.mysql.jdbc.Driver");
	// Connection conn_mysql = DriverManager.getConnection("jdbc:mysql://"
	// + MYSQL_DB_HOST + ":3306/" + MYSQL_DB_NAME
	// + "?zeroDateTimeBehavior=convertToNull", MYSQL_DB_USERNAME,
	// MYSQL_DB_PASSWORD);
	//
	// Statement statement = conn_mysql.createStatement();
	// ResultSet rs = statement
	// .executeQuery("select * from test where enable=1");
	// String SQL = "";
	// while (rs.next()) {
	// SQL = getSQL(rs.getString("input"), conn_mysql);
	// System.out.println(SQL);
	// System.out.println();
	// }
	// rs.close();
	//
	// // if (SQL != null) {
	// // rs = statement.executeQuery(SQL);
	// // JSONArray json = convert(rs);
	// // System.out.println(json);
	// // }
	//
	// conn_mysql.close();
	// }
}
