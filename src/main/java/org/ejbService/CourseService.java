package org.ejbService;

import org.model.course;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CourseService {

    @PersistenceContext(unitName="PU_MSSQL")
    private EntityManager em;

    public course create(course c){ em.persist(c); return c; }
    public course update(course c){ return em.merge(c); }
    public void delete(Long id){
        course c = em.find(course.class, id);
        if(c != null) em.remove(c);
    }
    public course find(Long id){ return em.find(course.class, id); }
    public List<course> findAll(){
        return em.createQuery("SELECT c FROM course c", course.class).getResultList();
    }
}
