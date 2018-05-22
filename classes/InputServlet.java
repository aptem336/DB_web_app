package DB;

import java.io.IOException;
import java.io.PrintWriter;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InputServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String type = request.getParameter("type");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Input data</title>");
            out.println("<meta charset=\"windows-1251\">");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            out.println("<link rel=\"stylesheet\" href=\"css/styles.css\" type=\"text/css\"/>");
            out.println("</head>");
            out.println("<body onload=\"check_valid()\">");
            out.println("<script>");
            out.println("function check_valid() {");
            out.println("\tinputs = document.getElementsByClassName('data_field');");
            out.println("\tfor (i = 0; i < inputs.length; i++) {");
            out.println("\t\tif (inputs[i].validity.valid === false) {");
            out.println("\t\t\tdocument.getElementById('submit').style.setProperty('display', 'none');");
            out.println("\t\t\treturn;");
            out.println("\t\t}");
            out.println("\t}");
            out.println("\tdocument.getElementById('submit').style.setProperty('display', 'block');");
            out.println("}");
            out.println("function apply() {");
            out.println("\tdata = '';");
            out.println("\tinputs = document.getElementsByClassName('data_field');");
            out.println("\tfor (i = 0; i < inputs.length; i++) {");
            out.println("\t\tdata += inputs[i].value+':';");
            out.println("\t}");
            out.print("\tdocument.location = 'MainServlet?type=" + type + "&data='+data");
            out.print((type.equals("update"))
                    ? "+'&id='+" + request.getParameter("id")
                    : "");
            out.println(";");
            out.println("}");
            out.println("</script>");
            try {
                switch (type) {
                    case "insert":
                        out.println(DBConnector.getRequiredInput());
                        break;
                    case "update":
                        out.println(DBConnector.getRequiredInput(request.getParameter("id")));
                        break;
                    case "progress":
                    case "attestation":
                        out.println(DBConnector.getRequiredReportInput(request.getParameter("query")));
                        break;
                }
            } catch (NamingException ex) {
                out.println("Ошибка подключения к БД!");
                out.println(ex.getMessage());
            }
            out.println("<button onclick=\"history.back();\">Отмена</button>");
            out.println("<button id=\"submit\" onclick=\"apply()\">Применить</button>");
            out.println("</body>");
            out.println("</html>");
        }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
