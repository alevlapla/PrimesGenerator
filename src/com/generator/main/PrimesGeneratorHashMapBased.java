package com.generator.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;

/**
 * Максимально оптимальная реализация генератора простых чисел на базе HashMap.
 * Каждая пара состоит из: key = проверяемое число Long, value = List<Long> с
 * возможными простыми делителями проверяемого числа Long (key). После просмотра
 * данного key - пара удаляется. Если key на момент проверки не имело ранее
 * найденных простых делителей - оно простое.
 * 
 * Переполнение Integer: происходит при возведении в квадрат проверяемого числа
 * начиная с 46_341. Переполненная переменная после прокрутки несколько раз
 * может принять значение ещё не проверенного простого числа. Таким образом,
 * перегруженные ключи беспорядочно заполняли проверенный и непроверенный
 * диапазоны. При этом на работе алгоритма это почти не сказывалось (выпадающие
 * числа - см. след. абзац), т. к. генерируемые ключи (квадраты чисел) были
 * заведомо на порядки больше фактически проверяемого ограниченного диапазона
 * (при бесконечной генерации они внесут свои нарушения).
 * 
 * Переполнение Long: случится при возведении в квадрат числа 3_037_000_500
 * 
 * Например, 92683^2 вызовет двойную перегрузку Integer с результатом 203897.
 * Это число является простым, ещё не проверенным, но оно уже вычеркнуто. При
 * проверке 10^6 чисел из-за этого выпадают числа 203897, 262147, 299281,
 * 526307, 643649. При увеличении диапазона генерации - потенциально простые
 * числа выпадают в ещё большем количестве.
 * 
 * Количество генерируемых простых чисел проверено до предела 10^8 (см.
 * пи-функция в Википедии).
 * 
 * Реализация LinkedHashMap - пары хранятся в порядке создания - для данной
 * задачи это бессмысленно.
 * 
 * @author bratishka
 * @version 1.0
 **/

public class PrimesGeneratorHashMapBased implements Runnable {
	/*
	 * Список обрабатываемых чисел. Key - число, value - список найденных простый
	 * делителей данного числа
	 */
	private Map<Long, List<Long>> cand = new HashMap<>();
	/* Счётчик найденных простых чисел */
	private int count = 0;

	@Override
	public void run() {
		Long curr = Long.valueOf(3); // Начало перебора от 3

		for (;;) {
			// Если key в HashMap нет - данное число простое
			if (!cand.containsKey(curr)) {
				List<Long> temp = new ArrayList<>();
				temp.add(curr);

				// "Проброс" данного простого множителя на позицию i * i.
				// Необходимо помнить о переполнении. В данном случае проверка на переполнение
				// не выполняется в связи с заменой Integer на Long (при Integer переполнение
				// возникнет при возведении в квадрат 46_341)
				cand.put(curr * curr, temp);
				count++;

				Starter.logger.info("Prime={}, count={}, size={}, threshold={}", curr, count, cand.size(),
						reflectThreshold(cand));
			} else {
				// Если key в HashMap есть (данное число составное) - "пробросим" присутствующие
				// в List множители на соответствующие шаги вперёд. Проблем переполнения - см.
				// комментарий выше
				List<Long> temp = cand.get(curr);
				int len = temp.size();

				for (int i = 0; i < len; i++) {
					// Если целевого числа для "проброса" нет - создадим соответствующий key.
					// Или добавим очередной проброшенный простой делитель
					Long next_index = curr + temp.get(i) * 2;
					Long curr_sub = temp.get(i);
					List<Long> temp_next = cand.getOrDefault(next_index, new ArrayList<>());
					temp_next.add(curr_sub);
					cand.put(next_index, temp_next);
				}
			}
			// Текущая пара больше не нужна - удалим её
			cand.remove(curr);
			curr += 2;
		}
	}

	/**
	 * Извлекает текущее значение threshold при помощи Reflection API.
	 * 
	 * @param hm HashMap, значение поля threshold которой будет извлекаться
	 * @return значение поля threshold
	 */
	private static int reflectThreshold(Map<?, ?> hm) {
		int res = 0;
		try {
			Field f = hm.getClass().getDeclaredField("threshold");
			f.setAccessible(true);
			res = f.getInt(hm);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		return res;
	}
}
