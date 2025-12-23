package com.xeroxx.backend.repository;

import com.xeroxx.backend.entity.OrderStatus;
import com.xeroxx.backend.entity.PrintOrder;
import com.xeroxx.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<PrintOrder, Long> {
    List<PrintOrder> findByUser(User user);
    List<PrintOrder> findByStatus(OrderStatus status);
}



