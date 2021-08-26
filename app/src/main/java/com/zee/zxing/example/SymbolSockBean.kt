package com.zee.zxing.example

import com.google.gson.Gson
import java.io.Serializable

data class Bean(
        var msg: String,
        var to_id: Int
)

data class Clean(
        var arg: Arg,
        var data: List<SymbolInfoData>

) {
    fun isPrice(): Boolean {
        return arg.channel == "tickers"
    }
}

data class Arg(
        var channel: String,
        var instId: String
)

data class SymbolInfoData(
        var asks: List<List<String>>,
        var bids: List<List<String>>,
        var instId: String,
        var last: String, //最新价格
        var ts: String
)

class SymbolInfo : Serializable {
    var action = "send"
    var from = "371774"
    var to = "6789"
    var msg = "BTC-USDT-SWAP"

    fun getJson(): String {
        return Gson().toJson(this)
    }
}

class ChatHeart : Serializable {
    var action: String = "heart"
    var uid: String = "371774"
    fun getJson(): String {
        return Gson().toJson(this)
    }
}

class ChatBindingUid : Serializable {
    var action = "binding"
    var uid = "371774"

    fun getJson(): String {
        return Gson().toJson(this)
    }
}