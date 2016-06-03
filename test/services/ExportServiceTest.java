package services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.parameters.CisDivision;
import services.parameters.MkdChs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Косых Евгений
 */
public class ExportServiceTest {

    private Connection connection;

    private ExportService exportService;

    @Before
    public void setUp() throws Exception {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@proddb.lesk.ru:1521:LESKMIGR", "lcmccb", "lcmccb2l1");

        exportService = new ExportService(connection);
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void getBills() throws Exception {

        Date month = new SimpleDateFormat("dd.MM.yyyy").parse("01.04.2016");

        List<ArrayList<String>> bills = exportService.getBills(month, MkdChs.MKD, CisDivision.LESK, "13");

        assertTrue(bills.size() > 30_000);
    }

    @Test
    public void getBills1() throws Exception {

        Date month = new SimpleDateFormat("dd.MM.yyyy").parse("01.04.2016");

        List<ArrayList<String>> bills = exportService.getBills(month, MkdChs.CHS, CisDivision.LESK, "13");

        assertTrue(bills.size() > 25_000);
    }

    @Test
    public void getBills2() throws Exception {

        Date month = new SimpleDateFormat("dd.MM.yyyy").parse("01.04.2016");

        List<ArrayList<String>> bills = exportService.getBills(month, MkdChs.MKD, CisDivision.GESK, "88");

        assertTrue(bills.size() > 30_000);
    }

    @Test
    public void getBills3() throws Exception {

        Date month = new SimpleDateFormat("dd.MM.yyyy").parse("01.04.2016");

        List<ArrayList<String>> bills = exportService.getBills(month, MkdChs.CHS, CisDivision.GESK, "43");

        assertTrue(bills.size() > 1_000);
    }
}