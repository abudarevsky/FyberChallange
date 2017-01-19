package com.abudarevskiy.fyberchallenge;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.abudarevskiy.fyberchallenge.model.FyberResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONException;

import java.util.List;

import volley.AppController;

public class Activity_Main extends Activity {
    private FyberResponse fyberResponse=null;
    private List<FyberResponse.Offer> offers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fyberResponse = (FyberResponse) this.getIntent().getSerializableExtra("response");
        if (fyberResponse != null) {
            try {
                offers = fyberResponse.getOffers();
                makeTable();
            } catch (FyberResponse.SponsorPayNoOffersException e) {
                e.printStackTrace();
            } catch (FyberResponse.SponsorPayBadResponseException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(),
                    "Response object error!", Toast.LENGTH_LONG).show();
        }
    }

    private void makeTable(){
        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        tableLayout.setColumnStretchable(1,true);
        tableLayout.setColumnStretchable(2,true);
        ImageLoader imgLoader = AppController.getInstance().getImageLoader();
        for (FyberResponse.Offer offer: offers) {

            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            tableRow.setMinimumHeight(200);

            final ImageView imageView = new ImageView(this);
            TextView view = null;
            try {
                imgLoader.get(offer.getJSONObject("thumbnail").getString("hires"), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        imageView.setImageBitmap(imageContainer.getBitmap());
                        imageView.setPadding(0,0,10,0);
                        imageView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                view = new TextView(this);
                view.setText("No hires data available!");
            } catch (FyberResponse.SponsorPayBadResponseException e) {
                e.printStackTrace();
                view = new TextView(this);
                view.setText("No tumbnail data available!");
            }
            tableRow.addView(view!=null?view:imageView);

            view = new TextView(this);
            try {
                String text = String.format("<h6>%s</h6><p>%s</p>",offer.getString("title"),offer.getString("teaser"));

                view.setText(Html.fromHtml(text));
            } catch (FyberResponse.SponsorPayBadResponseException e) {
                view.setText("No title available!");
            }
            view.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1f));

            tableRow.addView(view);

            view = new TextView(this);
            try {
                view.setText(offer.getString("payout")+" "+fyberResponse.getResponse().getJSONObject("information").getString("virtual_currency"));
            } catch (FyberResponse.SponsorPayBadResponseException e) {
                view.setText("No payout available!");
            } catch (JSONException oops){
                view.setText("No payout available!");
            }
            view.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.addView(view);

            tableLayout.addView(tableRow);
        }
    }


}