package sn.esp.authservice.sec.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.esp.authservice.sec.entities.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser,Long> {
    public AppUser findByUsername(String username);
}
