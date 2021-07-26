package com.zerohub.challenge.utils

import com.zerohub.challenge.proto.ConvertRequest
import com.zerohub.challenge.proto.PublishRequest

const val TEST_CURRENCY_1 = "BTC"
const val TEST_CURRENCY_2 = "USD"
const val TEST_CURRENCY_3 = "RUB"
const val TEST_CURRENCY_4 = "CAD"

const val TEST_AMOUNT = "123.111"

const val BTC = "BTC"
const val EUR = "EUR"
const val USD = "USD"
const val UAH = "UAH"
const val RUB = "RUB"
const val LTC = "LTC"
const val AUD = "AUD"

fun toPublishRequest(args: Array<String>): PublishRequest {
    return PublishRequest
        .newBuilder()
        .setBaseCurrency(args[0])
        .setQuoteCurrency(args[1])
        .setPrice(args[2])
        .build()
}

fun toConvertRequest(args: Array<String>): ConvertRequest {
    return ConvertRequest
        .newBuilder()
        .setFromCurrency(args[0])
        .setToCurrency(args[1])
        .setFromAmount(args[2])
        .build()
}