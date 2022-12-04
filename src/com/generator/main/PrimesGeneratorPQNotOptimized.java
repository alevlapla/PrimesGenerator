package com.generator.main;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Генератор простых чисел {@link java.math.BigInteger BigInteger} на базе
 * {@link java.util.Iterator Iterator} и {@link java.util.PriorityQueue
 * PriorityQueue}. Для учёта составных множителей используется класс-контейнер
 * {@link NonPrimeSequence NonPrimeSequence}, содержащий значения чисел:
 * простого и составного на базе этого простого.
 * 
 * @author unknown (<a href=
 *         "https://rosettacode.org/wiki/Sieve_of_Eratosthenes">rosettacode.org</a
 *         href>)
 * @author bratishka
 * @version 1.0 *
 */
public class PrimesGeneratorPQNotOptimized implements Iterator<BigInteger>, Iterable<BigInteger> {
	/**
	 * Контейнер для составного числа. Состоит из простого числа {@code base} и
	 * последнего "продвинутого" числа {@code last = base * x} (т. е. на шаг
	 * {@code base}).
	 */
	private class NonPrimeSequence implements Comparable<NonPrimeSequence> {
		BigInteger base;
		BigInteger last;

		private NonPrimeSequence(BigInteger base) {
			this.base = base;
			this.last = base.multiply(base);
		}

		/* Объект в корне priorityQueue будет иметь минимальное значение last. */
		@Override
		public int compareTo(NonPrimeSequence other) {
			return last.compareTo(other.last);
		}
	}

	/*
	 * PriorityQueue хранилище контейнеров составных чисел, составленных на базе
	 * ранее выявленных простых чисел
	 */
	private final PriorityQueue<NonPrimeSequence> cand = new PriorityQueue<>();

	/* Начальное значение BigInteger. */
	private BigInteger i = BigInteger.valueOf(2);
	
	/* Счётчик найденных простых чисел. */
	private int counter = 0;

	@Override
	public boolean hasNext() {
		return true; // Итератор бесконечный.
	}

	@Override
	public BigInteger next() {
		/*
		 * Бесконечный инкремент i. Пока i равно значению last очередного контейнера в
		 * корне PriorityQueue, то мы только продвигаем значения last соответствующих
		 * контейнеров на шаг base.
		 */
		for (; !cand.isEmpty() && i.equals(cand.peek().last); i = i.add(BigInteger.ONE)) {
			/*
			 * Неоптимизировано - разные контейнеры с одинаковыми числами last могут
			 * перебираться неоднократно. Например, last = 12 будет обрабатываться для
			 * контейнеров с base = 2 и с base = 3.
			 */
			while (cand.peek().last.equals(i)) {
				NonPrimeSequence temp = cand.poll();
				// Сдвигаем last на шаг base
				temp.last = temp.last.add(temp.base);
				cand.offer(temp);
			}
		}
		/*
		 * Если мы дошли до сюда, то в корне PriorityQueue больше не оказалось
		 * контейнеров со значением last = i. А это значит, что i ранее не было
		 * множителем какого-либо числа, т. е. i - простое число.
		 */
		cand.add(new NonPrimeSequence(i));
		BigInteger res = i;
		i = i.add(BigInteger.ONE);
		
		Starter.logger.info("prime={}, counter={}", res, ++counter);
		
		return res;
	}
	
	@Override
	public Iterator<BigInteger> iterator() {
		return this;
	}
}
