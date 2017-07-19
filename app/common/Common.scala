package common

import com.google.inject.Singleton

/**
  * Created by Rachel on 2017. 7. 19..
  */
@Singleton
object Common {

  def createAuthSMSNumber: String = {
    val now = System.currentTimeMillis().toString
    val preNow = now.substring(0, 4).toInt
    val postNow = now.substring(now.length-4, now.length).toInt
    (preNow * postNow).toString.substring(0, 4)
  }

}
