package co.com.pagatodo.core.views.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.DeviceUtil.Companion.getOperatorSim
import co.com.pagatodo.core.views.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initTimerHandler()
    }

    private fun initTimerHandler() {
        Handler().postDelayed({
            DeviceUtil.setupAPN(getOperatorSim(),this)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            clearMemory()
            finish()
        }, 1000)
    }

    private fun clearMemory(){
        cacheDir.deleteRecursively()
        Runtime.getRuntime().gc()

    }

}
