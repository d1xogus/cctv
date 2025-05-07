package cctv.Repository;

import cctv.Entity.Cctv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CctvRepository extends JpaRepository<Cctv, String> {
    List<Cctv> findByStreamIn(List<String> stream);
    Cctv findByStream(String stream);
}
