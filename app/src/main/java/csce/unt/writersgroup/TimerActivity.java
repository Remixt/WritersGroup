package csce.unt.writersgroup;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TimerActivity extends AppCompatActivity
{
    /**
     * Argument for passing a predefined timer value to the activity
     */
    public static final String ARG_TIMER_LENGTH = "argTimerLength";
    private static final String START_TIMER_TEXT = "Start Timer";
    private static final String STOP_TIMER_TEXT = "Stop Timer";
    private CountDownTimer countDownTimer;

    private static String formatInterval(final long l)
    {
        final long hr = TimeUnit.MILLISECONDS.toHours(l);
        final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) -
                TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit
                .MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format(Locale.getDefault(), "%02d:%02d:%02d.%03d", hr, min, sec, ms);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timer);

        final TextView timerTextView = (TextView) findViewById(R.id.text_timer);
        long millisInFuture = 30000;
        Long millisArg = (Long) ActivityUtil.getBundleArg(getIntent().getExtras(),
                ARG_TIMER_LENGTH);
        if (millisArg != null)
        {
            millisInFuture = millisArg;
        }
        countDownTimer = new CountDownTimer(millisInFuture, 121)
        {

            public void onTick(long millisUntilFinished)
            {
                timerTextView.setText(formatInterval(millisUntilFinished));
            }

            public void onFinish()
            {
                timerTextView.setText("done!");
            }
        };
        final Button startTimer = (Button) findViewById(R.id.button_start_timer);
        startTimer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (startTimer.getText().equals(START_TIMER_TEXT))
                {
                    startTimer.setText(STOP_TIMER_TEXT);
                    countDownTimer.start();
                }
                else
                {
                    startTimer.setText(START_TIMER_TEXT);
                    countDownTimer.cancel();
                }
            }
        });
    }
}
