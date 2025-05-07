package cctv.Repository;

import cctv.Entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByCctv_StreamIn(List<String> stream);
}
