package com.signity.bonbon.ui.shopping;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;

public class ShoppingListActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        Fragment fragment = ShoppingList.newInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("check", "check");
        fragment.setArguments(bundle);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(ShoppingListActivity.this);
    }
}
