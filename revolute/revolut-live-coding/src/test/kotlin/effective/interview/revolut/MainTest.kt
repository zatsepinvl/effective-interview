package effective.interview.revolut

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class MainTest {

    @Test
    fun `should return greeting message`() {
        // Given
        val main = Main()

        // When
        val result = main.greeting()

        // Then
        assertThat(result).isEqualTo("Hello, Yandex!")
    }
}