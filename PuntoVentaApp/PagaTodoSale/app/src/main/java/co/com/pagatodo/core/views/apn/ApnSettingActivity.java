package co.com.pagatodo.core.views.apn;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wizarpos.wizarviewagentassistant.aidl.IAPNManagerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.com.pagatodo.core.R;
import co.com.pagatodo.core.util.SharedPreferencesUtil;
import co.com.pagatodo.core.views.base.BaseActivity;


public class ApnSettingActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    private MaterialSpinner spinner_apn;
    private String[] strAPN;
    private List<String> listAPN;
    private ArrayAdapter<String> comboAdapter;
    private String apn_seleccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apn_setting);
        updateTitle(getString(R.string.apn_title));
        setupBaseView(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listAPN = new ArrayList<>();
        spinner_apn = findViewById(R.id.spinnerAPN);
        strAPN = new String[]{getString(R.string.apn_claro), getString(R.string.apn_movistar), getString(R.string.apn_tigo)};
        Collections.addAll(listAPN, strAPN);

        comboAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listAPN);
        spinner_apn.setAdapter(comboAdapter);

        for (int x = 0; x < strAPN.length; x++) {

            if (strAPN[x].equals(SharedPreferencesUtil.Companion.getSharedPreferenceObject().getString(getString(R.string.apn), ""))) {
                spinner_apn.setSelectedIndex(x);
            }

        }

        actionevents();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinnerAPN:

                apn_seleccion = listAPN.get(position);

                Toast.makeText(this, "Apn Seleccionado: " + apn_seleccion, Toast.LENGTH_SHORT).show();
                bindApn();

                break;
            default:

                break;

        }
    }

    private void actionevents() {

        spinner_apn.setOnItemSelectedListener((view, position, id, item) -> {

            apn_seleccion = listAPN.get(position);

            Toast.makeText(ApnSettingActivity.this, "Apn Seleccionado: " + apn_seleccion, Toast.LENGTH_SHORT).show();
            bindApn();

        });


    }

    public void bindApn() {

        SharedPreferencesUtil.Companion.savePreference(getString(R.string.apn), apn_seleccion);
        ServiceConnection serviceConnectionAPN1 = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                // not implemented
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                IAPNManagerService apnManagerService = IAPNManagerService.Stub.asInterface(service);

                try {
                    apnManagerService.addByAllArgs(apn_seleccion, apn_seleccion, "732", "101", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
                    apnManagerService.setSelected(apn_seleccion);

                    Log.d("IAPNManagerService", "IAPNManagerService  bind success.");
                } catch (Exception e) {
                    // not implemented
                } finally {
                    unbindService(this);
                }

            }
        };
        startConnectService(this, new ComponentName("com.wizarpos.wizarviewagentassistant", "com.wizarpos.wizarviewagentassistant.APNManagerService"), serviceConnectionAPN1);


    }

    private void startConnectService(Context context, ComponentName comp, ServiceConnection connection) {
        try {
            Intent intent = new Intent();
            intent.setPackage(comp.getPackageName());
            intent.setComponent(comp);
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            // not implemented
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // not implemented
    }
}
