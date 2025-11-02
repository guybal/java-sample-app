package org.example.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class TransactionService {

	private final List<Transaction> transactions = new ArrayList<>();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final ObjectMapper objectMapper;
	private final File dataFile;

	public TransactionService() {
		// Configure Jackson to handle LocalDateTime
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
		this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		
		// Determine file path - use HOME directory or temp directory as fallback
		// In Azure, HOME is writable and persistent
		String homeDir = System.getProperty("user.home");
		if (homeDir == null || homeDir.isEmpty()) {
			homeDir = System.getProperty("java.io.tmpdir");
		}
		
		// Create data directory if it doesn't exist
		Path dataDir = Paths.get(homeDir, ".drinks-app");
		try {
			Files.createDirectories(dataDir);
		} catch (IOException e) {
			throw new RuntimeException("Failed to create data directory: " + dataDir, e);
		}
		
		this.dataFile = dataDir.resolve("transactions.json").toFile();
	}

	@PostConstruct
	public void loadTransactions() {
		lock.writeLock().lock();
		try {
			if (dataFile.exists() && dataFile.length() > 0) {
				try {
					List<TransactionData> dataList = objectMapper.readValue(dataFile, new TypeReference<List<TransactionData>>() {});
					transactions.clear();
					for (TransactionData data : dataList) {
						transactions.add(new Transaction(
							data.name,
							data.drink,
							data.price,
							data.timestamp
						));
					}
					System.out.println("Loaded " + transactions.size() + " transactions from " + dataFile.getAbsolutePath());
				} catch (IOException e) {
					System.err.println("Failed to load transactions: " + e.getMessage());
					// Continue with empty list if file is corrupted
				}
			} else {
				System.out.println("No existing transaction file found. Starting with empty list.");
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void addTransaction(String name, String drink, double price) {
		Transaction transaction = new Transaction(name, drink, price, LocalDateTime.now());
		
		lock.writeLock().lock();
		try {
			transactions.add(transaction);
			saveToFile();
		} finally {
			lock.writeLock().unlock();
		}
	}

	public List<Transaction> getAllTransactions() {
		lock.readLock().lock();
		try {
			return new ArrayList<>(transactions);
		} finally {
			lock.readLock().unlock();
		}
	}

	private void saveToFile() {
		try {
			// Convert to DTOs for serialization
			List<TransactionData> dataList = transactions.stream()
				.map(t -> new TransactionData(t.name, t.drink, t.price, t.timestamp))
				.toList();
			
			// Write to file atomically
			File tempFile = new File(dataFile.getParent(), dataFile.getName() + ".tmp");
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(tempFile, dataList);
			
			// Atomic rename (works on both Windows and Unix)
			if (dataFile.exists()) {
				Files.delete(dataFile.toPath());
			}
			Files.move(tempFile.toPath(), dataFile.toPath());
			
			System.out.println("Saved " + transactions.size() + " transactions to " + dataFile.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Failed to save transactions: " + e.getMessage());
			throw new RuntimeException("Failed to persist transactions", e);
		}
	}

	// DTO for JSON serialization
	private static class TransactionData {
		public String name;
		public String drink;
		public double price;
		public LocalDateTime timestamp;

		// Default constructor for Jackson
		public TransactionData() {}

		public TransactionData(String name, String drink, double price, LocalDateTime timestamp) {
			this.name = name;
			this.drink = drink;
			this.price = price;
			this.timestamp = timestamp;
		}
	}
}

