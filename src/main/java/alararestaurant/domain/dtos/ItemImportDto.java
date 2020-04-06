package alararestaurant.domain.dtos;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemImportDto {

    @Expose
    private String name;

    @Expose
    private BigDecimal price;

    @Expose
    private String category;
}
