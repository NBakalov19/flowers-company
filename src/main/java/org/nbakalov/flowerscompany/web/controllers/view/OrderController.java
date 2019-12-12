package org.nbakalov.flowerscompany.web.controllers.view;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Variety;
import org.nbakalov.flowerscompany.data.models.models.orders.OrderCreateModel;
import org.nbakalov.flowerscompany.services.models.OrderServiceModel;
import org.nbakalov.flowerscompany.services.services.OrderService;
import org.nbakalov.flowerscompany.web.controllers.BaseController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController extends BaseController {

  private final OrderService orderService;
  private final ModelMapper modelMapper;

  @GetMapping("/make-order")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public ModelAndView makeOrder(ModelAndView modelAndView) {

    List<Variety> varieties = Variety.stream().collect(Collectors.toList());
    modelAndView.addObject("varieties", varieties);

    return view("/orders/make-order", modelAndView);
  }

  @PostMapping("/make-order")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public ModelAndView makeOrderConfirm(@ModelAttribute OrderCreateModel createModel, Principal principal) {

    OrderServiceModel serviceModel =
            modelMapper.map(createModel, OrderServiceModel.class);

    orderService.makeOrder(serviceModel, principal.getName());

    return redirect("/orders/my-orders");
  }

  @GetMapping("/my-orders")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public ModelAndView myOrders() {

    return view("/orders/my-orders");
  }
}
