package com.zerohub.challenge.service

import com.zerohub.challenge.model.Conversion
import com.zerohub.challenge.model.ConversionException
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ConvertService {
    private val conversions = mutableSetOf<Conversion>()

    //todo rework with graphs?
    private val conversionChain = mutableMapOf<String, List<Conversion>>()

    private fun getConversionChain(fromCurrency: String, toCurrency: String):List<Conversion> =
        with (conversionChain["$fromCurrency-$toCurrency"]){
            if (this.isNullOrEmpty())
                createConversionChain(fromCurrency,toCurrency)
            else this
        }
         ?: throw ConversionException("Currencies for $fromCurrency/$toCurrency conversion aren't configured")

    private fun createConversionChain(fromCurrency: String, toCurrency: String): List<Conversion>? {
        TODO()
    }

    suspend fun convert(fromCurrency: String, toCurrency: String, fromAmount: String): BigDecimal {

        if (conversions.filter { conversion -> conversion.from == fromCurrency }.none { conversion -> conversion.to == toCurrency })
            throw ConversionException("Currencies for $fromCurrency/$toCurrency conversion aren't configured")

        getConversionChain(fromCurrency,toCurrency).forEach {  }

        return BigDecimal("100.0")
    }

    suspend fun publish(baseCurrency: String, quoteCurrency: String, price: String) {
        conversions.add(Conversion(from = baseCurrency, to = quoteCurrency, rate = BigDecimal(price)))

    }


}