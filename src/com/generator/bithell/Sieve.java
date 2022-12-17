package com.generator.bithell;

import java.util.Iterator;

/* Оператор "== 0 >>> 0" приводит левый ноль к int (перед смещением, т. е. умножением на 2^0 = 1, а затем сравнивает? */
/**
 * Классическое решето Эратосфена (без каких-либо оптимизаций).
 * 
 * @author GordonBGood (<a href=
 *         "https://stackoverflow.com/questions/39312107/implementing-the-page-segmented-sieve-of-eratosthenes-in-javascript">StackOverflow</a
 *         href>)
 * @author bratishka
 * @version 1.0
 */
public class Sieve implements Iterator<Long>, Iterable<Long> {
	/* Последнее проверяемое число. */
	private final long limit = 100;
	/* Размер буфера для учёта проверяемых чисел. */
	private final long size = limit - 1;
	/*
	 * Массив с числами, из которых будут вычёркиваться составные числа
	 * (инициализирован значениями "0"). i = 0 подразумевает число 2, i = 1
	 * подразумевает число 3 и т. д.
	 */
	private final int[] composites = new int[(int) size];
	/*
	 * Индекс последнего проверенного простого числа. bi - bit index, из алгоритма,
	 * основанного на битах.
	 */
	private long bi = 0;

	@Override
	public boolean hasNext() {
		return true; // Бесконечная итерация.
	}

	@Override
	public Long next() {
		/* Бесконечный цикл, проходящий кандидаты в простые числа 2, 3, 4... */
		for (long p = 2;; ++p) {
			long sqr = p * p;
			if (sqr > limit)
				break; // Выход за пределы limit.

			// Массив условно начинается с числа 2. 0 = false, 1 = true, false - простое
			if (composites[(int) p - 2] == 0) {
				// Проход массива начиная с квадрата кандидата (с учётом того, что массив
				// начинается с числа 2) с шагом данного кандидата.
				for (long c = sqr - 2; c < limit; c += p) {
					composites[(int) c] = 1;
				}
			}
		}
		// Поиск очередного не равного нуля индекса в массива. bi + 2 - будет простым
		// числом, если composites[bi] == 1.
		while (bi < size && composites[(int) bi] != 0) {
			++bi;
		}
		// Инкремент постфиксальный, чтобы новый вызов next() не вернул тот же самый
		// результат.
		return Long.valueOf(bi++ + 2);
	}

	@Override
	public Iterator<Long> iterator() {
		return this;
	}
}
