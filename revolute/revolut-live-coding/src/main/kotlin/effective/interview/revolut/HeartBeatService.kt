package effective.interview.revolut

interface HeartBeatService {

    fun isOnline(server: Server): Boolean
}