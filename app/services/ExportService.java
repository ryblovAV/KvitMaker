package services;

import static play.Logger.*;

public class ExportService {

    public static String run(String codebase) {
        info("codeBase = " + codebase);
        return "Success";
    }

}
