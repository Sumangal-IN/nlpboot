package com.tcs.nlpboot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
@EnableAutoConfiguration
public class App {

	public static final String MYSQL_DB_HOST = "u3y93bv513l7zv6o.chr7pe7iynqr.eu-west-1.rds.amazonaws.com";
	public static final String MYSQL_DB_NAME = "ip83rudxmkl9e98c";
	public static final String MYSQL_DB_USERNAME = "eegxixxjams6wn5e";
	public static final String MYSQL_DB_PASSWORD = "ib05z62xqbk0uozd";

	@RequestMapping(value = "/QueryProcessor", method = RequestMethod.GET)
	@ResponseBody
	String home(@RequestParam("text") String input) throws SQLException {
		String SQL = "";
		Connection conn_mysql = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn_mysql = DriverManager.getConnection("jdbc:mysql://"
					+ MYSQL_DB_HOST + ":3306/" + MYSQL_DB_NAME
					+ "?zeroDateTimeBehavior=convertToNull", MYSQL_DB_USERNAME,
					MYSQL_DB_PASSWORD);
			Statement statement = conn_mysql.createStatement();
			SQL = app2.getSQL(input, conn_mysql);
			if (SQL != null) {
				ResultSet rs = statement.executeQuery(SQL);
				JSONArray json = app2.convert(rs);
				return ("{\"Result\":" + json.toString() + ",\"SQL\":\"" + SQL + "\"}");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ("{\"Error\":\"" + e.getMessage() + "\",\"SQL\":\"" + SQL + "\"}");
		} finally {
			conn_mysql.close();
		}
		return "HW";
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(App.class, args);
	}
}