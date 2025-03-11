package effective.interview.revolut

/*
You work in a team that delivers a load balancer project.
Your task is to implement a component of the solution - an in-memory registry that keeps track of deployed service instances.
Each time a new service instance comes online it will be registered in your registry.
- It should be possible to register a service instance, identified by an address
- The registry should accept up to 10 address
- Get next server
 */
interface LoadBalancer {

    fun addServer(server: Server): Boolean

    fun removeServer(server: Server): Boolean

    fun getNext(): Server
}

data class Server(
    /**
     * Unique identifier
     */
    // Should URL
    val address: String,
    // Should be part of LoadBalancerImpl
    var online: Boolean = true
)

