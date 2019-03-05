package db;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
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
                out.println("<script type=\"text/javascript\">");
                out.println("\tparent.document.getElementById('table_name').setAttribute('value', '" + table_name + "');");
                out.println("</script>");
                out.println("<span class=\"table_title\">" + table_name + "</span><br>");
                out.println(type[0] + "<br>");
                switch (type[0]) {
                    case "insert":
                        DBHandler.TABLES.get(table_name).column.keySet().forEach((name) -> {
                            out.println(name + "<br>");
                            out.println("<input type=\"" + TypeMapping.MAPPING.get(DBHandler.TABLES.get(table_name).column.get(name)).HTML_TYPE + "\"><br>");
                        });
                        break;
                }
                out.println("<script type=\"text/javascript\" src=\"DB/script.js\"></script>");
            } else {
                out.println("\t<form action=\"\" target=\"data_frame\" method=\"post\">");
                DBHandler.TABLES.keySet().forEach((record) -> {
                    out.println("\t\t<button name=\"table_name\" value=\"" + record + "\" type=\"submit\">" + record + "</button>");
                });
                out.println("\t\t<input type=\"hidden\" name=\"type\" value=\"\">");
                out.println("\t</form>");
                
                out.println("\t<form action=\"\" target=\"data_frame\" method=\"post\">");
                out.println("\t\t<button name=\"type\" value=\"insert\" type=\"submit\">Добавить</button>");
                out.println("\t\t<button name=\"type\" value=\"update\" type=\"submit\">Изменить</button>");
                out.println("\t\t<button name=\"type\" value=\"delete\" type=\"submit\">Удалить</button>");
                out.println("\t\t<input id=\"table_name\" type=\"hidden\" name=\"table_name\">");
                out.println("\t\t<input id=\"row_index\" type=\"hidden\" name=\"row_index\">");
                out.println("\t</form>");
                
                out.println("\t<iframe name=\"data_frame\" src=\"?type=\"\"></iframe>");
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
