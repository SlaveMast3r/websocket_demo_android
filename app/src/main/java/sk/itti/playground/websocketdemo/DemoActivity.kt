package sk.itti.playground.websocketdemo

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_demo.*
import okhttp3.*
import okio.ByteString

class DemoActivity : AppCompatActivity() {
    private lateinit var client: OkHttpClient

    private inner class EchoWebSocketListener : WebSocketListener() {
        private val NORMAL_CLOSURE_STATUS = 1000

        override fun onOpen(webSocket: WebSocket, response: Response) {
            webSocket.send("Hello, it's SSaurel !")
            webSocket.send("What's up ?")
            webSocket.send(ByteString.decodeHex("deadbeef"))
            webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            output("Receiving : " + text!!)
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
            output("Receiving bytes : " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            output("Closing : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response) {
            output("Error : " + t.message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        client = OkHttpClient()
        start.setOnClickListener {
            start()
        }
    }

    private fun start() {
        val request = Request.Builder().url("ws://echo.websocket.org").build()
        val listener = EchoWebSocketListener()
        val ws = client.newWebSocket(request, listener)

        client.dispatcher().executorService().shutdown()
    }

    @SuppressLint("SetTextI18n")
    private fun output(txt: String) {
        runOnUiThread { output!!.text = output!!.text.toString() + "\n\n" + txt }
    }
}
