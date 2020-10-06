package no.nav.personbruker.dittnav.common.logging.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T: Any> T.createClassLogger(): Logger = LoggerFactory.getLogger(T::class.java)