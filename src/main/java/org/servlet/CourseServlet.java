package org.servlet;

import org.ejbService.CourseService;
import org.model.course;
import jakarta.ejb.EJB;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.json.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/courses")
public class CourseServlet extends HttpServlet {

    @EJB
    private CourseService courseService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String idParam = req.getParameter("id");

        if (idParam != null) {
            try {
                Long id = Long.parseLong(idParam);
                course c = courseService.find(id);
                if (c == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Course not found\"}");
                    return;
                }
                resp.getWriter().write(Json.createObjectBuilder()
                        .add("id", c.getId())
                        .add("name", c.getName())
                        .build()
                        .toString());
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid id format\"}");
            }
        } else {
            List<course> courses = courseService.findAll();
            JsonArrayBuilder arr = Json.createArrayBuilder();
            for (course c : courses) {
                arr.add(Json.createObjectBuilder()
                        .add("id", c.getId())
                        .add("name", c.getName())
                        .build());
            }
            resp.getWriter().write(arr.build().toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject json = Json.createReader(req.getInputStream()).readObject();
        course c = new course();
        c.setName(json.getString("name"));
        courseService.create(c);
        resp.setContentType("application/json");
        resp.getWriter().write(Json.createObjectBuilder()
                .add("message", "Course created")
                .add("id", c.getId())
                .build()
                .toString());
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject json = Json.createReader(req.getInputStream()).readObject();
        resp.setContentType("application/json");

        if (!json.containsKey("id")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Missing id\"}");
            return;
        }

        Long id;
        try {
            id = json.getJsonNumber("id").longValue();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid id format\"}");
            return;
        }

        course c = courseService.find(id);
        if (c == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Course not found\"}");
            return;
        }

        if (json.containsKey("name")) {
            c.setName(json.getString("name"));
        }

        courseService.update(c);
        resp.getWriter().write("{\"message\":\"Course updated\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        resp.setContentType("application/json");

        if (idParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Missing id\"}");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            course c = courseService.find(id);
            if (c == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Course not found\"}");
                return;
            }
            courseService.delete(id);
            resp.getWriter().write("{\"message\":\"Course deleted\"}");
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid id format\"}");
        }
    }
}
