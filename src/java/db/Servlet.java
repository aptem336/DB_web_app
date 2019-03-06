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
                out.printf("\t<span class=\"table_title\">%s</span>\n\t<br>\n", table_name);
                switch (type[0]) {
                    case "insert":
                    case "update":
                        out.println("\t<form action=\"\" target=\"data_frame\" method=\"post\">");
                        out.printf("\t\t<input type=\"hidden\" name=\"table_name\" value=\"%s\">\n", table_name);
                        out.printf("\t\t<table id=\"input_table\">\n%s\t\t</table>\n\t<br>\n", DBHandler.TABLES.get(table_name).buildHTMLInput(Integer.parseInt(parameters.get("row_index")[0])));
                        out.printf("\t<button type=\"submit\" name=\"type\" value=\"%s\">Применить</button>\n", type[0] + "_apply");
                        out.println("\t</form>");
                        break;
                    case "insert_apply":
                        try {
                            out.printf("\t<table id=\"data_table\">\n%s\t</table>\n", DBHandler.TABLES.get(table_name).insert(parameters));
                        } catch (NamingException | SQLException ex) {
                            System.out.println(ex.getMessage());
                        }
                        break;
                    case "update_apply":
                        break;
                    default:
                        out.println("\t<script type=\"text/javascript\">");
                        out.printf("\t\tparent.document.getElementById('row_index').setAttribute('value', '%d');\n", -1);
                        out.printf("\t\tparent.document.getElementById('table_name').setAttribute('value', '%s');\n", table_name);
                        out.println("\t</script>");
                        out.printf("\t<table id=\"data_table\">\n%s\t</table>\n", DBHandler.TABLES.get(table_name).buildHTMLData());
                }
                out.println("<script type=\"text/javascript\" src=\"DB/script.js\"></script>");
            } else {
                out.println("\t<form action=\"\" target=\"data_frame\" method=\"post\">");
                DBHandler.TABLES.keySet().forEach((record) -> {
                    out.printf("\t\t<button type=\"submit\" name=\"table_name\" value=\"%1$s\">%1$s</button>\n", record);
                });
                out.println("\t\t<input type=\"hidden\" name=\"type\" value=\"\">");
                out.println("\t</form>");

                out.println("\t<form action=\"\" target=\"data_frame\" method=\"post\">");
                out.println("\t\t<button type=\"submit\" name=\"type\" value=\"insert\">Добавить</button>");
                out.println("\t\t<button type=\"submit\" name=\"type\" value=\"update\">Изменить</button>");
                out.println("\t\t<button type=\"submit\" name=\"type\" value=\"delete\">Удалить</button>");
                out.println("\t\t<input id=\"table_name\" type=\"hidden\" name=\"table_name\">");
                out.println("\t\t<input id=\"row_index\" type=\"hidden\" name=\"row_index\" value=\"-1\">");
                out.println("\t</form>");
                out.println("\t<iframe id=\"data_frame\" name=\"data_frame\"></iframe>");
            }
            out.println("</body>");
            out.println("</html>");
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
