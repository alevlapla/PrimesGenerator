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

		Integer i = Integer.valueOf(1);
		Integer i2 = i;
		List<Integer> lst = new ArrayList<>();
		lst.add(i);
		lst.add(i2);

		// PrimesGeneratorIteratorBased реализует интерфейс Iterable<E>, а значит может
		// быть целью foreach
		PrimesGeneratorIteratorBased primes = new PrimesGeneratorIteratorBased();
		for (Long l : primes) {}
	}
}
