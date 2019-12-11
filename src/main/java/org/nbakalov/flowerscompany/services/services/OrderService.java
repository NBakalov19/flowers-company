package org.nbakalov.flowerscompany.services.services;


import org.nbakalov.flowerscompany.services.models.OrderServiceModel;
import org.nbakalov.flowerscompany.services.models.UserServiceModel;

import java.util.List;

public interface OrderService {
  OrderServiceModel makeOrder(OrderServiceModel serviceModel, String username);

  List<OrderServiceModel> findAllMyOrders(String customerName);
}
