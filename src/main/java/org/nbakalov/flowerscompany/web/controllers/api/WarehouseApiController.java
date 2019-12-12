package org.nbakalov.flowerscompany.web.controllers.api;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.nbakalov.flowerscompany.web.models.api.AllWarehouseApiModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/warehouses/api")
@AllArgsConstructor
public class WarehouseApiController {

  private final WarehouseService warehouseService;
  private final ModelMapper modelMapper;


  @GetMapping("/all")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public List<AllWarehouseApiModel> findAllWarehouses() {

    return warehouseService.findAllWarehouses()
            .stream()
            .map(warehouseServiceModel ->
                    modelMapper.map(warehouseServiceModel, AllWarehouseApiModel.class))
            .collect(Collectors.toList());
  }
}
