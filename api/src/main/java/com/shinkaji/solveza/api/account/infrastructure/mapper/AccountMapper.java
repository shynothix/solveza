package com.shinkaji.solveza.api.account.infrastructure.mapper;

import com.shinkaji.solveza.api.account.infrastructure.mapper.dto.AccountDto;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountMapper {

  Optional<AccountDto> findById(@Param("id") String id);

  List<AccountDto> findByUserId(@Param("userId") String userId);

  List<AccountDto> findByRequesterId(@Param("requesterId") String requesterId);

  List<AccountDto> findByPayerId(@Param("payerId") String payerId);

  void insert(@Param("account") AccountDto account);

  void update(@Param("account") AccountDto account);

  void delete(@Param("id") String id);

  boolean existsById(@Param("id") String id);

  boolean existsByRequesterIdAndPayerId(
      @Param("requesterId") String requesterId, @Param("payerId") String payerId);
}
