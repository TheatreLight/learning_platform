package mephi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<mephi.entity.Module, Long> {
    public List<mephi.entity.Module> findByCourseId(Long id);
}
