package com.shinkaji.solveza.api.usermanagement.domain.repository;

import com.shinkaji.solveza.api.shared.domain.Provider;
import com.shinkaji.solveza.api.shared.domain.UserId;
import com.shinkaji.solveza.api.usermanagement.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

  Optional<User> findById(UserId userId);

  Optional<User> findByProviderAndExternalId(Provider provider, String externalId);

  List<User> findByCriteria(UserSearchCriteria criteria);

  void save(User user);

  void delete(UserId userId);

  boolean existsById(UserId userId);

  boolean existsByProviderAndExternalId(Provider provider, String externalId);
}
