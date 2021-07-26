package com.zerohub.challenge.utils

import com.zerohub.challenge.BaseTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@BaseTest
/**
 * todo add validation tests
 */
class BFSUtilsTest {

    private val bfsUtils = BFSUtils()

    @Test
    fun `when bfs is empty then return empty`() {
        Assertions.assertThat(bfsUtils.findShortestPath(TEST_CURRENCY_1, TEST_CURRENCY_2, emptyList())).isEmpty()
    }

    @Test
    fun `when self to self and bfs is empty then return empty`() {
        Assertions.assertThat(bfsUtils.findShortestPath(TEST_CURRENCY_1, TEST_CURRENCY_1, listOf())).isEmpty()
    }

    @Test
    fun `when self to self and bfs contains self to self then return self`() {
        assertEquals(listOf(TEST_CURRENCY_1,TEST_CURRENCY_1),bfsUtils.findShortestPath(TEST_CURRENCY_1, TEST_CURRENCY_1, listOf("$TEST_CURRENCY_1-$TEST_CURRENCY_1")))
    }

    @Test
    fun `when bfs contains no required conversions then return empty`() {
        Assertions.assertThat(bfsUtils.findShortestPath(TEST_CURRENCY_1, TEST_CURRENCY_2,
            listOf("$TEST_CURRENCY_1-$TEST_CURRENCY_1","$TEST_CURRENCY_2-$TEST_CURRENCY_2"))

        ).isEmpty()
    }

    @Test
    fun `when bfs contains required conversions directly then return this conversion`() {
        assertEquals(listOf(TEST_CURRENCY_1,TEST_CURRENCY_2),bfsUtils.findShortestPath(TEST_CURRENCY_1, TEST_CURRENCY_2,
            listOf("$TEST_CURRENCY_1-$TEST_CURRENCY_2"))
        )
    }

    @Test
    fun `when bfs contains required conversions through 3 different conversion then return the correct conversion chain`() {
        assertEquals(listOf(TEST_CURRENCY_1,TEST_CURRENCY_2,TEST_CURRENCY_3,TEST_CURRENCY_4),bfsUtils.findShortestPath(TEST_CURRENCY_1, TEST_CURRENCY_4,
            listOf("$TEST_CURRENCY_1-$TEST_CURRENCY_2",
                "$TEST_CURRENCY_2-$TEST_CURRENCY_3",
                "$TEST_CURRENCY_3-$TEST_CURRENCY_4",
                ))
        )
    }

    @Test
    fun `when bfs contains required conversions but contains cycles as well then return the correct conversion chain`() {
        assertEquals(listOf(TEST_CURRENCY_1,TEST_CURRENCY_2,TEST_CURRENCY_3,TEST_CURRENCY_4),bfsUtils.findShortestPath(TEST_CURRENCY_1, TEST_CURRENCY_4,
            listOf("$TEST_CURRENCY_1-$TEST_CURRENCY_2",
                "$TEST_CURRENCY_2-$TEST_CURRENCY_1",
                "$TEST_CURRENCY_2-$TEST_CURRENCY_3",
                "$TEST_CURRENCY_3-$TEST_CURRENCY_1",
                "$TEST_CURRENCY_3-$TEST_CURRENCY_4",
                ))
        )
    }




    @Test
    fun `when bfs contains required reverse conversions  through 3 different conversions then return the correct conversion chain`() {
        assertEquals(listOf(TEST_CURRENCY_4,TEST_CURRENCY_3,TEST_CURRENCY_2,TEST_CURRENCY_1),
            bfsUtils.findShortestPath(TEST_CURRENCY_4, TEST_CURRENCY_1,
            listOf("$TEST_CURRENCY_1-$TEST_CURRENCY_2","$TEST_CURRENCY_2-$TEST_CURRENCY_1",
                "$TEST_CURRENCY_2-$TEST_CURRENCY_3","$TEST_CURRENCY_3-$TEST_CURRENCY_2",
                "$TEST_CURRENCY_3-$TEST_CURRENCY_4","$TEST_CURRENCY_4-$TEST_CURRENCY_3",
            ))
        )
    }
}