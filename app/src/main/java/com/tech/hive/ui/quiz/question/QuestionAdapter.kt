package com.tech.hive.ui.quiz.question

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tech.hive.R
import com.tech.hive.data.model.QuestionModel

class QuestionAdapter(private val questions: List<QuestionModel>) :
    RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHeading = itemView.findViewById<AppCompatTextView>(R.id.tvTitle)
        private val tvQuestion1 = itemView.findViewById<AppCompatTextView>(R.id.tvQuestion1)
        private val tvQuestion = itemView.findViewById<AppCompatTextView>(R.id.tvQuestion)
        private val rvAnswers = itemView.findViewById<RecyclerView>(R.id.rvAnswer)
        private val rvQuestion1 = itemView.findViewById<RecyclerView>(R.id.rvQuestion1)

        fun bind(questionModel: QuestionModel) {
            tvHeading.text = questionModel.heading
            tvQuestion.text = questionModel.question
            tvQuestion1.text = questionModel.question1
            if (questionModel.type == 2) {
                tvQuestion1.visibility = View.VISIBLE
                rvQuestion1.visibility = View.VISIBLE
            } else {
                tvQuestion1.visibility = View.GONE
                rvQuestion1.visibility = View.GONE
            }


            val adapterAnswers = AnswerAdapter(questionModel.answer) { selectedPosition ->
                questionModel.answer.forEachIndexed { index, answer ->
                    answer.selectedAnswer = index == selectedPosition
                }
                questionModel.selectedAnswerPosition = selectedPosition
                notifyItemChanged(adapterPosition)
            }

            val adapterQuestion1 = AnswerAdapter(questionModel.answer1) { selectedPosition ->
                questionModel.answer1.forEachIndexed { index, answer ->
                    answer.selectedAnswer = index == selectedPosition
                }
                notifyItemChanged(adapterPosition)
            }

            rvAnswers.adapter = adapterAnswers
            rvQuestion1.adapter = adapterQuestion1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rv_qustion_item, parent, false)
        return QuestionViewHolder(view)
    }

    override fun getItemCount(): Int = questions.size

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position])
    }
}
