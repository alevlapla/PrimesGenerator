package com.generator.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starter {
	final static Logger logger = LoggerFactory.getLogger(Starter.class);

	public static void main(String[] args) throws InterruptedException {
//		Thread pg_int = new Thread(new PrimeGeneratorIntBased(), "generator");
		Thread pg_int = new Thread(new PrimesGeneratorBitSetBased(), "generator");

		pg_int.start();

		try {
			pg_int.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
