package org.nbakalov.flowerscompany.web.controllers.view;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.enums.Variety;
import org.nbakalov.flowerscompany.data.models.models.flowers.FlowersBatchCreateModel;
import org.nbakalov.flowerscompany.data.models.models.flowers.FlowersBatchUpdateModel;
import org.nbakalov.flowerscompany.data.models.models.flowers.MoveBatchModel;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.services.FlowersBatchService;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.nbakalov.flowerscompany.web.annotations.PageTitle;
import org.nbakalov.flowerscompany.web.controllers.BaseController;
import org.nbakalov.flowerscompany.web.models.view.flowerbatch.FlowerBatchDeleteViewModel;
import org.nbakalov.flowerscompany.web.models.view.flowerbatch.FlowersBatchViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.PageTitleConstants.*;

@Controller
@RequestMapping("/flowers")
@AllArgsConstructor
public class FlowersBatchController extends BaseController {

  private final FlowersBatchService flowersBatchService;
  private final WarehouseService warehouseService;
  private final ModelMapper modelMapper;

  @GetMapping("/create-batch")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  @PageTitle(CREATE_FLOWERS_BATCH)
  public ModelAndView createFlowersBatch(ModelAndView modelAndView) {

    List<Variety> varieties = Variety.stream().collect(Collectors.toList());
    modelAndView.addObject("varieties", varieties);

    return view("/flowers/create-flowers-batch", modelAndView);
  }

  @PostMapping("/create-batch")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView createFlowersBatchConfirm(@ModelAttribute FlowersBatchCreateModel createModel,
                                                Principal principal) {

    FlowersBatchServiceModel serviceModel =
            modelMapper.map(createModel, FlowersBatchServiceModel.class);

    WarehouseServiceModel warehouseServiceModel =
            warehouseService.findWarehouseById(createModel.getWarehouse());

    serviceModel.setWarehouse(warehouseServiceModel);

    flowersBatchService.registerBatch(serviceModel, principal.getName());

    return redirect("/flowers/todays-batches");
  }

  @GetMapping("/todays-batches")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  @PageTitle(TODAYS_FLOWERS_BATCHES)
  public ModelAndView todaysFlowersBatch(ModelAndView modelAndView) {

    List<FlowersBatchServiceModel> todaysBatches =
            flowersBatchService.findAllBatchesRegisteredToday();

    modelAndView.addObject("todaysBatchesCount", todaysBatches.size());

    return view("/flowers/todays-batches", modelAndView);
  }

  @GetMapping("/edit-batch/{id}")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  @PageTitle(EDIT_FLOWERS_BATCH)
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
                                              @ModelAttribute FlowersBatchUpdateModel updateModel,
                                              Principal principal) {

    FlowersBatchServiceModel serviceModel =
            modelMapper.map(updateModel, FlowersBatchServiceModel.class);

    flowersBatchService.editFlowerBatch(id, serviceModel, principal.getName());

    return redirect("/flowers/todays-batches");
  }

  @GetMapping("/move-batch/{id}")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  @PageTitle(MOVE_FLOWERS_BATCH)
  public ModelAndView moveFlowersBatch(@PathVariable String id, ModelAndView modelAndView) {

    FlowersBatchServiceModel serviceModel =
            flowersBatchService.findBatchById(id);
    modelAndView.addObject("batch", serviceModel);

    Long warehousesCount = warehouseService.getWarehousesCount();
    modelAndView.addObject("warehousesCount", warehousesCount);

    return view("flowers/move-batch", modelAndView);
  }

  @PostMapping("/move-batch/{id}")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView moveFlowersBatchConfirm(@PathVariable String id,
                                              @ModelAttribute MoveBatchModel model,
                                              Principal principal) {

    flowersBatchService.moveBatch(id, model, principal.getName());

    return redirect("/flowers/todays-batches");
  }

  @GetMapping("/delete-batch/{id}")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  @PageTitle(DELETE_FLOWERS_BATCH)
  public ModelAndView deleteFlowersBatch(@PathVariable String id, ModelAndView modelAndView) {

    FlowersBatchServiceModel serviceModel =
            flowersBatchService.findBatchById(id);

    FlowerBatchDeleteViewModel viewModel =
            modelMapper.map(serviceModel, FlowerBatchDeleteViewModel.class);

    modelAndView.addObject("batch", viewModel);

    return view("flowers/delete-batch", modelAndView);
  }

  @PostMapping("/delete-batch/{id}")
  @PreAuthorize("hasRole('ROLE_OPERATOR')")
  public ModelAndView deleteFlowersBatch(@PathVariable String id, Principal principal) {

    flowersBatchService.deleteBatch(id, principal.getName());

    return redirect("/flowers/todays-batches");
  }
}
