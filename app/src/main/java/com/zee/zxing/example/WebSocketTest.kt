package com.zee.zxing.example

import com.zee.http.socket.MyWebSocket
import com.zee.http.socket.MyWebSocketListener
import com.zee.log.ZLog
import com.zee.utils.UIUtils
import okhttp3.Response

/**
 *created by zee on 2021/6/10.
 *
 */
class WebSocketTest {
    init {
        val mySocket = MyWebSocket.Builder("ws://wnf.acutengle.com:2345").build()
        mySocket.setMyWebSocketListener(object : MyWebSocketListener() {
            override fun onSocketMessage(text: String?) {
                super.onSocketMessage(text)
                ZLog.i(text)
            }

            override fun onSocketFailure(t: Throwable?, response: Response?) {
                super.onSocketFailure(t, response)
            }
        })
        mySocket.startConnect()
        UIUtils.postDelayed(object : Runnable {
            override fun run() {
                mySocket.sendMessage("今天币价大涨")
                UIUtils.postDelayed(this, 2000)
            }
        }, 100)
        
    }
}