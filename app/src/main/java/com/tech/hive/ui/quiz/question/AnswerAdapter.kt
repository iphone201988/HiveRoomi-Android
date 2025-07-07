package com.tech.hive.ui.quiz.question

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.tech.hive.R
import com.tech.hive.data.model.Answer
import kotlin.toString


class AnswerAdapter(
    private val answers: List<Answer>, private val onAnswerSelected: (Int) -> Unit
) : RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>()
{

    inner class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val etEmail = itemView.findViewById<AppCompatEditText>(R.id.etEmail)
        private val tvAnswer = itemView.findViewById<AppCompatTextView>(R.id.tvAnswer)
        private val ivLocation = itemView.findViewById<AppCompatImageView>(R.id.ivLocation)
        private val ivFirstQuiz = itemView.findViewById<AppCompatImageView>(R.id.ivFirstQuiz)

        fun bind(answer: Answer, position: Int) {
            tvAnswer.text = answer.label
            if (answer.selectedAnswer) {
                ivFirstQuiz.setImageResource(R.drawable.selected_circle)
                etEmail.setBackgroundResource(R.drawable.select_edittext_bg_color)
                tvAnswer.setBackgroundResource(R.drawable.select_edittext_bg_color)
            } else {
                ivFirstQuiz.setImageResource(R.drawable.un_selected_circle)
                etEmail.setBackgroundResource(R.drawable.white_et_bg)
                tvAnswer.setBackgroundResource(R.drawable.white_et_bg)
            }
            if (answer.type == 2) {
                etEmail.visibility = View.VISIBLE
                ivLocation.visibility = View.VISIBLE
                tvAnswer.visibility = View.GONE
                ivFirstQuiz.visibility = View.GONE
            } else {
                etEmail.visibility = View.GONE
                tvAnswer.visibility = View.VISIBLE
                ivFirstQuiz.visibility = View.VISIBLE
                ivLocation.visibility = View.GONE
            }

            etEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.isNotEmpty() == true) {
                        answers[position].value = s.toString()
                        etEmail.setBackgroundResource(R.drawable.select_edittext_bg_color)
                        answer.selectedAnswer = true
                    } else {
                        etEmail.setBackgroundResource(R.drawable.white_et_bg)
                        answer.selectedAnswer = false
                    }
                }


            })

            tvAnswer.setOnClickListener {
                onAnswerSelected(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rv_answer_item, parent, false)
        return AnswerViewHolder(view)
    }

    override fun getItemCount(): Int = answers.size

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(answers[position], position)
    }
}
