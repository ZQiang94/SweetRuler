package zqiang94.github.io.ruler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import zqiang94.github.io.ruler.view.RulerView;

public class MainActivity extends AppCompatActivity {

    private TextView txt;
    private RulerView rulerView;
    private EditText mMin;
    private EditText mMax;
    private EditText mPerWidth;
    private EditText mSpacingValue;
    private EditText mLongSpacingValue;
    private Button mBtn;

    private int min;
    private int max;
    private int perWidth;
    private int spacingValue;
    private int longSpacingValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rulerView = findViewById(R.id.rulerView);
        rulerView.setOnValueChangedListener(new RulerView.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                txt.setText("￥" + String.valueOf(value));
            }
        });
        rulerView.scrollToValue(50);
        rulerView.setShowBaseLine(true);
        txt = findViewById(R.id.txt);
        txt.setClickable(true);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rulerView.scrollToValue((int) (Math.random() * 100));
            }
        });
        initView();
    }

    private void initView() {
        mMin = (EditText) findViewById(R.id.min);
        mMax = (EditText) findViewById(R.id.max);
        mPerWidth = (EditText) findViewById(R.id.perWidth);
        mSpacingValue = (EditText) findViewById(R.id.spacingValue);
        mLongSpacingValue = (EditText) findViewById(R.id.longSpacingValue);
        mBtn = (Button) findViewById(R.id.btn);

        min = Integer.valueOf(mMin.getText().toString());
        max = Integer.valueOf(mMax.getText().toString());
        perWidth = Integer.valueOf(mPerWidth.getText().toString());
        spacingValue = Integer.valueOf(mSpacingValue.getText().toString());
        longSpacingValue = Integer.valueOf(mLongSpacingValue.getText().toString());

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                min = Integer.valueOf(mMin.getText().toString());
                max = Integer.valueOf(mMax.getText().toString());
                perWidth = Integer.valueOf(mPerWidth.getText().toString());
                spacingValue = Integer.valueOf(mSpacingValue.getText().toString());
                longSpacingValue = Integer.valueOf(mLongSpacingValue.getText().toString());


                rulerView.setMinValue(min);
                rulerView.setMaxValue(max);
                rulerView.setPerWidth(perWidth);
                rulerView.setSpacingValue(spacingValue);
                rulerView.setLongSpacingValue(longSpacingValue);
            }
        });
    }
}
