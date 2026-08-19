package com.thanaphat2005.food.ordering.system.payment.service.dataacess.creditentry.adpater;

import com.thanaphat2005.food.ordering.system.domain.valueobject.CustomerId;
import com.thanaphat2005.food.ordering.system.payment.service.dataacess.creditentry.mapper.CreditEntryDataAccessMapper;
import com.thanaphat2005.food.ordering.system.payment.service.dataacess.creditentry.repository.CreditEntryJpaRepository;
import com.thanaphat2005.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.thanaphat2005.food.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class CreditEntryRepositoryImpl implements CreditEntryRepository {

    private final CreditEntryJpaRepository creditEntryJpaRepository;
    private final CreditEntryDataAccessMapper creditEntryDataAccessMapper;

    public CreditEntryRepositoryImpl(CreditEntryJpaRepository creditEntryJpaRepository, CreditEntryDataAccessMapper creditEntryDataAccessMapper) {
        this.creditEntryJpaRepository = creditEntryJpaRepository;
        this.creditEntryDataAccessMapper = creditEntryDataAccessMapper;
    }

    @Override
    public CreditEntry save(CreditEntry creditEntry) {
        return creditEntryDataAccessMapper.CreditEntryEntityTocreditEntry(creditEntryJpaRepository.save(creditEntryDataAccessMapper.creditEntryToCreditEntryEntity(creditEntry)));
    }

    @Override
    public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
        return creditEntryJpaRepository.findByCustomerId(customerId.getValue()).map(creditEntryDataAccessMapper::CreditEntryEntityTocreditEntry);
    }
}
