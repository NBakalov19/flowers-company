package org.nbakalov.flowerscompany.data.repositories;

import org.nbakalov.flowerscompany.data.models.entities.Order;
import org.nbakalov.flowerscompany.data.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {


  List<Order> findAllByCustomerOrderByOrderDateDescQuantityDesc(User customer);
}
