package no.nav.personbruker.dittnav.common.metrics.influx

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.*
import org.influxdb.dto.Point
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

internal class InfluxMetricsReporterTest {
    val dataPointRelay: DataPointRelay = mockk()

    val application = "testApp"
    val cluster = "test"
    val namespace = "test1"

    val sensuConfig = InfluxConfig(
        "", 0, "",
        application,
        cluster,
        namespace
    )

    val metricsReporter = InfluxMetricsReporter(sensuConfig, dataPointRelay)

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


        resultMeasurement `should be equal to` measurementName
        resultFields `should be equal to` fields
        resultTags.values `should contain same` listOf(application, cluster, namespace, tagVal)
        resultTime `should be greater or equal to` start
        resultTime `should be less or equal to` end
        resultPrecision `should be equal to` TimeUnit.MILLISECONDS
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
