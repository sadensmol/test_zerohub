package com.zerohub.challenge.model

import java.lang.RuntimeException
import java.math.BigDecimal

class ConversionException(message:String,e:Exception? = null) : RuntimeException(message,e)