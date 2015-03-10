package com.rockyniu.stickers.activity;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rockyniu.stickers.R;
import com.rockyniu.stickers.database.LinkDataSource;
import com.rockyniu.stickers.model.Link;
import com.rockyniu.stickers.util.DialogHelper;
import com.rockyniu.stickers.util.ToastHelper;

import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditLinkActivity extends BaseActivity {

    private final String TAG = "EditLinkActivity";
//    private final int MAX_LENGTH = 140; // max length of name

    private LinkDataSource linkDataSource;
    private Link link;
    private String userId;
    private String linkId;
    private int linkType;
    private EditText titleEditText;
    private EditText addressEditText;
    private Button saveButton;
    private Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_link);
        linkDataSource = new LinkDataSource(this);
        titleEditText = (EditText) findViewById(R.id.edit_title_edittext);
        addressEditText = (EditText) findViewById(R.id.edit_address_edittext);
        saveButton = (Button) findViewById(R.id.edit_save_button);
        cancelButton = (Button) findViewById(R.id.edit_cancel_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveItem()) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    //TODO
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEditing();
            }
        });
        userId = ""; //TODO
        linkId = "";
        linkType = 0;

        Intent intent = getIntent();

        String type = intent.getType();
        Bundle bundle = intent.getExtras();
        if (type != null && type.equals("text/plain") && intent.getClipData() != null) {
            ClipData data = intent.getClipData();
            addressEditText.setText(data.getItemAt(0).getText());
        } else if (bundle != null) {
            if (bundle.keySet().contains(Intent.EXTRA_TEXT)) { // for HuaWei cell phone
                String content = bundle.getString(Intent.EXTRA_TEXT);
                String uri = grabUri(content);
                addressEditText.setText(uri);
            } else {
                userId = bundle.getString("com.rockyniu.stickers.userId");
                linkId = bundle.getString("com.rockyniu.stickers.linkId");
                linkType = bundle.getInt("com.rockyniu.stickers.linkType");
            }
        }

        titleEditText.requestFocus();

        link = new Link();
        if (linkId.equals("")) {
            setTitle(R.string.add_link);
        } else {
            this.setTitle(R.string.edit_link);
            link = linkDataSource.getItemById(linkId);
            if (link == null) {
                ToastHelper.showErrorToast(this, getResources().getString(R.string.link_does_not_exist));
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
            titleEditText.setText(link.getTitle());
            addressEditText.setText(link.getAddress());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_link, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_sendSmsMessage:
                if (saveItem()) {
                    String smsMessage = link.toSmsMessage();
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.putExtra("sms_body", smsMessage);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    startActivity(smsIntent);
                } else {
                    ToastHelper.showErrorToast(EditLinkActivity.this,
                            getResources().getString(R.string.fail_to_save_link));
                }
                return true;
            case R.id.menu_sendEmail:
                if (saveItem()) {
                    String emailContent = link.toSmsMessage();
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    String[] recipients = new String[]{"", "",};
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_title));
                    emailIntent.putExtra(Intent.EXTRA_TEXT, emailContent);
                    emailIntent.setType("text/plain");
                    startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.send_email)));
                } else {
                    ToastHelper.showErrorToast(EditLinkActivity.this,
                            getResources().getString(R.string.fail_to_save_link));
                }
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        cancelEditing();
        super.onBackPressed();
    }

    private String grabUri(String content) {
        String uri = "";
        Pattern pattern = Pattern.compile(
                "\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" +
                        "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" +
                        "|mil|biz|info|mobi|name|aero|jobs|museum" +
                        "|travel|[a-z]{2}))(:[\\d]{1,5})?" +
                        "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" +
                        "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
                        "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" +
                        "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
                        "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" +
                        "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            uri = matcher.group();
        }
        return uri;
    }

    private void cancelEditing() {
        ToastHelper.showToastInternal(EditLinkActivity.this, getResources().getString(R.string.cancel_editing));
        setResult(RESULT_CANCELED);
        finish();
    }

    private boolean saveItem() {
        String title = titleEditText.getText().toString().trim()
                .replaceAll("\\s+", " ");
        if (title.isEmpty()) {
            DialogHelper.showNeedClickDialog(EditLinkActivity.this, getResources().getString(R.string.title_is_empty), getResources().getString(R.string.please_fill_title));
            return false;
        }

        String address = addressEditText.getText().toString().trim()
                .replaceAll("\\s+", " ");
        if (address.isEmpty()) {
            DialogHelper.showNeedClickDialog(EditLinkActivity.this, getResources().getString(R.string.address_is_empty), getResources().getString(R.string.please_fill_title));
            return false;
        }
        try {
            if (linkId.equals("")) {
                // Add item
                addLink(title, address);
            } else {
                // Update item
                updateLink(title, address);
            }
            ToastHelper.showToastInternal(EditLinkActivity.this, getResources().getString(R.string.link_updated));
        } catch (Exception e) {
            String errorMessage = getResources().getString(R.string.fail_to_save_link);
            Log.e(TAG, errorMessage, e);
            ToastHelper.showErrorToast(EditLinkActivity.this, errorMessage);
            return false;
        }
        return true;
    }

    private void addLink(String title, String address) {
        linkId = UUID.randomUUID().toString();
        updateLink(title, address);
    }

    private void updateLink(String title, String address) {
        Link newLink = new Link();
        newLink.setId(linkId);
        newLink.setUserId(userId);
        newLink.setLinkType(linkType);
//        if (name.length() > MAX_LENGTH) {
//            name = name.substring(0, MAX_LENGTH);
//            ToastHelper.showToastInternal(this,
//                    "Name is truncated to 140 characters.");
//        }
        newLink.setTitle(title);
        newLink.setAddress(address);

        newLink.setModifiedTime(Calendar.getInstance().getTimeInMillis());
        linkDataSource.updateItem(newLink);
        link = newLink;
    }
}
