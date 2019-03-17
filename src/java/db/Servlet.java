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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
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
                String table_name = parameters.get("table_name")[0];
                switch (type[0]) {
                    case "apply":
                        DBHandler.TABLE_HANDLERS.get(table_name).apply(parameters);
                        break;
                    case "delete":
                        DBHandler.TABLE_HANDLERS.get(table_name).delete(parameters);
                        break;
                }
                out.println("\t<form id=\"data_form\" action=\"\" target=\"data_frame\" method=\"post\">");
                out.printf("\t\t<table id=\"data_table\">\n%s\t\t</table>\n", DBHandler.TABLE_HANDLERS.get(table_name).getHTMLTable());
                out.printf("\t\t<button id=\"apply\" name=\"type\" value=\"%s\" hidden=\"true\">%s</button>\n", "apply", "Применить");
                out.printf("\t\t<button id=\"reset\" name=\"type\" value=\"%s\"  hidden=\"true\" formnovalidate autofocus>%s</button>\n", "", "Отменить");
                out.println("\t\t<input type=\"hidden\" name=\"table_name\" value=\"" + table_name + "\">");
                out.println("\t</form>");
                out.println("\t<script type=\"text/javascript\" src=\"DB/datatable.js\"></script>");
            } else {
                out.println("\t<form action=\"\" target=\"data_frame\" method=\"post\">");
                DBHandler.TABLE_HANDLERS.keySet().forEach((table_name) -> {
                    out.printf("\t\t<button name=\"table_name\" value=\"%1$s\">%1$s</button>\n", table_name);
                });
                out.println("\t\t<input type=\"hidden\" name=\"type\" value=\"\">");
                out.println("\t</form>");
                out.println("\t<iframe id=\"data_frame\" name=\"data_frame\"></iframe>");
                out.println("\t<script type=\"text/javascript\" src=\"DB/tablebuttons.js\"></script>");
            }
            out.println("</body>");
            out.println("</html>");
        } catch (SQLException | NamingException ex) {
            System.out.println(ex.getMessage());
        }
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
