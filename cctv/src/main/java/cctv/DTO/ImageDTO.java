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

    private CctvDTO cctv;

    private String path;

    private String time;


    public ImageDTO(Image image) {
        this.imageId = image.getImageId();
        this.name = image.getName();
        this.path = image.getPath();
        this.time = image.getTime();
        this.cctv = new CctvDTO(image.getCctv().getCctvId(), image.getCctv().getLocation(), image.getCctv().getCctvDate(), image.getCctv().getCctvName()
                , image.getCctv().getWebcamId(), image.getCctv().getStream()); //  LazyInitializationException 방지
    }

}
