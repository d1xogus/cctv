package cctv.Repository;

import cctv.Entity.Cctv;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CctvRepository extends JpaRepository<Cctv, Long> {
}
