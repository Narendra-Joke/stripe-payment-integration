package com.cpt.payments.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.cpt.payments.constant.Constants;
import com.cpt.payments.constant.ErrorCodeEnum;
import com.cpt.payments.constant.TransactionStatusEnum;
import com.cpt.payments.dao.interfaces.TransactionDao;
import com.cpt.payments.dto.TransactionDTO;
import com.cpt.payments.entity.TransactionEntity;
import com.cpt.payments.exception.ProcessingServiceException;

@Component
public class TransactionDaoImpl implements TransactionDao {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public TransactionDTO createTransaction(TransactionDTO txnDTO) {
		// TODO Auto-generated method stub

		System.out.println("*****namedParameterJdbcTemplate:" + namedParameterJdbcTemplate);

		System.out.println("TransactionDaoImpl.createTransaction()" + "| received txnDTO:" + txnDTO);

		TransactionEntity txnEntity = modelMapper.map(txnDTO, TransactionEntity.class);

		System.out.println("Converted Entity:" + txnEntity);

		String sql = "INSERT INTO `Transaction` (userId, paymentMethodId, providerId, paymentTypeId, txnStatusId, amount, currency, merchantTransactionReference, txnReference, providerReference, providerCode, providerMessage, retryCount) "
				+ "VALUES (:userId, :paymentMethodId, :providerId, :paymentTypeId, :txnStatusId, :amount, :currency, :merchantTransactionReference, :txnReference, :providerReference, :providerCode, :providerMessage, :retryCount)";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		try {

			int insertedRowCount = namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(txnEntity),
					keyHolder, new String[] { "id" });

			int transactionId = keyHolder.getKey().intValue();

			txnDTO.setId(transactionId);

			System.out.println("Inserted value in DB count insertedRowCount:" + insertedRowCount + ""
					+ "|transactionId:" + transactionId);

			return txnDTO;

		} catch (DuplicateKeyException ex) {
			System.out.println("Unable to save txn in DB, Duplicate txnReference" + txnDTO.getTxnReference());
			ex.printStackTrace();

			throw new ProcessingServiceException(ErrorCodeEnum.DUPLICATE_TXN_REF.getErrorCode(),
					ErrorCodeEnum.DUPLICATE_TXN_REF.getErrorMessage(), HttpStatus.BAD_REQUEST);

		}
	}

	public TransactionDTO getTransactionById(int transactionId) {
		TransactionEntity txnEntity = null;
		try {
			String sql = "Select * from Transaction where id=:id";
			txnEntity = namedParameterJdbcTemplate.queryForObject(sql,
					new BeanPropertySqlParameterSource(TransactionEntity.builder().id((int) transactionId).build()),
					new BeanPropertyRowMapper<>(TransactionEntity.class));
			System.out.println(" :: transaction Details from DB  = " + txnEntity);

			TransactionDTO txnDto = modelMapper.map(txnEntity, TransactionDTO.class);

			System.out.println("Returning txnEntity:" + txnDto);
			return txnDto;

		} catch (Exception e) {
			System.out.println("unable to get transaction Details " + e);

			throw e;
		}
	}

	@Override
	public boolean updateTransactionStatus(TransactionDTO txnDto) {

		System.out.println("TransactionDaoImpl.updateTransactionStatus() | txnDto:" + txnDto);

		TransactionEntity txnEntity = modelMapper.map(txnDto, TransactionEntity.class);
		System.out.println("Converted Entity:" + txnEntity);

		String sql = "Update Transaction SET txnStatusId=:txnStatusId, providerReference=:providerReference WHERE id=:id";

		int updateCount = namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(txnEntity));

		return (updateCount == 1);
	}

	@Override
	public TransactionDTO getTransactionByProviderReference(String id) {

		System.out.println("TransactionDaoImpl.getTransactionByProviderReference()" + "|id:" + id);

		TransactionEntity txnEntity = null;

		String sql = "Select * from Transaction where providerReference=:providerReference";

		try {
			SqlParameterSource params = new MapSqlParameterSource().addValue("providerReference", id);

			txnEntity = namedParameterJdbcTemplate.queryForObject(sql, params,
					new BeanPropertyRowMapper<>(TransactionEntity.class));
			System.out.println(" :: transaction Details from DB  = " + txnEntity);

			TransactionDTO txnDto = modelMapper.map(txnEntity, TransactionDTO.class);

			System.out.println("Returning txnEntity:" + txnDto);
			return txnDto;

		} catch (Exception e) {
			System.out.println("unable to getTransactionByProviderReference " + e);

			throw e;
		}
	}

	@Override
	public List<TransactionDTO> fetchTransactionsForReconcilation() {
		System.out.println(" :: fetching Transaction Details  for retry :: ");

		String sql = "Select * from Transaction where retryCount < :maxRetryCount and txnStatusId = :reconsileStatusId";

		List<TransactionEntity> transaction = new ArrayList<>();
		try {

			SqlParameterSource params = new MapSqlParameterSource().addValue("maxRetryCount", Constants.MAX_RETRY_COUNT)
					.addValue("reconsileStatusId", TransactionStatusEnum.PENDING.getId());

			transaction = namedParameterJdbcTemplate.query(sql, params,
					new BeanPropertyRowMapper<>(TransactionEntity.class));

			System.out.println(" :: transaction Details from DB  = " + transaction);
		} catch (Exception e) {
			System.out.println("unable to get transaction Details " + e);
			e.printStackTrace();
		}

		return convertToDTOList(transaction);
	}

	private List<TransactionDTO> convertToDTOList(List<TransactionEntity> transactionEntities) {
		return transactionEntities.stream().map(entity -> modelMapper.map(entity, TransactionDTO.class))
				.collect(Collectors.toList());
	}

}
