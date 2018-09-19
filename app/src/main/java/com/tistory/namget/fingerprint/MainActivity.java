package com.tistory.namget.fingerprint;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements FingerprintAuthenticationDialogFragment.SecretAuthorize , View.OnClickListener {

    private FingerprintAuthenticationDialogFragment mFragment;
    private EditText input;
    private Button save;
    private Button show;
    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        //세팅부분
        mFragment = new FingerprintAuthenticationDialogFragment();
        mFragment.setCallback(this);

    }

    private void init(){
        //android 3.0부터는 타입변환 괄호 안에 생략 가능
        input = (EditText)findViewById(R.id.input);
        save = (Button)findViewById(R.id.save);
        show = (Button)findViewById(R.id.show);
        input.setOnClickListener(this);
        save.setOnClickListener(this);
        show.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.show:
                mFragment.show(this.getFragmentManager(), "my_fragment");
                break;
            case R.id.save:
                //빈값이 아니면 저장
                if(!input.getText().toString().equals("")) {
                    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(Constant.PASSWORD_NUN, input.getText().toString());
                    editor.commit();
                    Toast.makeText(this, "비번 저장 성공: " + input.getText().toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.input:

                break;
        }
    }

    @Override
    public void success() {
        Toast.makeText(this, "인증 성공", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fail() {
        Toast.makeText(this, "인증 실패", Toast.LENGTH_SHORT).show();
    }
}
