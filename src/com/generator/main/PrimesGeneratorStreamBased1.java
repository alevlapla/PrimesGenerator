package com.generator.main;

import java.util.stream.IntStream;
import java.util.function.IntPredicate;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

/**
 * Суть: isPrime - содержит IntPredicate функцию. Поле является static и
 * mutable. Каждая очередная проверка в filter вызывает предикат isPrime,
 * который был изменён в предыдущей итерации стрима. Если число успешно прошло
 * проверку в filter(), то оно пропускается дальше в peek(). В peek() происходит
 * модификация static предиката следующим образом: к данному состоянию предиката
 * добавляется дополнительное условие v % i != 0 методом .and(). Это условие
 * требует, чтобы прошедшее число i (простое число, прошедшее filter(),
 * предыдущей итерации) не делило нацело следующее проверяемое число (полученное
 * из метода .iterate()). Таким образом, после каждого успешного нахождения
 * простого числа, мы бесконечно наращиваем цепочку Predicate(true).add(v % 2 !=
 * 0).add(v % 3 != 0)... При последующих проверках, царь-предикат разматывается
 * "в порядке очереди". Например, если число делится на 2, то первый .add()
 * возвращает false и дальнейшая оценка цепочки предиката не производится
 * (.and() обеспечивает short-circuiting logical AND).
 * 
 * На числе 31399 возникает StackOverflowError.
 * 
 * @author Alec (<a href=
 *         "https://stackoverflow.com/questions/43760641/java-8-streams-and-the-sieve-of-eratosthenes">StackOverflow</a
 *         href>)
 * @author bratishka
 * @version 1.0
 */
public class PrimesGeneratorStreamBased1 implements Runnable {
	/* Счётчик количества проверенных предикатов в данном царь-предикате. */
	static int currentCheck = 1;
	/* Счётчик простых чисел */
	static int counter = 0;

	/*
	 * Static mutable объект, который будет изменяться в конце стрима, если текущий
	 * элемент стрима успешно дойдёт до терминального оператора. Дошедший до конца
	 * стрима элемент будет добавлять "цепочкой" дополнительное условие предикату.
	 */
	static IntPredicate isPrime = x -> {
//		System.out.println("   ...звено цепи предикатов вызвано (звено № " + currentCheck + ")... первое всегда true");
		currentCheck++;
		// Первый проход - проверенных чисел ещё нет.
		return true;
	};

	/*
	 * static IntPredicate isPrime = new IntPredicate() {
	 * 
	 * @Override public boolean test(int x) { System.out.println(
	 * "   ...звено цепи предикатов вызвано (звено № " + currentCheck +
	 * ")... первое всегда true"); currentCheck++; return true; } };
	 */

	static IntStream primes = IntStream.iterate(2, i -> {
//		System.out.println("iterate() генерирует число \"" + i + "\"");
		return i + 1;
	})
			/*
			 * В фильтр входит последовательность 2, 3, 4, 5... Число проверяется по
			 * предикату, который является бесконечно наращиваемой цепочкой предикатов
			 * (методом .and()). Каждое простое число добавляет в царь-предикат условие
			 * .and(следующий_кандидат % это_простое_число != 0). В итоге получается цепочка
			 * предикатов Predicate(true).add(v % 2 != 0).add(v % 3 != 0).
			 */
			.filter(i -> {
				currentCheck = 1;

//				System.out.println("filter() проверяет число \"" + i + "\" цепочкой предикатов... ");

				// TODO попробуй - точно слева направо цепочка разматывается?

				// Прежде чем вычислить .test(i), будет вычислена цепочка предикатов до:
				// Predicate(true), затем .add(v % 2 != * 0), потом .add(v % 3 != 0)...
				return isPrime.test(i);
			})

			// В peek входят только простые числа. Очередное простое число модифицирует
			// царь-предикат, добавляя ему очередное условие .and(следующий_кандидат %
			// это_простое_число != 0)
			.peek(i -> isPrime = isPrime.and(v -> {
//				System.out.println(
//						"   ...звено цепи предикатов вызвано (звено № " + currentCheck + ")... test(" + i + ")");
				currentCheck++;
				return v % i != 0;
			}));

	/*
	 * .peek(new IntConsumer() {
	 * 
	 * @Override public void accept(int i) {
	 * System.out.println("peek() приняло очередное простое число \"" + i +
	 * "\", которым будет дополнительно ограничен предикат"); isPrime =
	 * isPrime.and(new IntPredicate() {
	 * 
	 * @Override public boolean test(int v) {
	 * System.out.println("   ...звено цепи предикатов вызвано (звено № " +
	 * currentCheck + ")... test(" + i + ")"); currentCheck++; return v % i != 0; }
	 * }); } });
	 */

	@Override
	public void run() {
		primes.forEach(p -> {
			counter++;
			Starter.logger.info("prime={}, count={}", p, counter);
		});
	}
}
