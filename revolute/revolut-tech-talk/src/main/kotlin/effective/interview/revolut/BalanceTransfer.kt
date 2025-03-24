package effective.interview.revolut

import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicReference


interface BalanceTransferService {

    // Should be thread-safe and non-blocking
    fun transfer(from: Account, to: Account, amount: BigDecimal)
}


data class Account(
    val accountId: String,
    private val balanceRef: AtomicReference<BigDecimal>,
) {

    constructor(accountId: String, initialBalance: BigDecimal)
            : this(accountId, AtomicReference(initialBalance))

    val balance: BigDecimal get() = balanceRef.get()

    fun tryWithdraw(amount: BigDecimal): Boolean {
        while (true) {
            val currentBalance = balance
            if (currentBalance < amount) {
                // Insufficient balance
                return false
            }
            val newBalance = currentBalance - amount
            if (balanceRef.compareAndSet(currentBalance, newBalance)) {
                // Successful withdrawal
                return true
            }
        }
    }

    fun deposit(amount: BigDecimal) {
        while (true) {
            val currentBalance = balance
            val newBalance = currentBalance + amount
            if (balanceRef.compareAndSet(currentBalance, newBalance)) {
                // Successful deposit
                break
            }
        }
    }
}

class BalanceTransferServiceImpl : BalanceTransferService {

    override fun transfer(from: Account, to: Account, amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "Amount should be more than 0" }
        if (from.tryWithdraw(amount)) {
            to.deposit(amount)
        } else {
            throw IllegalStateException("Insufficient balance on account ${from.accountId}")
        }
    }

}

fun main() {
    val accountA = Account("A", BigDecimal("1000.50"))
    val accountB = Account("B", BigDecimal("500.25"))

    val service = BalanceTransferServiceImpl()
    service.transfer(accountA, accountB, BigDecimal("200.00"))

    println("Account A balance: ${accountA.balance}")  // 800.50
    println("Account B balance: ${accountB.balance}")  // 700.25

    try {
        service.transfer(accountA, accountB, BigDecimal("801.00"))
    } catch (e: IllegalStateException) {
        // Insufficient balance on account A
        println("Exception: ${e.message}")
    }
}


