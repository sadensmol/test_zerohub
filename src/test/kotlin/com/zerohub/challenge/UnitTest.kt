package com.zerohub.challenge

import org.junit.jupiter.api.TestInstance
import org.springframework.test.context.ActiveProfiles


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
annotation class UnitTest
