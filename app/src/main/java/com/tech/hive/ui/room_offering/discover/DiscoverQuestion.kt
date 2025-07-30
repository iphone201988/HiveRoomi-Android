package com.tech.hive.ui.room_offering.discover

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.tech.hive.R
import com.tech.hive.data.model.DiscoverQuestionModel

class DiscoverQuestion(private val questions: List<DiscoverQuestionModel>) :
    RecyclerView.Adapter<DiscoverQuestion.QuestionViewHolder>() {

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvQuestion = itemView.findViewById<AppCompatTextView>(R.id.tvQuestion)
        private val rvAnswers = itemView.findViewById<RecyclerView>(R.id.rvAnswer)


        fun bind(questionModel: DiscoverQuestionModel) {
            tvQuestion.text = questionModel.question


            val adapterAnswers = DiscoverAnswer(questionModel.answer) { selectedPosition ->
                questionModel.answer.forEachIndexed { index, answer ->
                    answer.selectedAnswer = index == selectedPosition
                }

                questionModel.selectedAnswerIndex = selectedPosition
                notifyItemChanged(adapterPosition)
            }


            rvAnswers.adapter = adapterAnswers
        }
    }

    fun getSelectedAnswers(): List<Pair<String, String>> {
        return questions.mapNotNull { question ->
            val selectedIndex = question.selectedAnswerIndex
            val answerList = question.answer

            if (
                selectedIndex != -1 &&
                selectedIndex in answerList.indices &&
                answerList[selectedIndex].answer.isNotBlank()
            ) {
                question.question to answerList[selectedIndex].answer
            } else {
                null
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_discover_questions, parent, false)
        return QuestionViewHolder(view)
    }

    override fun getItemCount(): Int = questions.size

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position])
    }
}
