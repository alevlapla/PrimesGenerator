package com.generator.main;

import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.util.HashMap;

/**
 * Число будет простым, если в HashMap не будет равного ему значения. Простые
 * числа pr добавляются в HashMap парой (pr, pr). Перед тем, как признать число
 * простым и записать его в HashMap, алгоритм перебирает уже ранее найденные
 * простые числа и их наибольшие составные множители. Если наибольший составной
 * множитель оказывается меньше проверяемого числа, то оно увеличивается на шаг
 * своего простого числа-"партнёра" (т. е. ключа данной пары). Таким образом
 * гарантируется то, что все составные числа "в окрестности" проверяемого числа
 * будут продвинуты перед проверяемым числом, что приведёт к тому, что
 * проверяемое число уже будет содержится в значении некой пары HashMap, если
 * оно не простое.
 * 
 * @author Nobby Nobbs (<a href=
 *         "https://stackoverflow.com/questions/43760641/java-8-streams-and-the-sieve-of-eratosthenes/">StackOverflow</a href>)
 * @author bratishka
 * @version 1.0
 */
public class PrimesGeneratorStreamBased2 implements Runnable {
	/* Счётчик найденных простых чисел */
	private static int counter = 0;

	private static Stream<Integer> primeStreamEra() {
		/*
		 * HashMap для хранения пар: [простое число pr]-[составное число pr*x]. Ключи
		 * представляют найденные простые числа. Значения представляют ближайшие
		 * составные числа к данному проверяемому простому числу
		 */
		final HashMap<Integer, Integer> seedsFactors = new HashMap<Integer, Integer>();

		return IntStream.iterate(2, i -> {
//			System.out.println("interate() генерирует число \"" + i + "\"");
			return i + 1;
		})
				/* filter() в бесконечной итерации - возвращает только true. */
				.filter(i -> {
					final int currentNum = i;

//					System.out.println("filter() обрабатывает число \"" + i + "\"");

					seedsFactors.entrySet().parallelStream().forEach(e -> {
						/*
						 * Если ранее найденное составное число, соответствующее данному простому числу
						 * pr, окажется меньше данного проверяемого числа x, то старое составное число
						 * будет увеличено на шаг pr, т. е. новое число = pr + (x * pr), где в скобках -
						 * старое составное число, соответствующее данному простому числу pr.
						 */
						while (e.getValue() < currentNum) {
//							System.out.println(
//									"...обновление значений hm для (" + e.getKey() + ", " + e.getValue() + ")");
							e.setValue(e.getValue() + e.getKey());
						}
					});

					if (!seedsFactors.containsValue(i)) {
						if (i != 1) {
//							System.out.println("...добавление пары (" + i + ", " + i + ")");
							seedsFactors.put(i, i);
						}
						counter++;
						return true; // Число простое.
					}
					return false; // Число не простое.
				}).boxed();
	}

	@Override
	public void run() {
		primeStreamEra().forEach(i -> {
			Starter.logger.info("prime={}, count={}", i, counter);
			return;
		});
	}
}
