package com.example.sleepontime.ui

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.example.sleepontime.R
import com.example.sleepontime.data.DatabaseHelper
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var codeButton = findViewById<Button>(R.id.getcode)
        var loginButton = findViewById<Button>(R.id.loginButton)
        val backButton = findViewById<Button>(R.id.backButton)

        //点击返回主页
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //验证码发送相关信息
        val subject = "Verify your email address"
        val body = "Login verify code:"
        var verifyCode: String = ""

        //点击发送验证码
        codeButton.setOnClickListener {

            var inputEmail = findViewById<TextInputEditText>(R.id.email).text.toString()
            val emailEditText = findViewById<TextInputEditText>(R.id.email)

            //检查邮箱是否为空
            if (inputEmail.isEmpty()) {
                //输入框为空，提示
                Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show()    //短时长Toast消息，LENGTH_SHORT 通常为2s
            } else if (!isValidEmail(inputEmail)) {
                //不是有效邮箱，提示
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            } else {
                //输入框不为空且邮箱有效，检查是否已经注册
                val dbHelper = DatabaseHelper(this) //创建数据库实例
                val db = dbHelper.readableDatabase  //创建可读的数据库实例

                val selection = "${DatabaseHelper.COLUMN1_EMAIL} = ?"
                val selectionArgs = arrayOf(inputEmail)
                val cursor = db.query(
                    "users",
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
                )

                val emailExists = cursor.moveToFirst()  //将光标移动到查询结果的第一行，返回值为是否成功移动到第一行
                cursor.close()

                //已经注册邮箱，随机生成6位验证码，发送邮件
                if (emailExists) {
                    verifyCode = generateVerificationCode()
                    sendEmail(inputEmail, subject, body+verifyCode)
                    emailEditText.isEnabled = false //发送后将邮箱输入框锁定，防止修改邮箱
                } else {
                    //未注册邮箱,提示文字
                    Toast.makeText(this, "This email has not been registered!", Toast.LENGTH_SHORT).show()  //显示提示文字
                }
            }
        }

        //点击登录
        loginButton.setOnClickListener {
            //两个输入框
            var inputCode = findViewById<TextInputEditText>(R.id.code).text.toString()
            val inputEmail = findViewById<TextInputEditText>(R.id.email).text.toString()
            var codeEditText = findViewById<TextInputEditText>(R.id.code)

            //检查是不是有填验证码
            if (inputCode.isEmpty()) {
                    Toast.makeText(this, "Please enter the verify code", Toast.LENGTH_SHORT).show()   //提示文
            } else {
                //检查验证码
                if (verifyCode!=inputCode) {
                    //验证码不相同
                    Toast.makeText(this, "The verify code is wrong", Toast.LENGTH_SHORT).show() //提示文字
                    codeEditText.setText("")    //清空输入框
                } else {
                    //验证码正确，跳转页面
                    val intent = Intent(this, Choose::class.java)

                    val dbHelper = DatabaseHelper(this@Login)
                    val userId = dbHelper.getUserIdByEmail(inputEmail)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                }
            }
        }

    }

    //邮箱验证函数
    private fun isValidEmail (email: String):Boolean {
        val pattern = Patterns.EMAIL_ADDRESS   //一个预定义的邮箱格式正则表达式
        return pattern.matcher(email).matches() //将给定字符串与邮箱的正则表达式匹配，返回匹配结果true 和false
    }

    //随机验证码生成函数
    private fun generateVerificationCode(): String {
        val verifyCodeLength = 6
        val random = Random()
        val stringBuilder = StringBuilder(verifyCodeLength)   //创建一个构建字符串的对象，指定字符串长度为6

        for (i in 0 until verifyCodeLength) {
            val digit = random.nextInt(10)  //10以内随机数
            stringBuilder.append(digit)
        }

        return stringBuilder.toString()
    }

    //邮箱发送函数
    private fun sendEmail(email: String, subject: String, body: String) {
        runBlocking {
            launch (Dispatchers.IO) {
                val properties = Properties()   //创建一个Properties对象，可以储存一系列键值对，每个键值对表示一个属性。用来存储Java Mail 的SMTP 设置

                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")

                properties["mail.smtp.host"] = "smtp.163.com"
                properties["mail.smtp.port"] = 465
                properties["mail.smtp.auth"] = "true"

                val username = "sleepontime2023@163.com"
                val passward = "KKBWCNIZVUDPAWSW"

                //创建Session对象，表示与邮件服务器的对话
                val session = Session.getInstance(properties, object: Authenticator(){
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(username, passward)
                    }
                }) //getInstance接受两个参数：properties 和 Authenticator 对象。properties 是用于配置会话的属性集，我们在之前的代码中设置了一些 SMTP 相关的属性。Authenticator 是一个抽象类，用于提供邮件服务器的身份验证信息。

                try{
                    val message = MimeMessage(session)  //MimeMessage 对象构造邮件内容
                    message.setFrom(InternetAddress(username))  //设置发件人地址。InternetAddress接收字符串，生成一个邮件地址
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))   //收件人类型为TO, 第二个参数为解析字符串为邮件地址对象

                    //邮件标题
                    message.subject = subject
                    //邮件内容
                    message.setText(body)

                    //发送邮件
                    Transport.send(message)

                    //提示发送成功
                    println("===================Email sent successfully===================")
                    println(body)

                } catch (e: MessagingException) {
                    println("===================Failed to send email. Error: ${e.message}===================")
                }
            }
        }
    }
}