package de.topobyte.android.seekbarpreference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * A base class for creating seek bar preferences.
 */
public abstract class SeekBarPreference extends DialogPreference implements
    OnSeekBarChangeListener {

  // Text above the seek bar
  protected String messageText;

  // Minimum and maximum values
  protected int min;
  protected int max;

  // Increment value for the arrow keys
  protected int increment = 1;

  // Views
  private TextView currentValueTextView;
  private TextView messageTextView;
  private SeekBar preferenceSeekBar;

  protected final SharedPreferences preferences;

  protected int currentValue;

  public SeekBarPreference(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    preferences = PreferenceManager.getDefaultSharedPreferences(context);

    setDialogLayoutResource(R.layout.seek_bar_preference);
  }

  @Override
  public void onClick(DialogInterface dialog, int which)
  {
    if (which == DialogInterface.BUTTON_POSITIVE) {
      int newValue = preferenceSeekBar.getProgress();
      if (currentValue != newValue) {
        currentValue = newValue;
        storeValue();
      }
    }
  }

  private void storeValue()
  {
    Editor editor = preferences.edit();
    editor.putInt(getKey(), min + currentValue);
    editor.commit();
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress,
                                boolean fromUser)
  {
    if (currentValueTextView != null) {
      currentValueTextView.setText(getCurrentValueText(progress));
    }
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar)
  {
    // ignore
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar)
  {
    // ignore
  }

  @Override
  protected View onCreateDialogView()
  {
    View view = super.onCreateDialogView();

    messageTextView = (TextView) view.findViewById(R.id.seekbar_preference_message);
    preferenceSeekBar = (SeekBar) view.findViewById(R.id.seekbar_preference_seekbar);
    currentValueTextView = (TextView) view.findViewById(R.id.seekbar_preference_current_value);

    if (messageText == null) {
      messageTextView.setVisibility(View.GONE);
    } else {
      messageTextView.setVisibility(View.VISIBLE);
      messageTextView.setText(messageText);
    }

    preferenceSeekBar.setOnSeekBarChangeListener(this);
    preferenceSeekBar.setMax(max - min);
    preferenceSeekBar.setProgress(Math.min(currentValue, max - min));
    preferenceSeekBar.setKeyProgressIncrement(increment);

    currentValueTextView.setText(getCurrentValueText(preferenceSeekBar
        .getProgress()));

    return view;
  }

  /**
   * Override this method to control the text below the seek bar.
   *
   * @param progress the current value of the seek bar.
   * @return the text to display.
   */
  protected abstract String getCurrentValueText(int progress);

}