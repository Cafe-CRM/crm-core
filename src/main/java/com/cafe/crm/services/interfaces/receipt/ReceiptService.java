package com.cafe.crm.services.interfaces.receipt;


import com.cafe.crm.models.user.Receipt;

import java.time.LocalDate;
import java.util.List;

public interface ReceiptService {

    void save(Receipt receipt);

    void delete(Receipt receipt);

    void delete(Long id);

    Receipt get(Long id);

    List<Receipt> getAll();

    List<Receipt> findByDateBetween(LocalDate from, LocalDate to);

    List<Receipt> findByReceiptCommentAndDateBetween(String receiptComment, LocalDate from, LocalDate to);

    List<Receipt> findByShiftId(Long shiftId);

}
