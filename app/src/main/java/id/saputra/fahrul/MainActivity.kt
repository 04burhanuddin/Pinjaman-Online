package id.saputra.fahrul

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import id.saputra.fahrul.databinding.ActivityMainBinding
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil


class MainActivity : AppCompatActivity() {

    private lateinit var binding: id.saputra.fahrul.databinding.ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hide action bar
        supportActionBar?.hide()

        val editText = binding.jumlahHariHutangEditText
        val maxTextLength = 15
        editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxTextLength))

        var jumlahHutang = binding.jumlahHutangEditText
        val jumlahHari = binding.jumlahHariHutangEditText
        val buttonConfirm = binding.ajukanHutang

        jumlahHutang.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val user = jumlahHutang.text.toString().trim()
                buttonConfirm.isEnabled = user.isNotEmpty()
            }


            override fun afterTextChanged(s: Editable) {}
        })
        jumlahHari.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val user = jumlahHari.text.toString().trim()
                buttonConfirm.isEnabled = user.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {}
        })



        binding.ajukanHutang.setOnClickListener { calculateTip() }

        binding.jumlahHutangEditText.setOnKeyListener { view, keyCode, _ ->
            handleKeyEvent(
                view,
                keyCode
            )
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun calculateTip() {
        val stringInTextField = binding.jumlahHutangEditText.text.toString()
        val cost = stringInTextField.toDoubleOrNull()


        val estimasiHutang = binding.jumlahHariHutangEditText.text.toString()
        var hari = estimasiHutang.toInt()


        var bungaPersentage: Double
        bungaPersentage = when (binding.hutangOptions.checkedRadioButtonId) {
            R.id.option_harian -> 0.0
            else -> 0.0375
        }


        var bulanOrhari = when (binding.hutangOptions.checkedRadioButtonId) {
            R.id.option_harian -> "Hari"
            else -> "Bulan"
        }

        if (cost == null || cost == 0.0) {
            displayTip(0.0, hari, bulanOrhari)
        }

        if(bulanOrhari == "Hari" && hari >= 20) {
            hari /= 30
            bulanOrhari = "Bulan"
            bungaPersentage = 0.0375
        }

        val layanan = 0.05 * cost!!
        val cairBersih = cost - layanan
        val totalBunga = bungaPersentage * cost
        val totalHutang = (hari.toDouble() * totalBunga) + cairBersih

        displayTip(totalHutang, hari, bulanOrhari, totalBunga)

        val snackBar = Snackbar.make(binding.layout, "Pengajuan Pinjaman Sukses", Snackbar.LENGTH_SHORT).setAction(
            "UNDO", null
        )
        snackBar.show()

    }

    @SuppressLint("StringFormatMatches")
    private fun displayTip(totalHutang: Double, hari: Int = 0, bulanOrhari: String = "", totalBunga: Double = 0.0) {
        val formattedHutang = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(ceil(totalHutang))
        binding.hutangResult.text = getString(R.string.jumlah_hutang, formattedHutang)
        binding.hariResult.text = getString(R.string.jumlah_hari, hari, bulanOrhari)
        val formattedBunga = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(totalBunga * hari)
        binding.bungaResult.text = getString(R.string.jumlah_bunga, formattedBunga)
    }

    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}