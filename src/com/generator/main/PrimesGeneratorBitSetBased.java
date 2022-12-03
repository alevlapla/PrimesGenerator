package com.generator.main;

import java.util.BitSet;
import java.util.NoSuchElementException;
import java.util.function.IntFunction;

/**
 * Фактически не является генератором, а является решетом (т. е. имеет
 * ограничение длины).
 * 
 * @author Владимир Куров (<a href="https://stackoverflow.com/questions/43760641/java-8-streams-and-the-sieve-of-eratosthenes/">StackOverflow</a href>)
 * @author bratishka
 * @version 1.0
 */
public class PrimesGeneratorBitSetBased implements Runnable {
	/* Квазибесконечный размер bitBitSet. */
	private int size = 10_000_000;
	/*
	 * BitSet, представляющий решето и содержащий информацию об уже исключённых
	 * множителях.
	 */
	private BitSet bs;
	/*
	 * Первый элемент, с которого начинается проверка. long - для защиты от
	 * переполнения при возведении в квадрат.
	 */
	private long curr;
	/* Счётчик найденных простых множителей. */
	private int counter = 0;

	@Override
	public void run() {
		bs = new BitSet(size);
		bs.set(2, size, true);

		int temp; // Текущий исключаемый множитель
		curr = bs.stream().min().getAsInt();
		for (;;) {
			// Здесь будет перегрузка int при 46341*46341.
			if (curr * curr <= size) {
				int i = 1;
				while ((temp = (int) curr + (int) curr * i) <= size) {
					bs.set(temp, false);
					i++;
				}
			}
			/*
			 * Следующий минимальный не вычеркнутый множитель, больше только что
			 * проверенного числа.
			 */
			counter++;
//			Starter.logger.info("prime={}, counter={}", curr, counter);
			try {
				// Если минимальный элемент не удастся извлечь из стрима - то мы достигли конца
				// решета
				curr = bs.stream().filter(candidate -> candidate > curr).min().getAsInt();
			} catch (NoSuchElementException e) {
				break;
			}
		}
		
		Starter.logger.info("prime={}, counter={}", curr, counter);
	}
}
