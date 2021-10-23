package crabster.rudakov.sberschoollesson16hwk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import crabster.rudakov.sberschoollesson16hwk.Constants.LOG_TAG
import crabster.rudakov.sberschoollesson16hwk.Constants.TOAST_MESSAGE
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

class TimerFragment : Fragment() {

    private var timerView: TextView? = null
    private var timer: Int = 10
    private var disposable: Disposable? = null

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
            getObservable(timer)
                .observeOn(AndroidSchedulers.mainThread())  //Планировщик для выполнения задач в UI-потоке, для модификации UI
                .subscribe(getObserver())  //Подписываем Observer
        }
    }

    private fun getObservable(_time: Int): Observable<String> {  //Создаём Cold-источник, который эмитит с заданным интервалом
        var time = _time + 1
        return Observable.interval(0, 1, TimeUnit.SECONDS)
            .map { --time }  //Отнимаем 1 у всех элементов
            .map { time.toString() }  //Преобразуем значения таймера к String
            .take(time.toLong())  //Берем первые time количество элементов
    }

    private fun getObserver(): Observer<String> {
        val observer: Observer<String> = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                disposable = d
            }
            override fun onNext(value: String) {
                timer = Integer.valueOf(value)
                timerView?.text = timer.toString()
                Log.d(LOG_TAG, "${Thread.currentThread().name} - $Constants.CURRENT_TIMER_READING : $timer")
            }
            override fun onError(e: Throwable) {}
            override fun onComplete() {
                Toast.makeText(context, TOAST_MESSAGE, Toast.LENGTH_LONG).show()
            }
        }
        return observer
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timerView = null
        disposable?.dispose()  //Отписываемся при потере контекста
    }

}