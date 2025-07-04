package com.tech.hive.ui.room_offering.discover

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.tech.hive.R
import com.tech.hive.data.model.DiscoverAnswerModel


class DiscoverAnswer(
    private val answersList: List<DiscoverAnswerModel>, private val onAnswerSelected: (Int) -> Unit
) : RecyclerView.Adapter<DiscoverAnswer.AnswerViewHolder>() {

    inner class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAnswer = itemView.findViewById<AppCompatTextView>(R.id.tvAnswer)
        private val ivFirstQuiz = itemView.findViewById<AppCompatImageView>(R.id.ivFirstQuiz)

        fun bind(answer: DiscoverAnswerModel, position: Int) {
            tvAnswer.text = answer.answer
            if (answer.selectedAnswer) {
                ivFirstQuiz.setImageResource(R.drawable.selected_circle)
                tvAnswer.setBackgroundResource(R.drawable.select_edittext_bg_color)
            } else {
                ivFirstQuiz.setImageResource(R.drawable.un_selected_circle)
                tvAnswer.setBackgroundResource(R.drawable.white_et_bg)
            }

            tvAnswer.setOnClickListener {
                onAnswerSelected(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_discover_answer_item, parent, false)
        return AnswerViewHolder(view)
    }

    override fun getItemCount(): Int = answersList.size

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(answersList[position], position)
    }
}