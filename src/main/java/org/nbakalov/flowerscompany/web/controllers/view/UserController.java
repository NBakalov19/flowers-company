package org.nbakalov.flowerscompany.web.controllers.view;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.models.users.UserCreateModel;
import org.nbakalov.flowerscompany.data.models.models.users.UserUpdateModel;
import org.nbakalov.flowerscompany.services.models.RoleServiceModel;
import org.nbakalov.flowerscompany.services.models.UserServiceModel;
import org.nbakalov.flowerscompany.services.services.CloudinaryService;
import org.nbakalov.flowerscompany.services.services.UserService;
import org.nbakalov.flowerscompany.validations.user.UserCreateValidation;
import org.nbakalov.flowerscompany.validations.user.UserUpdateValidation;
import org.nbakalov.flowerscompany.web.annotations.PageTitle;
import org.nbakalov.flowerscompany.web.controllers.BaseController;
import org.nbakalov.flowerscompany.web.models.view.user.AllUsersViewModel;
import org.nbakalov.flowerscompany.web.models.view.user.UserProfileViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.PageTitleConstants.*;

@Controller
@RequestMapping("/users")
@AllArgsConstructor
public class UserController extends BaseController {

  private final UserService userService;
  private final UserCreateValidation userCreateValidation;
  private final UserUpdateValidation userUpdateValidation;
  private final CloudinaryService cloudinaryService;
  private final ModelMapper modelMapper;

  @GetMapping("/login")
  @PreAuthorize("isAnonymous()")
  @PageTitle(LOGIN)
  public ModelAndView login() {
    return view("/users/login");
  }

  @GetMapping("/register")
  @PreAuthorize("isAnonymous()")
  @PageTitle(REGISTER)
  public ModelAndView register(ModelAndView modelAndView,
                               @ModelAttribute UserCreateModel createModel) {

    modelAndView.addObject("user", createModel);

    return view("/users/register", modelAndView);
  }

  @PostMapping("/register")
  @PreAuthorize("isAnonymous()")
  public ModelAndView registerConfirm(ModelAndView modelAndView,
                                      @ModelAttribute UserCreateModel createModel,
                                      BindingResult bindingResult) throws IOException {

    userCreateValidation.validate(createModel, bindingResult);

    if (bindingResult.hasErrors()) {
      createModel.setPassword(null);
      createModel.setConfirmPassword(null);
      modelAndView.addObject("user", createModel);

      return view("/users/register", modelAndView);
    }

    UserServiceModel serviceModel =
            modelMapper.map(createModel, UserServiceModel.class);

    serviceModel.setProfilePictureUrl(
            cloudinaryService.uploadImage(createModel.getImage()));

    userService.registerUser(serviceModel);

    return redirect("/login");
  }

  @GetMapping("/profile")
  @PreAuthorize("isAuthenticated()")
  @PageTitle(PROFILE)
  public ModelAndView profile(Principal principal, ModelAndView modelAndView) {

    UserServiceModel userServiceModel = userService.findByUsername(principal.getName());

    UserProfileViewModel profileModel = modelMapper.map(
            userServiceModel, UserProfileViewModel.class);

    modelAndView.addObject("user", profileModel);

    return view("/users/profile", modelAndView);
  }

  @GetMapping("/edit")
  @PreAuthorize("isAuthenticated()")
  @PageTitle(EDIT_PROFILE)
  public ModelAndView editProfile(Principal principal,
                                  ModelAndView modelAndView,
                                  @ModelAttribute UserUpdateModel updateModel) {

    UserServiceModel serviceModel =
            userService.findByUsername(principal.getName());

    updateModel = modelMapper.map(serviceModel, UserUpdateModel.class);
    updateModel.setPassword(null);
    modelAndView.addObject("user", updateModel);

    return view("/users/edit-profile", modelAndView);
  }

  @PostMapping("/edit")
  @PreAuthorize("isAuthenticated()")
  public ModelAndView editProfileConfirm(ModelAndView modelAndView,
                                         @ModelAttribute UserUpdateModel updateModel,
                                         BindingResult bindingResult) {

    userUpdateValidation.validate(updateModel, bindingResult);

    if (bindingResult.hasErrors()) {
      updateModel.setOldPassword(null);
      updateModel.setPassword(null);
      updateModel.setConfirmPassword(null);

      modelAndView.addObject("user", updateModel);
      return view("/users/edit-profile", modelAndView);
    }

    UserServiceModel serviceModel =
            modelMapper.map(updateModel, UserServiceModel.class);

    userService.editUserProfile(serviceModel, updateModel.getOldPassword());

    return redirect("/users/profile");
  }

  @GetMapping("/all")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PageTitle(ALL_USERS)
  public ModelAndView getAllUsers(ModelAndView modelAndView) {

    List<AllUsersViewModel> allUsers = this.userService.findAllUsers()
            .stream()
            .map(userServiceModel -> {
              AllUsersViewModel user = this.modelMapper.map(userServiceModel, AllUsersViewModel.class);
              Set<String> authorities = userServiceModel.getAuthorities()
                      .stream()
                      .map(RoleServiceModel::getAuthority)
                      .collect(Collectors.toSet());

              user.setAuthorities(authorities);
              return user;
            })
            .collect(Collectors.toList());

    modelAndView.addObject("users", allUsers);

    return super.view("/users/all-users", modelAndView);
  }

  @PostMapping("/set-operator/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView setModerator(@PathVariable String id) {
    userService.setUserRole(id, "operator");

    return redirect("/users/all");
  }

  @PostMapping("/set-admin/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView setAdmin(@PathVariable String id) {
    userService.setUserRole(id, "admin");

    return redirect("/users/all");
  }
}
