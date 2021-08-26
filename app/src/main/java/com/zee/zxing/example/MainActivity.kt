package com.zee.zxing.example

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.view.CropImageView
import com.zee.extendobject.postDelayed
import com.zee.http.MyOk
import com.zee.http.request.DownAPKAndInstallCallBackListener
import com.zee.http.request.GsonTools
import com.zee.http.request.ZResultString
import com.zee.http.request.ZStringResult
import com.zee.http.socket.MyWebSocket
import com.zee.http.socket.MyWebSocketListener
import com.zee.listener.OnPermissionListener
import com.zee.log.ZLog
import com.zee.utils.SuperZPerMissionUtils
import com.zee.utils.UIUtils
import com.zee.utils.ZLibrary
import okhttp3.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        initViews()
//        initSelectPic()
        SuperZPerMissionUtils.getInstance().add(Manifest.permission.WRITE_EXTERNAL_STORAGE).requestPermissions(object : OnPermissionListener {
            override fun onPerMission(deniedPermissions: MutableList<String>, permissionExplain: MutableList<String>?) {

            }
        })

        MyOk.downloadFile("https://brand1.oss-cn-beijing.aliyuncs.com/ibtc/ibtc.apk").execute(object : DownAPKAndInstallCallBackListener() {


            override fun onError(e: java.lang.Exception?) {
                super.onError(e)
            }

        })
//        loginOne(0, "86", "15911082840", "", "")
    }

    private fun initViews() {
        //371774
        var isFirst = true
        val mMyWebSocket = MyWebSocket.Builder("ws://47.91.240.77:2345/").setHeartPackage(45000, ChatHeart().getJson()).build()
        mMyWebSocket.startConnect()
        postDelayed(1000) {
            //            mMyWebSocket.sendMessage(ChatHeart().getJson())
            mMyWebSocket.sendMessage(ChatBindingUid().getJson())
        }
        mMyWebSocket.setMyWebSocketListener(object : MyWebSocketListener() {
            override fun onSocketMessage(text: String?) {
                super.onSocketMessage(text)
                ZLog.i(text)
                try {
                    val bean = GsonTools.getObject<Bean>(text, Bean::class.java)
                    val clean = GsonTools.getObject<Clean>(bean.msg, Clean::class.java)
                    if (clean.isPrice()) {
                        ZLog.e("----》》最新价格：${clean}")
                    } else {
                        ZLog.e("深度图：${clean}")
                    }
                } catch (e: Exception) {
                    ZLog.e(e)
                }
                if (isFirst) {
                    mMyWebSocket.sendMessage(SymbolInfo().getJson())
                    isFirst = false
                }
            }

            override fun onSocketFailure(t: Throwable?, response: Response?) {
                super.onSocketFailure(t, response)
            }

            override fun onSocketClosed(code: Int, reason: String?) {
                super.onSocketClosed(code, reason)
            }
        })
    }


    fun loginOne(loginType: Int, country: String, phoneNumber: String, email: String, inviteCode: String) {
        val hashMap = HashMap<String, Any>()
        hashMap["loginType"] = loginType
        hashMap["country"] = country
        hashMap["phoneNumber"] = phoneNumber
//        if (email.isNotBlank()) {
//            hashMap["email"] = email
//        }
//        if (inviteCode.isNotBlank()) {
//            hashMap["inviteCode"] = inviteCode
//        }
//        hashMap.put("time", System.currentTimeMillis().toString())
//        hashMap.put("sign", RequestParamUtil.signParams(vParams))

        val param = HashMap<String, Any>()
        param["Company-ID"] = 1033
        MyOk.load("http://php.wn.work/api/ad/notice").addHeaders(param).post(hashMap).showLog(true, "Login").execute(object : ZStringResult() {
            override fun onSuccessAsyncThread(data: String?) {
                val list = optList<TestBean>("data", TestBean::class.java)
                try {
                    val tempBean = jsonObject.optJSONObject("list")
                    val taset = optValueToObject<TestBean>("list", TestBean::class.java)
                } catch (e: java.lang.Exception) {
                    try {
                        val taset = optValueToObject<TestBean>("{isSerializationFailed:true}", TestBean::class.java)
                        ZLog.i(taset)
                    } catch (e: java.lang.Exception) {
                        ZLog.i(e.message)
                    }
                }
            }
        })
    }

    private fun initSelectPic() {
        //设置头像相关
        val imagePicker = ImagePicker.getInstance()
        imagePicker.imageLoader = PhotoPickerImageLoader() //设置图片加载器
        imagePicker.isShowCamera = true //显示拍照按钮(相机选择的时候才会有用)
        imagePicker.isMultiMode = false //为真的时候每个图片上显示选择的框，选中的也会有遮盖
        imagePicker.isCrop = true //允许裁剪（单选才有效）
        imagePicker.isSaveRectangle = true //是否按矩形区域保存
        imagePicker.selectLimit = 1 //选中数量限制
        imagePicker.style = CropImageView.Style.RECTANGLE //裁剪框的形状
        imagePicker.setFocusSize(800, 800) //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutSize(1000, 1000) //保存文件的高度。单位像素
    }
}

data class TestBean(
        var isSerializationFailed: Boolean,
        var id: Int,
        var title: String,
        var url: String
)