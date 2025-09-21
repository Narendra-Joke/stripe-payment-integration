package com.cpt.payments.service.impl.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cpt.payments.dao.interfaces.TransactionDao;
import com.cpt.payments.dao.interfaces.TransactionLogDao;
import com.cpt.payments.dto.TransactionDTO;
import com.cpt.payments.dto.TransactionLogDTO;
import com.cpt.payments.service.interfaces.PaymentStatusHandler;

@Component
public class PendingStatusHandler extends PaymentStatusHandler {

	@Autowired
	private TransactionDao txnDao;

	@Autowired
	private TransactionLogDao txnLogDao;

	@Override
	public TransactionDTO processStatus(TransactionDTO txnDTO) {
		System.out.println("PendingStatusHandler.processStatus()|| " + "payment:" + txnDTO);

		TransactionDTO beforeTxn = txnDao.getTransactionById(txnDTO.getId());

		boolean isUpdate = txnDao.updateTransactionStatus(txnDTO);

		System.out.println("REceived below response from DAO" + "|isUpdate:" + isUpdate);

		TransactionLogDTO logDto = new TransactionLogDTO();
		logDto.setTransactionId(txnDTO.getId());
		logDto.setTxnFromStatus(beforeTxn.getTxnStatus());
		logDto.setTxnToStatus(txnDTO.getTxnStatus());

		int insertCount = txnLogDao.createTransactionLog(logDto);

		if (insertCount == 1) {
			System.out.println("Txn Log Created|logDto:" + logDto);
		} else {
			System.out.println("FAILED to insert Log in DB|logDto:" + logDto);
		}

		return txnDTO;
	}

}
