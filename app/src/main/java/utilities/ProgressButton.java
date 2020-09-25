package utilities;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.loginstats.R;

public class ProgressButton {
    private CardView cardView;
    private ProgressBar progressBar;
    private TextView textView;
    private ConstraintLayout constraintLayout;
    Animation fadeIn;
    public ProgressButton(Context ct, View view)
    {
     cardView=view.findViewById(R.id.cardview);
     constraintLayout=view.findViewById(R.id.constrainLayout);
     progressBar=view.findViewById(R.id.progressBar);
     textView=view.findViewById(R.id.textView);
    }

    public void ButtonActivated()
    {
      progressBar.setVisibility(View.VISIBLE);
      textView.setText(R.string.plswa);
    }

    public void ButtonError()
    {
        constraintLayout.setBackgroundColor(cardView.getResources().getColor(R.color.red));
        progressBar.setVisibility(View.GONE);
        textView.setText(R.string.restr);
    }
    public void ButtonFinished()
    {
     constraintLayout.setBackgroundColor(cardView.getResources().getColor(R.color.green));
     progressBar.setVisibility(View.GONE);
     textView.setText(R.string.done);
    }
}
