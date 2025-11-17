package org.servlet;

import org.ejbService.StudentService;
import org.model.student;
import jakarta.ejb.EJB;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.json.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    @EJB
    private StudentService studentService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        resp.setContentType("application/json");
        if(idParam != null){
            Long id = Long.valueOf(idParam);
            student s = studentService.find(id);
            if(s == null){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Student not found\"}");
                return;
            }
            resp.getWriter().write("{\"id\":" + s.getId() + ",\"name\":\"" + s.getName() + "\",\"email\":\"" + s.getEmail() + "\"}");
        } else {
            List<student> students = studentService.findAll();
            JsonArrayBuilder arr = Json.createArrayBuilder();
            for(student s : students){
                arr.add(Json.createObjectBuilder()
                        .add("id", s.getId())
                        .add("name", s.getName())
                        .add("email", s.getEmail()));
            }
            resp.getWriter().write(arr.build().toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject json = Json.createReader(req.getInputStream()).readObject();
        student s = new student();
        s.setName(json.getString("name"));
        s.setEmail(json.getString("email"));
        studentService.create(s);
        resp.getWriter().write("{\"message\":\"Student created\",\"id\":" + s.getId() + "}");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject json = Json.createReader(req.getInputStream()).readObject();
        Long id = json.getJsonNumber("id").longValue();
        student s = studentService.find(id);
        if(s == null){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Student not found\"}");
            return;
        }
        s.setName(json.getString("name"));
        s.setEmail(json.getString("email"));
        studentService.update(s);
        resp.getWriter().write("{\"message\":\"Student updated\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        if(idParam == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Missing id\"}");
            return;
        }
        studentService.delete(Long.valueOf(idParam));
        resp.getWriter().write("{\"message\":\"Student deleted\"}");
    }
}