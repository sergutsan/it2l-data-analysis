package db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MysqlToXls {

	public List<Map<String, String>> makeQuery(String wherePartOfQuery) throws SQLException, ClassNotFoundException, IOException {
		List<Map<String, String>> res = new LinkedList<Map<String, String>>();
		// Execute SQL query

		String sqlQuery = "select exercisequiz.*, user.user, user.cond from exercisequiz join user on exercisequiz.id_user = user.id_user where "+ wherePartOfQuery;
		PreparedStatement stmt = DbConnection.getConnection().prepareStatement(sqlQuery);
		ResultSet rs = stmt.executeQuery();

		// Get the list of column names and store them as the first
		// row of the spreadsheet.
		ResultSetMetaData colInfo = rs.getMetaData();
		List<String> colNames = new ArrayList<String>();
		for (int i = 1; i <= colInfo.getColumnCount(); i++) {
			colNames.add(colInfo.getColumnName(i));
		}

		// Save all the data from the database table rows
		while (rs.next()) {
			Map<String, String> map = new HashMap<String, String>();
			for (String colName : colNames) {
				if(colName.equals("jsonval")){
					String val = rs.getString(colName);
					map.put(colName, val);
				} else {
					map.put(colName, rs.getString(colName));
				}
			}
			res.add(map);
		}
		DbConnection.closeConnection();
		return res;
	}

	/**
	 * Initial attempt at documenting this method...
	 * 
	 * 
	 * @param json  a string of JSON taken from the DB
	 * @param wherePartOfSqlStatementToGetData sorry for name
	 * @return a list equivalent to the received JSON
	 */
	public static List<Map<String, String>> processStringJson(String json, String wherePartOfSqlStatementToGetData){		
		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(json);
		if (!wherePartOfSqlStatementToGetData.contains("2")) {
			jsonElement = parser.parse(jsonElement.getAsString()); // SGS: Is it parsing itself? Does this change anything?
		}
		printJson(jsonElement, result);
		return result;
	}
	
	public static void printJson(JsonElement jsonElement, List<Map<String, String>>list) {
        
        // Check whether jsonElement is JsonObject or not
        if (jsonElement.isJsonObject()) {
            Set<Entry<String, JsonElement>> ens = ((JsonObject) jsonElement).entrySet();
            if (ens != null) {
                // Iterate JSON Elements with Key values
            	for (Entry<String, JsonElement> en : ens) {
            		Map<String, String> map = new HashMap<String, String>();
            		String value = "";	
            		if(en.getValue().isJsonPrimitive())
            		{
            			value = en.getValue().getAsString();
						map.put(en.getKey(), value);
						list.add(map);
            		}
            		else if(en.getValue().isJsonArray())
            		{
						value = en.getValue().getAsJsonArray().toString();
						value = value.substring(1);
						value = value.substring(0, value.length() - 1);
						map.put(en.getKey(), value);
						list.add(map);
					}
            
            		printJson(en.getValue(), list);
            	}
            }
        } 
        
        // Check whether jsonElement is Arrary or not
        else if (jsonElement.isJsonArray()) {
                    JsonArray jarr = jsonElement.getAsJsonArray();
                    // Iterate JSON Array to JSON Elements
                    for (JsonElement je : jarr) {
                    	printJson(je, list);
                    }
        }
        
        // Check whether jsonElement is NULL or not
        else if (jsonElement.isJsonNull()) { 

        } 
        // Check whether jsonElement is Primitive or not
        else if (jsonElement.isJsonPrimitive()) {
        	
        } 
    }
	
	private static List<String> getColumnsName(List<Map<String, String>> resultados, String wherePartOfSqlStatementToGetData){
		LinkedList<String> names = new LinkedList<String>();
		for(Map<String, String> map : resultados){
			Set<String> setit = map.keySet();
			Iterator<String> it = setit.iterator();
			while(it.hasNext()){
				String key = it.next();
				if(key.equals("jsonval")){
					List<Map<String, String>> lista = MysqlToXls.processStringJson(map.get("jsonval"),wherePartOfSqlStatementToGetData);
					for(Map<String, String> map2 : lista){
						Set<String> setit2 = map2.keySet();
						Iterator<String> it2 = setit2.iterator();
						while(it2.hasNext()){
							String added="-pos";
							if(map.get("typeQuiz").equals("0")){
								added = "-pre";
							}
							String key2 = it2.next()+added;
							if(!names.contains(key2)){
								names.add(key2);
							}
						}
					}
				}else{
					if(!names.contains(key))
						names.add(key);
				}
			}
		}
		
		return names;
	}
	
	/**
	 * 
	 * @param tablename
	 * @param filename
	 * @param wherePartOfSqlStatementToGetData sorry for name
	 * @throws Exception
	 */
	public void generateXls(String tablename, String filename, String wherePartOfSqlStatementToGetData) throws Exception {
		MysqlToXls mysqlToXls = new MysqlToXls();
		List<Map<String, String>> resultados = mysqlToXls.makeQuery(wherePartOfSqlStatementToGetData);// or typeQuiz = 1");
		
		// Create new Excel workbook and sheet
		HSSFWorkbook xlsWorkbook = new HSSFWorkbook();
		HSSFSheet xlsSheet = xlsWorkbook.createSheet();
		short rowIndex = 0;

		// Get the list of column names and store them as the first
		// row of the spreadsheet.
		List<String> colNames = getColumnsName(resultados,wherePartOfSqlStatementToGetData);
		HSSFRow titleRow = xlsSheet.createRow(rowIndex++);
		int i = 0;
 		for (String colN : colNames) {
			titleRow.createCell((short) (i)).setCellValue(new HSSFRichTextString(colN));
			xlsSheet.setColumnWidth((short) (i), (short) 4000);
			i++;
		}
		for(Map<String, String> mapping: resultados){
			HSSFRow dataRow = xlsSheet.createRow(rowIndex++);
			short colIndex = 0;
			int colInd2 = 0;
			for (String colName : colNames) {
				String value = "";
				//System.out.println("Column: " + colName);
				if (colName.contains("pre")|| colName.contains("pos")){
					colName = colName.split("-")[0];
					String id = mapping.get("id_exercisequiz");
					String jsonValueInDB = mapping.get("jsonval");
					if ("1890".equals(id) || "1879".equals(id)) {
					    System.out.println("ID: " + id);
					    System.out.println("JSON value: " + jsonValueInDB);
					}
					List<Map<String, String>> lista = MysqlToXls.processStringJson(jsonValueInDB,wherePartOfSqlStatementToGetData);
					if (colInd2 < lista.size()) {
						value = lista.get(colInd2++).get(colName);
						if ("1890".equals(id) || "1879".equals(id)) {
							System.out.println("Column name: " + colName + " Value: " + value);
						}
					}
				} else {
					value = mapping.get(colName);
				}
				dataRow.createCell(colIndex++).setCellValue(new HSSFRichTextString(value));
			}
		}

		// Write to disk
		xlsWorkbook.write(new FileOutputStream(filename));
		xlsWorkbook.close();
	}

	public static void main(String[] args) {
		try {
			Scanner scanner = new Scanner(System.in);
			boolean correct = false;
			int number = 0;
			while (correct == false)
			{
				System.out.print("Give me type of quiz (0-2): ");
				if(scanner.hasNextInt() == false)
				{
					System.out.print("not a number!\n");
					scanner.next();
				}
				else
				{
					number = scanner.nextInt();
					if(number < 0 || number > 2)
					{
						System.out.print("invalid number!\n");
					}
					else
					{
						correct = true;
					}
				}
			}
			MysqlToXls thisObject = new MysqlToXls();
			thisObject.generateXls("", "data.xls", "exercisequiz.typeQuiz = " + number);		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

