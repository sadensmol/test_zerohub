package com.zerohub.challenge

import net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.test.context.ActiveProfiles

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("grpc-setup")
@BaseTest
@ImportAutoConfiguration(GrpcServerAutoConfiguration::class,
    GrpcServerFactoryAutoConfiguration::class)
annotation class GrpcTest
