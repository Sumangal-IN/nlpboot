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
	public static final String MYSQL_DB_HOST = "localhost";
	public static final String MYSQL_DB_NAME = "nlp";
	public static final String MYSQL_DB_USERNAME = "root";
	public static final String MYSQL_DB_PASSWORD = "root";
	public static final String MYSQL_DB_PORT = "3311";

	// public static final String MYSQL_DB_HOST =
	// "nlp.cjotpija7r7c.us-east-1.rds.amazonaws.com";
	// public static final String MYSQL_DB_NAME = "ip83rudxmkl9e98c";
	// public static final String MYSQL_DB_USERNAME = "eegxixxjams6wn5e";
	// public static final String MYSQL_DB_PASSWORD = "ib05z62xqbk0uozd";
	// public static final String MYSQL_DB_PORT = "3306";

	static int relationship_depth = 4;
	static ArrayList<String[]> word_replace = new ArrayList<String[]>();
	static ArrayList<String[]> wordpool = new ArrayList<String[]>();
	static ArrayList<String[]> table_join_conditions = new ArrayList<String[]>();
	static ArrayList<String> nv_tables = new ArrayList<String>();
	static ArrayList<String> v_tables = new ArrayList<String>();
	static ArrayList<String> tables_in_use = new ArrayList<String>();
	static ArrayList<String> columns = new ArrayList<String>();
	static ArrayList<String> columnsProjectionEligible = new ArrayList<String>();
	static ArrayList<String> primaryColumns = new ArrayList<String>();
	static ArrayList<String> projectionList = new ArrayList<String>();
	static ArrayList<String> conditionList = new ArrayList<String>();
	static ArrayList<String> orderList = new ArrayList<String>();
	static ArrayList<String> limitList = new ArrayList<String>();
	static ArrayList<String> groupList = new ArrayList<String>();
	static ArrayList<String> havingList = new ArrayList<String>();
	static ArrayList<String[]> sentence_coverage = new ArrayList<String[]>();
	static String header = "0";

	public static String showHeader() {
		return header;
	}

	public static String getUnknown() {
		String unknown = "";
		boolean first = true;
		for (String[] words : sentence_coverage) {
			if (words[1].equals("0")) {
				if (!first) {
					unknown = unknown.replaceAll(" and", " ,");
					unknown += " and ";
				}
				unknown += words[0];
				if (first)
					first = false;
			}
		}
		return unknown;
	}

	public static String getSQL(String input, Connection conn_mysql)
			throws Exception {
		header = "0";
		System.out.println("Input :           " + input);
		word_replace = new ArrayList<String[]>();
		wordpool = new ArrayList<String[]>();
		table_join_conditions = new ArrayList<String[]>();
		nv_tables = new ArrayList<String>();
		v_tables = new ArrayList<String>();
		sentence_coverage = new ArrayList<String[]>();

		Statement statement = conn_mysql.createStatement();
		Statement statement2 = conn_mysql.createStatement();
		Statement statement3 = conn_mysql.createStatement();
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
			ResultSet rs2 = statement2
					.executeQuery("select * from metadata_column where auto_resolve=1 and table_id="
							+ rs.getInt("id"));
			while (rs2.next()) {
				ResultSet rs3 = statement3.executeQuery("select "
						+ rs2.getString("name") + " from "
						+ rs.getString("name"));
				while (rs3.next()) {
					String[] words_pair = new String[2];
					words_pair[0] = rs.getString("name") + "."
							+ rs2.getString("name");
					words_pair[1] = rs3.getString(1);
					wordpool.add(words_pair);
				}
				rs3.close();
			}
			rs2.close();
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
		// System.out.println("Verb tables : " + v_tables);
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
		columnsProjectionEligible = new ArrayList<String>();
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

		for (int i = 0; i < input_words.length; i++) {
			String[] word_pair = new String[2];
			word_pair[0] = input_words[i];
			word_pair[1] = "0";
			sentence_coverage.add(word_pair);
		}

		Set<String> tables = new HashSet<String>();
		for (int i = 0; i < input_words.length; i++) {
			if (isVerbTable(input_words[i]) || isNonVerbTable(input_words[i])) {
				tables.add(input_words[i]);
			}
		}

		for (String word_pair[] : wordpool) {
			p = Pattern.compile("^" + word_pair[1] + " | " + word_pair[1]
					+ " | " + word_pair[1] + "$");
			m = p.matcher(input);
			if (m.find()) {
				markKnown(input.substring(m.start(), m.end()));
				conditionList.add(word_pair[0] + "='" + word_pair[1] + "'");
				tables.add(word_pair[0].split("\\.")[0]);
			}
		}

		for (String table : tables) {
			tables_in_use.add(table);
		}
		System.out.println("Related tables :  " + tables_in_use);

		// columns in use
		if (tables_in_use.size() != 0) {
			rs = conn_mysql
					.createStatement()
					.executeQuery(
							"select concat(metadata_tables.name,'.',metadata_column.name),metadata_column.project from "
									+ "metadata_column,metadata_tables where "
									+ "metadata_column.table_id=metadata_tables.id and metadata_tables.name in ("
									+ listToCSVWithQuote(tables_in_use) + ")");
			while (rs.next()) {
				columns.add(rs.getString(1));
				if (rs.getString(2).equals("1"))
					columnsProjectionEligible.add(rs.getString(1));
			}
		}
		rs.close();
		System.out.println("Related columns : " + columns);

		for (int i = 0; i < input_words.length; i++) {
			if (isVerbTable(input_words[i]) || isNonVerbTable(input_words[i])
					|| isColumn(input_words[i])) {
				markKnown(input_words[i]);
			}
		}

		rs = conn_mysql.createStatement().executeQuery(
				"select * from metadata_starter");
		while (rs.next()) {
			if (input.startsWith(rs.getString(2))) {
				markKnown(rs.getString(2));
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
							field.addAll(getAllColumnsProjectionEligible(input_words[i]));
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
					projectionList.add(rs.getString(4).replaceAll(
							"\\[" + rs.getString(3) + "\\]", field.get(0)));
				}
				if (rs.getString(5) != null) {
					groupList.addAll(field);
				}
				header = rs.getString(6);
			}
		}
		rs.close();

		// projection list
		rs = conn_mysql
				.createStatement()
				.executeQuery(
						"select metadata_expression.id,expression,projection,metadata_expression.condition,metadata_expression.having,metadata_expression.limit,metadata_expression.order from metadata_expression,metadata_tables "
								+ "where metadata_expression.table_id=metadata_tables.id and "
								+ "metadata_tables.name in ("
								+ listToCSVWithQuote(tables_in_use) + ")");
		while (rs.next()) {
			p = Pattern.compile(rs.getString("expression"));
			m = p.matcher(input);
			if (m.find()) {
				markKnown(input.substring(m.start(), m.end()));
				if (rs.getString("metadata_expression.projection") != null)
					projectionList.add(evaluateExpressions(input,
							rs.getInt("metadata_expression.id"),
							rs.getString("metadata_expression.projection"),
							conn_mysql, m.start(), m.end()));
				if (rs.getString("metadata_expression.condition") != null)
					conditionList.add(evaluateExpressions(input,
							rs.getInt("metadata_expression.id"),
							rs.getString("metadata_expression.condition"),
							conn_mysql, m.start(), m.end()));
				if (rs.getString("metadata_expression.order") != null)
					orderList.add(evaluateExpressions(input,
							rs.getInt("metadata_expression.id"),
							rs.getString("metadata_expression.order"),
							conn_mysql, m.start(), m.end()));
				if (rs.getString("metadata_expression.limit") != null)
					limitList.add(evaluateExpressions(input,
							rs.getInt("metadata_expression.id"),
							rs.getString("metadata_expression.limit"),
							conn_mysql, m.start(), m.end()));
				if (rs.getString("metadata_expression.having") != null)
					havingList.add(evaluateExpressions(input,
							rs.getInt("metadata_expression.id"),
							rs.getString("metadata_expression.having"),
							conn_mysql, m.start(), m.end()));
			}
		}
		rs.close();
		conditionList.addAll(getJoinConditions(tables_in_use, conn_mysql));
		String SQL = completeQuery(projectionList, conditionList, orderList,
				limitList, groupList, havingList, tables_in_use, conn_mysql);
		System.out.println("Unknow words : ");
		for (String[] words : sentence_coverage) {
			if (words[1].equals("0"))
				System.out.println(words[0]);
		}
		return SQL;
	}

	private static void markKnown(String exclude) {
		exclude = exclude.trim();
		String tokens[] = exclude.split(" ");
		for (int a = 0; a < sentence_coverage.size(); a++) {
			if (sentence_coverage.get(a)[0].equals(tokens[0])) {
				boolean match = true;
				for (int i = 1; i < tokens.length; i++) {
					if ((a + i) == sentence_coverage.size()) {
						match = false;
						break;
					}
					if (!sentence_coverage.get(a + i)[0].equals(tokens[i])) {
						match = false;
						break;
					}
				}
				if (match) {
					for (int i = 0; i < tokens.length; i++) {
						String words[] = sentence_coverage.get(a + i);
						words[1] = "1";
						sentence_coverage.set(a + i, words);
					}
					continue;
				}
			}
		}
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
			sql += "*";
		sql += " from ";
		if (!tables_in_use.isEmpty())
			sql += listToCSV(tables_in_use) + " ";
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
		Set<String> conditions = new HashSet<String>();
		ArrayList<String> conditions_temp = new ArrayList<String>();
		ArrayList<String> tables_temp = new ArrayList<String>();
		ArrayList<String> tables_to_add = new ArrayList<String>();

		for (String table : tables) {
			conditions_temp.clear();
			tables_temp.clear();
			int depth = 0;
			String current_table = table;
			while (depth <= relationship_depth && current_table != null) {
				ResultSet rs = conn_mysql.createStatement().executeQuery(
						"SELECT * FROM metadata_table_join where table2 ='"
								+ current_table + "'");
				if (rs.next()) {
					current_table = rs.getString("table1");
					tables_temp.add(current_table);
					conditions_temp.add(rs.getString("condition"));
					boolean match = false;
					for (String table_other : tables) {
						if (table_other.equals(current_table)) {
							tables_to_add.addAll(tables_temp);
							match = true;
							break;
						}
					}
					if (match) {
						conditions.addAll(conditions_temp);
					}
				} else
					current_table = null;
				depth++;
				rs.close();
			}
			conditions_temp.clear();
			tables_temp.clear();
			depth = 0;
			current_table = table;
			while (depth <= relationship_depth && current_table != null) {
				ResultSet rs = conn_mysql.createStatement().executeQuery(
						"SELECT * FROM metadata_table_join where table1 ='"
								+ current_table + "'");
				if (rs.next()) {
					current_table = rs.getString("table2");
					tables_temp.add(current_table);
					conditions_temp.add(rs.getString("condition"));
					boolean match = false;
					for (String table_other : tables) {
						if (table_other.equals(current_table)) {
							tables_to_add.addAll(tables_temp);
							match = true;
							break;
						}
					}
					if (match) {
						conditions.addAll(conditions_temp);
					}
				} else
					current_table = null;
				depth++;
				rs.close();
			}
		}
		conditions_temp.clear();
		conditions_temp.addAll(conditions);
		for (String table_to_add : tables_to_add) {
			if (!tables_in_use.toString().contains(table_to_add))
				tables_in_use.add(table_to_add);
		}
		return conditions_temp;
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

	public static ArrayList<String> getAllColumnsProjectionEligible(String table) {
		ArrayList<String> all_columns = new ArrayList<String>();
		for (String column : columnsProjectionEligible) {
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

	public static void main(String args[]) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn_mysql = DriverManager.getConnection("jdbc:mysql://"
				+ MYSQL_DB_HOST + ":" + MYSQL_DB_PORT + "/" + MYSQL_DB_NAME
				+ "?zeroDateTimeBehavior=convertToNull", MYSQL_DB_USERNAME,
				MYSQL_DB_PASSWORD);

		Statement statement = conn_mysql.createStatement();
		ResultSet rs = statement
				.executeQuery("select * from test where enable=1");
		String SQL = "";
		while (rs.next()) {
			SQL = getSQL(rs.getString("input"), conn_mysql);
			System.out.println("output : " +SQL);
			System.out.println();
		}
		rs.close();

		conn_mysql.close();
	}
}
