package com.zerohub.challenge.controller

import com.google.protobuf.Empty
import com.zerohub.challenge.model.ConversionException
import com.zerohub.challenge.model.PublishingException
import com.zerohub.challenge.proto.ConvertRequest
import com.zerohub.challenge.proto.ConvertResponse
import com.zerohub.challenge.proto.PublishRequest
import com.zerohub.challenge.proto.RatesServiceGrpcKt
import com.zerohub.challenge.service.ConvertService
import net.devh.boot.grpc.server.service.GrpcService
import java.math.BigDecimal

@GrpcService
class ConvertController(private val convertService: ConvertService) : RatesServiceGrpcKt.RatesServiceCoroutineImplBase() {
    override suspend fun convert(request: ConvertRequest): ConvertResponse {
        if (request.fromAmount.isBlank()) throw ConversionException("Incorrect amount $request.fromAmount")

        var fromAmount = try{
            BigDecimal(request.fromAmount)
        }catch (e:Exception) {throw ConversionException("Wrong conversion request",e)}

        val price = convertService.convert(request.fromCurrency, request.toCurrency, fromAmount)

        //fixme required by provided tests
        return price.setScale(4).toConvertResponse()
    }

    override suspend fun publish(request: PublishRequest): Empty {
        if (request.price.isBlank()) throw ConversionException("Incorrect price ${request.price}")

        val bdPrice = try{BigDecimal(request.price)}catch(e:Exception){
            throw PublishingException("Incorrect publish request",e)
        }

        convertService.publish(request.baseCurrency, request.quoteCurrency, bdPrice)
        return Empty.getDefaultInstance()
    }
}

private fun BigDecimal.toConvertResponse(): ConvertResponse = ConvertResponse.newBuilder().setPrice(this.toString()).build()