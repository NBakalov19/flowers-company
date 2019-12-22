package org.nbakalov.flowerscompany.services.services;

import org.nbakalov.flowerscompany.services.models.RoleServiceModel;

import javax.management.relation.RoleNotFoundException;
import java.util.Set;

public interface RoleService {

  RoleServiceModel findByAuthority(String authority) throws RoleNotFoundException;

  Set<RoleServiceModel> findAllRoles();

  void seedRolesInDb();
}
