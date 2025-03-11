package effective.interview.revolut

import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs


class RoundRobinBalancingStrategy: BalancingStrategy {

    private var count = AtomicInteger()

    override fun getNext(servers: List<Server>): Server {
        val index = abs(count.getAndIncrement()) % servers.size
        return servers[index]
    }
}