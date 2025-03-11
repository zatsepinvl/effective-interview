package effective.interview.revolut

import com.google.common.truth.Truth.assertThat
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class LoadBalancerImplTest {

    class DummyStrategy : BalancingStrategy {
        override fun getNext(servers: List<Server>): Server {
            return servers.first()
        }
    }

    class DummyHeartBeats(
        private val offline: Set<String> = emptySet()
    ) : HeartBeatService {
        override fun isOnline(server: Server): Boolean {
            return !offline.contains(server.address)
        }
    }

    @Test
    fun `should add and remove server`() {
        // Given
        val server = Server("address1")
        val balancer = LoadBalancerImpl(10, DummyStrategy(), DummyHeartBeats())

        // When
        val addResult = balancer.addServer(server)
        val removeResult = balancer.removeServer(server)

        // Then
        assertThat(addResult).isTrue()
        assertThat(removeResult).isTrue()
    }

    @Test
    fun `should throw exception when too many`() {
        // Given
        val server1 = Server("address1")
        val server2 = Server("address2")
        val balancer = LoadBalancerImpl(1, DummyStrategy(), DummyHeartBeats())

        // When
        balancer.addServer(server1)
        val exception = assertThrows<RuntimeException> { balancer.addServer(server2) }

        // Then
        assertThat(exception.message).contains("The limit of servers of 1 is reached")
    }

    @Test
    fun `should not add server several times`() {
        // Given
        val server1 = Server("address1")
        val balancer = LoadBalancerImpl(1, DummyStrategy(), DummyHeartBeats())

        // When
        val result1 = balancer.addServer(server1)
        val result2 = balancer.addServer(server1)


        // Then
        assertThat(result1).isTrue()
        assertThat(result2).isFalse()
    }

    @Test
    fun `should be integrated with balancing `() {
        // Given
        val servers = (1..3).map {
            Server("address$it")
        }
        val balancer = LoadBalancerImpl(
            3, RandomBalancingStrategy(), DummyHeartBeats()
        )

        // When
        servers.forEach {
            assertThat(balancer.addServer(it)).isTrue()
        }
        val result = HashSet<Server>()
        repeat(100) { balancer.getNext().let { result.add(it) } }

        // Then
        assertThat(result).containsExactly(*servers.toTypedArray())
    }

    @Test
    fun `should filter offline servers out`() {
        // Given
        val schedulerMock = mockk<ScheduledExecutorService>()
        val task = CapturingSlot<Runnable>()
        every { schedulerMock.scheduleAtFixedRate(capture(task), 0, 10, TimeUnit.SECONDS) } returns mockk()
        val offline = setOf("address1")
        val balancer = LoadBalancerImpl(
            3,
            RoundRobinBalancingStrategy(),
            DummyHeartBeats(offline),
            schedulerMock
        )
        (1..3).map {
            val server = Server("address$it")
            balancer.addServer(server)
        }

        // When
        var resultOnline = (1..3).map { balancer.getNext() }.toSet()
        assertThat(resultOnline).hasSize(3)
        task.captured.run()

        // Then
        resultOnline = (1..3).map { balancer.getNext() }.toSet()
        assertThat(resultOnline).hasSize(2)
    }
}