package db;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Servlet extends HttpServlet {

    private static TableHandler tableHandler;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            request.setCharacterEncoding("UTF-8");
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("</head>");
            out.println("<body>");
            out.println("<link rel=\"stylesheet\" href=\"DB/styles.css\">");
            HashMap<String, String[]> parameters = new HashMap<>(request.getParameterMap());
            String[] type = parameters.get("type");
            if (type != null) {
                switch (type[0]) {
                    case "":
                        tableHandler = DBHandler.TABLE_HANDLERS.get(parameters.get("table_name")[0]);
                        tableHandler.prepareStatements();
                        break;
                    case "apply":
                        tableHandler.apply(parameters);
                        break;
                    case "delete":
                        tableHandler.delete(parameters);
                        break;
                }
                out.println("\t<form id=\"data_form\" action=\"\" target=\"data_frame\" method=\"post\">");
                out.printf("\t\t<table id=\"data_table\">\n%s\t\t</table>\n", tableHandler.getHTMLTable());
                out.printf("\t\t<button id=\"apply\" name=\"type\" value=\"%s\" hidden=\"true\">%s</button>\n", "apply", "Применить");
                out.printf("\t\t<button id=\"reset\" name=\"type\" value=\"%s\"  hidden=\"true\" formnovalidate autofocus>%s</button>\n", "reset", "Отменить");
                out.println("\t</form>");
                out.println("\t<script type=\"text/javascript\" src=\"DB/datatable.js\"></script>");
            } else {
                out.println("\t<form action=\"\" target=\"data_frame\" method=\"post\">");
                DBHandler.TABLE_HANDLERS.keySet().forEach((tableName) -> {
                    out.printf("\t\t<button name=\"table_name\" value=\"%1$s\">%1$s</button>\n", tableName);
                });
                out.println("\t\t<input type=\"hidden\" name=\"type\" value=\"\">");
                out.println("\t</form>");
                out.println("\t<iframe id=\"data_frame\" name=\"data_frame\"></iframe>");
                out.println("\t<script type=\"text/javascript\" src=\"DB/tablebuttons.js\"></script>");
            }
            out.println("</body>");
            out.println("</html>");
        } catch (SQLException | NamingException ex) {
            out.printf("<div id=\"exception\">%s</div>", ex.getMessage());
            System.out.println(ex.getMessage());
        } finally {
            out.close();
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
        DBHandler.init();
    }

    @Override
    public void destroy() {
        super.destroy();
        DBHandler.destroy();
    }

    //<editor-fold defaultstate="collapsed" desc="go/get">
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    //</editor-fold>
}
