package com.lookiero.kibana.`buying-back`.projection.query.get_problematic_orderline

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lookiero.kibana.`buying-back`.projection.query.get_problematic_orderline.model.OrderLineEditedByReception
import com.lookiero.kibana.infrastructure.KibanaRestBuilder
import org.joda.time.DateTime

class GetProblematicOrderlines(var kibanaRestBuilder: KibanaRestBuilder) {

    fun handle() {
        val result = kibanaRestBuilder
            .performQuery("labels.application:buying-back AND \"Request END [POST " +
                    "/api/edit-order-line-from-reception\"")
        val response = result?.map { item -> item.sourceAsMap }
        val map = response?.map { item -> item["metadata"] }
        val messages: List<OrderLineEditedByReception?>? = map?.map { hash ->
            val properties = hash as HashMap<String, String>
            properties["message"]?.let { OrderLineEditedByReception(DateTime(properties["@timestamp"]), it) }
        }

        messages?.map { orderLine -> orderLine?.parseFromString() }

        val distinctBy = messages?.distinctBy { orderLineEditedByReception -> orderLineEditedByReception?.id }
        distinctBy?.forEach { message ->
            val toString = message?.timestamp?.toString("YYYY-MM-dd")?:""
            val result2 = kibanaRestBuilder.performQuery(
                 "labels.application:buying-back AND \"Request END [POST " +
                         "/api/edit-order-line-from-buying-assistant\" AND \""+ message?.id + "\"",
                 toString
             )
            if (result2?.size!! > 0 ) {
                println("UPDATE order_line SET title = '${message?.title}', printed = '${message?.printed}'," +
                        " cost_price_eur = ${message?.costPriceEur}, selling_price_eur=${message?.sellingPriceEur}, " +
                        "selling_price_gbp=${message?.sellingPriceGbp}, delivery_estimation='${message?.deliveryEstimation}'" +
                        " where id='${message?.id}'")
            }
         }
    }
}