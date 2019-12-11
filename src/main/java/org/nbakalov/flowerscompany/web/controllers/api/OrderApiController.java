package org.nbakalov.flowerscompany.web.controllers.api;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.nbakalov.flowerscompany.services.services.OrderService;
import org.nbakalov.flowerscompany.web.controllers.BaseApiController;
import org.nbakalov.flowerscompany.web.models.api.MyOrdersApiModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders/api/")
@AllArgsConstructor
@NoArgsConstructor
public class OrderApiController extends BaseApiController {

  private OrderService orderService;

  @GetMapping("/my-orders")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public List<MyOrdersApiModel> findMyOrders(Principal principal) {

    return orderService.findAllMyOrders(principal.getName())
            .stream()
            .map(orderServiceModel ->
                    modelMapper.map(orderServiceModel, MyOrdersApiModel.class))
            .collect(Collectors.toList());
  }
}