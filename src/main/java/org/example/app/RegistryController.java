package org.example.app;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/registry")
public class RegistryController {

	private final List<Transaction> transactions = new CopyOnWriteArrayList<>();
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@PostMapping("/record")
	public ResponseEntity<String> recordTransaction(
			@RequestParam String name,
			@RequestParam String drink,
			@RequestParam double price) {
		
		Transaction transaction = new Transaction(name, drink, price, LocalDateTime.now());
		transactions.add(transaction);
		
		return ResponseEntity.ok("Transaction recorded successfully");
	}

	@GetMapping(produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> viewAllTransactions() throws IOException {
		// Load template
		var resource = new ClassPathResource("registry-template.html");
		String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
		
		// Calculate statistics
		long totalTransactions = transactions.size();
		double totalRevenue = transactions.stream().mapToDouble(t -> t.price).sum();
		long coffeeCount = transactions.stream().filter(t -> "coffee".equalsIgnoreCase(t.drink)).count();
		long teaCount = transactions.stream().filter(t -> "tea".equalsIgnoreCase(t.drink)).count();
		long waterCount = transactions.stream().filter(t -> "water".equalsIgnoreCase(t.drink)).count();
		
		// Build transaction rows
		StringBuilder transactionRows = new StringBuilder();
		if (transactions.isEmpty()) {
			transactionRows.append("                <tr>\n");
			transactionRows.append("                    <td colspan=\"5\" style=\"text-align: center; color: rgba(255, 255, 255, 0.5);\">No transactions yet</td>\n");
			transactionRows.append("                </tr>\n");
		} else {
			for (int i = 0; i < transactions.size(); i++) {
				Transaction t = transactions.get(i);
				transactionRows.append("                <tr>\n");
				transactionRows.append("                    <td>").append(i + 1).append("</td>\n");
				transactionRows.append("                    <td>").append(escapeHtml(t.name)).append("</td>\n");
				transactionRows.append("                    <td>").append(escapeHtml(t.drink)).append("</td>\n");
				transactionRows.append("                    <td>$").append(String.format("%.2f", t.price)).append("</td>\n");
				transactionRows.append("                    <td>").append(t.timestamp.format(formatter)).append("</td>\n");
				transactionRows.append("                </tr>\n");
			}
		}
		
		// Replace placeholders
		String html = template
			.replace("${TOTAL_TRANSACTIONS}", String.valueOf(totalTransactions))
			.replace("${TOTAL_REVENUE}", "$" + String.format("%.2f", totalRevenue))
			.replace("${COFFEE_COUNT}", String.valueOf(coffeeCount))
			.replace("${TEA_COUNT}", String.valueOf(teaCount))
			.replace("${WATER_COUNT}", String.valueOf(waterCount))
			.replace("${TRANSACTION_ROWS}", transactionRows.toString());
		
		return ResponseEntity.ok(html);
	}

	private String escapeHtml(String input) {
		if (input == null) {
			return "";
		}
		return input
			.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;")
			.replace("\"", "&quot;")
			.replace("'", "&#39;");
	}

	private static class Transaction {
		final String name;
		final String drink;
		final double price;
		final LocalDateTime timestamp;

		Transaction(String name, String drink, double price, LocalDateTime timestamp) {
			this.name = name;
			this.drink = drink;
			this.price = price;
			this.timestamp = timestamp;
		}
	}
}

