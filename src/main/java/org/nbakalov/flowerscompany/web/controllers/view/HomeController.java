package org.nbakalov.flowerscompany.web.controllers.view;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Role;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.services.FlowersBatchService;
import org.nbakalov.flowerscompany.services.services.OrderService;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.nbakalov.flowerscompany.web.annotations.PageTitle;
import org.nbakalov.flowerscompany.web.controllers.BaseController;
import org.nbakalov.flowerscompany.web.models.view.order.OrderViewModel;
import org.nbakalov.flowerscompany.web.models.view.warehouese.AllWarehousesViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.PageTitleConstants.HOME;
import static org.nbakalov.flowerscompany.constants.PageTitleConstants.INDEX;
import static org.nbakalov.flowerscompany.constants.RoleConstants.*;

@Controller
@AllArgsConstructor
public class HomeController extends BaseController {

  private final FlowersBatchService flowersBatchService;
  private final WarehouseService warehouseService;
  private final OrderService orderService;
  private final ModelMapper modelMapper;

  @GetMapping("/index")
  @PreAuthorize("isAnonymous()")
  @PageTitle(INDEX)
  public ModelAndView index() {
    return view("index");
  }

  @GetMapping("/home")
  @PageTitle(HOME)
  public ModelAndView home(Authentication authResult, ModelAndView modelAndView, Principal principal) {

    Set<String> userRoles = getUserRoles(authResult);

    getUserModelAndViewByUserRoles(modelAndView, userRoles, principal);

    return view("home", modelAndView);
  }


  private Set<String> getUserRoles(Authentication authResult) {

    return authResult.getAuthorities()
            .stream()
            .map(o -> (Role) o)
            .map(Role::getAuthority)
            .collect(Collectors.toSet());
  }

  private ModelAndView getUserModelAndViewByUserRoles(ModelAndView modelAndView,
                                                      Set<String> userRoles,
                                                      Principal principal) {

    if (userRoles.contains(CUSTOMER) && !userRoles.contains(ROOT)) {
      getCustomerModelAndView(modelAndView, principal);
    } else if (userRoles.contains(OPERATOR) && !userRoles.contains(ADMIN)) {
      getOperatorModelAndView(modelAndView);
    } else {
      geAdminModelAndView(modelAndView);
    }

    return modelAndView;
  }

  private ModelAndView geAdminModelAndView(ModelAndView modelAndView) {
    List<OrderViewModel> findAllOrders =
            orderService.findAllOrders()
                    .stream()
                    .map(orderServiceModel ->
                            modelMapper.map(orderServiceModel, OrderViewModel.class))
                    .collect(Collectors.toList());

    modelAndView.addObject("orders", findAllOrders);

    Long warehousesCount = warehouseService.getWarehousesCount();
    modelAndView.addObject("warehousesCount", warehousesCount);

    List<AllWarehousesViewModel> allWarehouses = warehouseService.findAllWarehouses()
            .stream()
            .map(warehouseServiceModel ->
                    modelMapper.map(warehouseServiceModel, AllWarehousesViewModel.class))
            .collect(Collectors.toList());

    modelAndView.addObject("warehouses", allWarehouses);

    return modelAndView;
  }

  private ModelAndView getOperatorModelAndView(ModelAndView modelAndView) {
    List<FlowersBatchServiceModel> todaysBatches =
            flowersBatchService.findAllBatchesRegisteredToday();

    modelAndView.addObject("todaysBatchesCount", todaysBatches.size());

    return modelAndView;
  }

  private ModelAndView getCustomerModelAndView(ModelAndView modelAndView, Principal principal) {

    List<OrderViewModel> myOrders =
            orderService.findAllMyOrders(principal.getName())
                    .stream()
                    .map(orderServiceModel ->
                            modelMapper.map(orderServiceModel, OrderViewModel.class))
                    .collect(Collectors.toList());

    modelAndView.addObject("orders", myOrders);
    return modelAndView;
  }

}
