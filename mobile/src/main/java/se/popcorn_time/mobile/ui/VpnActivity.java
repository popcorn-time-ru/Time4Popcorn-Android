package se.popcorn_time.mobile.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import se.popcorn_time.base.api.AppApi;
import se.popcorn_time.model.messaging.IMessagingUseCase;
import se.popcorn_time.model.messaging.IMessagingData;
import se.popcorn_time.mobile.PopcornApplication;
import se.popcorn_time.mobile.R;
import se.popcorn_time.mobile.ui.dialog.FirebaseMessagingDialog;

public final class VpnActivity extends UpdateActivity implements IMessagingUseCase.Observer {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_vpn);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.vpn);
        ((PopcornApplication) getApplication()).getVpnUseCase().clearVpnClients();
        AppApi.getVpnStatus(VpnActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((PopcornApplication) getApplication()).getMessagingUseCase().subscribe(VpnActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((PopcornApplication) getApplication()).getMessagingUseCase().unsubscribe(VpnActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onShowMessagingDialog(@NonNull IMessagingData data) {
        FirebaseMessagingDialog.show(getSupportFragmentManager(), data, "firebase_messaging_dialog");
    }
}
