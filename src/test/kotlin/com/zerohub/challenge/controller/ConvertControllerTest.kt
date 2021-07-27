package com.zerohub.challenge.controller

import com.zerohub.challenge.GrpcTest
import com.zerohub.challenge.model.PublishingException
import com.zerohub.challenge.proto.ConvertRequest
import com.zerohub.challenge.proto.PublishRequest
import com.zerohub.challenge.proto.RatesServiceGrpc
import com.zerohub.challenge.proto.RatesServiceGrpcKt
import com.zerohub.challenge.utils.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import java.lang.Exception
import java.math.BigDecimal
import java.util.stream.Stream

@GrpcTest
@DirtiesContext
/**
 * todo add more tests
 */
class ConvertControllerTest {

    @Autowired
    private lateinit var ratesStub: RatesServiceGrpcKt.RatesServiceCoroutineStub

    @BeforeAll
    fun setUp()  {
        runBlocking {
        ratesStub.publish(toPublishRequest(arrayOf(BTC, EUR, "50000.0000")))
        ratesStub.publish(toPublishRequest(arrayOf(EUR, AUD, "1.5000")))
        ratesStub.publish(toPublishRequest(arrayOf(EUR, USD, "1.2000")))
        ratesStub.publish(toPublishRequest(arrayOf(USD, RUB, "80.0000")))
        }
    }

    /**
     * fixme correct exceptions don't setup
     */
    @Test
    fun `when publishing wrong rate then return error`() {
        assertThrows<Exception> {
            runBlocking {
                ratesStub.publish(toPublishRequest(arrayOf(TEST_CURRENCY_1, TEST_CURRENCY_2, TEST_WRONG_AMOUNT)))
            }
        }
    }

    @Test
    fun `when publishing correct rate then no error`() {
            runBlocking {
                ratesStub.publish(toPublishRequest(arrayOf(TEST_CURRENCY_1, TEST_CURRENCY_2, "1.0000")))
            }
    }

    @Test
    fun `when do same currency conversion then return same value`() = runBlocking {
        Assertions.assertEquals("0.9997",
            ratesStub.convert(toConvertRequest(arrayOf(BTC, BTC, "0.9997"))).price
        )
    }

    @Test
    fun `when do simple conversion then return correct value`() = runBlocking {

        Assertions.assertEquals("1.0000",
            ratesStub.convert(toConvertRequest(arrayOf(EUR, BTC, "50000.0000"))).price
        )
    }


    @Test
    fun `when do simple reversed conversion then return correct value`() = runBlocking {

        Assertions.assertEquals("250000.0000",
            ratesStub.convert(toConvertRequest(arrayOf(BTC, EUR, "5.0000"))).price
        )
    }

    @Test
    fun `when do conversion with one hop then return correct value`() = runBlocking {
        Assertions.assertEquals("75000.0000",
            ratesStub.convert(toConvertRequest(arrayOf(BTC, AUD, "1.0000"))).price
        )
    }

    @Test
    fun `when do conversion with two hops then return correct value`() = runBlocking {
        Assertions.assertEquals("6000000.0000",
            ratesStub.convert(toConvertRequest(arrayOf(BTC, RUB, "1.0000"))).price
        )
    }

    @Test
    fun `when do conversion reversed with two hops then return correct value`() = runBlocking {
        Assertions.assertEquals("1.0000",
            ratesStub.convert(toConvertRequest(arrayOf(RUB, EUR, "96.0000"))).price
        )
    }

    @Test
    fun `when do custom conversion from linkedin update then return correct value`() = runBlocking {
        Assertions.assertEquals("4800000.0000",
            ratesStub.convert(toConvertRequest(arrayOf(BTC, RUB, "1.0000"))).price
        )
    }




}