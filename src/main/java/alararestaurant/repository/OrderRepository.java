package alararestaurant.repository;

import alararestaurant.domain.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("select o from Order o " +
            "where o.employee.position.name = 'Burger Flipper' " +
            "order by o.employee.name, o.id")
    List<Order> findAllByBurgerFlippersOrderByEmployeeNameAndOrderId();
}
