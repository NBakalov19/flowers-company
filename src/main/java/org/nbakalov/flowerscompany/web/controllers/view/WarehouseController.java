package org.nbakalov.flowerscompany.web.controllers.view;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.models.warehouse.WarehouseCreateModel;
import org.nbakalov.flowerscompany.data.models.models.warehouse.WarehouseUpdateModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.services.LogService;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.nbakalov.flowerscompany.web.annotations.PageTitle;
import org.nbakalov.flowerscompany.web.controllers.BaseController;
import org.nbakalov.flowerscompany.web.models.view.warehouese.AllWarehousesViewModel;
import org.nbakalov.flowerscompany.web.models.view.warehouese.WarehouseDetailsViewModel;
import org.nbakalov.flowerscompany.web.models.view.warehouese.WarehouseUpdateViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.PageTitleConstants.*;

@Controller
@RequestMapping("/warehouses")
@AllArgsConstructor
public class WarehouseController extends BaseController {

  private final WarehouseService warehouseService;
  private final ModelMapper modelMapper;

  @GetMapping("/create-warehouse")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PageTitle(CREATE_WAREHOUSE)
  public ModelAndView createWarehouse() {

    return view("/warehouses/create-warehouse");
  }

  @PostMapping("/create-warehouse")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView createWarehouseConfirm(@ModelAttribute WarehouseCreateModel model,
                                             Principal principal) {

    WarehouseServiceModel serviceModel = modelMapper.map(model, WarehouseServiceModel.class);

    warehouseService.createWarehouse(serviceModel, principal.getName());

    return redirect("/warehouses/all");
  }

  @GetMapping("/all")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  @PageTitle(ALL_WAREHOUSES)
  public ModelAndView allWarehouses(ModelAndView modelAndView) {

    Long warehousesCount = warehouseService.getWarehousesCount();
    modelAndView.addObject("warehousesCount", warehousesCount);

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
  @PageTitle(DETAILS_WAREHOUSE)
  public ModelAndView warehouseDetails(@PathVariable String id, ModelAndView modelAndView) {

    WarehouseServiceModel warehouseServiceModel =
            warehouseService.findWarehouseById(id);

    WarehouseDetailsViewModel detailsViewModel =
            modelMapper.map(warehouseServiceModel, WarehouseDetailsViewModel.class);

    warehouseService.updateCurrCapacity(warehouseServiceModel);

    Long warehousesCount = warehouseService.getWarehousesCount();

    modelAndView.addObject("warehouse", detailsViewModel);
    modelAndView.addObject("warehousesCount", warehousesCount);

    return view("warehouses/warehouse-details", modelAndView);
  }

  @GetMapping("/edit/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PageTitle(EDIT_WAREHOUSE)
  public ModelAndView editWarehouse(@PathVariable String id, ModelAndView modelAndView) {

    WarehouseServiceModel warehouseServiceModel =
            warehouseService.findWarehouseById(id);

    WarehouseUpdateViewModel updateViewModel =
            modelMapper.map(warehouseServiceModel, WarehouseUpdateViewModel.class);

    modelAndView.addObject("warehouse", updateViewModel);

    return view("warehouses/edit-warehouse", modelAndView);
  }

  @PostMapping("/edit/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView editWarehouseConfirm(@PathVariable String id,
                                           @ModelAttribute WarehouseUpdateModel warehouseUpdateModel,
                                           Principal principal) {

    WarehouseServiceModel warehouseServiceModel =
            modelMapper.map(warehouseUpdateModel, WarehouseServiceModel.class);

    warehouseService.editWarehouse(id, warehouseServiceModel, principal.getName());

    return redirect("/warehouses/details/" + id);
  }

  @GetMapping("/delete/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PageTitle(DELETE_WAREHOUSE)
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
                                             @ModelAttribute WarehouseUpdateModel warehouseUpdateModel,
                                             Principal principal) {

    warehouseService.deleteWarehouse(id, principal.getName());

    return redirect("/warehouses/all");
  }

  @PostMapping("/empty-warehouse/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView emptyWarehouse(@PathVariable String id,
                                     ModelAndView modelAndView, Principal principal) {

    warehouseService.emptyWarehouse(id, principal.getName());

    WarehouseServiceModel warehouseServiceModel =
            warehouseService.findWarehouseById(id);

    WarehouseUpdateViewModel warehouseUpdateViewModel =
            modelMapper.map(warehouseServiceModel, WarehouseUpdateViewModel.class);

    modelAndView.addObject("warehouse", warehouseUpdateViewModel);

    return redirect("/warehouses/delete/" + id);
  }
}
