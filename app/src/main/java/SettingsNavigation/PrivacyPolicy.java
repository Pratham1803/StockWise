package SettingsNavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivitySettingsBinding;

import MenuScreens.Settings;

public class PrivacyPolicy extends AppCompatActivity {
    private com.example.stockwise.databinding.ActivityPrivacyPolicyBinding bind; // declaring view binding


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        bind = com.example.stockwise.databinding.ActivityPrivacyPolicyBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot());
        bind.scrollView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        bind.backFromPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PrivacyPolicy.this, Settings.class));
                finish();
            }
        });

        bind.btnIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bind.ShowIntroduction.getVisibility()==View.GONE){

                    if(bind.ShowInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowUseInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowUpdates.getVisibility()==View.VISIBLE ||
                            bind.ShowSecurity.getVisibility()==View.VISIBLE)
                    {

                        bind.ShowSecurity.setVisibility(View.GONE);
                        bind.ShowInformation.setVisibility(View.GONE);
                        bind.ShowUseInformation.setVisibility(View.GONE);
                        bind.ShowUpdates.setVisibility(View.GONE);
                    }
                }

                if(bind.ShowIntroduction.getVisibility() == View.GONE) {
                    bind.ShowIntroduction.setVisibility(View.VISIBLE);
                } else {
                    bind.ShowIntroduction.setVisibility(View.GONE);
                }

            }
        });

        bind.btnInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bind.ShowInformation.getVisibility()==View.GONE){

                    if(bind.ShowIntroduction.getVisibility()==View.VISIBLE ||
                            bind.ShowUseInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowUpdates.getVisibility()==View.VISIBLE ||
                            bind.ShowSecurity.getVisibility()==View.VISIBLE)
                    {

                        bind.ShowSecurity.setVisibility(View.GONE);
                        bind.ShowIntroduction.setVisibility(View.GONE);
                        bind.ShowUseInformation.setVisibility(View.GONE);
                        bind.ShowUpdates.setVisibility(View.GONE);
                    }
                }

                if(bind.ShowInformation.getVisibility() == View.GONE) {
                    bind.ShowInformation.setVisibility(View.VISIBLE);
                } else {
                    bind.ShowInformation.setVisibility(View.GONE);
                }
            }
        });

        bind.btnUseInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bind.ShowUseInformation.getVisibility()==View.GONE){

                    if(bind.ShowInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowIntroduction.getVisibility()==View.VISIBLE ||
                            bind.ShowUpdates.getVisibility()==View.VISIBLE ||
                            bind.ShowSecurity.getVisibility()==View.VISIBLE)
                    {

                        bind.ShowSecurity.setVisibility(View.GONE);
                        bind.ShowInformation.setVisibility(View.GONE);
                        bind.ShowIntroduction.setVisibility(View.GONE);
                        bind.ShowUpdates.setVisibility(View.GONE);
                    }
                }
                if(bind.ShowUseInformation.getVisibility() == View.GONE) {
                    bind.ShowUseInformation.setVisibility(View.VISIBLE);
                } else {
                    bind.ShowUseInformation.setVisibility(View.GONE);
                }
            }
        });

        bind.btnSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bind.ShowSecurity.getVisibility()==View.GONE){

                    if(bind.ShowInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowUseInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowUpdates.getVisibility()==View.VISIBLE ||
                            bind.ShowIntroduction.getVisibility()==View.VISIBLE)
                    {

                        bind.ShowSecurity.setVisibility(View.GONE);
                        bind.ShowInformation.setVisibility(View.GONE);
                        bind.ShowUseInformation.setVisibility(View.GONE);
                        bind.ShowIntroduction.setVisibility(View.GONE);
                    }
                }
                if(bind.ShowSecurity.getVisibility() == View.GONE) {
                    bind.ShowSecurity.setVisibility(View.VISIBLE);
                } else {
                    bind.ShowSecurity.setVisibility(View.GONE);
                }
            }
        });

        bind.btnUpdatees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bind.ShowUpdates.getVisibility()==View.GONE){

                    if(bind.ShowInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowUseInformation.getVisibility()==View.VISIBLE ||
                            bind.ShowIntroduction.getVisibility()==View.VISIBLE ||
                            bind.ShowSecurity.getVisibility()==View.VISIBLE)
                    {

                        bind.ShowIntroduction.setVisibility(View.GONE);
                        bind.ShowInformation.setVisibility(View.GONE);
                        bind.ShowUseInformation.setVisibility(View.GONE);
                        bind.ShowSecurity.setVisibility(View.GONE);
                    }
                }
                if(bind.ShowUpdates.getVisibility() == View.GONE) {
                    bind.ShowUpdates.setVisibility(View.VISIBLE);
                } else {
                    bind.ShowUpdates.setVisibility(View.GONE);
                }
            }
        });


    }// End of onCreate
}