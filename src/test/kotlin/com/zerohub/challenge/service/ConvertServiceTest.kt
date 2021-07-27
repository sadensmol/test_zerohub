package com.zerohub.challenge.service

import com.zerohub.challenge.UnitTest
import com.zerohub.challenge.model.ConversionException
import com.zerohub.challenge.model.PublishingException
import com.zerohub.challenge.utils.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.test.annotation.DirtiesContext
import java.math.BigDecimal


/**
 * split into 2 different tests - publishing and converting
 */
@UnitTest
class ConvertServiceTest {
    private val bfsUtils: BFSUtils = BFSUtils()
    private lateinit var convertService: ConvertService

    @BeforeEach
    fun setUp() {
        convertService = ConvertService(bfsUtils)
    }

    @Test
    fun `when converting and both currencies aren't configured then get exception`() {
        assertThrows<ConversionException> {
            runBlocking {
                convertService.convert(TEST_CURRENCY_1, TEST_CURRENCY_2, TEST_AMOUNT)
            }
        }
    }

    @Test
    fun `when converting and from currency isn't configured then get exception`() {
        assertThrows<ConversionException> {
            runBlocking {
                convertService.publish(TEST_CURRENCY_1, TEST_CURRENCY_1, TEST_AMOUNT)
                convertService.convert(TEST_CURRENCY_1, TEST_CURRENCY_2, TEST_AMOUNT)
            }
        }
    }

    @Test
    fun `when converting and to currency isn't configured then get exception`() {
        assertThrows<ConversionException> {
            runBlocking {
                convertService.publish(TEST_CURRENCY_2, TEST_CURRENCY_2, TEST_AMOUNT)
                convertService.convert(TEST_CURRENCY_1, TEST_CURRENCY_2, TEST_AMOUNT)
            }
        }
    }

    @Test
    fun `when converting and both currencies are configured then return correct value`() =
        runBlocking {
            convertService.publish(TEST_CURRENCY_1, TEST_CURRENCY_2, TEST_AMOUNT)
            assertEquals(TEST_AMOUNT, convertService.convert(TEST_CURRENCY_1, TEST_CURRENCY_2, BigDecimal("1.0000")))
        }

    @Test
    fun `when publishing with zero rate then return error`() {
        assertThrows<PublishingException> {
            runBlocking {
                convertService.publish(TEST_CURRENCY_1, TEST_CURRENCY_2, TEST_ZERO_AMOUNT)
            }
        }
    }


    @Test
    fun `when converting small values then`() {
        //todo what to check in case of rounding error? throw an exception that cannot be converted?
    }



    @Test
    fun `when do same currency conversion then return same value`() = runBlocking {
        assertEquals(BigDecimal("0.9997"),
            convertService.convert(BTC, BTC, BigDecimal("0.9997")))

    }

    @Test
    fun `when do simple conversion then return correct value`() = runBlocking {
        convertService.publish(BTC, EUR, BigDecimal(50000.0000))
        assertEquals(BigDecimal("1.0000"),
            convertService.convert(EUR, BTC, BigDecimal("50000.0000")))

    }


    @Test
    fun `when do simple reversed conversion then return correct value`() = runBlocking {
        convertService.publish(BTC, EUR, BigDecimal("50000.0000"))
        assertEquals(BigDecimal("250000.0000"),
            convertService.convert(BTC, EUR, BigDecimal("5.0000")))

    }

    @Test
    fun `when do conversion with one hop then return correct value`() = runBlocking {
        convertService.publish(BTC, EUR, BigDecimal("50000.0000"))
        convertService.publish(EUR, AUD, BigDecimal("1.5000"))
        assertEquals(BigDecimal("75000.0000"),
            convertService.convert(BTC, AUD, BigDecimal("1.0000")))

    }

    @Test
    fun `when do conversion with two hops then return correct value`() = runBlocking {
        convertService.publish(BTC, EUR, BigDecimal("50000.0000"))
        convertService.publish(EUR, USD, BigDecimal("1.2000"))
        convertService.publish(USD, RUB, BigDecimal("80.0000"))
        assertEquals(BigDecimal("4800000.0000"),
            convertService.convert(BTC, RUB, BigDecimal("1.0000")))

    }

    @Test
    fun `when do conversion reversed with two hops then return correct value`() = runBlocking {
        convertService.publish(USD, RUB, BigDecimal("80.0000"))
        convertService.publish(EUR, USD, BigDecimal("1.2000"))
        assertEquals(BigDecimal("1.0000"),
            convertService.convert(RUB, EUR, BigDecimal("96.0000")))

    }

    @Test
    fun `when do custom conversion from linkedin update then return correct value`() = runBlocking {
        convertService.publish(BTC, EUR, BigDecimal("27000.0000"))
        convertService.publish(EUR, USD, BigDecimal("1.2000"))
        convertService.publish(USD, RUB, BigDecimal("70.0000"))
        assertEquals(BigDecimal("2268000.0000"),
            convertService.convert(BTC, RUB, BigDecimal("1.0000")))

    }




}
