package org.example.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
public class HelloController {

	@Value("${drink.image.coffee}")
	private String coffeeImageBase64;

	@Value("${drink.image.tea}")
	private String teaImageBase64;

	@Value("${drink.image.water}")
	private String waterImageBase64;

	@Autowired
	private TransactionService transactionService;

	// Drink prices
	private static final double COFFEE_PRICE = 4.99;
	private static final double TEA_PRICE = 2.99;
	private static final double WATER_PRICE = 0.99;

	private enum ColorPalette {
		// Coffee palette - warm browns and dark tones (backgrounds)
		COFFEE_DARK("#6F5B4F"),  // Warm medium brown matching the coffee card
		COFFEE_MEDIUM("#4a3428"),
		COFFEE_RICH("#5c3a21"),
		COFFEE_WARM("#6b4e37"),
		
		// Tea palette - greens and soft tones (backgrounds)
		TEA_FOREST("#335C4A"),  // Lighter green/teal matching the tea card
		TEA_SAGE("#2d5a4a"),
		TEA_MINT("#0d9488"),
		TEA_SOFT("#0f766e"),
		
		// Water palette - blues and aqua tones (backgrounds)
		WATER_DEEP("#0e4c6e"),
		WATER_OCEAN("#1e40af"),
		WATER_AQUA("#0891b2"),
		WATER_SKY("#0284c7"),
		
		// General dark backgrounds
		DARK_SLATE("#0f172a"),
		DARK_BLUE("#1e293b"),
		DARK_GRAY("#1f2937"),
		
		// Warm/cream tones for coffee (text)
		CREAM("#ffffff"),  // Pure white for better contrast with coffee card
		IVORY("#fffef7"),
		WARM_WHITE("#fef3c7"),
		GOLDEN("#fbbf24"),
		
		// Light greens for tea (text)
		MINT_WHITE("#ffffff"),  // Pure white for better contrast with tea card
		SAGE_LIGHT("#ecfdf5"),
		PALE_GREEN("#dcfce7"),
		FRESH_GREEN("#86efac"),
		
		// Light blues for water (text)
		SKY_WHITE("#f0f9ff"),
		AQUA_LIGHT("#e0f2fe"),
		ICE_BLUE("#bae6fd"),
		CRYSTAL("#7dd3fc"),
		
		// General (text)
		WHITE("#ffffff"),
		LIGHT_GRAY("#e5e7eb"),
		OFF_WHITE("#f9fafb");

		private final String hexValue;

		ColorPalette(String hexValue) {
			this.hexValue = hexValue;
		}

		public String getHexValue() {
			return hexValue;
		}
	}

	@GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> landing() throws IOException {
		var resource = new ClassPathResource("landing-page.html");
		var html = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
		return ResponseEntity.ok(html);
	}

	@GetMapping("/welcome")
	public String welcome() {
		String message = System.getenv().getOrDefault("GREETING_MSG", "");
		if (!message.isEmpty()) {
			System.out.println(message);
		}
		return message;
	}

	@PostMapping(value = "/coffee", produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> coffee(@RequestParam(defaultValue = "Guest") String name) throws IOException {
		String displayName = name == null || name.trim().isEmpty() ? "Guest" : name.trim();
		registerTransaction(displayName, "coffee", COFFEE_PRICE);
		
		String header = String.format("Hi %s!%nYour Coffee is ready!", displayName);
		return ResponseEntity.ok(renderDrinkTemplate(
			header, 
			ColorPalette.COFFEE_DARK, 
			ColorPalette.CREAM, 
			coffeeImageBase64,
			COFFEE_PRICE
		));
	}

	@PostMapping(value = "/tea", produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> tea(@RequestParam(defaultValue = "Guest") String name) throws IOException {
		String displayName = name == null || name.trim().isEmpty() ? "Guest" : name.trim();
		registerTransaction(displayName, "tea", TEA_PRICE);
		
		String header = String.format("Hi %s!%nYour Tea is ready!", displayName);
		return ResponseEntity.ok(renderDrinkTemplate(
			header, 
			ColorPalette.TEA_FOREST, 
			ColorPalette.MINT_WHITE, 
			teaImageBase64,
			TEA_PRICE
		));
	}

	@PostMapping(value = "/water", produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> water(@RequestParam(defaultValue = "Guest") String name) throws IOException {
		String displayName = name == null || name.trim().isEmpty() ? "Guest" : name.trim();
		registerTransaction(displayName, "water", WATER_PRICE);
		
		String header = String.format("Hi %s!%nYour Water is ready!", displayName);
		return ResponseEntity.ok(renderDrinkTemplate(
			header, 
			ColorPalette.WATER_DEEP, 
			ColorPalette.SKY_WHITE, 
			waterImageBase64,
			WATER_PRICE
		));
	}

	private void registerTransaction(String name, String drink, double price) {
		try {
			transactionService.addTransaction(name, drink, price);
		} catch (Exception e) {
			// Log error but don't fail the request if registry is unavailable
			System.err.println("Failed to register transaction: " + e.getMessage());
		}
	}

	private String renderDrinkTemplate(String header, ColorPalette bgColor, ColorPalette textColor, String base64Image, double price) throws IOException {
		return renderDrinkTemplate(header, bgColor.getHexValue(), textColor.getHexValue(), base64Image, price);
	}

	private String renderDrinkTemplate(String header, String bgColor, String textColor, String base64Image, double price) throws IOException {
		var resource = new ClassPathResource("drink-template.html");
		var template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
		
		// Construct the full data URL from base64 string
		String imageDataUrl = buildImageDataUrl(base64Image);
		
		// Format header with line breaks
		String formattedHeader = escapeHtml(header).replace("\n", "<br>");
		
		return template
			.replace("${HEADER_TEXT}", formattedHeader)
			.replace("${BG_COLOR}", bgColor)
			.replace("${TEXT_COLOR}", textColor)
			.replace("${BASE64_IMAGE}", imageDataUrl)
			.replace("${PRICE}", String.format("$%.2f", price));
	}

	private String buildImageDataUrl(String base64String) {
		if (base64String == null || base64String.trim().isEmpty()) {
			return "";
		}
		
		String cleanBase64 = base64String.trim();
		
		// If it already has data URL prefix, return as-is
		if (cleanBase64.startsWith("data:image/")) {
			return cleanBase64;
		}
		
		// Otherwise, construct the data URL (defaulting to PNG format)
		return "data:image/png;base64," + cleanBase64;
	}

	
	private String escapeHtml(String input) {
		if (input == null || input.isEmpty()) {
			return "";
		}
		return input
			.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;")
			.replace("\"", "&quot;")
			.replace("'", "&#39;");
	}

}
