package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Order;
import org.nbakalov.flowerscompany.data.models.entities.Status;
import org.nbakalov.flowerscompany.data.models.entities.User;
import org.nbakalov.flowerscompany.data.repositories.OrderRepository;
import org.nbakalov.flowerscompany.services.models.OrderServiceModel;
import org.nbakalov.flowerscompany.services.models.UserServiceModel;
import org.nbakalov.flowerscompany.services.services.OrderService;
import org.nbakalov.flowerscompany.services.services.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final UserService userService;
  private final ModelMapper modelMapper;

  @Override
  public OrderServiceModel makeOrder(OrderServiceModel serviceModel, String customerName) {

    UserServiceModel customer = userService.findByUsername(customerName);

    serviceModel.setCustomer(customer);
    serviceModel.setOrderDate(LocalDate.now());
    serviceModel.setStatus(Status.INPROGRESS);

    Order order = modelMapper.map(serviceModel, Order.class);
    orderRepository.saveAndFlush(order);

    return modelMapper.map(order, OrderServiceModel.class);
  }

  @Override
  public List<OrderServiceModel> findAllMyOrders(String username) {

    User customer = modelMapper.map(userService.findByUsername(username), User.class);

    return orderRepository.findAllByCustomerOrderByOrderDateDescQuantityDesc(customer)
            .stream()
            .map(order -> modelMapper.map(order, OrderServiceModel.class))
            .collect(Collectors.toList());
  }
}
