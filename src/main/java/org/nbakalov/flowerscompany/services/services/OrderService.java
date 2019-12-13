package org.nbakalov.flowerscompany.services.services;

import org.nbakalov.flowerscompany.services.models.OrderServiceModel;

import java.util.List;

public interface OrderService {
  OrderServiceModel makeOrder(OrderServiceModel serviceModel, String username);

  OrderServiceModel findOrderById(String id);

  OrderServiceModel editOrder(String id, OrderServiceModel updateModel);

  List<OrderServiceModel> findAllOrders();

  List<OrderServiceModel> findAllMyOrders(String customerName);

  void cancelOrder(String id);

  void reviewOrder(String id);
}
