package integration.shift;

import com.ibatis.common.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import ch.qos.logback.classic.Logger;
import org.springframework.stereotype.Component;

@Component
class DBInitSQLScript {

	@Autowired
	private static Logger logger;

	static void run() {
		String SQLScriptFilePath = "test/resources/shiftTestDataInit.sql";
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/world?serverTimezone=Europe/Moscow&verifyServerCertificate=false&useSSL=true",
					"root", "root");
		} catch (Exception e) {
			logger.error("Error on unsuccessful authenticate" + e.getMessage());
		}
		try {
			ScriptRunner sr = new ScriptRunner(con, false, false);
			Reader reader = new BufferedReader(new FileReader(SQLScriptFilePath));
			sr.runScript(reader);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
