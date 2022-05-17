package com.swissre.project;

import org.junit.jupiter.api.Test;

class PortfolioTests {

	@Test
	void readPortfolio() {
		Portfolio portfolio = new Portfolio("/bobs_crypto.txt");
		portfolio.printPortfolio();
	}
}
