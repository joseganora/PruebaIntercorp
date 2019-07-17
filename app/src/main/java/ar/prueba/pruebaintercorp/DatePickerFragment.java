package ar.prueba.pruebaintercorp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;
    private boolean soloFechaFutura=false;
    private boolean soloFechaPasada=false;
    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setListener(listener);

        return fragment;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dD=new DatePickerDialog(getActivity(), listener, year, month, day);
        if(soloFechaFutura)dD.getDatePicker().setMinDate(new Date().getTime());
        else if(soloFechaPasada)dD.getDatePicker().setMaxDate(new Date().getTime());
        return dD;
    }

    public boolean isSoloFechaFutura() {
        return soloFechaFutura;
    }

    public void setSoloFechaFutura(boolean soloFechaFutura) {
        this.soloFechaFutura = soloFechaFutura;
    }

    public boolean isSoloFechaPasada() {
        return soloFechaPasada;
    }

    public void setSoloFechaPasada(boolean soloFechaPasada) {
        this.soloFechaPasada = soloFechaPasada;
    }
}
