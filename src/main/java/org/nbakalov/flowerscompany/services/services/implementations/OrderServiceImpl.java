package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Order;
import org.nbakalov.flowerscompany.data.models.entities.Status;
import org.nbakalov.flowerscompany.data.models.entities.User;
import org.nbakalov.flowerscompany.data.repositories.OrderRepository;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.OrderServiceModel;
import org.nbakalov.flowerscompany.services.models.UserServiceModel;
import org.nbakalov.flowerscompany.services.services.FlowersBatchService;
import org.nbakalov.flowerscompany.services.services.OrderService;
import org.nbakalov.flowerscompany.services.services.UserService;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.FlowersBatchConstants.TODAY;
import static org.nbakalov.flowerscompany.data.models.entities.Status.APPROVED;
import static org.nbakalov.flowerscompany.data.models.entities.Status.DENIED;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final UserService userService;
  private final FlowersBatchService flowersBatchService;
  private final ModelMapper modelMapper;

  @Override
  public OrderServiceModel makeOrder(OrderServiceModel serviceModel, String customerName) {

    UserServiceModel customer = userService.findByUsername(customerName);

    serviceModel.setCustomer(customer);
    serviceModel.setOrderDate(TODAY);
    serviceModel.setStatus(Status.INPROGRESS);

    Order order = modelMapper.map(serviceModel, Order.class);
    orderRepository.saveAndFlush(order);

    return modelMapper.map(order, OrderServiceModel.class);
  }

  @Override
  public OrderServiceModel findOrderById(String id) {

    return orderRepository.findById(id)
            .map(order -> modelMapper.map(order, OrderServiceModel.class))
            .orElseThrow(() -> new NoResultException("Order not found."));
  }

  @Override
  public List<OrderServiceModel> findAllOrders() {

    return orderRepository.findAllByOrderByOrderDateDescQuantityDesc()
            .stream()
            .map(order -> modelMapper.map(order, OrderServiceModel.class))
            .collect(Collectors.toList());
  }

  @Override
  public List<OrderServiceModel> findAllMyOrders(String username) {

    User customer = modelMapper.map(userService.findByUsername(username), User.class);

    return orderRepository.findAllByCustomerOrderByOrderDateDescQuantityDesc(customer)
            .stream()
            .map(order -> modelMapper.map(order, OrderServiceModel.class))
            .collect(Collectors.toList());
  }

  @Override
  public OrderServiceModel editOrder(String id, OrderServiceModel updateModel) {

    OrderServiceModel oldOrder =
            orderRepository.findById(id)
                    .map(order -> modelMapper.map(order, OrderServiceModel.class))
                    .orElseThrow(() -> new NoResultException("Order not found."));

    oldOrder.setVariety(updateModel.getVariety());
    oldOrder.setQuantity(updateModel.getQuantity());
    oldOrder.setBunchesPerTray(updateModel.getBunchesPerTray());

    Order order = modelMapper.map(oldOrder, Order.class);
    orderRepository.saveAndFlush(order);

    return modelMapper.map(order, OrderServiceModel.class);
  }

  @Override
  public void cancelOrder(String id) {

    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NoResultException("Order not found."));

    orderRepository.delete(order);
  }

  @Override
  public void reviewOrder(String id) {

    OrderServiceModel order = findOrderById(id);

    if (order.getVariety() != null) {

      List<FlowersBatchServiceModel> batches = flowersBatchService
              .findAllBatchesByVarietyAndBunchesPerTray(order.getVariety(), order.getBunchesPerTray());

      processOrder(order, batches);

    } else {
      List<FlowersBatchServiceModel> batches =
              flowersBatchService.findAllBatchesByBunchesPerTray(order.getBunchesPerTray());

      processOrder(order, batches);
    }
  }

  private void deniesOrder(OrderServiceModel order) {
    order.setStatus(DENIED);
    order.setFinishedOn(TODAY);
    orderRepository.saveAndFlush(modelMapper.map(order, Order.class));
  }

  private void approveOrder(OrderServiceModel order) {
    order.setStatus(APPROVED);
    order.setFinishedOn(TODAY);
    orderRepository.saveAndFlush(modelMapper.map(order, Order.class));
  }

  private void processOrder(OrderServiceModel order, List<FlowersBatchServiceModel> batches) {

    if (batches.isEmpty()) {
      deniesOrder(order);
    } else {

      int batchesTotalTrays = batches.stream().mapToInt(FlowersBatchServiceModel::getTrays).sum();

      if (order.getQuantity() > batchesTotalTrays) {
        deniesOrder(order);
      } else {
        int orderQuantity = order.getQuantity();

        for (FlowersBatchServiceModel batch : batches) {
          int currBatchTrays = batch.getTrays();

          if (orderQuantity > currBatchTrays) {
            flowersBatchService.deleteBatch(batch.getId());
            orderQuantity -= currBatchTrays;
          } else if (orderQuantity < currBatchTrays) {
            int leftTrays = currBatchTrays - orderQuantity;
            batch.setTrays(leftTrays);
            flowersBatchService.editFlowerBatch(batch.getId(), batch);
            approveOrder(order);
            break;
          } else {
            flowersBatchService.deleteBatch(batch.getId());
            approveOrder(order);
            break;
          }
        }
      }
    }
  }
}