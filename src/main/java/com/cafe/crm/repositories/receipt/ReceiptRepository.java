package com.cafe.crm.repositories.receipt;

import com.cafe.crm.models.user.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long>{

    List<Receipt> findByDateBetween(LocalDate from, LocalDate to);

    List<Receipt> findByReceiptCommentAndDateBetween(String receiptComment, LocalDate from, LocalDate to);

    List<Receipt> findByShiftId(Long shiftId);
}
