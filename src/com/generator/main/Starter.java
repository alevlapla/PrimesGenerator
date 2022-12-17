package com.generator.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generator.bithell.BitSievePageSegmented;

public class Starter {
	public final static Logger logger = LoggerFactory.getLogger(Starter.class);

	public static void main(String[] args) throws InterruptedException {
//		Thread pg_int = new Thread(new PrimesGeneratorHashMapBased(), "generator");
//		pg_int.start();
//
//		try {
//			pg_int.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		BitSievePageSegmented primes = new BitSievePageSegmented();	
		for (int i = 0; i < 1_000_000; i++) {
			Starter.logger.info("prime={}, count={}", primes.next());
		}
	}
}
