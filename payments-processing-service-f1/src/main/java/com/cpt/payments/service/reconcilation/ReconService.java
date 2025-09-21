package com.cpt.payments.service.reconcilation;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cpt.payments.dao.interfaces.TransactionDao;
import com.cpt.payments.dto.TransactionDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReconService {
	
	@Autowired
	private ReconTransactionAsync reconTxnAsync;
	
	@Autowired
	private TransactionDao txnDao;
	
	@Scheduled(cron = "0 0/15 * * * ?")
	public void performTask() {
		log.info("Executing task...");
		
		List<TransactionDTO> txnForRecon = txnDao.fetchTransactionsForReconcilation();
		log.info("About to process recon for txnForRecon.size() : " + txnForRecon.size());
		
		for(TransactionDTO txnDto : txnForRecon) {
			log.trace("submit task for async execution | txnDto : " + txnDto);
			reconTxnAsync.processItem(txnDto);
		}
	}
}
