package com.thanaphat2005.food.ordering.system.payment.service.dataacess.credithistory.adpater;

import com.thanaphat2005.food.ordering.system.domain.valueobject.CustomerId;
import com.thanaphat2005.food.ordering.system.payment.service.dataacess.credithistory.entity.CreditHistoryEntity;
import com.thanaphat2005.food.ordering.system.payment.service.dataacess.credithistory.mapper.CreditHistoryDataAccessMapper;
import com.thanaphat2005.food.ordering.system.payment.service.dataacess.credithistory.repository.CreditHistoryJpaRepository;
import com.thanaphat2005.food.ordering.system.restaurant.service.domain.entity.CreditHistory;
import com.thanaphat2005.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {
    private final CreditHistoryJpaRepository creditHistoryJpaRepository;
    private final CreditHistoryDataAccessMapper creditHistoryDataAccessMapper;

    public CreditHistoryRepositoryImpl(CreditHistoryJpaRepository creditHistoryJpaRepository, CreditHistoryDataAccessMapper creditHistoryDataAccessMapper) {
        this.creditHistoryJpaRepository = creditHistoryJpaRepository;
        this.creditHistoryDataAccessMapper = creditHistoryDataAccessMapper;
    }

    @Override
    public CreditHistory save(CreditHistory creditHistory) {
        return creditHistoryDataAccessMapper.CreditHistoryEntityToCreditHistory
                (creditHistoryJpaRepository.save(creditHistoryDataAccessMapper.creditHistoryToCreditHistoryEntity(creditHistory)));
    }

    @Override
    public Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId) {
        Optional<List<CreditHistoryEntity>> creditHistoryEntities = creditHistoryJpaRepository.findByCustomerId(customerId.getValue());
        return creditHistoryEntities.map(creditHistoryList -> creditHistoryList.stream().map(creditHistoryDataAccessMapper::CreditHistoryEntityToCreditHistory).collect(Collectors.toList()));
    }
}
