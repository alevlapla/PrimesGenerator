package com.generator.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Collector;

/**
 * Генератор простых чисел на базе коллектора. Поток 3, 5, 7, ... фильтруется
 * проверкой {@link java.util.stream.Stream#noneMatch() .noneMatch()} в методе
 * {@link java.util.stream.Stream#accumulator .accumulator()}.
 *
 * @author wilmol (<a href=
 *         "https://stackoverflow.com/questions/43760641/java-8-streams-and-the-sieve-of-eratosthenes/">StakOverflow</a
 *         href>)
 * @author bratishka
 * @version 1.0
 */
public class PrimesGeneratorCollectorBased implements Runnable {
	/* Счётчик найденных простых чисел. */
	private static int counter = 0;

	/* Кастомный коллектор, выполняющий проверку ранее найденных простых чисел. */
	Collector<Integer, ?, List<Integer>> sieve = new Collector<Integer, List<Integer>, List<Integer>>() {
		@Override
		public Supplier<List<Integer>> supplier() {
			return () -> new ArrayList<>();
		}

		@Override
		public BiConsumer<List<Integer>, Integer> accumulator() {
			return (primes, candidate) -> {
				// Если candidate не делится на весь список primes - то это очередное простое
				// число
				if (primes.stream().noneMatch(p -> candidate % p == 0)) {
					primes.add(candidate);
					counter++;
					Starter.logger.info("count={}, prime={}", counter, candidate);
				}
			};
		}

		@Override
		public BinaryOperator<List<Integer>> combiner() {
			return (l1, l2) -> {
				l1.addAll(l2);
				return l1;
			};
		}

		@Override
		public Function<List<Integer>, List<Integer>> finisher() {
			return Function.identity();
		}

		@Override
		public Set<Collector.Characteristics> characteristics() {
			Set<Collector.Characteristics> set = new HashSet<>();
			set.add(Collector.Characteristics.IDENTITY_FINISH);
			return set;
		}
	};

	@Override
	public void run() {
		IntStream.iterate(3, candidate -> candidate + 2).boxed().collect(sieve);
		
		/* Более компактные вариации */
		
//		IntStream.iterate(3, candidate -> candidate + 2).boxed().collect(ArrayList<Integer>::new, (primes, candidate) -> {
//			if(primes.stream().noneMatch(p -> candidate % p == 0)) {
//				primes.add(candidate);
//				counter++;
//				Starter.logger.info("count={}, prime={}", counter, candidate);
//			}
//		}, ArrayList::addAll);
		
//		IntStream.iterate(3, candidate -> candidate + 2).boxed().collect(ArrayList<Integer>::new, (primes, candidate) -> {
//			int ceil = (int) Math.sqrt(candidate);
//			
//			
//			if(primes.stream().takeWhile(p -> p <= ceil).noneMatch(p -> candidate % p == 0)) {
//				primes.add(candidate);
//				counter++;
//				Starter.logger.info("count={}, prime={}", counter, candidate);
//			}
//		}, ArrayList::addAll);
	}
}
