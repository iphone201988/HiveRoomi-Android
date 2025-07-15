package com.tech.hive.ui.quiz.question

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.tech.hive.R
import com.tech.hive.data.model.QuestionData
import com.tech.hive.data.model.Quiz
import com.tech.hive.ui.dashboard.DashboardActivity


class QuestionHeaderAdapter(private var question: QuestionData?) :
    RecyclerView.Adapter<QuestionHeaderAdapter.QuestionViewHolder>() {


    init {
        setHasStableIds(true)
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHeading = itemView.findViewById<AppCompatTextView>(R.id.tvSelectLanguage)
        private val tvQuestion = itemView.findViewById<AppCompatTextView>(R.id.tvChoose)
        private val tcCompleteLater = itemView.findViewById<AppCompatTextView>(R.id.tcCompleteLater)
        private val rvFirst = itemView.findViewById<RecyclerView>(R.id.rvFirst)

        fun bind(questionModel: QuestionData) {
            tvHeading.text = questionModel.title
            tvQuestion.text = questionModel.description
            if (questionModel.title?.contains("Essential Filters") == true) {
                tcCompleteLater.visibility = View.VISIBLE
                tcCompleteLater.setOnClickListener {
                    val context = tcCompleteLater.context
                    val intent = Intent(context, DashboardActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                    if (context is Activity) {
                        context.finishAffinity()
                    }
                }

            } else {
                tcCompleteLater.setOnClickListener(null)
                tcCompleteLater.visibility = View.GONE
            }
            rvFirst.adapter = QuestionNumberAdapter(questionModel.quiz as List<Quiz>)
        }
    }


    fun updateQuestion(newQuestion: QuestionData) {
        this.question = newQuestion
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rv_qustion_item, parent, false)
        return QuestionViewHolder(view)
    }

    override fun getItemCount(): Int = if (question != null) 1 else 0

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        question?.let { holder.bind(it) }
    }


}
