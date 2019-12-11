package org.nbakalov.flowerscompany.web.controllers.view;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Variety;
import org.nbakalov.flowerscompany.data.models.models.FlowersBatchCreateModel;
import org.nbakalov.flowerscompany.data.models.models.FlowersBatchUpdateModel;
import org.nbakalov.flowerscompany.data.models.models.MoveBatchModel;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.services.FlowersBatchService;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.nbakalov.flowerscompany.web.controllers.BaseController;
import org.nbakalov.flowerscompany.web.models.view.FlowerBatchDeleteViewModel;
import org.nbakalov.flowerscompany.web.models.view.FlowersBatchViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/flowers")
@AllArgsConstructor
public class FlowersBatchController extends BaseController {

  private final FlowersBatchService flowersBatchService;
  private final WarehouseService warehouseService;
  private final ModelMapper modelMapper;

  @GetMapping("/create-batch")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView createFlowersBatch(ModelAndView modelAndView) {

    List<Variety> varieties = Variety.stream().collect(Collectors.toList());
    modelAndView.addObject("varieties", varieties);

    return view("/flowers/create-flowers-batch", modelAndView);
  }

  @PostMapping("/create-batch")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView createFlowersBatchConfirm(@ModelAttribute FlowersBatchCreateModel createModel) {

    FlowersBatchServiceModel serviceModel =
            modelMapper.map(createModel, FlowersBatchServiceModel.class);

    WarehouseServiceModel warehouseServiceModel =
            warehouseService.findWarehouseById(createModel.getWarehouse());

    serviceModel.setWarehouse(warehouseServiceModel);

    flowersBatchService.registerBatch(serviceModel);

    return redirect("/flowers/todays-batches");
  }

  @GetMapping("/todays-batches")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView todaysFlowersBatch() {

    return view("/flowers/todays-batches");
  }

  @GetMapping("/edit-batch/{id}")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView editFlowersBatch(@PathVariable String id, ModelAndView modelAndView) {

    FlowersBatchServiceModel serviceModel =
            flowersBatchService.findBatchById(id);

    FlowersBatchViewModel viewModel =
            modelMapper.map(serviceModel, FlowersBatchViewModel.class);

    modelAndView.addObject("batch", viewModel);

    List<Variety> varieties = Variety.stream().collect(Collectors.toList());
    modelAndView.addObject("varieties", varieties);

    return view("flowers/edit-flowers-batch", modelAndView);
  }

  @PostMapping("/edit-batch/{id}")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView editFlowersBatchConfirm(@PathVariable String id,
                                              @ModelAttribute FlowersBatchUpdateModel updateModel) {

    FlowersBatchServiceModel serviceModel =
            modelMapper.map(updateModel, FlowersBatchServiceModel.class);

    flowersBatchService.editFlowerBatch(id, serviceModel);

    return redirect("/flowers/todays-batches");
  }

  @GetMapping("/move-batch/{id}")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView moveFlowersBatch(@PathVariable String id, ModelAndView modelAndView) {

    FlowersBatchServiceModel serviceModel =
            flowersBatchService.findBatchById(id);

    modelAndView.addObject("batch", serviceModel);

    return view("flowers/move-batch", modelAndView);
  }

  @PostMapping("/move-batch/{id}")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView moveFlowersBatchConfirm(@PathVariable String id, @ModelAttribute MoveBatchModel model) {

    flowersBatchService.moveBatch(id, model);

    return redirect("/flowers/todays-batches");
  }

  @GetMapping("/delete-batch/{id}")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView deleteFlowersBatch(@PathVariable String id, ModelAndView modelAndView) {

    FlowersBatchServiceModel serviceModel =
            flowersBatchService.findBatchById(id);

    FlowerBatchDeleteViewModel deleteModel =
            modelMapper.map(serviceModel, FlowerBatchDeleteViewModel.class);

    modelAndView.addObject("batch", deleteModel);

    return view("flowers/delete-batch", modelAndView);
  }

  @PostMapping("/delete-batch/{id}")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView deleteFlowersBatch(@PathVariable String id,
                                         @ModelAttribute FlowersBatchUpdateModel updateModel) {

    flowersBatchService.deleteBatch(id);

    return redirect("/flowers/todays-batches");
  }
}
