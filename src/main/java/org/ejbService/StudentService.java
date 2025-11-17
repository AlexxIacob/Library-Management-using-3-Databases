package org.ejbService;

import org.model.student;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class StudentService {

    @PersistenceContext(unitName="PU_Postgres")
    private EntityManager em;

    public student create(student student) {
        em.persist(student);
        return student;
    }

    public student update(student student) {
        em.merge(student);
        return student;
    }

    public void delete(Long Id) {
        student student = em.find(student.class, Id);
        if (student != null) {
            em.remove(student);
        }
    }

    public student find(Long Id) {
        return em.find(student.class, Id);
    }

    public List<student> findAll() {
        return em.createQuery("SELECT s FROM student s", student.class).getResultList();
    }

}
