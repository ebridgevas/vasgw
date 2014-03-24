package com.ebridgecommerce.smppgw;

import com.ebridgecommerce.db.DBAdapter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class SystemParameters {
	
	public final static Map<String, String> SYSTEM_PARAMETERS;
	
	static {
		
		SYSTEM_PARAMETERS = new HashMap<String, String>();
		
		try {		
			
			Connection conn = DBAdapter.getConnection();
			Statement stmt = conn.createStatement();
			String sql = "SELECT parameter_key, parameter_value FROM system_parameters";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()){
				SYSTEM_PARAMETERS.put(rs.getString("parameter_key"), rs.getString("parameter_value"));
			}
			
		} catch (Exception e) {
				System.out.print("############ FATAL - FAILED TO LOAD SYSTEM_PARAMETERS : " + e.getMessage());
		}	
	}
}
