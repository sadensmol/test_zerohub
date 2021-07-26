package com.zerohub.challenge.controller

import com.zerohub.challenge.GrpcTest
import com.zerohub.challenge.proto.ConvertRequest
import com.zerohub.challenge.proto.PublishRequest
import com.zerohub.challenge.utils.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import java.math.BigDecimal
import java.util.stream.Stream

@GrpcTest
class ConvertControllerTest {

    @Autowired
    private lateinit var convertController: ConvertController

    @Test
    fun `when do same currency conversion then return same value`() = runBlocking {
        Assertions.assertEquals("0.9997",
            convertController.convert(toConvertRequest(arrayOf(BTC, BTC, "0.9997"))).price
        )
    }

    @Test
    fun `when do simple conversion then return correct value`() = runBlocking {
        convertController.publish(toPublishRequest(arrayOf(BTC, EUR, "50000.0000")))
        Assertions.assertEquals("1.0000",
            convertController.convert(toConvertRequest(arrayOf(EUR, BTC, "50000.0000"))).price
        )
    }

    @Test
    fun `when do simple reversed conversion then return correct value`() = runBlocking {
        convertController.publish(toPublishRequest(arrayOf(BTC, EUR, "50000.0000")))
        Assertions.assertEquals("250000.0000",
            convertController.convert(toConvertRequest(arrayOf(BTC, EUR, "5.0000"))).price
        )
    }

    @Test
    fun `when do conversion with one hop then return correct value`() = runBlocking {
        convertController.publish(toPublishRequest(arrayOf(BTC, EUR, "50000.0000")))
        convertController.publish(toPublishRequest(arrayOf(EUR, AUD, "1.5000")))
        Assertions.assertEquals("75000.0000",
            convertController.convert(toConvertRequest(arrayOf(BTC, AUD, "1.0000"))).price
        )
    }

    @Test
    fun `when do conversion with two hops then return correct value`() = runBlocking {
        convertController.publish(toPublishRequest(arrayOf(BTC, EUR, "50000.0000")))
        convertController.publish(toPublishRequest(arrayOf(EUR, USD, "1.2000")))
        convertController.publish(toPublishRequest(arrayOf(USD, RUB, "80.0000")))
        Assertions.assertEquals("4800000.0000",
            convertController.convert(toConvertRequest(arrayOf(BTC, RUB, "1.0000"))).price
        )
    }

    @Test
    fun `when do conversion reversed with two hops then return correct value`() = runBlocking {
        convertController.publish(toPublishRequest(arrayOf(USD,RUB, "80.0000")))
        convertController.publish(toPublishRequest(arrayOf(EUR, USD, "1.2000")))
        Assertions.assertEquals("1.0000",
            convertController.convert(toConvertRequest(arrayOf(RUB, EUR, "96.0000"))).price
        )
    }

    @Test
    fun `when do custom conversion from linkedin update then return correct value`() = runBlocking {
        convertController.publish(toPublishRequest(arrayOf(BTC, EUR, "27000.0000")))
        convertController.publish(toPublishRequest(arrayOf(EUR, USD, "1.2000")))
        convertController.publish(toPublishRequest(arrayOf(USD,RUB, "70.0000")))
        Assertions.assertEquals("2268000.0000",
            convertController.convert(toConvertRequest(arrayOf(BTC, RUB, "1.0000"))).price
        )
    }




}