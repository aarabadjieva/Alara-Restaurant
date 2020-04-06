package alararestaurant.domain.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "items")
public class Item extends BaseEntity {

    @Column(name = "name", unique = true)
    @NotNull(message = "Item name cannot be null")
    @Length(min = 3, max = 30, message = "Item name must be between 3 and 30 characters")
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Column(name = "price")
    @NotNull(message = "Item price cannot be null")
    @DecimalMin(value = "0.01", message = "Item price must be at least 0.01")
    private BigDecimal price;

    @OneToMany(mappedBy = "item")
    private List<OrderItem> orderItems;
}
