package com.cpt.payments.dao.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cpt.payments.dao.interfaces.TransactionLogDao;
import com.cpt.payments.dto.TransactionLogDTO;
import com.cpt.payments.entity.TransactionLogEntity;

@Repository
public class TransactionLogDaoImpl implements TransactionLogDao {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public int createTransactionLog(TransactionLogDTO transactionLog) {
		System.out.println("*****namedParameterJdbcTemplate:" + namedParameterJdbcTemplate);
		System.out.println("TransactionLogDaoImpl.createTransaction()" + "| received txnDTO:" + transactionLog);

		TransactionLogEntity txnEntity = modelMapper.map(transactionLog, TransactionLogEntity.class);
		System.out.println("Converted Entity:" + txnEntity);

		String sql = "INSERT INTO `Transaction_Log` (transactionId, txnFromStatus, txnToStatus) "
				+ "VALUES (:transactionId, :txnFromStatus, :txnToStatus)";

		System.out.println("Inserting txnLog query| sql:" + sql);

		int insertedRowCount = namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(txnEntity));

		if (insertedRowCount == 1) {
			System.out.println("Successfully stored Txn Log in DB| txnEntity:" + txnEntity);
		} else {
			System.out.println("FAILED to store Txn Log in DB| txnEntity:" + txnEntity);
		}

		return insertedRowCount;
	}

}
