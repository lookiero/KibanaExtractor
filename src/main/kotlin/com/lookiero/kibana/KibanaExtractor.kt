package com.lookiero.kibana

import com.lookiero.kibana.`buying-back`.projection.query.get_problematic_orderline.GetProblematicOrderlines
import kotlin.jvm.JvmStatic
import com.lookiero.kibana.infrastructure.KibanaRestBuilder

object KibanaExtractor {
    @JvmStatic
    fun main(args: Array<String>) {
        val kibanaRestBuilder = KibanaRestBuilder()
        GetProblematicOrderlines(kibanaRestBuilder).handle()
    }
}