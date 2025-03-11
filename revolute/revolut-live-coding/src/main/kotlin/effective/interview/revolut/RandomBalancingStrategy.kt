package effective.interview.revolut

import java.util.concurrent.ThreadLocalRandom


class RandomBalancingStrategy: BalancingStrategy {

    override fun getNext(servers: List<Server>): Server {
        val index = ThreadLocalRandom.current().nextInt(servers.size)
        return servers[index]
    }
}