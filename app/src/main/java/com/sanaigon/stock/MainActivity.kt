package com.sanaigon.stock

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {


    inner class GetPriceTask(val code: String, val kVal: String): AsyncTask<Void, Void, List<String>>() {
        override fun doInBackground(vararg params: Void?): List<String> {
            val doc = Jsoup.connect("https://finance.naver.com/item/sise_day.nhn?code=$code").get()

            return doc.select("td[class]").take(11).map {
                it.text().replace(",", "")
            }
        }

        override fun onPostExecute(result: List<String>?) {

            val yHigh = result!![9].toInt()
            val yLow = result!![10].toInt()
            val yOpen = result!![8].toInt()
            val yClose = result!![6].toInt()

            val tOpen = result!![2].toInt()


            todayOpen.text = tOpen.toString()

            yesterdayHigh.text = yHigh.toString()
            yesterdayLow.text = yLow.toString()
            yesterdayOpen.text = yOpen.toString()
            yesterdayClose.text = yClose.toString()

            buyPrice.text = (((yHigh - yLow) * kVal.toDouble()).toInt() + tOpen).toString()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.search.setOnClickListener {
            val code = stockcode.text.toString()
            val kValue = kValue.text.toString()

            //UI Thread 에서는 Network 을 처리할 수 없으므로 asyncTask를 별로로 만든다.git
            GetPriceTask(code, kValue).execute()

            val imm = (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
        }
    }
}
