package com.tech.hive.ui.quiz.question

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.tech.hive.R
import com.tech.hive.data.model.Option


class QuestionAnswerAdapter(
    private val selectedAnswer: String,
    private val type: String?,
    private val answers: List<Option?>,
    private val onAnswerSelected: (Int) -> Unit
) : RecyclerView.Adapter<QuestionAnswerAdapter.AnswerViewHolder>() {
    inner class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val etEmail = itemView.findViewById<AppCompatEditText>(R.id.etEmail)
        private val tvAnswer = itemView.findViewById<AppCompatTextView>(R.id.tvAnswer)
        private val ivLocation = itemView.findViewById<AppCompatImageView>(R.id.ivLocation)
        private val ivFirstQuiz = itemView.findViewById<AppCompatImageView>(R.id.ivFirstQuiz)

        fun bind(answer: Option, position: Int) {
            tvAnswer.text = answer.label


            val isSelected = if (selectedAnswer.isNotEmpty()) {
                answer.value == selectedAnswer
            } else {
                answer.selectedAnswer
            }

            if (isSelected) {
                ivFirstQuiz.setImageResource(R.drawable.selected_circle)
                etEmail.setBackgroundResource(R.drawable.select_edittext_bg_color)
                tvAnswer.setBackgroundResource(R.drawable.select_edittext_bg_color)
            } else {
                ivFirstQuiz.setImageResource(R.drawable.un_selected_circle)
                etEmail.setBackgroundResource(R.drawable.white_et_bg)
                tvAnswer.setBackgroundResource(R.drawable.white_et_bg)
            }

            when (type) {
                "option" -> {
                    etEmail.visibility = View.GONE
                    tvAnswer.visibility = View.VISIBLE
                    ivFirstQuiz.visibility = View.VISIBLE
                    ivLocation.visibility = View.GONE
                }

                "text" -> {
                    etEmail.setText(selectedAnswer)
                    etEmail.visibility = View.VISIBLE
                    ivLocation.visibility = View.VISIBLE
                    tvAnswer.visibility = View.GONE
                    ivFirstQuiz.visibility = View.GONE
                }
            }

            etEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (position in answers.indices) {
                        val currentOption = answers[position]
                        if (s?.isNotEmpty() == true) {
                            currentOption?.inputValue = s.toString()
                            currentOption?.selectedAnswer = true
                        } else {
                            currentOption?.inputValue = ""
                            currentOption?.selectedAnswer = false
                        }
                    }
                }
            })

            tvAnswer.setOnClickListener {
                answers.forEachIndexed { index, option ->
                    option?.selectedAnswer = index == position
                }
                notifyDataSetChanged()
                onAnswerSelected(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rv_answer_item, parent, false)
        return AnswerViewHolder(view)
    }

    //override fun getItemCount(): Int = answers.size

    override fun getItemCount(): Int {
        return if (type == "text" && answers.isEmpty()) {
            1 // shows one input field
        } else {
            answers.size
        }
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {

        val answer = if (answers.isEmpty()) Option(
            label = "", value = "", selectedAnswer = false
        ) else answers[position]
        holder.bind(answer as Option, position)
    }


}
