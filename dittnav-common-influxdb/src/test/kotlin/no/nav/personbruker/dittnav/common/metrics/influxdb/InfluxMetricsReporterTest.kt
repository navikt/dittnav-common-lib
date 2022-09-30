package no.nav.personbruker.dittnav.common.metrics.influxdb

import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.influxdb.dto.Point
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

internal class InfluxMetricsReporterTest {
    val dataPointRelay: DataPointRelay = mockk()

    val databaseName = "testdb"
    val retentionPolicyName = "retention"
    val application = "testApp"
    val cluster = "test"
    val namespace = "test1"

    val influxConfig = InfluxConfig(
        "",
        "",
        "",
        0,
        databaseName,
        retentionPolicyName,
        application,
        cluster,
        namespace
    )

    val metricsReporter = InfluxMetricsReporter(influxConfig, dataPointRelay)

    @Test
    fun `Should construct a data point and add time of measurement and application-global tags`() {

        val pointSlot = slot<Point>()

        val measurementName = "INVENTORY"

        val fieldName = "value"
        val fieldVal = 123

        val tagName = "type"
        val tagVal = "APPLE"

        coEvery { dataPointRelay.submitDataPoint(capture(pointSlot)) } returns Unit

        val fields = mapOf(fieldName to fieldVal)
        val tags = mapOf(tagName to tagVal)

        val start = System.currentTimeMillis()

        runBlocking {
            metricsReporter.registerDataPoint(measurementName, fields, tags)
        }

        val point = pointSlot.captured

        val resultMeasurement: String = point.getPrivateField("measurement")
        val resultFields: Map<String, Any> = point.getPrivateField("fields")
        val resultTags: Map<String, String> = point.getPrivateField("tags")
        val resultTime: Long = point.getPrivateField("time")
        val resultPrecision: TimeUnit = point.getPrivateField("precision")

        val end = System.currentTimeMillis()


        resultMeasurement shouldBe measurementName
        resultFields shouldBe fields
        resultTags.values shouldContainAll listOf(application, cluster, namespace, tagVal)
        resultTime shouldBeGreaterThanOrEqual start
        resultTime shouldBeLessThanOrEqual end
        resultPrecision shouldBe TimeUnit.MILLISECONDS
    }

    private inline fun <reified T: Any> Point.getPrivateField(fieldName: String): T {
        return this::class.java.getDeclaredField(fieldName).let {
            it.isAccessible = true
            val field = it.get(this)

            if (field is T) {
                field
            } else {
                throw TypeCastException("Could not fetch private field '$fieldName' as ${T::class.simpleName}")
            }
        }
    }
}
