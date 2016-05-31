package services.exporters;

import services.parameters.Format;

import java.sql.ResultSet;

/**
 * @author Косых Евгений
 */
public abstract class Exporter {

    public static synchronized Exporter getExporter(Format kvitFormat) {

        if (kvitFormat == Format.CSV)
            return new CsvExporter();
        else if (kvitFormat == Format.DBF)
            return new DbfExporter();
        else
            return null;
    }


    public abstract void export(ResultSet kvitResultSet, ResultSet reestrResultSet);
}
