package com.tech.hive.ui.quiz.question

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.tech.hive.R
import com.tech.hive.data.model.GroupValue
import com.tech.hive.data.model.Option

class QuestionTitleAdapter(private val questions: List<GroupValue>) :
    RecyclerView.Adapter<QuestionTitleAdapter.QuestionViewHolder>() {

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHeading = itemView.findViewById<AppCompatTextView>(R.id.tvQuestion)
        private val rvAnswers = itemView.findViewById<RecyclerView>(R.id.rvAnswer)

        fun bind(questionModel: GroupValue) {
            tvHeading.text = questionModel.title
            if (questionModel.type == "text" && questionModel.options.isNullOrEmpty()) {
                questionModel.options = mutableListOf(Option(label = "", value = ""))
            }
            val options = questionModel.options ?: emptyList()
            val adapterAnswers = QuestionAnswerAdapter(
                questionModel.answer ?: "",
                questionModel.type,
                options
            ) { selectedAnswer, selectedPosition ->
                questionModel.options?.forEachIndexed { index, option ->
                    option?.selectedAnswer = index == selectedPosition
                }
                questionModel.answer = questionModel.options?.getOrNull(selectedPosition)?.value
            //    questionModel.answer = selectedAnswer
                notifyItemChanged(adapterPosition)
            }
            rvAnswers.adapter = adapterAnswers
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.question_title_rv_item, parent, false)
        return QuestionViewHolder(view)
    }

    override fun getItemCount(): Int = questions.size

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position])
    }
}
