package com.cpt.payments.dao.interfaces;

import java.util.List;

import com.cpt.payments.dto.TransactionDTO;

public interface TransactionDao {

	TransactionDTO createTransaction(TransactionDTO txnDTO);

	TransactionDTO getTransactionById(int transactionId);

	boolean updateTransactionStatus(TransactionDTO transaction);

	TransactionDTO getTransactionByProviderReference(String id);
	
	List<TransactionDTO> fetchTransactionsForReconcilation();

}
