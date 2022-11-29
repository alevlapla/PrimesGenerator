package com.generator.main;

import java.util.Arrays;

/**
 * Версия генератора простых чисел на базе int (генерация окончится
 * переполнением int по объёму массива int[])
 *
 * @author bratishka
 * @version 1.0
 */

public class PrimeGeneratorIntBased implements Runnable {
	// Бесконечный генератор не реализовать - в любом случае будет ограничение
	// памяти компьютера. При объёме массива в Integer.MAX_VALUE будет
	// OutOfMemoryError (requested array size exceeds VM limit)
	int generatorCapacity = 50_000_000;

	// Массив с найденными простыми числами. Все найденные простые числа
	// перебираются одно за другим вне зависимости от того, сколько простых чисел
	// было найдено в данном диапазоне
	int pr[] = new int[generatorCapacity];

	// Индекс простого числа из pr[], которое обрабатывается в данный момент
	int pr_current = 0;
	// Общее количество найденных простых чисел
	int pr_counter = 0;

	// Массив с составными числами, на которых закончилось исключение составных
	// чисел в предыдущем диапазоне поиска. В новом диапазоне поиска числа из этого
	// массива будут "стартовым" значением для вычёркивания с соответствующим шагом
	// данного простого числа
	int lpr[] = new int[generatorCapacity];

	// Буфер данного диапазона: начинается на верхней границе предыдущего диапазона
	// (включая), а заканчивается на нижней границе следующего диапазона (исключая)
	boolean buff[];

	// Границы буфера данного диапазона. Тип long для не такого быстрого
	// переполнения переменной
	long start;
	long end;

	@Override
	public void run() {
		end = 1; // Стартовое значение начальной границы первого диапазона
		pr[0] = 2; // Первое простое число известно по условиям задачи
		lpr[0] = 2; // Максимальное составное число, полученное из данного простого числа

		// Бесконечный генератор
		for (;;) {
			// Определяем границы данного диапазона и его длину
			start = end + 1;
			end = pr[pr_current] * pr[pr_current] - 1L;

			// Если buff_len > Integer.MAX_VALUE, то создать массив технически не удастся.
			// После этого диапазоны следует просматривать только c равной длиной
			// Integer.MAX_VALUE (здесь не реализуется)
			long buff_len = end - start + 1;
			if (buff_len > Integer.MAX_VALUE) {
				System.out.println("buff_len quasi-overflowed");
				return;
			}

			// Массив хранения результатов обработки данного диапазона, заполненный true.
			// Если после вычёркивания чисел из данного массива - элемент остаётся равный
			// true, то скорректированный индекс данного элемента является простым числом
			buff = new boolean[(int) buff_len];
			Arrays.fill(buff, true);

			// Вычёркивание из текущего диапазона составных множителей ранее найденных
			// простых чисел. Для этого используются максимальные составные множители,
			// использовавшиеся в последний раз (в предыдущем диапазоне), размещаемые в
			// lpr[]
			// Первый диапазон [2,4) будет пропущен - но оно нам и не надо
			// Обрабатываем каждое найденное ранее простое число
			for (int j = 0; j < pr_counter; j++) {
				// Для каждого ранее найденного простого числа вычёркиваем числа из нового
				// диапазона (с шагом данного простого числа)

				// Коэффициент для уменьшения количества итераций (если pr[] - нечётное, то
				// достаточно проходить с шагом 2*x
				int coeff = pr[j] % 2 == 0 ? 1 : 2;
				int lpr_current = lpr[j];

				for (int m = lpr[j] + coeff * pr[j]; m <= end; m = m + coeff * pr[j]) {
					// Корректировка необходима для обработки случаев, когда последнее
					// составное число в lpr[] для данного простого числа оказывается сильно меньше
					// начала диапазона start, то есть суммы lpr[i] + pr[i] не хватает
					long corrected = m - start;
					while (corrected < 0) {
						corrected = corrected + pr[j];
					}

					// Если после корректировки мы оказались за диапазоном buff[] - то с этим числом
					// ловить дальше нечего
					if (corrected > buff_len) {
						break;
					}

					// Число присутствует с шагом ранее найденного простого числа - значит оно
					// составное
					if (buff[(int) corrected] == true) {
						buff[(int) corrected] = false;

						// Последнее использовавшееся составное число на базе данного простого числа
						// обновим в lpr[], только если оно больше старого
						if (m > lpr[j]) {
							lpr[j] = m;
						}
					}
				}
			}

			// Извлечение простых чисел из данного boolean[]
			for (int j = 0; j < buff_len; j++) {
				// Выход за границу generatorCapacity равнозначно прекращению работы треда
				if (pr_current == generatorCapacity) {
					return;
				}

				if (buff[j] == true) {
					int temp = j + (int) start; // Найденное простое число. Риск переполнения int
					pr[pr_counter] = temp;
					lpr[pr_counter] = temp;
					pr_counter++; // Общее число найденных простых чисел увеличивается

					Starter.logger.info("i={}, pr_counter={}, prime_found={}, prime_base={} [{}, {})", pr_current, pr_counter, temp, pr[pr_current], start, end + 1);
				}
			}
			pr_current++; // Переход к разбору следующего необработанного простого числа
		}
	}
}
