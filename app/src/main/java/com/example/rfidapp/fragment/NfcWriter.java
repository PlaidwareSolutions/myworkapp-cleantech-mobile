package com.example.rfidapp.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rfidapp.R;
import com.example.rfidapp.activity.MainActivity;
import com.example.rfidapp.adapter.NfcReaderAdapter;
import com.example.rfidapp.databinding.FragmentNfcWriterBinding;
import com.example.rfidapp.model.NfcReadModel;
import com.example.rfidapp.nfc_docs.NFCManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class NfcWriter extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FragmentNfcWriterBinding binding;
    LinearLayout complete;
    String dataType = "Text Record";
    String dataValue = "";
    ArrayList<NfcReadModel> list = new ArrayList<>();
    MainActivity mContext;
    Tag mCurrentTag;
    Dialog mDialog;
    NFCManager mNfcManager;
    NdefMessage mNfcMessage;
    private String mParam1;
    private String mParam2;
    NfcAdapter nfcAdapter;
    LinearLayout nfcGif;
    List<NdefRecord> nfrec;
    String selectType = "";

    public static NfcWriter newInstance(String str, String str2) {
        NfcWriter nfcWriter = new NfcWriter();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        nfcWriter.setArguments(bundle);
        return nfcWriter;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = FragmentNfcWriterBinding.inflate(layoutInflater, viewGroup, false);
        this.mContext = (MainActivity) getActivity();
        return this.binding.getRoot();
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        init();
    }

    public void init() {
        this.mNfcManager = new NFCManager(this.mContext);
        this.nfrec = new ArrayList();
        this.binding.llAddRecord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NfcWriter.this.openRecordBotmSheet();
            }
        });
        this.binding.llWriteRecord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (NfcWriter.this.list.size() > 0) {
                    NfcWriter.this.onWrite();
                }
            }
        });
    }

    public void onResume() {
        super.onResume();
        this.mContext.fragment = this;
        if (this.mContext.nfcAdapter != null) {
            if (!this.mContext.nfcAdapter.isEnabled()) {
                showWirelessSettings();
            }
            NfcAdapter nfcAdapter2 = this.mContext.nfcAdapter;
            MainActivity mainActivity = this.mContext;
            nfcAdapter2.enableForegroundDispatch(mainActivity, mainActivity.pendingIntent, (IntentFilter[]) null, (String[][]) null);
        }
        try {
            this.mNfcManager.verifyNFC();
            new Intent(this.mContext, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            String[][] strArr = {new String[]{Ndef.class.getName()}, new String[]{NdefFormatable.class.getName()}};
            NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this.mContext);
            MainActivity mainActivity2 = this.mContext;
            defaultAdapter.enableForegroundDispatch(mainActivity2, mainActivity2.pendingIntent, new IntentFilter[0], strArr);
        } catch (NFCManager.NFCNotSupported unused) {
            Toast.makeText(this.mContext, "NFC not supported", Toast.LENGTH_SHORT).show();
        } catch (NFCManager.NFCNotEnabled unused2) {
            Toast.makeText(this.mContext, "NFC Not enable", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    public void onPause() {
        super.onPause();
        this.mNfcManager.disableDispatch();
    }

    private void showWirelessSettings() {
        Toast.makeText(this.mContext, "You need to enable NFC", android.widget.Toast.LENGTH_SHORT).show();
        startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
    }

    public void writtingTag(Intent intent) {
        if (this.mContext.isReady) {
            Tag tag = (Tag) intent.getParcelableExtra("android.nfc.extra.TAG");
            this.mCurrentTag = tag;
            NdefMessage ndefMessage = this.mNfcMessage;
            if (ndefMessage != null) {
                this.mNfcManager.writeTag(tag, ndefMessage);
                this.nfcGif.setVisibility(android.view.View.GONE);
                this.mContext.isReady = false;
                this.complete.setVisibility(android.view.View.VISIBLE);
                return;
            }
            return;
        }
        Toast.makeText(this.mContext, "Select write button before approaching NFC Tag.", Toast.LENGTH_SHORT).show();
    }

    public void onWrite() {
        Log.e("as_con", "dear start");
        this.mNfcMessage = new NdefMessage(setMyRecords(this.nfrec));
        Log.e("as_con", "dear last");
        if (this.mNfcMessage != null) {
            this.mContext.isReady = true;
            Dialog dialog = new Dialog(this.mContext);
            this.mDialog = dialog;
            dialog.setContentView(R.layout.dialog_nfc_process);
            this.mDialog.getWindow().setLayout(-1, -2);
            this.mDialog.setCancelable(false);
            this.nfcGif = (LinearLayout) this.mDialog.findViewById(R.id.nfc_gif);
            this.complete = (LinearLayout) this.mDialog.findViewById(R.id.complete);
            ((Button) this.mDialog.findViewById(R.id.dl_cancel)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    NfcWriter.this.mDialog.dismiss();
                }
            });
            ((Button) this.mDialog.findViewById(R.id.dl_ok)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    NfcWriter.this.mDialog.dismiss();
                }
            });
            this.mDialog.show();
        }
    }

    public NdefRecord[] setMyRecords(List<NdefRecord> list2) {
        Log.e("as_con", "dear meth");
        NdefRecord[] ndefRecordArr = new NdefRecord[list2.size()];
        Log.e("as_con", "dear meth 2");
        for (int i = 0; i < list2.size(); i++) {
            Log.e("as_con", "dear for " + i);
            try {
                ndefRecordArr[i] = list2.get(i);
            } catch (Exception e) {
                Log.e("as_con", "dear for excep" + e);
            }
        }
        return ndefRecordArr;
    }

    public void openRecordBotmSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(LayoutInflater.from(this.mContext).inflate(R.layout.bottom_sheet_record, (ViewGroup) null));
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.show();
        LinearLayout linearLayout = (LinearLayout) bottomSheetDialog.findViewById(R.id.tv_add);
        LinearLayout linearLayout2 = (LinearLayout) bottomSheetDialog.findViewById(R.id.tv_back);
        LinearLayout linearLayout3 = (LinearLayout) bottomSheetDialog.findViewById(R.id.ll_buttons);
        LinearLayout linearLayout4 = (LinearLayout) bottomSheetDialog.findViewById(R.id.ll_choose_record);
        LinearLayout linearLayout5 = (LinearLayout) bottomSheetDialog.findViewById(R.id.ll_text_record);
        LinearLayout linearLayout6 = (LinearLayout) bottomSheetDialog.findViewById(R.id.ll_uri_record);
        Spinner spinner = (Spinner) bottomSheetDialog.findViewById(R.id.sp_uri_type);
        ArrayAdapter<CharSequence> createFromResource = ArrayAdapter.createFromResource(requireContext(), R.array.uri_type, R.layout.support_simple_spinner_dropdown_item);
        createFromResource.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(createFromResource);
        final LinearLayout linearLayout7 = linearLayout4;
        final LinearLayout linearLayout8 = linearLayout6;
        EditText editText = (EditText) bottomSheetDialog.findViewById(R.id.et_uri_record);
        final LinearLayout linearLayout9 = linearLayout5;
        Spinner spinner2 = spinner;
        final LinearLayout linearLayout10 = linearLayout3;
        EditText editText2 = (EditText) bottomSheetDialog.findViewById(R.id.et_text_record);
        LinearLayout linearLayout11 = (LinearLayout) bottomSheetDialog.findViewById(R.id.ll_op_uri);
        final LinearLayout linearLayout12 = linearLayout2;
        LinearLayout linearLayout13 = (LinearLayout) bottomSheetDialog.findViewById(R.id.tv_close);
        LinearLayout linearLayout14 = (LinearLayout) bottomSheetDialog.findViewById(R.id.ll_op_text);
        final LinearLayout linearLayout15 = linearLayout;
        linearLayout14.setOnClickListener(view -> {
            NfcWriter.this.selectType = "text";
            linearLayout7.setVisibility(View.GONE);
            linearLayout8.setVisibility(View.GONE);
            linearLayout9.setVisibility(View.VISIBLE);
            linearLayout10.setVisibility(View.VISIBLE);
            linearLayout12.setVisibility(View.VISIBLE);
            linearLayout15.setVisibility(View.VISIBLE);
        });
        final LinearLayout linearLayout16 = linearLayout5;
        final LinearLayout linearLayout17 = linearLayout6;
        linearLayout11.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NfcWriter.this.selectType = "uri";
                linearLayout7.setVisibility(View.GONE);
                linearLayout16.setVisibility(android.view.View.GONE);
                linearLayout17.setVisibility(android.view.View.VISIBLE);
                linearLayout10.setVisibility(View.VISIBLE);
                linearLayout12.setVisibility(android.view.View.VISIBLE);
                linearLayout15.setVisibility(android.view.View.VISIBLE);
            }
        });
        linearLayout13.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });
        final EditText editText3 = editText2;
        final EditText editText4 = editText;
        final LinearLayout linearLayout18 = linearLayout5;
        final LinearLayout linearLayout19 = linearLayout6;
        final LinearLayout linearLayout20 = linearLayout3;
        final LinearLayout linearLayout21 = linearLayout2;
        final LinearLayout linearLayout22 = linearLayout;
        final LinearLayout linearLayout23 = linearLayout4;
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                editText3.setBackgroundResource(R.drawable.et_style);
                editText4.setBackgroundResource(R.drawable.et_style);
                linearLayout18.setVisibility(View.GONE);
                linearLayout19.setVisibility(android.view.View.GONE);
                linearLayout20.setVisibility(android.view.View.GONE);
                linearLayout21.setVisibility(android.view.View.GONE);
                linearLayout22.setVisibility(android.view.View.GONE);
                linearLayout23.setVisibility(View.VISIBLE);
            }
        });
        editText3.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (editText3.getText().toString().trim().length() > 0) {
                    editText3.setBackgroundResource(R.drawable.et_style);
                }
            }
        });
        final EditText editText5 = editText;
        editText5.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (editText5.getText().toString().trim().length() > 0) {
                    editText5.setBackgroundResource(R.drawable.et_style);
                }
            }
        });
        final BottomSheetDialog bottomSheetDialog2 = bottomSheetDialog;
        final Spinner spinner3 = spinner2;
        linearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String str = NfcWriter.this.selectType;
                str.hashCode();
                if (str.equals("uri")) {
                    String trim = editText5.getText().toString().trim();
                    if (trim.length() > 0) {
                        NfcWriter.this.dataType = "Uri Record";
                        NfcWriter.this.dataValue = spinner3.getSelectedItem() + trim;
                        NfcWriter.this.nfrec.add(NdefRecord.createUri(NfcWriter.this.dataValue));
                        NfcWriter.this.list.add(new NfcReadModel("Uri Record", NfcWriter.this.dataValue));
                        NfcWriter.this.binding.rvAddRecord.setLayoutManager(new LinearLayoutManager(NfcWriter.this.mContext));
                        NfcWriter.this.binding.rvAddRecord.setAdapter(new NfcReaderAdapter(NfcWriter.this.mContext, NfcWriter.this.list));
                        if (NfcWriter.this.list.size() > 0) {
                            NfcWriter.this.binding.llWriteRecord.setVisibility(View.VISIBLE);
                        } else {
                            NfcWriter.this.binding.llWriteRecord.setVisibility(android.view.View.GONE);
                        }
                        bottomSheetDialog2.cancel();
                        return;
                    }
                    editText5.setBackgroundResource(R.drawable.et_style_red);
                    editText5.requestFocus();
                    Toast.makeText(NfcWriter.this.mContext, "Please add a record...", android.widget.Toast.LENGTH_SHORT).show();
                } else if (str.equals("text")) {
                    String trim2 = editText3.getText().toString().trim();
                    if (trim2.length() > 0) {
                        NfcWriter.this.dataType = "Text Record";
                        NfcWriter.this.dataValue = trim2;
                        NfcWriter.this.nfrec.add(NdefRecord.createTextRecord("en", trim2));
                        NfcWriter.this.list.add(new NfcReadModel("Text Record", trim2));
                        NfcWriter.this.binding.rvAddRecord.setLayoutManager(new LinearLayoutManager(NfcWriter.this.mContext));
                        if (NfcWriter.this.list.size() > 0) {
                            NfcWriter.this.binding.llWriteRecord.setVisibility(android.view.View.VISIBLE);
                        } else {
                            NfcWriter.this.binding.llWriteRecord.setVisibility(android.view.View.GONE);
                        }
                        NfcWriter.this.binding.rvAddRecord.setAdapter(new NfcReaderAdapter(NfcWriter.this.mContext, NfcWriter.this.list));
                        bottomSheetDialog2.cancel();
                        return;
                    }
                    editText3.setBackgroundResource(R.drawable.et_style_red);
                    editText3.requestFocus();
                    Toast.makeText(NfcWriter.this.mContext, "Please add a record...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
