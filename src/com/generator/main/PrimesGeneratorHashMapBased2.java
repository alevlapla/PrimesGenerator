package com.generator.main;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;

/**
 * Генератор состоит из: 1) внешнего итератора, 2) бесконечного каскада
 * внутренних итераторов (все итераторы возвращают простые числа), 3) HashMap, в
 * которой учитываются пары, создаваемые после прохождения каждого квадрата
 * простого числа basepr (pr^2 + advance, advance). Своя HashMap будет
 * присутствовать в каждом итераторе.
 * 
 * Вызов next() внешнего итератора после его возврата 5L - создаст внутренний
 * итератор и вызывает у него 2 раза метод next() (состояние внутреннего
 * итератора приведено к: basepr = 3, baseprsqr = 9, candidate = 5). Состояние
 * внешнего итератора после этого вызова next(): basepr = 3, baseprsqr = 9,
 * candidate = 7.
 * 
 * Простые числа 2L, 3L, 5L обрабатываются итератором особым образом.
 * 
 * Если данное проверяемое число candidate становится больше basepr^2, или если
 * оно уже содержится в HashMap, то это число необходимо обработать следующим
 * образом. Если candidate >= baseprsqr, то в HashMap добавляется пара вида
 * (basepr^2 + advance, advance). Если candidate < baseprsqr, то из HashMap
 * извлекается пара (candidate, advance) и смещается на (candidate + advance,
 * advance).
 * 
 * Проблема: когда candidate = 5 - создаётся вложенный итератор. Получается
 * бесконечная вложенность итераторов, которые периодически прокручивают сами
 * себя. При проверке 1_000_000 создаются 5 вложенных итераторов, хотя на первый
 * взгляд требуется только один вложенный. У каждого вложенного итератора будет
 * в том или ином месте вызов своего вложенного итератора .next(). Например,
 * если ограничить вложенные итераторы только 1, то при candidate = 23^2 = 529
 * будет попытка извлечь из вложенного итератора следующее простое число (27).
 * Но, т. к. у вложенного итератора нет своего вложенного итератора - то извлечь
 * следующее простое число (27) неоткуда. Вместо 27 итератор пропускает 25 как
 * простое число - с этого момента алгоритм работает неправильно.
 * 
 * Рефакторинг: вместо вложенного итератора можно аккумулировать найденные
 * простые числа в ArrayList/ArrayDeque, а вместо вызова next() вложенного
 * итератора, извлекать очередной элемент из листа/очереди. Но в таком случае
 * появится некое "промежуточное" хранилище простых чисел - и нарушается замысел
 * данного генератора, как основывающегося на отсутствии какого-либо хранилища
 * чисел.
 * 
 * @author unknown (<a href=
 *         "https://rosettacode.org/wiki/Sieve_of_Eratosthenes">rosettacode.org</a
 *         href>)
 * @author bratishka
 * @version 1.0
 */
public class PrimesGeneratorHashMapBased2 implements Iterator<Long> {
	/*
	 * HashMap, хранящая пары чисел: candidate и advance = candidate * 2 (шаг сдвига
	 * данного candidate). Когда итератор встречает число candidate - он будет
	 * продвигать его на шаг candidate + advance (т. к. оно не может быть простым).
	 */
	private final Map<Long, Long> nonprimes = new HashMap<>();
	/*
	 * Очередное число, которое будет проверено итератором при вызове next(). После
	 * 5L итератор проверяет числа с шагом 2L
	 */
	private long candidate = 2L;
	/*
	 * Текущие базовые числа (простое и квадрат простого). Если candidate >=
	 * baseprsqr, то в HashMap добавляется пара вида (baseprsqr + advance, advance),
	 * т. к. все числа вида baseprsqr + advance * x являются составными числами.
	 * Данная пара учитывает все дальнейшие составные множители на базе данного
	 * простого числа basepr и будет продвигаться дальше, когда очередной candidate
	 * = baseprsqr + advance некой пары в HashMap.
	 */
	private long basepr = 3L;
	private long baseprsqr = 9L;
	/*
	 * Внутренний итератор для получения следующего простого числа, используемого в
	 * сравнениях с baseprsqr, создании очередной пары в HashMap (идентичен внешнему
	 * итератору).
	 */
	private Iterator<Long> baseprimes = null;
	/* Счётчик найденных простых чисел. */
	private int counter = 0;

	@Override
	public boolean hasNext() {
		return true; // Бесконечная итерация.
	}

	@Override
	public Long next() {
		// Особая обработка простых чисел candidate = 2L...5L. Когда candidate = 7L,
		// создаётся внутренний итератор для создания пар для HashMap.
		if (candidate <= 5L) {
			if (candidate++ == 2L) {
				counter++;
				return 2L;
			} else {
				candidate++; // Инкремент при вычислении выражения в if выше и данный -> +2
				if (candidate == 5L) {
					counter++;
					return 3L;
				} else {
					// Внутренний итератор для создания пар для HashMap.
					baseprimes = new PrimesGeneratorHashMapBased2();
					/*
					 * У внутреннего итератора дважды вызываем next() чтобы привести его в состояние
					 * basepr = 3, baseprsqr = 9, candidate = 5 с последним return = 3L. При
					 * очередном вызове он возвратит 5L, перейдя в состояние basepr = 3, baseprsqr =
					 * 9, candidate = 7. При следующем вызове особой обработки больше не будет,
					 * candidate = 7 будет возвращён как простое число.
					 */
					baseprimes.next();
					baseprimes.next();

					counter++;
					return 5L;
				}
			}
		}

		// Обработка чисел >= 7L с шагом 2.
		// Если candidate == baseprsqr (или == candidate^2), то это не простое число.
		// Если candidate содержится в HashMap, то это не простое число.
		for (; candidate == baseprsqr || nonprimes.containsKey(candidate); candidate += 2) {
			// Обработка первого условия условного OR в цикле
			if (candidate == baseprsqr) {
				// candidate досиг своего значения basepsqr = basep * basep. С этого момента
				// необходимо постоянно сдвигать составной множитель данного простого числа
				// Величина сдвига = 2 * basep
				long advance = basepr << 1; // *2
				// В HashMap заносится пара (basep^2 + сдвиг, сдвиг). Обрабатывать пару с чистым
				// квадратом не имеет смысла (это гарантированно не простой множитель).
				nonprimes.put(baseprsqr + advance, advance);
				// Извлечение следующего базового простого числа basepr из внутреннего
				// итератора.
				// Предыдущие значения basepr уже учтены в парах HashMap.
				basepr = baseprimes.next();
				baseprsqr = basepr * basepr;
			}
			// Обработка второго условия условного OR в цикле. candidate содержится в
			// HashMap: пару вида (candidate, advance) необходимо сместить на место
			// (candidate + advance, advance). Если смещённая пара уже существует -
			// продолжаем смещение на advance (чтобы бессмысленно не перезаписывать
			// присутствующее число candidate, которое гарантированно уже не простое число).
			else {
				long advance = nonprimes.remove(candidate);
				long next = candidate + advance;
				while (nonprimes.containsKey(next)) {
					next += advance;
				}
				nonprimes.put(next, advance);
			}
		}

		// candidate, дошедший до сюда, является простым числом.
		long res = candidate;
		candidate += 2;
		counter++;
		return res;
	}
}
