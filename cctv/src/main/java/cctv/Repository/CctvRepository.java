package cctv.Repository;

import cctv.Entity.Cctv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CctvRepository extends JpaRepository<Cctv, Long> {
    List<Cctv> findById(List<Long> cctvIds);
    Cctv findByCctvId(Long cctvId);
}
