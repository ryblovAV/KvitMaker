package services;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OraclePreparedStatement;
import services.parameters.CisDivision;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * @author Косых Евгений
 */
final class SqlScript implements AutoCloseable {

    private final OracleCallableStatement procedureMkdStatement;
    private final OracleCallableStatement procedureNotMkdStatement;
    private final OraclePreparedStatement kvitMkdStatement;
    private final OraclePreparedStatement kvitNotMkdStatement;
    private final OraclePreparedStatement organizationStatement;


    SqlScript(File script, Connection connection) throws IOException, SQLException {

        // Чтение файла со сценарием.
        StringBuilder fileData = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(script));
        char[] buf = new char[1024];
        int numRead;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();

        String procedureMkd;
        String procedureNotMkd;
        String kvitMkd;
        String kvitNotMkd;
        String organization;
        try {
            StringTokenizer splitter = new StringTokenizer(fileData.toString(), ";");
            procedureMkd = splitter.nextToken();
            procedureNotMkd = splitter.nextToken();
            kvitMkd = splitter.nextToken();
            kvitNotMkd = splitter.nextToken();
            organization = splitter.nextToken();
        } catch (NoSuchElementException e) {
            throw new IOException("The script file is not valid!");
        }

        procedureMkdStatement = (OracleCallableStatement) connection.prepareCall(procedureMkd);
        procedureNotMkdStatement = (OracleCallableStatement) connection.prepareCall(procedureNotMkd);
        kvitMkdStatement = (OraclePreparedStatement) connection.prepareStatement(kvitMkd,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        kvitNotMkdStatement = (OraclePreparedStatement) connection.prepareStatement(kvitNotMkd,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        organizationStatement = (OraclePreparedStatement) connection.prepareStatement(organization);
    }

    int executeProcedureMkd(Date date, CisDivision cisDivision, String state, String mkdId) throws SQLException {

        procedureMkdStatement.setDateAtName("pdat", date);
        procedureMkdStatement.setStringAtName("pleskgesk", cisDivision.toString());
        procedureMkdStatement.setStringAtName("pbd_lesk", state);
        procedureMkdStatement.setStringAtName("mkd_id", mkdId);

        return procedureMkdStatement.executeUpdate();
    }

    int executeProcedureNotMkd(Date date, CisDivision cisDivision, String state) throws SQLException {

        procedureNotMkdStatement.setDateAtName("pdat", date);
        procedureNotMkdStatement.setStringAtName("pleskgesk", cisDivision.toString());
        procedureNotMkdStatement.setStringAtName("pbd_lesk", state);

        return procedureNotMkdStatement.executeUpdate();
    }

    ResultSet getKvitMkdCursor(Date date, CisDivision cisDivision, String state, String mkdId) throws SQLException {

        kvitMkdStatement.setDateAtName("pdat", date);
        kvitMkdStatement.setStringAtName("pleskgesk", cisDivision.toString());
        kvitMkdStatement.setStringAtName("pbd_lesk", state);
        kvitMkdStatement.setStringAtName("mkd_id", mkdId);

        return kvitMkdStatement.executeQuery();
    }

    ResultSet getKvitNotMkdCursor(Date date, CisDivision cisDivision, String state) throws SQLException {

        kvitNotMkdStatement.setDateAtName("pdat", date);
        kvitNotMkdStatement.setStringAtName("pleskgesk", cisDivision.toString());
        kvitNotMkdStatement.setStringAtName("pbd_lesk", state);

        return kvitNotMkdStatement.executeQuery();
    }

    String getOrganization(String state) throws SQLException {

        organizationStatement.setStringAtName("pbd_lesk", state);

        ResultSet resultSet = organizationStatement.executeQuery();

        String result = resultSet.first() ? resultSet.getString(1) : null;

        resultSet.close();

        return result;
    }

    @Override
    public void close() throws Exception {

        procedureMkdStatement.close();
        procedureNotMkdStatement.close();
        kvitMkdStatement.close();
        kvitNotMkdStatement.close();
//        reestrMkdStatement.close();
//        reestrNotMkdStatement.close();
        organizationStatement.close();
    }
}
