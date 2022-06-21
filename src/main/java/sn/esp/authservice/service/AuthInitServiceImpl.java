package sn.esp.authservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.esp.authservice.sec.entities.AppRole;
import sn.esp.authservice.sec.entities.AppUser;
import sn.esp.authservice.sec.service.AccountService;

import java.util.ArrayList;
import java.util.stream.Stream;

@Service
@Transactional
public class AuthInitServiceImpl implements IAuthInitService {
    private final AccountService accountService;


    public AuthInitServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void initAppRoles() {
        Stream.of("USER","ADMIN","CUSTOMER_MANAGER","PRODUCT_MANAGER","BILL_MANAGER")
                .forEach(role -> this.accountService.addNewRole(new AppRole(null,role)));
    }

    @Override
    public void initAppUsers() {
        Stream.of("user1","admin","user2","user3","user4")
                .forEach(user -> this.accountService.addNewUser(new AppUser(null,user,"passer",new ArrayList<>())));
    }

    @Override
    public void initAppUserRoles() {
        this.accountService.addRoleToUser("user1","USER");
        this.accountService.addRoleToUser("admin","USER");
        this.accountService.addRoleToUser("admin","ADMIN");
        this.accountService.addRoleToUser("user2","USER");
        this.accountService.addRoleToUser("user2","CUSTOMER_MANAGER");
        this.accountService.addRoleToUser("user3","USER");
        this.accountService.addRoleToUser("user3","PRODUCT_MANAGER");
        this.accountService.addRoleToUser("user4","USER");
        this.accountService.addRoleToUser("user4","BILL_MANAGER");
    }
}
