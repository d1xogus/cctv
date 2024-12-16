package cctv.Repository;

import cctv.Entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findByImage_ImageIdIn(List<Long> imageIds);
}
