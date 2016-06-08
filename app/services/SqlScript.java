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
    private final OraclePreparedStatement kvitMkdPostalStatement;
    private final OraclePreparedStatement kvitMkdStreetStatement;
    private final OraclePreparedStatement kvitNotMkdPostalStatement;
    private final OraclePreparedStatement kvitNotMkdStreetStatement;
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
        String kvitMkdPostal;
        String kvitMkdStreet;
        String kvitNotMkdPostal;
        String kvitNotMkdStreet;
        String organization;
        try {
            StringTokenizer splitter = new StringTokenizer(fileData.toString(), ";");
            procedureMkd = splitter.nextToken();
            procedureNotMkd = splitter.nextToken();
            kvitMkdPostal = splitter.nextToken();
            kvitMkdStreet = splitter.nextToken();
            kvitNotMkdPostal = splitter.nextToken();
            kvitNotMkdStreet = splitter.nextToken();
            organization = splitter.nextToken();
        } catch (NoSuchElementException e) {
            throw new IOException("The script file is not valid!");
        }

        procedureMkdStatement = (OracleCallableStatement) connection.prepareCall(procedureMkd);
        procedureNotMkdStatement = (OracleCallableStatement) connection.prepareCall(procedureNotMkd);
        kvitMkdPostalStatement = (OraclePreparedStatement) connection.prepareStatement(kvitMkdPostal,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        kvitMkdStreetStatement = (OraclePreparedStatement) connection.prepareStatement(kvitMkdStreet,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        kvitNotMkdPostalStatement = (OraclePreparedStatement) connection.prepareStatement(kvitNotMkdPostal,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        kvitNotMkdStreetStatement = (OraclePreparedStatement) connection.prepareStatement(kvitNotMkdStreet,
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

    ResultSet getKvitMkdPostalCursor(Date date, CisDivision cisDivision, String state, String mkdId) throws SQLException {

        kvitMkdPostalStatement.setDateAtName("pdat", date);
        kvitMkdPostalStatement.setStringAtName("pleskgesk", cisDivision.toString());
        kvitMkdPostalStatement.setStringAtName("pbd_lesk", state);
        kvitMkdPostalStatement.setStringAtName("mkd_id", mkdId);

        return kvitMkdPostalStatement.executeQuery();
    }

    ResultSet getKvitMkdStreetCursor(Date date, CisDivision cisDivision, String state, String mkdId) throws SQLException {

        kvitMkdStreetStatement.setDateAtName("pdat", date);
        kvitMkdStreetStatement.setStringAtName("pleskgesk", cisDivision.toString());
        kvitMkdStreetStatement.setStringAtName("pbd_lesk", state);
        kvitMkdStreetStatement.setStringAtName("mkd_id", mkdId);

        return kvitMkdStreetStatement.executeQuery();
    }

    ResultSet getKvitNotMkdPostalCursor(Date date, CisDivision cisDivision, String state) throws SQLException {

        kvitNotMkdPostalStatement.setDateAtName("pdat", date);
        kvitNotMkdPostalStatement.setStringAtName("pleskgesk", cisDivision.toString());
        kvitNotMkdPostalStatement.setStringAtName("pbd_lesk", state);

        return kvitNotMkdPostalStatement.executeQuery();
    }

    ResultSet getKvitNotMkdStreetCursor(Date date, CisDivision cisDivision, String state) throws SQLException {

        kvitNotMkdStreetStatement.setDateAtName("pdat", date);
        kvitNotMkdStreetStatement.setStringAtName("pleskgesk", cisDivision.toString());
        kvitNotMkdStreetStatement.setStringAtName("pbd_lesk", state);

        return kvitNotMkdStreetStatement.executeQuery();
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
        kvitMkdPostalStatement.close();
        kvitNotMkdPostalStatement.close();
//        reestrMkdStatement.close();
//        reestrNotMkdStatement.close();
        organizationStatement.close();
    }
}
