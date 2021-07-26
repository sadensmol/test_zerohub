package com.zerohub.challenge.model

import java.math.BigDecimal

data class Conversion(val from:String, val to:String, val rate: BigDecimal,val direct:Boolean = true) {
}