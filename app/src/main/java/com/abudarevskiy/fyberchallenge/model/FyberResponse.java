package com.abudarevskiy.fyberchallenge.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import volley.Config_URL;

/*
Represents a SponsorPay response
Key moments:
 - Response is Json
 - Only few fields are needed for the task
 - It is good to proxy access to data having certain type like int or Object in order to get it from Json
 - Valid response is not only has right signature but also should have all required fields to process the response, e.g. Code, offers, etc
 */
public class FyberResponse implements Serializable{
    private static final String[] string_fields = {"title","teaser","offer_id","required_actions","link","offer_types","store_id"};
    private static final String[] numeric_fields = {"payout","amount"};
    private static final String[] object_fields ={"thumbnail","time_to_payout"};

    private static List stringTypes, numericTypes, objectTypes;

    static {
        stringTypes = Arrays.asList(string_fields);
        numericTypes = Arrays.asList(numeric_fields);
        objectTypes = Arrays.asList(object_fields);
    }
    /**
     * "title": "Betmaster",
     "offer_id": 1077771,
     "teaser": "Зарегистрироваться, внести первый депозит (350 руб.), сделать первую ставку",
     "required_actions": "Зарегистрироваться, внести первый депозит (350 руб.), сделать первую ставку",
     "link": "http://offer.fyber.com/mobile?impression=true&appid=2070&uid=spiderman&client=api&platform=android&appname=VC+Backend+Test&traffic_source=offer_api&country_code=RU&pubid=249&ip=93.100.22.7&pub0=campaign2&ad_id=1077771&os_version=6.0&ad_format=offer&group=Fyber&sig=1412141da19b1bb156b25e466eb2d5810b41938b",
     "offer_types": [
     {
     "offer_type_id": 103,
     "readable": "Shopping"
     },
     {
     "offer_type_id": 106,
     "readable": "Games"
     },
     {
     "offer_type_id": 109,
     "readable": "Games"
     }
     ],
     "payout": 2146,
     "time_to_payout": {
     "amount": 43800,
     "readable": "12 hours"
     },
     "thumbnail": {
     "lowres": "http://cdn4.sponsorpay.com/assets/65667/bm-icon-120x120_square_60.png",
     "hires": "http://cdn4.sponsorpay.com/assets/65667/bm-icon-120x120_square_175.png"
     },
     "store_id": "com.socialgaming.betmaster"
     */

    public class SponsorPayBadResponseException extends Exception {
        public SponsorPayBadResponseException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public SponsorPayBadResponseException(String detailMessage) {
            super(detailMessage);
        }

        public SponsorPayBadResponseException(Throwable throwable) {
            super(throwable);
        }
    }

    public class SponsorPayNoOffersException extends Exception {
        public SponsorPayNoOffersException() {
            super();
        }
    }

    public class Offer{
        JSONObject data;

        public Offer(JSONObject data) {
            this.data = data;

        }

        public String getString(String aKey) throws SponsorPayBadResponseException {
            assert stringTypes.contains(aKey) || numericTypes.contains(aKey);

            try {
                return data.getString(aKey);
            } catch (JSONException e) {
                throw new SponsorPayBadResponseException(aKey, e);
            }
        }

        public int getInt(String aKey) throws SponsorPayBadResponseException {
            assert numericTypes.contains(aKey);

            try {
                return data.getInt(aKey);
            } catch (JSONException e) {
                throw new SponsorPayBadResponseException(aKey, e);
            }
        }

        public JSONObject getJSONObject(String aKey) throws SponsorPayBadResponseException {
            assert objectTypes.contains(aKey);

            try {
                return data.getJSONObject(aKey);
            } catch (JSONException e) {
                throw new SponsorPayBadResponseException(aKey, e);
            }
        }
    }


    private transient List<FyberResponse.Offer> offers = null;
    private transient JSONObject jsonResponse=null;
    private Map<String,String> headers = null;
    private String response = null;
    private FyberRequest fyberRequest=null;

    private String [] mandatoryAttributes={"code","message","count","offers"};

    public FyberResponse( String response, Map headers) throws SponsorPayBadResponseException{
        this.response = response;
        this.headers = headers;
        validateJson();
    }

    /**
     * Check if response has all mandatory attributes
     * @throws SponsorPayBadResponseException thrown when at least one of mandatory is missing
     */
    protected void validateJson() throws SponsorPayBadResponseException{
        if(jsonResponse!= null) return;
        try{
            this.jsonResponse = new JSONObject(response);
            for(String attrName: Arrays.asList(mandatoryAttributes)){
                if(!jsonResponse.has(attrName)) {
                    throw new SponsorPayBadResponseException("Attribute is missing: "+attrName);
                }
            }
        }catch (JSONException oops){
            throw new SponsorPayBadResponseException(oops.getMessage(), oops);
        }
    }

    /**
     * Returns true if response has signature matching with request (apikey) and has code=OK
     * @param aFyberRequest request object
     * @return
     */
    public boolean checkReponseMatchWithRequest(FyberRequest aFyberRequest){
        assert headers != null;
        this.fyberRequest = aFyberRequest;
        boolean responseFromSponsorPay = headers.containsKey("X-Sponsorpay-Response-Signature");
        boolean checkSignature=false;
        if(responseFromSponsorPay) {
            String signatureHeader = headers.get("X-Sponsorpay-Response-Signature");
            String apikey = aFyberRequest.getApiKey();
            final StringBuilder builder = new StringBuilder(response).append(apikey);
            final String sha1FromString = Config_URL.getSha1Hex(builder.toString());
            checkSignature = signatureHeader.equals(sha1FromString);
        }

        boolean responseIsOK = jsonResponse.optString("code","NOK").equals("OK");
        return responseFromSponsorPay && checkSignature && responseIsOK;
    }


    /**
     * Json response getter
     * @return JSONObject
     * @throws SponsorPayBadResponseException
     */
    public JSONObject getResponse() throws SponsorPayBadResponseException{
        validateJson();
        return jsonResponse;
    }

    /**
     * Gets list of offers wrapped as FyberResponse.Offer type
     * @return List
     * @throws SponsorPayBadResponseException when response was not properly parsed
     * @throws SponsorPayNoOffersException when response has no offers
     */
    public List<FyberResponse.Offer> getOffers() throws SponsorPayBadResponseException, SponsorPayNoOffersException{
        if(offers == null)
            updateOffers();
        return offers;
    }

    /**
     * Parse offers structure in response
     * @throws SponsorPayBadResponseException
     * @throws SponsorPayNoOffersException
     */
    protected void updateOffers() throws SponsorPayBadResponseException, SponsorPayNoOffersException{
        try {
            JSONArray arr = getResponse().getJSONArray("offers");

            if(arr==null || arr.length()==0)
                throw new SponsorPayNoOffersException();

            offers = new ArrayList(arr.length());
            for(int j=0;j<arr.length();j++){
                offers.add(new Offer(arr.getJSONObject(j)));
            }
        } catch (JSONException e) {
            throw new SponsorPayBadResponseException(e);
        }
    }
}
