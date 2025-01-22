package effective.iterview.yandex

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ATMTest {

    @Test
    fun `should single deposit and then withdraw`() {
        // Given
        val atm = ATM()
        atm.deposit(Currency.RUB, 100, banknoteCount = 1)

        // When
        val result = atm.withdraw(Currency.RUB, 100)

        // Then
        assertThat(result.banknotes).isEqualTo(
            mapOf(100 to 1)
        )
    }

    @Test
    fun `should single deposit and then withdraw then withdraw with error`() {
        // Given
        val atm = ATM()
        atm.deposit(Currency.RUB, 100, banknoteCount = 1)

        // When
        val result1 = atm.withdraw(Currency.RUB, 100)
        val result2 = atm.withdraw(Currency.RUB, 100)

        // Then
        assertThat(result1.banknotes).isEqualTo(mapOf(100 to 1))
        assertThat(result2.error).isNotNull()
    }

    @Test
    fun `should  deposit single in EUR and then withdraw`() {
        // Given
        val atm = ATM()
        atm.deposit(Currency.RUB, 100, banknoteCount = 1)
        atm.deposit(Currency.EUR, 20, banknoteCount = 1)

        // When
        val result = atm.withdraw(Currency.EUR, 20)

        // Then
        assertThat(result.banknotes).isEqualTo(mapOf(20 to 1))
        assertThat(result.currency).isEqualTo(Currency.EUR)
    }

    @Test
    fun `should deposit multiple and then withdraw then withdraw with error`() {
        // Given
        val atm = ATM()
        atm.deposit(Currency.RUB, 100, banknoteCount = 6)

        // When
        val result1 = atm.withdraw(Currency.RUB, 500)
        val result2 = atm.withdraw(Currency.RUB, 500)

        // Then
        assertThat(result1.banknotes).isEqualTo(mapOf(100 to 5))
        assertThat(result2.error).isNotNull()
    }

    @Test
    fun `should many deposit and then withdraw`() {
        // Given
        val atm = ATM()
        atm.deposit(Currency.RUB, 50, banknoteCount = 1)
        atm.deposit(Currency.RUB, 100, banknoteCount = 1)
        atm.deposit(Currency.RUB, 500, banknoteCount = 1)
        atm.deposit(Currency.RUB, 1000, banknoteCount = 1)
        atm.deposit(Currency.RUB, 5000, banknoteCount = 1)


        // When
        val result = atm.withdraw(Currency.RUB, 6650)

        // Then
        assertThat(result.banknotes).isEqualTo(
            mapOf(50 to 1, 100 to 1, 500 to 1, 1000 to 1, 5000 to 1)
        )
    }

    @Test
    fun `should many deposit and then withdraw 2`() {
        // Given
        val atm = ATM()
        atm.deposit(Currency.RUB, 100, banknoteCount = 5)
        atm.deposit(Currency.RUB, 1000, banknoteCount = 1)

        // When
        val result = atm.withdraw(Currency.RUB, 1400)

        // Then
        assertThat(result.banknotes).isEqualTo(
            mapOf(
                100 to 4,
                1000 to 1,
            )
        )
    }

    @Test
    fun `should many deposit and then withdraw 3`() {
        // Given
        val atm = ATM()
        atm.deposit(Currency.RUB, 50, banknoteCount = 5)
        atm.deposit(Currency.RUB, 100, banknoteCount = 5)

        // When
        val result = atm.withdraw(Currency.RUB, 700)

        // Then
        assertThat(result.banknotes).isEqualTo(
            mapOf(
                50 to 4,
                100 to 5,
            )
        )
    }

    @Test
    fun `should withdraw with exception when no money`() {
        // Given
        val atm = ATM()

        // When
        val result = atm.withdraw(Currency.RUB, 100)

        // Then
        assertThat(result.error).isNotNull()
    }

    @Test
    fun `should withdraw with exception when no money of denomination`() {
        // Given
        val atm = ATM()
        atm.deposit(Currency.RUB, 100, 9)

        // When
        val result = atm.withdraw(Currency.RUB, 1000)

        // Then
        assertThat(result.error).isNotNull()
    }

}