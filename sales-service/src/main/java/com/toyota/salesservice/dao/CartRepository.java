package com.toyota.salesservice.dao;

import com.toyota.salesservice.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
