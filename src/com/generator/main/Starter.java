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

		// PrimesGeneratorIteratorBased реализует интерфейс Iterable<E>, а значит может
		// быть целью foreach
		PrimesGeneratorHashMapBased2 primes = new PrimesGeneratorHashMapBased2();
//		Test primes = new Test();
//		for (Long l : primes) {}
		int counter = 0;
//		for (int i = 0; i < 10000; i++) {
		for (;;) {
			counter++;
//			primes.next();
			
			
			Starter.logger.info("prime={}, count={}", primes.next(), counter);
		}
		
//		System.out.println("itrs.size()=" + PrimesGeneratorHashMapBased3.itrs.size());
	}
}
