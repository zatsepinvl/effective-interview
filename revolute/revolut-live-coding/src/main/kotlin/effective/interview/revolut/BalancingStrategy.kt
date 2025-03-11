package effective.interview.revolut

interface BalancingStrategy {

    fun getNext(servers: List<Server>): Server
}