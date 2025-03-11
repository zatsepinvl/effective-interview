package effective.interview.revolut

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class LoadBalancerImpl(
    val capacity: Int = 10,
    val balancingStrategy: BalancingStrategy,
    val heartBeatService: HeartBeatService,
    scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
) : LoadBalancer {

    private val servers = ArrayList<Server>(capacity)
    private val rwLock = ReentrantReadWriteLock()

    init {
        scheduler.scheduleAtFixedRate({
            servers.forEach {
                it.online = heartBeatService.isOnline(it)
            }
        }, 0, 10, TimeUnit.SECONDS)
    }

    // 1 OPS
    override fun addServer(server: Server): Boolean {
        rwLock.write {
            if (servers.any { server.address == it.address }) {
                return false
            }
            if (servers.size >= capacity) {
                throw IllegalStateException("The limit of servers of $capacity is reached")
            }
            return servers.add(server)
        }
    }

    // 1 OPS
    override fun removeServer(server: Server): Boolean {
        rwLock.write {
            return servers.remove(server)
        }
    }

    // 1000 OPS
    override fun getNext(): Server {
        rwLock.read {
            val onlineServers = servers.filter { it.online }
            return balancingStrategy.getNext(onlineServers)
        }
    }
}