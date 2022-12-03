package com.generator.main;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Генератор простых чисел на базе стрима. Фильтр проверяет очередное число,
 * пытаясь разделить его нацело на ранее найденные простые числа, размещаемые в
 * ArrayList. Если число оказалось составным (разделилось нацело) - переход к
 * следующему элементу (фильтр возвращает false). Иначе число добавляется в
 * ArrayList.
 * 
 * @author Nobby Nobbs (<a href=
 *         "https://stackoverflow.com/questions/43760641/java-8-streams-and-the-sieve-of-eratosthenes/">StackOverflow
 *         thread</href>)
 * @author bratishka
 * @version 1.0
 */
public class PrimesGeneratorStreamBased3 implements Runnable {
	/* Счётчик найденных простых чисел */
	private static int counter = 0;

	private static IntStream primeStream() {
		final ArrayList<Integer> primes = new ArrayList<>();

		IntStream primesThreeToLimit = IntStream.iterate(3, i -> i + 2).filter(i -> {
			for (int j = 0; j < primes.size(); j++) {
				if (i % primes.get(j) == 0) {
					return false;
				}
			}
			primes.add(i);
			counter++;
			return true;
		});
		return primesThreeToLimit;
	}

	@Override
	public void run() {
		primeStream().forEach(x -> Starter.logger.info("prime={}, count={}", x, counter));
	}
}
