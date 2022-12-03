package com.generator.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

/**
 * Реализация генератора простых чисел в виде итератора на базе HashMap. Числа
 * хранятся в виде: ключ - число для проверки (кандидат), значение -
 * List<Integer> с простыми множителями кандидата. Если проверяемое число
 * отсутствует в HashMap - то оно простое, т. к. ранее не был найден ни один
 * простой множитель. Пара, соответствующая проверенному числу, из HashMap
 * удаляется.
 * 
 * Проверяются только нечётные числа.
 * 
 * @author PabloDons (<a href=
 *         "https://gist.github.com/PabloDons/0017157ea95fb2fa8ca553b041a8972f">github.com</a
 *         href>)
 * @author bratishka
 * @version 1.0
 */
public class PrimesGeneratorIteratorBased implements Iterator<Long>, Iterable<Long> {
	/* HashMap хранилище простых делителей очередного кандидата для проверки. */
	private Map<Long, List<Long>> cand = new HashMap<>();

	/* Текущее проверяемое число. */
	private long curr = 2;

	/* Счётчик найденных простых чисел. */
	private int counter = 0;

	@Override
	public boolean hasNext() {
		// Бесконечная последовательность.
		return true;
	}

	@Override
	public Long next() {
		if (curr == 2) {
			counter++;
			/*
			 * Единственная возможность вернуть чётное число с последующим выходом на
			 * итерацию только по нечётным числам.
			 */
			return curr--;
		}
		curr += 2;

		if (cand.containsKey(curr)) {
			for (int i = 0; i < cand.get(curr).size(); i++) {
				Long currentFactor = cand.get(curr).get(i);
				Long next = curr + 2 * currentFactor;
				List<Long> temp = cand.getOrDefault(next, new ArrayList<>());
				temp.add(currentFactor);
				cand.put(next, temp);
			}
			cand.remove(curr); // Проверенная пара больше не понадобится.

			return next();
		} else {
			List<Long> temp = new ArrayList<>();
			temp.add(curr);
			cand.put(curr * curr, temp);

			Starter.logger.info("prime={}, counter={}", curr, ++counter);

			return curr;
		}
	}

	@Override
	public Iterator<Long> iterator() {
		return this;
	}
}
