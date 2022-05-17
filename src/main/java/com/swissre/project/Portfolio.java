package com.swissre.project;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class Portfolio {
	private static final String BASE_URL = "https://min-api.cryptocompare.com/data/price?fsym=%s&tsyms=EUR";
	private final ArrayList<Position> portfolio;

	private class Position {
		String coin;
		double ammount;
		double price;
	}

	Portfolio(String fileName) {
		portfolio = new ArrayList<>();
		readPortfolio(fileName);
		getPrices();
	}

	private void readPortfolio(String fileName) {
		InputStream input = Portfolio.class.getResourceAsStream(fileName);
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input))) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] inputArray = line.split("=");
				Position position = new Position();
				position.coin = inputArray[0];
				position.ammount = Double.parseDouble(inputArray[1]);
				position.price = -0.0;
				portfolio.add(position);
			}
		} catch (Exception e) {
			System.out.println("Exception reading portfolio: " + e);
		}
	}

	private void getPrices() {
		for (Position position : portfolio) {
			URL url = null;
			String response = "";
			try {
				url = new URL(String.format(BASE_URL, position.coin));
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("GET Failed : " + conn.getResponseCode());
				}
				InputStreamReader in = new InputStreamReader(conn.getInputStream());
				BufferedReader br = new BufferedReader(in);
				while ((response = br.readLine()) != null) {
					position.price = Double.parseDouble(response.split(":")[1].split("}")[0]);
				}
				conn.disconnect();
			} catch (Exception e) {
				System.out.println(String.format("Exception in GET %s : ", url.toString()) + response);
			}
		}
	}

	public void printPortfolio() {
		double total = 0.0;
		for (Position position : portfolio) {
			double value = position.ammount * position.price;
			System.out.printf(("%s  : %12.2f EUR\n"), position.coin, value);
			total += value;
		}

		System.out.printf("\nTotal: %12.2f EUR\n", total);
	}

	public static void main(String[] args) {
		for (String fileName : args) {
			Portfolio portfolio = new Portfolio("/" + fileName);
			portfolio.printPortfolio();
		}
	}
}
