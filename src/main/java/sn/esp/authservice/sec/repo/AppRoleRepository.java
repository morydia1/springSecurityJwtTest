package sn.esp.authservice.sec.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.esp.authservice.sec.entities.AppRole;

public interface AppRoleRepository extends JpaRepository<AppRole,Long> {
    public AppRole findByRoleName(String roleName);
}
