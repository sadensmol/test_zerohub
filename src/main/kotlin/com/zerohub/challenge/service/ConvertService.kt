package com.zerohub.challenge.service

import com.zerohub.challenge.model.Conversion
import com.zerohub.challenge.model.ConversionException
import com.zerohub.challenge.model.PublishingException
import com.zerohub.challenge.utils.BFSUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
/**
 * fixme - store just conversion coefficient instead of the whole conversion chain!
 */


class ConvertService(private val bfsUtils:BFSUtils) {
    private val conversions = mutableSetOf<Conversion>()


    /**
     * additional structure to store cross conversion paths between different currencies
     * which cannot be directly converted
     *  [from]-[to] currencies names are used for key
     *
     * todo change to graph?
     */
    private val conversionChain = mutableMapOf<String, List<Conversion>>()

    private fun getConversionChain(fromCurrency: String, toCurrency: String):List<Conversion> =
        with( conversionChain["$fromCurrency-$toCurrency"]?:createConversionChain(fromCurrency,toCurrency)) {
            if (isEmpty()) throw ConversionException("Currencies for $fromCurrency/$toCurrency conversion aren't configured")
            this
        }

    private fun createConversionChain(fromCurrency: String, toCurrency: String): List<Conversion> {
        val listCurrencies = conversions.map { conversion -> "${conversion.from}-${conversion.to}" } +conversions.map { conversion -> "${conversion.to}-${conversion.from}" }
        val t = bfsUtils.findShortestPath(fromCurrency,  toCurrency,listCurrencies)
        val tt = bfsUtils.findShortestPath(fromCurrency,  toCurrency,listCurrencies).zipWithNext()

        print(tt)
        val conversions = bfsUtils.findShortestPath(fromCurrency,  toCurrency,listCurrencies)
                .zipWithNext()
                .mapNotNull { pair->
                    conversions.find { conversion -> conversion.from == pair.first && conversion.to == pair.second }?:
                            conversions.find {conversion-> conversion.from == pair.second && conversion.to == pair.first}?.copy(direct = false)
                }

        //skip direct conversions
        if (conversions.size >1)
            conversionChain["$fromCurrency-$toCurrency"] = conversions

        return conversions
    }

    /**
     * returns money amount in [toCurrency] after the conversion from [fromCurrency]
     * conversion is based on published conversions
     *
     */
    suspend fun convert(fromCurrency: String, toCurrency: String, fromAmount: BigDecimal): BigDecimal {
        if (fromCurrency.isBlank() || toCurrency.isBlank()) throw ConversionException("Incorrect currency $fromCurrency-$toCurrency")

        //fixme move to external const
        if (fromCurrency == toCurrency) return fromAmount

        var amount = fromAmount
        val conversionChain = getConversionChain(fromCurrency, toCurrency)

        if (conversionChain.isEmpty())
            throw ConversionException("Currencies for $fromCurrency/$toCurrency conversion aren't configured")

        conversionChain.forEach {
            if (it.direct)
                amount *= it.rate
                else {
                amount /= it.rate
            }
        }


        //fixme move to external const
        return amount.setScale(4)
    }

    // bigdecil division by 0.000
    suspend fun publish(baseCurrency: String, quoteCurrency: String, price:BigDecimal) {
        if (price.compareTo(BigDecimal.ZERO) ==0) throw PublishingException("No rate $price for $baseCurrency/$quoteCurrency")

        conversions.removeAll(conversions.filter { conversion -> conversion.from ==baseCurrency && conversion.to == quoteCurrency })
        conversions.add(Conversion(from = baseCurrency, to = quoteCurrency, rate = price))
        conversionChain.clear()
    }


}

