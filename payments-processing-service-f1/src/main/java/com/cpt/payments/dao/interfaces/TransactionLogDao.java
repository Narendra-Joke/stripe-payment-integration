package com.cpt.payments.dao.interfaces;

import com.cpt.payments.dto.TransactionLogDTO;

public interface TransactionLogDao {

	public int createTransactionLog(TransactionLogDTO transactionLog);

}
