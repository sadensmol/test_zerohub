package com.zerohub.challenge.service

import com.zerohub.challenge.BaseTest
import com.zerohub.challenge.model.ConversionException
import com.zerohub.challenge.utils.TEST_AMOUNT
import com.zerohub.challenge.utils.TEST_CURRENCY_1
import com.zerohub.challenge.utils.TEST_CURRENCY_2
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


@BaseTest
class ConvertServiceTest {
    private val convertService: ConvertService = ConvertService()

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
                assertEquals(TEST_AMOUNT,convertService.convert(TEST_CURRENCY_1, TEST_CURRENCY_2, TEST_AMOUNT))
            }
    }