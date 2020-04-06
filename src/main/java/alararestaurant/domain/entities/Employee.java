package alararestaurant.domain.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "employees")
public class Employee extends BaseEntity {

    @Column(name = "name")
    @NotNull(message = "Employee name cannot be null")
    @Length(min = 3, max = 30, message = "Employee name must be between 3 and 30 characters")
    private String name;

    @Column(name = "age")
    @NotNull(message = "Employee age cannot be null")
    @Min(value = 15, message = "Employee must be over 15 years old")
    @Max(value = 80, message = "Employee must be below 80 years old")
    private int age;

    @ManyToOne(optional = false)
    @JoinColumn(name = "position_id", referencedColumnName = "id")
    private Position position;

    @OneToMany(mappedBy = "employee")
    private List<Order> orders;
}
