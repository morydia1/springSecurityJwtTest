package sn.esp.authservice.sec.service;

import sn.esp.authservice.sec.entities.AppRole;
import sn.esp.authservice.sec.entities.AppUser;

import java.util.List;

public interface AccountService {
    AppUser addNewUser(AppUser appUser);

    AppRole addNewRole(AppRole appRole);

    void addRoleToUser(String username, String roleName);

    AppUser loadUserByUsername(String username);

    List<AppUser> listUsers();

}
