package alararestaurant.domain.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Column(name = "name")
    @NotNull(message = "Category name cannot be null")
    @Length(min = 3, max = 30, message = "Category name must be between 3 and 30 characters")
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Item> items;
}
