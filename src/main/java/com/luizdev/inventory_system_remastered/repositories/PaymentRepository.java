package com.luizdev.inventory_system_remastered.repositories;

import com.luizdev.inventory_system_remastered.entity.Payment;
import com.luizdev.inventory_system_remastered.enums.PaymentMethod;
import com.luizdev.inventory_system_remastered.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findBySaleId(Long saleId);

    Page<Payment> findByPaymentMethod(PaymentMethod method, Pageable pageable);

    List<Payment> findByStatus(PaymentStatus status);
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

}