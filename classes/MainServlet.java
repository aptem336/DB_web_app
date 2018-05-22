package DB;

import java.io.IOException;
import java.io.PrintWriter;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Result</title>");
            out.println("<meta charset=\"windows-1251\">");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            out.println("<link rel=\"stylesheet\" href=\"css/styles.css\" type=\"text/css\"/>");
            out.println("</head>");
            out.println("<body onload=\"window.top.show_all()\">");
            out.println("<script>");
            out.println("function mark_row(new_id) {");
            out.println("\tif (window.parent.id !== -1) {");
            out.println("\t\tdocument.getElementById(window.parent.id).style.setProperty('background-color', '#ffffff');");
            out.println("\t} else {");
            out.println("\t\twindow.parent.show_edit();");
            out.println("\t}");
            out.println("\tif (window.parent.id !== new_id) {");
            out.println("\t\tdocument.getElementById(new_id).style.setProperty('background-color', '#ffda79');");
            out.println("\t\twindow.parent.id = new_id;");
            out.println("\t} else {");
            out.println("\t\twindow.parent.hide_edit();");
            out.println("\t}");
            out.println("}");
            out.println("</script>");
            try {
                switch (request.getParameter("type")) {
                    case "change_table":
                        out.println(
                                DBConnector.change_table(Integer.parseInt(request.getParameter("table_index"))));
                        break;
                    case "delete":
                        out.println(
                                DBConnector.delete(Integer.parseInt(request.getParameter("id"))));
                        break;
                    case "insert":
                        out.println(
                                DBConnector.insert(request.getParameter("data")));
                        break;
                    case "update":
                        out.println(
                                DBConnector.update(Integer.parseInt(request.getParameter("id")), request.getParameter("data")));
                        break;
                    case "plan":
                        out.print(
                                DBConnector.build_data_table("Учебный план", request.getParameter("query")));
                        break;
                    case "progress":
                        String[] progress_fields = request.getParameter("data").split(":");
                        out.print(
                                DBConnector.build_data_table("Успеваемость студента", "SELECT Аттестации.Семестр, Предметы.Название, Оценки.Балл FROM Предметы JOIN Аттестации ON Предметы.Шифр=Аттестации.Предмет JOIN Оценки ON Оценки.Аттестация=Аттестации.Шифр WHERE Оценки.НЗК=" + progress_fields[0] + " AND Аттестации.Семестр>=" + progress_fields[1] + " AND Аттестации.Семестр<=" + progress_fields[2] + " ORDER BY Аттестации.Семестр"));
                        break;
                    case "attestation":
                        String[] attestation_fields = request.getParameter("data").split(":");
                        out.print(
                                DBConnector.build_data_table("Список аттестаций", "SELECT Специальности.Название AS \"Специальность\", Предметы.Название AS \"Предмет\", Аттестации.Вид FROM Аттестации JOIN Предметы ON Аттестации.Предмет=Предметы.Шифр JOIN Специальности ON Аттестации.Специальность=Специальности.Шифр WHERE Аттестации.Семестр="+attestation_fields[0]+" ORDER BY Предметы.Название"));
                        break;
                    default:
                        out.print("bad type operation!");
                        break;
                }
            } catch (NamingException ex) {
                out.println("Ошибка подключения к БД!");
                out.println(ex.getMessage());
            }
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
