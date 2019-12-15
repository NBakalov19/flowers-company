package org.nbakalov.flowerscompany.web.controllers.view;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.models.users.UserCreateModel;
import org.nbakalov.flowerscompany.data.models.models.users.UserUpdateModel;
import org.nbakalov.flowerscompany.services.models.RoleServiceModel;
import org.nbakalov.flowerscompany.services.models.UserServiceModel;
import org.nbakalov.flowerscompany.services.services.CloudinaryService;
import org.nbakalov.flowerscompany.services.services.UserService;
import org.nbakalov.flowerscompany.web.annotations.PageTitle;
import org.nbakalov.flowerscompany.web.controllers.BaseController;
import org.nbakalov.flowerscompany.web.models.view.user.AllUsersViewModel;
import org.nbakalov.flowerscompany.web.models.view.user.UserProfileViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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
  public ModelAndView register() {
    return view("/users/register");
  }

  @PostMapping("/register")
  @PreAuthorize("isAnonymous()")
  public ModelAndView registerConfirm(@ModelAttribute UserCreateModel createModel) throws IOException {

    if (!createModel.getPassword().equals(createModel.getConfirmPassword())) {

      return view("/users/register");
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
  public ModelAndView editProfile(Principal principal, ModelAndView modelAndView) {

    UserServiceModel serviceModel =
            userService.findByUsername(principal.getName());

    UserProfileViewModel profileModel = modelMapper.map(
            serviceModel, UserProfileViewModel.class);

    modelAndView.addObject("user", profileModel);

    return view("/users/edit-profile", modelAndView);
  }

  @PostMapping("/edit")
  @PreAuthorize("isAuthenticated()")
  public ModelAndView editProfileConfirm(@ModelAttribute UserUpdateModel model) {

    if (!model.getPassword().equals(model.getConfirmPassword())) {
      return view("/users/edit-profile");
    }

    UserServiceModel serviceModel =
            modelMapper.map(model, UserServiceModel.class);

    userService.editUserProfile(serviceModel, model.getOldPassword());

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
