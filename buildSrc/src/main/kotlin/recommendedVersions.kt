object Influxdb {
    private const val version = "2.20"
    const val java = "org.influxdb:influxdb-java:$version"
}

object Junit {
    private const val version = "5.4.1"
    private const val groupId = "org.junit.jupiter"

    const val api = "$groupId:junit-jupiter-api:$version"
    const val engine = "$groupId:junit-jupiter-engine:$version"
}
object Jjwt {
    private const val version = "0.11.2"
    private const val groupId = "io.jsonwebtoken"

    const val api = "$groupId:jjwt-api:$version"
    const val impl = "$groupId:jjwt-impl:$version"
    const val jackson = "$groupId:jjwt-jackson:$version"
}

object Kluent {
    private const val version = "1.68"
    const val kluent = "org.amshove.kluent:kluent:$version"
}

object Kotest {
    const val version = "4.3.1"
    private const val groupId = "io.kotest"

    const val extensions = "$groupId:kotest-extensions:$version"
}

object Kotlin {
    const val version = "1.6.10"
}

object Kotlinx {
    private const val groupId = "org.jetbrains.kotlinx"

    const val coroutines = "$groupId:kotlinx-coroutines-core:1.3.9"
    const val datetime = "$groupId:kotlinx-datetime:0.3.2"
}

object Logback {
    private const val version = "1.2.3"
    const val classic = "ch.qos.logback:logback-classic:$version"
}

object Logstash {
    private const val version = "6.4"
    const val logbackEncoder = "net.logstash.logback:logstash-logback-encoder:$version"
}

object Mockk {
    private const val version = "1.12.2"
    const val mockk = "io.mockk:mockk:$version"
}
