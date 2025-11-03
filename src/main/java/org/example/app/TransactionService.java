package org.example.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Transactional
	public void addTransaction(String name, String drink, double price) {
		TransactionEntity entity = new TransactionEntity(name, drink, price, LocalDateTime.now());
		transactionRepository.save(entity);
	}

	public List<Transaction> getAllTransactions() {
		return transactionRepository.findAllByOrderByTimestampDesc().stream()
			.map(entity -> new Transaction(
				entity.getName(),
				entity.getDrink(),
				entity.getPrice(),
				entity.getTimestamp()
			))
			.collect(Collectors.toList());
	}
}

