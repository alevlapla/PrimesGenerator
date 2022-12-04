package com.generator.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class Starter {
	final static Logger logger = LoggerFactory.getLogger(Starter.class);

	public static void main(String[] args) throws InterruptedException {
//		Thread pg_int = new Thread(new PrimesGeneratorHashMapBased(), "generator");
//		pg_int.start();
//
//		try {
//			pg_int.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		PrimesGeneratorHashMapBased2 primes = new PrimesGeneratorHashMapBased2();
		int counter = 0;
		for (;;) {
			Starter.logger.info("prime={}, count={}", primes.next(), ++counter);
		}
	}
}
