package org.nbakalov.flowerscompany.web.controllers.api;


import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.services.services.FlowersBatchService;
import org.nbakalov.flowerscompany.web.models.api.TodayFlowersBatchApiModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/flowers")
@AllArgsConstructor
public class FlowerBatchApiController {

  private final FlowersBatchService flowersBatchService;
  private final ModelMapper modelMapper;

  @GetMapping("/todays-batches")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public List<TodayFlowersBatchApiModel> findTodaysBatches() {

    return flowersBatchService.findAllBatchesRegisteredToday()
            .stream()
            .map(fb -> modelMapper.map(fb, TodayFlowersBatchApiModel.class))
            .collect(Collectors.toList());
  }
}
