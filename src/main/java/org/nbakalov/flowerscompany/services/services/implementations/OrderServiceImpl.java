package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.constants.FlowersBatchConstants;
import org.nbakalov.flowerscompany.data.models.entities.Order;
import org.nbakalov.flowerscompany.data.models.entities.User;
import org.nbakalov.flowerscompany.data.models.enums.Status;
import org.nbakalov.flowerscompany.data.repositories.OrderRepository;
import org.nbakalov.flowerscompany.errors.illegalservicemodels.IllegalOrderServiceModelException;
import org.nbakalov.flowerscompany.errors.notfound.OrderNotFoundException;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.LogServiceModel;
import org.nbakalov.flowerscompany.services.models.OrderServiceModel;
import org.nbakalov.flowerscompany.services.models.UserServiceModel;
import org.nbakalov.flowerscompany.services.services.FlowersBatchService;
import org.nbakalov.flowerscompany.services.services.LogService;
import org.nbakalov.flowerscompany.services.services.OrderService;
import org.nbakalov.flowerscompany.services.services.UserService;
import org.nbakalov.flowerscompany.services.validators.OrderServiceModelValidatorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.LogConstants.*;
import static org.nbakalov.flowerscompany.constants.OrderConstants.*;
import static org.nbakalov.flowerscompany.data.models.enums.Status.APPROVED;
import static org.nbakalov.flowerscompany.data.models.enums.Status.DENIED;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final UserService userService;
  private final FlowersBatchService flowersBatchService;
  private final OrderServiceModelValidatorService validatorService;
  private final ModelMapper modelMapper;
  private final LogService logService;

  @Override
  public OrderServiceModel makeOrder(OrderServiceModel serviceModel, String customerName) {

    if (!validatorService.isValid(serviceModel)) {
      throw new IllegalOrderServiceModelException(ORDER_BAD_CREDENTIALS);
    }

    UserServiceModel customer = userService.findByUsername(customerName);

    serviceModel.setCustomer(customer);
    serviceModel.setOrderDate(TODAY);
    serviceModel.setStatus(Status.INPROGRESS);

    Order order = modelMapper.map(serviceModel, Order.class);
    orderRepository.saveAndFlush(order);

    LogServiceModel log = createLog(customerName, MADE_ORDER);
    logService.saveLog(log);

    return modelMapper.map(order, OrderServiceModel.class);
  }


  @Override
  public OrderServiceModel editOrder(String id, OrderServiceModel updateModel) {

    OrderServiceModel oldOrder = orderRepository.findById(id)
            .map(order -> modelMapper.map(order, OrderServiceModel.class))
            .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

    oldOrder.setVariety(updateModel.getVariety());
    oldOrder.setQuantity(updateModel.getQuantity());
    oldOrder.setBunchesPerTray(updateModel.getBunchesPerTray());

    Order order = modelMapper.map(oldOrder, Order.class);
    orderRepository.saveAndFlush(order);

    LogServiceModel log = createLog(order.getCustomer().getUsername(), EDITED_ORDER);

    return modelMapper.map(order, OrderServiceModel.class);
  }

  @Override
  public OrderServiceModel findOrderById(String id) {

    return orderRepository.findById(id)
            .map(order -> modelMapper.map(order, OrderServiceModel.class))
            .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
  }

  @Override
  public List<OrderServiceModel> findAllOrders() {

    return orderRepository.findAllByOrderByOrderDateTimeDescQuantityDesc()
            .stream()
            .map(order -> modelMapper.map(order, OrderServiceModel.class))
            .collect(Collectors.toList());
  }

  @Override
  public List<OrderServiceModel> findAllMyOrders(String username) {

    User customer = modelMapper.map(userService.findByUsername(username), User.class);

    return orderRepository.findAllByCustomerOrderByOrderDateTimeDescQuantityDesc(customer)
            .stream()
            .map(order -> modelMapper.map(order, OrderServiceModel.class))
            .collect(Collectors.toList());
  }

  @Override
  public void cancelOrder(String id) {

    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

    orderRepository.delete(order);

    LogServiceModel log = createLog(order.getCustomer().getUsername(), DELETE_ORDER);
  }

  @Override
  public void reviewOrder(String id, String currentUser) {

    OrderServiceModel order = findOrderById(id);

    if (order.getVariety() != null) {

      List<FlowersBatchServiceModel> batches = flowersBatchService
              .findAllBatchesByVarietyAndBunchesPerTray(order.getVariety(), order.getBunchesPerTray());

      processOrder(order, batches, currentUser);

    } else {
      List<FlowersBatchServiceModel> batches =
              flowersBatchService.findAllBatchesByBunchesPerTray(order.getBunchesPerTray());

      processOrder(order, batches, currentUser);
    }


  }

  private void deniesOrder(OrderServiceModel order, String currentUser) {
    order.setStatus(DENIED);
    order.setFinishedOn(FlowersBatchConstants.TODAY);
    orderRepository.saveAndFlush(modelMapper.map(order, Order.class));

    LogServiceModel log = createLog(currentUser, REVIEW_ORDER);
    logService.saveLog(log);
  }

  private void approveOrder(OrderServiceModel order, String currentUser) {
    order.setStatus(APPROVED);
    order.setFinishedOn(FlowersBatchConstants.TODAY);
    orderRepository.saveAndFlush(modelMapper.map(order, Order.class));

    LogServiceModel log = createLog(currentUser, REVIEW_ORDER);
    logService.saveLog(log);
  }

  private void processOrder(OrderServiceModel order,
                            List<FlowersBatchServiceModel> batches,
                            String currentUser) {

    if (batches.isEmpty()) {
      deniesOrder(order, currentUser);
    } else {

      int batchesTotalTrays = batches.stream().mapToInt(FlowersBatchServiceModel::getTrays).sum();

      if (order.getQuantity() > batchesTotalTrays) {
        deniesOrder(order, currentUser);
      } else {
        int orderQuantity = order.getQuantity();

        for (FlowersBatchServiceModel batch : batches) {
          int currBatchTrays = batch.getTrays();

          if (orderQuantity > currBatchTrays) {
            flowersBatchService.deleteBatch(batch.getId(), currentUser);
            orderQuantity -= currBatchTrays;
          } else if (orderQuantity < currBatchTrays) {
            int leftTrays = currBatchTrays - orderQuantity;
            batch.setTrays(leftTrays);
            flowersBatchService.editFlowerBatch(batch.getId(), batch, currentUser);
            approveOrder(order, currentUser);
            break;
          } else {
            flowersBatchService.deleteBatch(batch.getId(), currentUser);
            approveOrder(order, currentUser);
            break;
          }
        }
      }
    }
  }

  private LogServiceModel createLog(String username, String description) {

    LogServiceModel log = new LogServiceModel();
    log.setCreatedOn(NOW);
    log.setUsername(username);
    log.setDescription(description);

    return log;
  }
}