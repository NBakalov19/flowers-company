package org.nbakalov.flowerscompany.web.controllers.view;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.nbakalov.flowerscompany.data.models.models.users.UserCreateModel;
import org.nbakalov.flowerscompany.data.models.models.users.UserEditModel;
import org.nbakalov.flowerscompany.services.models.RoleServiceModel;
import org.nbakalov.flowerscompany.services.models.UserServiceModel;
import org.nbakalov.flowerscompany.services.services.UserService;
import org.nbakalov.flowerscompany.web.controllers.BaseController;
import org.nbakalov.flowerscompany.web.models.view.AllUsersViewModel;
import org.nbakalov.flowerscompany.web.models.view.UserProfileViewModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
@NoArgsConstructor
@AllArgsConstructor
public class UserController extends BaseController {

  private UserService userService;

  @GetMapping("/login")
  @PreAuthorize("isAnonymous()")
  public ModelAndView login() {
    return view("/users/login");
  }

  @GetMapping("/register")
  @PreAuthorize("isAnonymous()")
  public ModelAndView register() {
    return view("/users/register");
  }

  @PostMapping("/register")
  @PreAuthorize("isAnonymous()")
  public ModelAndView registerConfirm(@ModelAttribute UserCreateModel model) {
    if (!model.getPassword().equals(model.getConfirmPassword())) {

      return view("/users/register");
    }

    userService.registerUser(modelMapper.map(model, UserServiceModel.class));

    return redirect("/login");
  }

  @GetMapping("/profile")
  @PreAuthorize("isAuthenticated()")
  public ModelAndView profile(Principal principal, ModelAndView modelAndView) {

    UserServiceModel userServiceModel = userService.findByUsername(principal.getName());

    UserProfileViewModel userProfileViewModel = modelMapper.map(
            userServiceModel, UserProfileViewModel.class);

    modelAndView.addObject("model", userProfileViewModel);

    return view("/users/profile", modelAndView);
  }

  @GetMapping("/edit")
  @PreAuthorize("isAuthenticated()")
  public ModelAndView editProfile(Principal principal, ModelAndView modelAndView) {

    UserProfileViewModel userProfileModel = modelMapper.map(
            userService.findByUsername(principal.getName()), UserProfileViewModel.class);

    modelAndView.addObject("model", userProfileModel);

    return view("/users/edit-profile", modelAndView);
  }

  @PostMapping("/edit")
  @PreAuthorize("isAuthenticated()")
  public ModelAndView editProfileConfirm(@ModelAttribute UserEditModel model) {

    if (!model.getPassword().equals(model.getConfirmPassword())) {
      return view("/users/edit-profile");
    }

    userService.editUserProfile(modelMapper.map(model, UserServiceModel.class), model.getOldPassword());

    return redirect("/users/profile");
  }

  @GetMapping("/all")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
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
