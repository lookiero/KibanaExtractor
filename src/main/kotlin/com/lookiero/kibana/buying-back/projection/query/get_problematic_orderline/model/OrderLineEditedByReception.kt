package com.lookiero.kibana.`buying-back`.projection.query.get_problematic_orderline.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.joda.time.DateTime

class OrderLineEditedByReception {
    var id: String
    var orderId: String
    var title: String
    var printed: Boolean
    var deliveryEstimation: DateTime?
    var costPriceEur: String
    var sellingPriceEur: String
    var sellingPriceGbp: String
    var timestamp: DateTime
    var message:String

    constructor(timestamp: DateTime, message: String) {
        this.timestamp = timestamp
        this.message = message
        this.title = ""
        this.printed = false
        this.deliveryEstimation = DateTime.now()
        this.costPriceEur = ""
        this.sellingPriceEur = ""
        this.id = ""
        this.orderId = ""
        this.sellingPriceGbp = ""
    }

    fun parseFromString() {
        val payload = message.replace("Request END [POST /api/edit-order-line-from-reception, payload=", "").trimEnd(']')
        val mapper = jacksonObjectMapper()
        val aMap: Map<*,*> = mapper.readValue(payload, Map::class.java)
        this.id = aMap["id"] as String
        this.orderId = aMap["order_id"] as String
        this.title = aMap["title"] as String
        this.printed = aMap["printed"] as Boolean
        this.deliveryEstimation = if (aMap["delivery_estimation"] != null) DateTime(aMap["delivery_estimation"]) else null
        this.costPriceEur = aMap["cost_price_eur"].toString()
        this.sellingPriceEur = aMap["selling_price_eur"].toString()
        this.sellingPriceGbp = aMap["selling_price_gbp"].toString()
    }
}

