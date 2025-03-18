package cctv.DTO;

import cctv.Entity.Cctv;
import cctv.Entity.Image;
import lombok.*;


@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {
    private Long imageId;

    private String name;

    private Long cctvId;

    private String path;

    private String time;


    public ImageDTO(Image image) {
        this.imageId = image.getImageId();
        this.name = image.getName();
        this.path = image.getPath();
        this.time = image.getTime();
        this.cctvId = image.getCctv().getCctvId(); //  LazyInitializationException 방지
    }

}
