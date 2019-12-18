package org.nbakalov.flowerscompany.web.controllers.view;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.enums.Variety;
import org.nbakalov.flowerscompany.data.models.models.orders.OrderCreateModel;
import org.nbakalov.flowerscompany.data.models.models.orders.OrderUpdateModel;
import org.nbakalov.flowerscompany.services.models.OrderServiceModel;
import org.nbakalov.flowerscompany.services.services.OrderService;
import org.nbakalov.flowerscompany.validations.orders.OrderCreateValidation;
import org.nbakalov.flowerscompany.web.annotations.PageTitle;
import org.nbakalov.flowerscompany.web.controllers.BaseController;
import org.nbakalov.flowerscompany.web.models.view.order.OrderCancelViewModel;
import org.nbakalov.flowerscompany.web.models.view.order.OrderEditViewModel;
import org.nbakalov.flowerscompany.web.models.view.order.OrderViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.PageTitleConstants.*;

@Controller
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController extends BaseController {

  private final OrderService orderService;
  private final OrderCreateValidation orderCreateValidation;
  private final ModelMapper modelMapper;

  @GetMapping("/make-order")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  @PageTitle(MAKE_ORDER)
  public ModelAndView makeOrder(ModelAndView modelAndView,
                                @ModelAttribute OrderCreateModel createModel) {

    List<Variety> varieties = Variety.stream().collect(Collectors.toList());
    modelAndView.addObject("varieties", varieties);
    modelAndView.addObject("order", createModel);

    return view("/orders/make-order", modelAndView);
  }

  @PostMapping("/make-order")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public ModelAndView makeOrderConfirm(@ModelAttribute OrderCreateModel createModel,
                                       Principal principal, ModelAndView modelAndView,
                                       BindingResult bindingResult) {

    orderCreateValidation.validate(createModel, bindingResult);

    if (bindingResult.hasErrors()) {
      createModel.setQuantity(null);

      modelAndView.addObject("order", createModel);

      return view("orders/make-order", modelAndView);
    }

    OrderServiceModel serviceModel =
            modelMapper.map(createModel, OrderServiceModel.class);

    orderService.makeOrder(serviceModel, principal.getName());

    return redirect("/orders/my-orders");
  }

  @GetMapping("/my-orders")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  @PageTitle(MY_ORDERS)
  public ModelAndView myOrders(Principal principal, ModelAndView modelAndView) {

    List<OrderViewModel> myOrders =
            orderService.findAllMyOrders(principal.getName())
                    .stream()
                    .map(orderServiceModel ->
                            modelMapper.map(orderServiceModel, OrderViewModel.class))
                    .collect(Collectors.toList());

    modelAndView.addObject("orders", myOrders);

    return view("/orders/my-orders", modelAndView);
  }

  @GetMapping("/edit-order/{id}")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  @PageTitle(EDIT_ORDER)
  public ModelAndView editOrder(@PathVariable String id, ModelAndView modelAndView) {

    OrderServiceModel serviceModel = orderService.findOrderById(id);

    OrderEditViewModel viewModel = modelMapper.map(serviceModel, OrderEditViewModel.class);

    modelAndView.addObject("order", viewModel);

    List<Variety> varieties = Variety.stream().collect(Collectors.toList());
    modelAndView.addObject("varieties", varieties);

    return view("/orders/edit-order", modelAndView);
  }

  @PostMapping("/edit-order/{id}")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public ModelAndView editOrderConfirm(@PathVariable String id,
                                       @ModelAttribute OrderUpdateModel updateModel) {

    OrderServiceModel serviceModel =
            modelMapper.map(updateModel, OrderServiceModel.class);

    orderService.editOrder(id, serviceModel);

    return redirect("/orders/my-orders");
  }

  @GetMapping("/cancel-order/{id}")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  @PageTitle(DELETE_ORDER)
  public ModelAndView cancelOrder(@PathVariable String id, ModelAndView modelAndView) {

    OrderServiceModel serviceModel =
            orderService.findOrderById(id);

    OrderCancelViewModel viewModel =
            modelMapper.map(serviceModel, OrderCancelViewModel.class);

    modelAndView.addObject("order", viewModel);

    return view("/orders/cancel-order", modelAndView);
  }

  @PostMapping("/cancel-order/{id}")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public ModelAndView cancelOrderConfirm(@PathVariable String id) {

    orderService.cancelOrder(id);

    return redirect("/orders/my-orders");
  }

  @GetMapping("/all")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PageTitle(ALL_ORDERS)
  public ModelAndView allOrders(ModelAndView modelAndView) {

    List<OrderViewModel> findAllOrders =
            orderService.findAllOrders()
                    .stream()
                    .map(orderServiceModel ->
                            modelMapper.map(orderServiceModel, OrderViewModel.class))
                    .collect(Collectors.toList());

    modelAndView.addObject("orders", findAllOrders);

    return view("/orders/all-orders", modelAndView);
  }

  @PostMapping("/review-order/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView reviewOrder(@PathVariable String id, Principal principal) {

    orderService.reviewOrder(id, principal.getName());

    return redirect("/orders/all");
  }
}
