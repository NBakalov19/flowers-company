package org.nbakalov.flowerscompany.web.controllers.api;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.nbakalov.flowerscompany.services.services.FlowersBatchService;
import org.nbakalov.flowerscompany.web.controllers.BaseApiController;
import org.nbakalov.flowerscompany.web.models.api.TodayFlowersBatchApiModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/flowers/api")
@AllArgsConstructor
@NoArgsConstructor
public class FlowerBatchApiController extends BaseApiController {

  private FlowersBatchService flowersBatchService;

  @GetMapping("/todays-batches")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public List<TodayFlowersBatchApiModel> findTodaysBatches() {

    return flowersBatchService.findAllBatchesRegisteredToday()
            .stream()
            .map(fb -> modelMapper.map(fb, TodayFlowersBatchApiModel.class))
            .collect(Collectors.toList());

  }
}
