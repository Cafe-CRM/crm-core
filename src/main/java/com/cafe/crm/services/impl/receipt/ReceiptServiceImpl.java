package com.cafe.crm.services.impl.receipt;

import com.cafe.crm.exceptions.receipt.ReceiptDataException;
import com.cafe.crm.models.user.Receipt;
import com.cafe.crm.repositories.receipt.ReceiptRepository;
import com.cafe.crm.services.interfaces.receipt.ReceiptService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;

    @Autowired
    private ShiftService shiftService;

    @Autowired
    public ReceiptServiceImpl(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    @Override
    public void save(Receipt receipt) {

        if (receipt.getReceiptAmount() > 0) {
            receipt.setShift(shiftService.getLast());
            receiptRepository.save(receipt);
        } else {
            throw new ReceiptDataException("Введена недопустимая сумма поступлений");
        }
    }

    @Override
    public void delete(Receipt receipt) {
        receiptRepository.delete(receipt);
    }

    @Override
    public void delete(Long id) {
        receiptRepository.delete(id);
    }

    @Override
    public Receipt get(Long id) {
        return receiptRepository.findOne(id);
    }

    @Override
    public List<Receipt> getAll() {
        return receiptRepository.findAll();
    }

    @Override
    public List<Receipt> findByDateBetween(LocalDate from, LocalDate to) {
        return receiptRepository.findByDateBetween(from, to);
    }

    @Override
    public List<Receipt> findByReceiptCommentAndDateBetween(String receiptComment, LocalDate from, LocalDate to){
        return receiptRepository.findByReceiptCommentAndDateBetween(receiptComment, from, to);
    }

    @Override
    public List<Receipt> findByShiftId(Long shiftId) {
        return receiptRepository.findByShiftId(shiftId);
    }
}
