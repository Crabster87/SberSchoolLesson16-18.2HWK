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
import crabster.rudakov.sberschoollesson16hwk.Constants.BACKGROUND_THREAD_NAME
import crabster.rudakov.sberschoollesson16hwk.Constants.CURRENT_TIMER_READING
import crabster.rudakov.sberschoollesson16hwk.Constants.LOG_TAG
import crabster.rudakov.sberschoollesson16hwk.Constants.TOAST_MESSAGE
import java.util.concurrent.TimeUnit

class TimerFragment : Fragment() {

    private var timerView: TextView? = null
    private var timer: Int = 6
    private var backgroundHandler: Handler

    //Назначается Handler для потока 'UI'
    private var uiHandler: Handler = Handler(Looper.getMainLooper())

    init {
        //Создаётся отдельная нить 'BACKGROUND' для вычисления показаний таймера
        val backgroundThread = HandlerThread(BACKGROUND_THREAD_NAME)
        backgroundThread.start()
        //Назначается Handler для потока 'BACKGROUND', выполняющий логику параллельных вычислений
        backgroundHandler = TimerHandler(backgroundThread.looper)
    }

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
            //Производится добавление значения показания таймера в очередь сообщений потока 'BACKGROUND'
            backgroundHandler.sendEmptyMessage(CALC)
        }
    }

    /**
     * Переопределяем данный метод с целью закрытия ресурсов по окончании работы приложения
     * */
    override fun onDestroyView() {
        super.onDestroyView()
        //Удаляются все работающие задачи
        backgroundHandler.removeCallbacksAndMessages(null)
        uiHandler.removeCallbacksAndMessages(null)
        //Зануляется ссылка на View для удаления её GC и высвобождения памяти
        timerView = null
    }

    /**
     * Метод устанавливает показание таймера в текстовое поле
     * */
    private fun updateTimer() {
        timerView?.text = timer.toString()
    }

    /**
     * Создаём собственный 'Handler' чтобы избавиться от 'Callback' и работать с идентификаторами сообщений
     * */
    private inner class TimerHandler(looper: Looper): Handler(looper) {

        override fun handleMessage(msg: Message) {
            Log.d(LOG_TAG, "${Thread.currentThread().name} - $CURRENT_TIMER_READING : $timer")
            when(msg.what) {//Производится проверка идентификатора сообщения
                CALC -> if (timer > 0) {
                    timer--
                    //Производится добавление сообщения с кодом метода 'updateTimer()' в очередь сообщений потока 'UI'
                    uiHandler.post { updateTimer() }
                    //Производится добавление показания таймера в очередь сообщений потока 'BACKGROUND' с задержкой в 1 с
                    backgroundHandler.sendEmptyMessageDelayed(CALC, TimeUnit.SECONDS.toMillis(1))
                }
                else {
                    Toast.makeText(context, TOAST_MESSAGE, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Назначается идентификатор сообщения
     * */
    private companion object {
        const val CALC: Int = 0
    }

}

