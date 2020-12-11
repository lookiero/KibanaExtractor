package com.lookiero.kibana.infrastructure

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.http.HttpHost
import org.apache.http.client.config.RequestConfig
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryStringQueryBuilder

import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.client.ElasticsearchClient
import org.elasticsearch.client.RestClientBuilder
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.index.query.RangeQueryBuilder
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.builder.SearchSourceBuilder
import java.time.LocalDateTime


class KibanaRestBuilder {
    var restClient: RestHighLevelClient

    init {
        restClient = RestHighLevelClient(
            RestClient.builder(
                HttpHost(
                    "search-logs-pa4jmpobt5jwvj3sdm32qofwxi.eu-west-1.es.amazonaws.com",
                    9200,
                    "https")
            ).setRequestConfigCallback(
                object: RestClientBuilder.RequestConfigCallback {
                    override fun customizeRequestConfig(
                        requestConfigBuilder:RequestConfig.Builder):RequestConfig.Builder {
                        return requestConfigBuilder
                            .setConnectTimeout(5000)
                            .setSocketTimeout(60000)
                    }
                }
            ).setMaxRetryTimeoutMillis(60000)
        )

    }

    fun performQuery(querySearch : String, from: String = "", to: String = ""): Array<out SearchHit>? {
        val searchRequest = SearchRequest()
        val startRangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp")
        if (!from.isBlank()) {
            startRangeQueryBuilder.gt(from);
        } else {
            startRangeQueryBuilder.gt("2019-01-01");
        }
        if (!to.isBlank()) {
            startRangeQueryBuilder.lt(to);
        } else {
            startRangeQueryBuilder.lt(LocalDateTime.now())
        }
        val boolQuery = QueryBuilders.boolQuery()
            .must(QueryBuilders.queryStringQuery(querySearch))
            .must(startRangeQueryBuilder);
        val searchSourceBuilder = SearchSourceBuilder()
        searchSourceBuilder.timeout(TimeValue.timeValueMinutes(2))
        searchSourceBuilder.query(boolQuery)
        searchSourceBuilder.from(0)
        searchSourceBuilder.size(5000)
        searchRequest.source(searchSourceBuilder)

        val search = this.restClient.search(searchRequest)
        return search.hits.hits
    }
}