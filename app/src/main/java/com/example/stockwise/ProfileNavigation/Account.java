package com.example.stockwise.ProfileNavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.stockwise.MenuScreens.Settings;
import com.example.stockwise.R;
import com.example.stockwise.databinding.ActivityAccountBinding;
import com.example.stockwise.databinding.ActivitySettingsBinding;
import com.example.stockwise.databinding.FragmentProfileBinding;
import com.example.stockwise.fragments.profile.ProfileFragment;

public class Account extends AppCompatActivity {

    private ActivityAccountBinding bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        bind = ActivityAccountBinding.inflate(getLayoutInflater()); // initializing view binding
        setContentView(bind.getRoot());

        bind.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Account.this, ProfileFragment.class));
            }
        });

        bind.AccountPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        // Initializing the popup menu and giving the reference as current context
                        PopupMenu popupMenu = new PopupMenu(Account.this, bind.AccountPopUp);

                        // Inflating popup menu from popup_menu.xml file
                        popupMenu.getMenuInflater().inflate(R.menu.account_edit_popup, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                int item = menuItem.getItemId();

                                if(item==R.id.PopUpEdit)
                                {
                                    startActivity(new Intent(Account.this, Account.class));
                                    return true;
                                }
                                else if(item==R.id.PopUpSettings)
                                {
                                    startActivity(new Intent(Account.this, Settings.class));
                                    return true;
                                }
                                return false;
                            }
                        });
                        // Showing the popup menu
                        popupMenu.show();

            }
        });
    }
}