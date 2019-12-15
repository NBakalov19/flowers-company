package org.nbakalov.flowerscompany.web.controllers.view;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Role;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.services.FlowersBatchService;
import org.nbakalov.flowerscompany.services.services.OrderService;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.nbakalov.flowerscompany.web.controllers.BaseController;
import org.nbakalov.flowerscompany.web.models.view.order.OrderViewModel;
import org.nbakalov.flowerscompany.web.models.view.warehouese.AllWarehousesViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
  public ModelAndView index() {
    return view("index");
  }

  @GetMapping("/home")
  public void loginPageRedirect(HttpServletRequest request,
                                HttpServletResponse response, Authentication authResult)
          throws IOException, ServletException, IOException {

    Set<String> roles = authResult.getAuthorities()
            .stream()
            .map(o -> (Role) o)
            .map(Role::getAuthority)
            .collect(Collectors.toSet());

    if (roles.contains(CUSTOMER) && !roles.contains(ROOT)) {
      response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/home-customer"));
    } else if (roles.contains(OPERATOR) && !roles.contains(ADMIN)) {
      response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/home-operator"));
    } else if (roles.contains(OPERATOR) && roles.contains(ADMIN) && !roles.contains(ROOT)) {
      response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/home-admin"));
    } else if (roles.contains(ROOT)) {
      response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/home-root"));
    }
  }

  @GetMapping("/home-customer")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public ModelAndView customerHome(Principal principal, ModelAndView modelAndView) {

    List<OrderViewModel> myOrders =
            orderService.findAllMyOrders(principal.getName())
                    .stream()
                    .map(orderServiceModel ->
                            modelMapper.map(orderServiceModel, OrderViewModel.class))
                    .collect(Collectors.toList());

    modelAndView.addObject("orders", myOrders);

    return view("/home/home-customer", modelAndView);
  }

  @GetMapping("/home-operator")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView operatorHome(ModelAndView modelAndView) {

    List<FlowersBatchServiceModel> todaysBatches =
            flowersBatchService.findAllBatchesRegisteredToday();

    modelAndView.addObject("todaysBatchesCount", todaysBatches.size());

    return view("/home/home-operator", modelAndView);
  }

  @GetMapping("/home-admin")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView adminHome(ModelAndView modelAndView) {

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

    return view("/home/home-admin", modelAndView);
  }

  @GetMapping("/home-root")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public ModelAndView rootHome() {

    return view("/home/home-root");
  }

}
