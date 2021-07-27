package com.zerohub.challenge.config

import com.zerohub.challenge.proto.RatesServiceGrpcKt
import io.grpc.Channel
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChallengeApplicationTestConfig {
    @GrpcClient("zerohub")
    private lateinit var serverChannel: Channel

    @Bean
    fun rateGRPCServiceStub(): RatesServiceGrpcKt.RatesServiceCoroutineStub? {
        return RatesServiceGrpcKt.RatesServiceCoroutineStub(serverChannel)
    }
}