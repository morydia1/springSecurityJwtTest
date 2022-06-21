package sn.esp.authservice.sec.web;

import lombok.Data;
import org.springframework.web.bind.annotation.*;
import sn.esp.authservice.sec.entities.AppRole;
import sn.esp.authservice.sec.entities.AppUser;
import sn.esp.authservice.sec.service.AccountService;

import java.util.List;

@RestController
@CrossOrigin("*")
public class AccountRestController {

    private AccountService accountService;
    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "/users")
    public List<AppUser> getAppUsers(){
        return accountService.listUsers();
    }

    @PostMapping(path = "/users")
    public AppUser saveUser(@RequestBody AppUser appUser){
        return accountService.addNewUser(appUser);
    }

    @PostMapping(path = "roles")
    public AppRole saveRole(@RequestBody AppRole appRole){
        return accountService.addNewRole(appRole);
    }

    @PostMapping(path = "/addRoleToUser")
    public void addRoleToUser(@RequestBody RoleUserForm roleUserForm ){
        accountService.addRoleToUser(roleUserForm.getUsername(), roleUserForm.getRoleName());
    }
}

@Data
class RoleUserForm{
    private String username;
    private String roleName;
}
