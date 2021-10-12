package crabster.rudakov.sberschoollesson16hwk

import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import crabster.rudakov.sberschoollesson16hwk.Constants.TOAST_MESSAGE

class TimerFragment : Fragment() {

    private var timerView: TextView? = null
    private var timer: Long = 10

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.timer_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timerView = view.findViewById(R.id.fragment_text_view)
        timerView?.text = timer.toString()

        val buttonStart = view.findViewById<Button>(R.id.button_start)
        buttonStart.setOnClickListener {
            object: CountDownTimer(timer * 1000, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    timerView?.text = (millisUntilFinished / 1000).toString()
                }

                override fun onFinish() {
                    Toast.makeText(context, TOAST_MESSAGE, Toast.LENGTH_LONG).show()
                }
            }.start()
        }
    }

}

