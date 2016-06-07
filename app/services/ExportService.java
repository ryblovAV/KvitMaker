package services;

import services.parameters.CisDivision;
import services.parameters.MkdChs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static play.Logger.debug;
import static play.Logger.info;
import static play.Logger.trace;

/**
 * @author Косых Евгений
 */
public final class ExportService {

    private final SqlScript script;

    public ExportService(Connection connection) throws SQLException, IOException {
        script = new SqlScript(new File("conf//queries//script.sql"), connection);
    }

    private List<ArrayList<String>> fillData(ResultSet resultSet) throws SQLException {

        List<String> fields = fields(resultSet.getMetaData());
        debug("The query returned " + fields.size() + " fields");

        List<ArrayList<String>> result = new LinkedList<>();

        while (resultSet.next()) {
            ArrayList<String> bill = new ArrayList<>();

            for (String field : fields) {
                String value = resultSet.getString(field);
                bill.add(value == null ? "" : value);
            }

            result.add(bill);
        }

        debug("The number of bills => " + result.size());
        return result;
    }

    public List<ArrayList<String>> getBills(Date month, MkdChs mkdChs, CisDivision cisDivision, String state) throws SQLException {

        debug("The method getBills was invoked:\n" +
                "\tDate month <= " + month + "\n" +
                "\tMkdChs mkdChs <= " + mkdChs + "\n" +
                "\tCisDivision cisDivision <= " + cisDivision + "\n" +
                "\tString state <= " + state);


        final java.sql.Date dbMonth = new java.sql.Date(month.getTime());

        ResultSet resultSet;
        if (mkdChs == MkdChs.MKD) {
            script.executeProcedureMkd(dbMonth, cisDivision, state, null);
            resultSet = script.getKvitMkdCursor(dbMonth, cisDivision, state, null);
        } else {
            script.executeProcedureNotMkd(dbMonth, cisDivision, state);
            resultSet = script.getKvitNotMkdCursor(dbMonth, cisDivision, state);
        }

        return fillData(resultSet);
    }

    public List<ArrayList<String>> getBills(Date month, String mkdPremiseId) throws SQLException {

        trace("The method getBills was invoked:\n" +
                "\tDate month <= " + month + "\n" +
                "\tString mkdPremiseId <= " + mkdPremiseId);

        final java.sql.Date dbMonth = new java.sql.Date(month.getTime());

        script.executeProcedureMkd(dbMonth, null, null, mkdPremiseId);

        ResultSet resultSet = script.getKvitMkdCursor(dbMonth, null, null, mkdPremiseId);

        return fillData(resultSet);
    }

    private List<String> fields(ResultSetMetaData resultSetMetaData) throws SQLException {

        List<String> result = new ArrayList<>(250);

        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
            result.add(resultSetMetaData.getColumnLabel(i));
        }

        return result;
    }

}
