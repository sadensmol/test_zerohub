package com.zerohub.challenge.controller

import com.zerohub.challenge.GrpcTest
import com.zerohub.challenge.proto.ConvertRequest
import com.zerohub.challenge.proto.PublishRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.util.stream.Stream

@GrpcTest
@DirtiesContext
class ConvertControllerTest() {

    @Autowired private lateinit var convertController: ConvertController


    @BeforeEach
    fun setup() {
        val rates = listOf(
            toPublishRequest(arrayOf(BTC, EUR, "50000.0000")),
            toPublishRequest(arrayOf(EUR, USD, "1.2000")),
            toPublishRequest(arrayOf(EUR, AUD, "1.5000")),
            toPublishRequest(arrayOf(RUB, USD, "80.0000")),
            toPublishRequest(arrayOf(UAH, RUB, "4.0000")),
            toPublishRequest(arrayOf(LTC, BTC, "0.0400")),
            toPublishRequest(arrayOf(LTC, USD, "2320.0000"))
        )
        runBlocking {
        for (rate in rates) {
             convertController.publish(rate)
        }}
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testData")
    fun `when we request converter service then receive correct answer`(title: String, request: ConvertRequest, expectedPrice: String) {

        // Request service and get price
        Assertions.assertEquals(expectedPrice, BigDecimal("0.0000"))
    }

    companion object {
        private const val BTC = "BTC"
        private const val EUR = "EUR"
        private const val USD = "USD"
        private const val UAH = "UAH"
        private const val RUB = "RUB"
        private const val LTC = "LTC"
        private const val AUD = "AUD"

        @JvmStatic
        fun testData(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("Same currency", toConvertRequest(arrayOf(BTC, BTC, "0.9997")), "0.9997"),
                Arguments.of("Simple conversion", toConvertRequest(arrayOf(EUR, BTC, "50000.0000")), "1.0000"),
                Arguments.of("Reversed conversion", toConvertRequest(arrayOf(BTC, EUR, "1.0000")), "50000.0000"),
                Arguments.of("Convert with one hop", toConvertRequest(arrayOf(BTC, AUD, "1.0000")), "75000.0000"),
                Arguments.of("Convert with two hops", toConvertRequest(arrayOf(BTC, RUB, "1.0000")), "4640000.0000"),
                Arguments.of("Reversed conversion with two hops", toConvertRequest(arrayOf(RUB, EUR, "96.0000")), "1.0000")
            )
        }

        private fun toPublishRequest(args: Array<String>): PublishRequest {
            return PublishRequest
                .newBuilder()
                .setBaseCurrency(args[0])
                .setQuoteCurrency(args[1])
                .setPrice(args[2])
                .build()
        }

        private fun toConvertRequest(args: Array<String>): ConvertRequest {
            return ConvertRequest
                .newBuilder()
                .setFromCurrency(args[0])
                .setToCurrency(args[1])
                .setFromAmount(args[2])
                .build()
        }
    }
}