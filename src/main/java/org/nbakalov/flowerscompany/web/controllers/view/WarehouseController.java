package org.nbakalov.flowerscompany.web.controllers.view;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.models.WarehouseCreateModel;
import org.nbakalov.flowerscompany.data.models.models.WarehouseUpdateModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.nbakalov.flowerscompany.web.controllers.BaseController;
import org.nbakalov.flowerscompany.web.models.view.AllWarehousesViewModel;
import org.nbakalov.flowerscompany.web.models.view.WarehouseUpdateViewModel;
import org.nbakalov.flowerscompany.web.models.view.WarehouseViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/warehouses")
@AllArgsConstructor
public class WarehouseController extends BaseController {

  private final WarehouseService warehouseService;
  private final ModelMapper modelMapper;

  @GetMapping("/create-warehouse")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView createWarehouse() {

    return view("/warehouses/create-warehouse");
  }

  @PostMapping("/create-warehouse")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView createWarehouseConfirm(@ModelAttribute WarehouseCreateModel model) {

    warehouseService.createWarehouse(modelMapper.map(model, WarehouseServiceModel.class));

    return redirect("/warehouses/all");
  }

  @GetMapping("/all")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView allWarehouses(ModelAndView modelAndView) {

    List<AllWarehousesViewModel> allWarehouses = warehouseService.findAllWarehouses()
            .stream()
            .map(warehouseServiceModel ->
                    modelMapper.map(warehouseServiceModel, AllWarehousesViewModel.class))
            .collect(Collectors.toList());

    modelAndView.addObject("warehouses", allWarehouses);

    return view("warehouses/all-warehouses", modelAndView);
  }

  @GetMapping("/details/{id}")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView warehouseDetails(@PathVariable String id, ModelAndView modelAndView) {

    WarehouseServiceModel warehouseServiceModel =
            warehouseService.findWarehouseById(id);

    WarehouseViewModel warehouseViewModel =
            modelMapper.map(warehouseServiceModel, WarehouseViewModel.class);

    modelAndView.addObject("warehouse", warehouseViewModel);

    return view("warehouses/warehouse-details", modelAndView);
  }

  @GetMapping("/edit/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView editWarehouse(@PathVariable String id, ModelAndView modelAndView) {

    WarehouseServiceModel warehouseServiceModel =
            warehouseService.findWarehouseById(id);

    WarehouseUpdateViewModel warehouseUpdateViewModel =
            modelMapper.map(warehouseServiceModel, WarehouseUpdateViewModel.class);

    modelAndView.addObject("warehouse", warehouseUpdateViewModel);

    return view("warehouses/edit-warehouse", modelAndView);
  }

  @PostMapping("/edit/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView editWarehouseConfirm(@PathVariable String id,
                                           @ModelAttribute WarehouseUpdateModel warehouseUpdateModel) {

    WarehouseServiceModel warehouseServiceModel =
            modelMapper.map(warehouseUpdateModel, WarehouseServiceModel.class);

    warehouseService.editWarehouse(id, warehouseServiceModel);

    return redirect("/warehouses/details/" + id);
  }

  @GetMapping("/delete/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView deleteWarehouse(@PathVariable String id, ModelAndView modelAndView) {

    WarehouseServiceModel warehouseServiceModel =
            warehouseService.findWarehouseById(id);

    WarehouseUpdateViewModel warehouseUpdateViewModel =
            modelMapper.map(warehouseServiceModel, WarehouseUpdateViewModel.class);

    modelAndView.addObject("warehouse", warehouseUpdateViewModel);

    return view("warehouses/delete-warehouse", modelAndView);
  }

  @PostMapping("/delete/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView deleteWarehouseConfirm(@PathVariable String id,
                                           @ModelAttribute WarehouseUpdateModel warehouseUpdateModel) {

    WarehouseServiceModel warehouseServiceModel =
            modelMapper.map(warehouseUpdateModel, WarehouseServiceModel.class);

    warehouseService.deleteWarehouse(id, warehouseServiceModel);

    return redirect("/warehouses/all");
  }

  @PostMapping("/empty-warehouse/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView emptyWarehouse(@PathVariable String id, ModelAndView modelAndView) {

    warehouseService.emptyWarehouse(id);

    WarehouseServiceModel warehouseServiceModel =
            warehouseService.findWarehouseById(id);

    WarehouseUpdateViewModel warehouseUpdateViewModel =
            modelMapper.map(warehouseServiceModel, WarehouseUpdateViewModel.class);

    modelAndView.addObject("warehouse", warehouseUpdateViewModel);

    return redirect("/warehouses/delete/" + id);
  }
}
