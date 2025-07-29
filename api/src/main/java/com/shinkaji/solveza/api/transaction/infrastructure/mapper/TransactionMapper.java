package com.shinkaji.solveza.api.transaction.infrastructure.mapper;

import com.shinkaji.solveza.api.transaction.infrastructure.mapper.dto.TransactionDto;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TransactionMapper {

  Optional<TransactionDto> findById(@Param("id") String id);

  List<TransactionDto> findByAccountId(@Param("accountId") String accountId);

  void insert(@Param("transaction") TransactionDto transaction);

  void delete(@Param("id") String id);

  boolean existsById(@Param("id") String id);
}
