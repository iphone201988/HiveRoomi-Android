package com.tech.hive.ui.quiz.question

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.tech.hive.R
import com.tech.hive.data.model.GroupValue
import com.tech.hive.data.model.Quiz


class QuestionNumberAdapter(private val questions: List<Quiz>) :
    RecyclerView.Adapter<QuestionNumberAdapter.QuestionViewHolder>() {


    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitel = itemView.findViewById<AppCompatTextView>(R.id.tvTitel)
        private val rvAnswer = itemView.findViewById<RecyclerView>(R.id.rvAnswer)


        fun bind(questionModel: Quiz) {
            tvTitel.text = questionModel.group
            val adapterAnswers = QuestionTitleAdapter(questionModel.groupValue as List<GroupValue>)

            rvAnswer.adapter = adapterAnswers
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rv_question_number_item, parent, false)
        return QuestionViewHolder(view)
    }

    override fun getItemCount(): Int = questions.size

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position])
    }
}
