package effective.iterview.yandex

import java.util.SortedMap
import java.util.TreeMap
import kotlin.math.min

/**
 * Банкомат.
 * Инициализируется набором купюр и умеет выдавать купюры для заданной суммы, либо отвечать отказом.
 * При выдаче купюры списываются с баланса банкомата.
 * Допустимые номиналы: 50₽, 100₽, 500₽, 1000₽, 5000₽.
 */
class ATM {

    private val money: MutableMap<Currency, SortedMap<Int, Int>> = TreeMap()

    fun withdraw(currency: Currency, amount: Int): WithdrawResult {
        val banknotes = mutableMapOf<Int, Int>()
        var left = amount
        val currencyMoney = money[currency]
            ?: return WithdrawResult("No money in ATM", currency = currency)
        for (denomination in currencyMoney.keys) {
            if (left == 0) {
                break
            }
            val currentCount = left / denomination
            val leftInAtm = currencyMoney[denomination] ?: 0
            if (currentCount > 0) {
                val countTake = min(currentCount, leftInAtm)
                banknotes[denomination] = countTake
                left -= countTake * denomination
            }
        }
        return if (left > 0) {
            WithdrawResult("No money in ATM", currency = currency)
        } else {
            banknotes.forEach { (denomination, count) ->
                currencyMoney[denomination] = currencyMoney[denomination]!! - count
            }
            WithdrawResult(null, banknotes, currency = currency)
        }
    }

    /**
     * Deposit with denomination of 50₽, 100₽, 500₽, 1000₽, 5000₽ and count of banknotes
     */
    fun deposit(currency: Currency, denomination: Int, banknoteCount: Int) {
        if (!currency.denominations.contains(denomination)) {
            throw IllegalArgumentException("No such denomination: $denomination")
        }
        money.putIfAbsent(currency, TreeMap { a, b -> b - a })
        money[currency]!![denomination] = money[currency]!![denomination] ?: (0 + banknoteCount)
    }
}


data class WithdrawResult(
    val error: String? = null,
    /**
     * (denomination to count of banknotes)
     */
    val banknotes: Map<Int, Int>? = null,
    val currency: Currency
)

enum class Currency(
    val denominations: Set<Int>
) {
    RUB(setOf(50, 100, 500, 1000, 5000)),
    USD(setOf(50, 100, 500, 1000, 5000)),
    EUR(setOf(20, 100, 500)),
}