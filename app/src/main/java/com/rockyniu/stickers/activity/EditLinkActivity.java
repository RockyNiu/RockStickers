package com.rockyniu.stickers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.rockyniu.stickers.R;
import com.rockyniu.stickers.database.LinkDataSource;
import com.rockyniu.stickers.model.Link;
import com.rockyniu.stickers.util.DialogHelper;
import com.rockyniu.stickers.util.ToastHelper;

import java.util.Calendar;
import java.util.UUID;

public class EditLinkActivity extends BaseActivity {

    private final static int UPDATE_DONE = 0;
//    private final int MAX_LENGTH = 140; // max length of name

    private LinkDataSource linkDataSource;
    private Link link;
    private String userId;
    private String linkId;
    private int linkType;
    private EditText titleEditText;
    private EditText linkEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_link);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userId = bundle.getString("com.rockyniu.stickers.userId");
        linkId = bundle.getString("com.rockyniu.stickers.linkId");
        linkType = bundle.getInt("com.rockyniu.stickers.linkType");

        linkDataSource = new LinkDataSource(this);
        titleEditText = (EditText) findViewById(R.id.edit_title_edittext);
        linkEditText = (EditText) findViewById(R.id.edit_link_edittext);

        titleEditText.requestFocus();

        link = new Link();
        if (linkId.equals("")) {
            setTitle(R.string.add_link);
        } else {
            this.setTitle(R.string.edit_link);
            link = linkDataSource.getItemById(linkId);
            if (link == null) {
                ToastHelper.showErrorToast(this, "Link does not exits.");
                finish();
                this.setResult(RESULT_CANCELED);
                return;
            }
            titleEditText.setText(link.getText());
            linkEditText.setText(link.getLink());
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
                    ToastHelper.showToastInternal(EditLinkActivity.this,
                            "Error happened when saving task.");
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
                    ToastHelper.showToastInternal(this,
                            "Error happened when saving task.");
                }
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onSaveClick(View view) {
        if (saveItem()) {
            this.setResult(RESULT_OK);
            this.finish();
        }
    }

    public void onCancelClick(View view) {
        ToastHelper.showToastInternal(EditLinkActivity.this, "Cancel Editing.");
        this.setResult(RESULT_CANCELED);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        if (saveItem()) {
            this.setResult(RESULT_OK);
            this.finish();
            super.onBackPressed();
        }
    }

    private boolean saveItem() {
        String title = titleEditText.getText().toString().trim()
                .replaceAll("\\s+", " ");
        if (title.isEmpty()) {
            DialogHelper.showNeedClickDialog(EditLinkActivity.this, getResources().getString(R.string.title_is_empty), getResources().getString(R.string.please_fill_title));
            return false;
        }

        String link = linkEditText.getText().toString().trim()
                .replaceAll("\\s+", " ");
        if (link.isEmpty()) {
            DialogHelper.showNeedClickDialog(EditLinkActivity.this, getResources().getString(R.string.address_is_empty), getResources().getString(R.string.please_fill_title));
            return false;
        }
        int updateResult;
        if (linkId.equals("")) {
            // Add item
            updateResult = addItem(title, link);
        } else {
            // Update item
            updateResult = updateItem(title, link);
        }
        if (updateResult == UPDATE_DONE) {
            ToastHelper.showToastInternal(EditLinkActivity.this, getResources().getString(R.string.link_updated));
        } else {
            //TODO
        }
        return true;
    }

    private int addItem(String title, String link) {
        // create item and save into database
        linkId = UUID.randomUUID().toString();
        Link newLink = new Link();
        newLink.setId(linkId);
        newLink.setUserId(userId);
        newLink.setLinkType(linkType);
//        if (name.length() > MAX_LENGTH) {
//            name = name.substring(0, MAX_LENGTH);
//            ToastHelper.showToastInternal(this,
//                    "Name is truncated to 140 characters.");
//        }
        newLink.setText(title);
        newLink.setLink(link);

        newLink.setModifiedTime(Calendar.getInstance().getTimeInMillis());
        linkDataSource.insertItemWithId(newLink);
        this.link = newLink;
        return UPDATE_DONE;
    }

    private int updateItem(String title, String link) {
        Link newLink = new Link();
        newLink.setId(linkId);
        newLink.setUserId(userId);
        newLink.setLinkType(linkType);
//        if (name.length() > MAX_LENGTH) {
//            name = name.substring(0, MAX_LENGTH);
//            ToastHelper.showToastInternal(this,
//                    "Name is truncated to 140 characters.");
//        }
        newLink.setText(title);
        newLink.setLink(link);

        newLink.setModifiedTime(Calendar.getInstance().getTimeInMillis());
        linkDataSource.updateItem(newLink);
        this.link = newLink;
        return UPDATE_DONE;
    }
}
