package in.bawvpl.Authify.repostory;

import in.bawvpl.Authify.entity.UserEntity;
import in.bawvpl.Authify.io.ProfileResponce;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepostory extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByEmail(String email);

    Boolean existsByEmail(String email);

   ;



}
