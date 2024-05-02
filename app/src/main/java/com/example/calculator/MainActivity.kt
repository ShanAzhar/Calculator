package com.example.calculator
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityMainBinding
import kotlin.math.round

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var clipboardManager: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        binding.tvResult.setOnLongClickListener {
            showContextMenu()
            true
        }

        binding.apply {
            btnOne.setOnClickListener { setNum("1") }
            btnTwo.setOnClickListener { setNum("2") }
            btnThree.setOnClickListener { setNum("3") }
            btnFour.setOnClickListener { setNum("4") }
            btnFive.setOnClickListener { setNum("5") }
            btnSix.setOnClickListener { setNum("6") }
            btnSeven.setOnClickListener { setNum("7") }
            btnEight.setOnClickListener { setNum("8") }
            btnNine.setOnClickListener { setNum("9") }
            btnZero.setOnClickListener { setNum("0") }
            btnCLR.setOnClickListener { clearNum() }
            btnBackSpace.setOnClickListener { removeLastDigit() }
            btnPrecent.setOnClickListener { performOperation("%") }
            btnDivide.setOnClickListener { performOperation("/") }
            btnMultiply.setOnClickListener { performOperation("*") }
            btnSubtract.setOnClickListener { performOperation("-") }
            btnAdd.setOnClickListener { performOperation("+") }
            btnEqual.setOnClickListener { calculateResult() }
            btnDot.setOnClickListener { addDecimalPoint() }

        }
    }

    private fun showContextMenu() {
        val menu = android.widget.PopupMenu(this, binding.tvResult)
        menu.menu.add("Copy")
        menu.menu.add("Paste")
        menu.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Copy" -> copyTextToClipboard()
                "Paste" -> pasteTextFromClipboard()
            }
            true
        }
        menu.show()
    }

    private fun copyTextToClipboard() {
        val text = binding.tvResult.text.toString()
        val clip = ClipData.newPlainText("Calculator Result", text)
        clipboardManager.setPrimaryClip(clip)
        Toast.makeText(this, "Result copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun pasteTextFromClipboard() {
        val clipData = clipboardManager.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val text = clipData.getItemAt(0).text.toString()
            binding.tvResult.append(text)
            Toast.makeText(this, "Text pasted from clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearNum() {
        binding.tvResult.text = ""
    }

    private fun setNum(number: String) {
        binding.tvResult.append(number)
    }

    private fun removeLastDigit() {
        val currentText = binding.tvResult.text.toString()
        if (currentText.isNotEmpty()) {
            binding.tvResult.text = currentText.dropLast(1)
        }
    }

    private fun performOperation(operator: String) {
        val currentText = binding.tvResult.text.toString()
        val lastChar = currentText.lastOrNull()
        if (currentText.isNotEmpty() && lastChar !in listOf('+', '-', '*', '/') && operator != "-") {
            // If the last character is not an operator and the new operator is not '-', append it
            binding.tvResult.append(operator)
        } else if (currentText.isNotEmpty() && lastChar in listOf('+', '-', '*', '/') && operator == "-") {
            // If the last character is an operator and the new operator is '-', append it
            binding.tvResult.append(operator)
        } else if (currentText.isNotEmpty() && lastChar !in listOf('+', '-', '*', '/') && operator == "-") {
            // If the last character is not an operator and the new operator is '-', append it
            binding.tvResult.append(operator)
        } else if (currentText.isNotEmpty() && lastChar in listOf('+', '*', '/')) {
            // If the last character is not '-' and the new operator is '+', '*', or '/', replace it
            binding.tvResult.text = currentText.dropLast(1) + operator
        }
    }

    private fun calculateResult() {
        try {
            val expression = binding.tvResult.text.toString()
            if (expression.isNotEmpty()) {
                val result = evaluateExpression(expression)
                binding.tvResult.text = round(result).toInt().toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addDecimalPoint() {
        val currentText = binding.tvResult.text.toString()
        if (currentText.isNotEmpty() && !currentText.contains(".")) {
            binding.tvResult.append(".")
        }
    }

    private fun evaluateExpression(expression: String): Double {
        val numbers = expression.split("[+\\-*/]".toRegex())
        val operators = expression.split("\\d+".toRegex()).filter { it.isNotEmpty() }

        var result = numbers[0].toDouble()

        for (i in 1 until numbers.size) {
            val number = numbers[i].toDouble()
            val operator = operators[i - 1]
            when (operator) {
                "+" -> result += number
                "-" -> result -= number
                "*" -> result *= number
                "/" -> result /= number
            }
        }

        return result
    }
}

