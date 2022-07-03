package sn.esp.authservice.sec.web;

import lombok.Data;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.esp.authservice.sec.JwtUtil;
import sn.esp.authservice.sec.entities.AppRole;
import sn.esp.authservice.sec.entities.AppUser;
import sn.esp.authservice.sec.service.AccountService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin("*")
public class AccountRestController {

    private AccountService accountService;
    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "/users")
    @PostAuthorize("hasAuthority('USER')")
    public List<AppUser> getAppUsers(){
        return accountService.listUsers();
    }

    @PostMapping(path = "/users")
    @PostAuthorize("hasAuthority('ADMIN')")
    public AppUser saveUser(@RequestBody AppUser appUser){
        return accountService.addNewUser(appUser);
    }

    @PostMapping(path = "roles")
    @PostAuthorize("hasAuthority('ADMIN')")
    public AppRole saveRole(@RequestBody AppRole appRole){
        return accountService.addNewRole(appRole);
    }

    @PostMapping(path = "/addRoleToUser")
    @PostAuthorize("hasAuthority('ADMIN')")
    public void addRoleToUser(@RequestBody RoleUserForm roleUserForm ){
        accountService.addRoleToUser(roleUserForm.getUsername(), roleUserForm.getRoleName());
    }

    @GetMapping(path = "/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String authorizationToken = request.getHeader(JwtUtil.AUTH_HEADER);

        JwtUtil.doFilterForRefresh(authorizationToken,accountService,request,response);
    }

    @GetMapping(path = "/profile")
    public AppUser profile(Principal principal){
        return accountService.loadUserByUsername(principal.getName());
    }
}

@Data
class RoleUserForm{
    private String username;
    private String roleName;
}
