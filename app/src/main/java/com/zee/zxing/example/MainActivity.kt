package com.zee.zxing.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.ImagePickerManager
import com.lzy.imagepicker.bean.OnImagePickerListener
import com.lzy.imagepicker.view.CropImageView
import com.zee.http.MyOk
import com.zee.http.bean.UploadFileParams
import com.zee.http.request.UploadingFileCallBackListener
import com.zee.log.ZLog
import com.zee.utils.ZLibrary
import com.zee.utils.ZScreenUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ZLibrary.init(application, true)
        setContentView(R.layout.activity_main)
        initViews()
        initSelectPic()
    }

    private fun initViews() {
        tv_up_load_pic.setOnClickListener {
            ImagePickerManager.with(this@MainActivity).singleSelectImage().letsGo { imageItemArrayList ->
                if (imageItemArrayList.isNotEmpty()) {
                    val uploadFileParams = UploadFileParams()
                    uploadFileParams.upLoadFile("avatar", imageItemArrayList[0].path)
                    ZLog.i(imageItemArrayList[0].path)
                }
            }
        }
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