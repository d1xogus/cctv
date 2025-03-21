package cctv.Repository;

import cctv.Entity.Image;
import cctv.Entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findByLogIdIn(List<Long> imageIds);
    List<Log> findByCctv_CctvIdIn(List<Long> cctvIds);
    boolean existsByImageId(Long imageId);
}
