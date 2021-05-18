package no.nav.personbruker.dittnav.common.metrics.influx

import org.influxdb.dto.Point

object InfluxDataPointObjectMother {
    fun createSimplePointsForMeasurents(measurementName: String, number: Int): List<Point> {
        return (0..number).map {
            Point.measurement(measurementName).fields(mapOf("value" to it)).build()
        }
    }
}