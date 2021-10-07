package crabster.rudakov.sberschoollesson16hwk

import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import crabster.rudakov.sberschoollesson16hwk.Constants.CURRENT_TIMER_READING
import crabster.rudakov.sberschoollesson16hwk.Constants.LOG_TAG
import crabster.rudakov.sberschoollesson16hwk.Constants.TOAST_MESSAGE
import java.util.concurrent.*

class TimerFragment : Fragment() {

    private var timerView: TextView? = null
    private var timer: Int = 6

    //Создаётся 'Executor' с 1 потоком
    private var executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private lateinit var timerFuture: Future<out Any>

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
            //передаётся значение таймера экзекутору
            timerFuture = executor.scheduleAtFixedRate({
                if (timer > 0) {
                    timer--
                    //Производится добавление сообщения с кодом метода 'updateTimer()' собственному 'Handler' TextView
                    timerView?.post { updateTimer() }
                } else {
                    timerFuture.cancel(true)
                }
            }, 0, 1, TimeUnit.SECONDS)
        }
    }

    /**
     * Переопределяем данный метод с целью закрытия ресурсов по окончании работы приложения
     * */
    override fun onDestroyView() {
        super.onDestroyView()
        //Удаляются все работающие задачи
        executor.shutdown()
        //Зануляется ссылка на View для удаления её GC и высвобождения памяти
        timerView = null
    }

    /**
     * Метод устанавливает показание таймера в текстовое поле
     * */
    private fun updateTimer() {
        Log.d(LOG_TAG, "${Thread.currentThread().name} - $CURRENT_TIMER_READING : $timer")
        timerView?.text = timer.toString()
        if (timer == 0) Toast.makeText(context, TOAST_MESSAGE, Toast.LENGTH_LONG).show()
    }

}

