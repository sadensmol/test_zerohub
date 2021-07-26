package com.zerohub.challenge.controller

import com.zerohub.challenge.service.ConvertService
import com.google.protobuf.Empty
import com.zerohub.challenge.proto.ConvertRequest
import com.zerohub.challenge.proto.ConvertResponse
import com.zerohub.challenge.proto.PublishRequest
import com.zerohub.challenge.proto.RatesServiceGrpcKt
import net.devh.boot.grpc.server.service.GrpcService
import java.math.BigDecimal

@GrpcService
class ConvertController(private val convertService: ConvertService) : RatesServiceGrpcKt.RatesServiceCoroutineImplBase() {
    override suspend fun convert(request: ConvertRequest): ConvertResponse {
        val price = convertService.convert(request.fromCurrency, request.toCurrency, request.fromAmount)

        //fixme required by provided tests
        return price.setScale(4).toConvertResponse()
    }

    override suspend fun publish(request: PublishRequest): Empty {
        convertService.publish(request.baseCurrency, request.quoteCurrency, request.price)
        return Empty.getDefaultInstance()
    }
}

private fun BigDecimal.toConvertResponse(): ConvertResponse = ConvertResponse.newBuilder().setPrice(this.toString()).build()