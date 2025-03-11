package effective.interview.revolut

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RoundRobinBalancingStrategyTest {

    @Test
    fun `should get by round robin`() {
        // Given
        val servers = (1..3).map {
            Server("address$it")
        }
        val strategy = RoundRobinBalancingStrategy()

        // When
        val results = (1..5).map {
            strategy.getNext(servers)
        }

        // Then
        assertThat(results).containsExactly(
            servers[0],
            servers[1],
            servers[2],
            servers[0],
            servers[1]
        ).inOrder()
    }
}

