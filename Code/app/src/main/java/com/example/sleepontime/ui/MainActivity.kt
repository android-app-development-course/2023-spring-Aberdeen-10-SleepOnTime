package com.example.sleepontime.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.sleepontime.R
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions


class MainActivity : AppCompatActivity() {

    companion object {
        private const val PACKAGE_PERMISSION_CODE = 100
        private const val APP_SETTINGS_REQUEST_CODE = 300
    }

//    val requestPermissionLauncher =
//        registerForActivityResult(RequestPermission()
//        ) {isGranted: Boolean ->
//            if (isGranted) {
//                //点击登录按钮
//                val loginButton = findViewById<ImageButton>(R.id.loginButton)
//                loginButton.setOnClickListener {
//                    val intent = Intent(this, Login::class.java)    //指定目标页面
//                    startActivity(intent)   //启动目标页面
//                }
//
//                //点击注册按钮
//                val registerButton = findViewById<ImageButton>(R.id.registerButton)
//                registerButton.setOnClickListener {
//                    val intent = Intent(this,Register::class.java)
//                    startActivity(intent)
//                }
//            } else {
//                println("not granted")
//            }
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  //设置布局文件

        XXPermissions.with(this)
            // 申请单个权限
            .permission(Permission.PACKAGE_USAGE_STATS)
//            // 申请多个权限
//            .permission(Permission.Group.CALENDAR)
            // 设置权限请求拦截器（局部设置）
            //.interceptor(new PermissionInterceptor())
            // 设置不触发错误检测机制（局部设置）
            //.unchecked()
            .request(object : OnPermissionCallback {

                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (!allGranted) {
                        Toast.makeText(this@MainActivity, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT).show()
                        return
                    }
                    Toast.makeText(this@MainActivity, "获取录音和日历权限成功", Toast.LENGTH_SHORT).show()
                    //点击登录按钮
                    val loginButton = findViewById<Button>(R.id.loginButton)
                    loginButton.setOnClickListener {
                        val intent = Intent(this@MainActivity, Login::class.java)    //指定目标页面
                        startActivity(intent)   //启动目标页面
                    }

                    //点击注册按钮
                    val registerButton = findViewById<Button>(R.id.registerButton)
                    registerButton.setOnClickListener {
                        val intent = Intent(this@MainActivity,Register::class.java)
                        startActivity(intent)
                    }

                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        Toast.makeText(this@MainActivity, "被永久拒绝授权，请手动授予录音和日历权限", Toast.LENGTH_SHORT).show()
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(this@MainActivity, permissions)
                    } else {
                        Toast.makeText(this@MainActivity, "获取录音和日历权限失败", Toast.LENGTH_SHORT).show()
                    }
                }
            })


//        checkUseagePermission()
//        when {
//            checkSelfPermission(
//                this,
//                PACKAGE_USAGE_STATS
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                println("使用API")
//            }
//            shouldShowRequestPermissionRationale(PACKAGE_USAGE_STATS) -> {
//
//            }
//            else -> {
//                requestPermissionLauncher.launch(
//                    Manifest.permission.PACKAGE_USAGE_STATS
//                )
//            }
//        }
    }

//    private fun openAppSettings() {
//        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
//        intent.data = Uri.fromParts("package", packageName, null)
//        startActivityForResult(intent, APP_SETTINGS_REQUEST_CODE)
//    }
//
//
//    private fun showPermissionRequestDialog() {
//        val dialogBuilder = AlertDialog.Builder(this)
//        dialogBuilder.setTitle("权限请求")
//        dialogBuilder.setMessage("为了正常使用该功能，需要获取应用使用情况权限，请手动授予权限。")
//        dialogBuilder.setPositiveButton("去设置") { _, _ ->
//            openAppSettings()
//        }
//        dialogBuilder.setNegativeButton("取消") { _, _ ->
//            // 用户选择取消
//            println("用户取消了权限请求")
//            finish()
//        }
//        val dialog = dialogBuilder.create()
//        dialog.show()
//    }
//
//
//    private fun checkUseagePermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val permission = ACTION_MANAGE_OVERLAY_PERMISSION
//            if (checkSelfPermission(this@MainActivity, permission) != PackageManager.PERMISSION_GRANTED) {
//                println("1没有权限")
//
//                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), PACKAGE_PERMISSION_CODE)
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    val permission = ACTION_MANAGE_OVERLAY_PERMISSION
//                    if (checkSelfPermission(this@MainActivity, permission) != PackageManager.PERMISSION_GRANTED) {
//                        if (shouldShowRequestPermissionRationale(permission)) {
//                            // 权限被拒绝，但没有选择"不再询问"选项
//                            println("权限被拒绝，但没有选择\"不再询问\"选项")
//                            // TODO: 提示用户解释为什么需要该权限
//
////                            // 引导用户前往应用设置页面
////                            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
////                            intent.data = Uri.fromParts("package", packageName, null)
////                            startActivityForResult(intent, APP_SETTINGS_REQUEST_CODE)
//                        } else {
//                            // 权限被拒绝，并且选择了"不再询问"选项
//                            println("权限被拒绝，并且选择了\"不再询问\"选项")
//                            // TODO: 提示用户手动授予权限
//                            showPermissionRequestDialog()
//                        }
//                    }
//                }
//
//
////                // 打开应用信息界面
////                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
////                intent.data = Uri.fromParts("package", packageName, null)
////                startActivityForResult(intent, APP_SETTINGS_REQUEST_CODE)
////                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), PACKAGE_PERMISSION_CODE)
//                println("申请完了")
//
//            } else {
//                //点击登录按钮
//                val loginButton = findViewById<ImageButton>(R.id.loginButton)
//                loginButton.setOnClickListener {
//                    val intent = Intent(this, Login::class.java)    //指定目标页面
//                    startActivity(intent)   //启动目标页面
//                }
//
//                //点击注册按钮
//                val registerButton = findViewById<ImageButton>(R.id.registerButton)
//                registerButton.setOnClickListener {
//                    val intent = Intent(this,Register::class.java)
//                    startActivity(intent)
//                }
//            }
//        }
//    }
//    @Deprecated("This method is deprecated")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == APP_SETTINGS_REQUEST_CODE) {
//            // 检查权限是否已经被用户手动授予
//            if (checkSelfPermission(this, ACTION_MANAGE_OVERLAY_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
//                // 用户已经手动授予了权限
//                println("用户手动授予权限")
//
//            } else {
//                // 用户未授予权限
//                println("用户未授予权限")
//            }
//            //点击登录按钮
//            val loginButton = findViewById<ImageButton>(R.id.loginButton)
//            loginButton.setOnClickListener {
//                val intent = Intent(this, Login::class.java)    //指定目标页面
//                startActivity(intent)   //启动目标页面
//            }
//
//            //点击注册按钮
//            val registerButton = findViewById<ImageButton>(R.id.registerButton)
//            registerButton.setOnClickListener {
//                val intent = Intent(this,Register::class.java)
//                startActivity(intent)
//            }
//        }
//    }
//
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            PACKAGE_PERMISSION_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    println("权限已经授予")
//                } else {
//                    println("权限被拒绝")
//                }
//            }
//        }
//    }
}