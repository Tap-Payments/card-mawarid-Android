package company.tap.tapcardsdk.internal.logic.api.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


import company.tap.tapcardsdk.internal.logic.api.responses.Merchant;
import company.tap.tapcardsdk.internal.logic.interfaces.BaseResponse;
import company.tap.tapcardsdk.internal.logic.api.enums.ChargeStatus;


//@RestrictTo(RestrictTo.Scope.LIBRARY)
public class Charge implements BaseResponse, Serializable {
    @SerializedName("id")
    @Expose
     String id;

    @SerializedName("merchant")
    @Expose
    Merchant merchant;

    @SerializedName("amount")
    @Expose
     double amount;

    @SerializedName("currency")
    @Expose
    private String currency;


    @SerializedName("selected_amount")
    @Expose
    private double selectedAmount;

    @SerializedName("selected_currency")
    @Expose
    private String selectedCurrency;


    @SerializedName("customer")
    @Expose
    private TapCustomer customer;

    @SerializedName("live_mode")
    @Expose
    private boolean liveMode;

    @SerializedName("object")
    @Expose
    private String object;

    @SerializedName("authenticate")
    @Expose
    @Nullable private Authenticate authenticate;

    @SerializedName("redirect")
    @Expose
    private TrackingURL redirect;

    @SerializedName("post")
    @Expose
    @Nullable private TrackingURL post;

    @SerializedName("source")
    @Expose
    private Source source;

    @SerializedName("status")
    @Expose
    private ChargeStatus status;

    @SerializedName("threeDSecure")
    @Expose
    private boolean threeDSecure;

    @SerializedName("transaction")
    @Expose
    TransactionDetails transaction;

    @SerializedName("description")
    @Expose
    @Nullable private String description;

    @SerializedName("metadata")
    @Expose
    @Nullable private HashMap<String, String> metadata;

    @SerializedName("reference")
    @Expose
    @Nullable private Reference reference;

    @SerializedName("receipt")
    @Expose
    @Nullable private Receipt receipt;

    @SerializedName("response")
    @Expose
    @Nullable private Response response;

    @SerializedName("statement_descriptor")
    @Expose
    @Nullable private String statementDescriptor;

    @SerializedName("destinations")
    @Expose
    @Nullable private Destinations destinations;


    @SerializedName("card")
    @Expose
    @Nullable private Card card;

    @SerializedName("acquirer")
    @Expose
    @Nullable private Acquirer acquirer;


    @SerializedName("expiry")
    @Expose
    @Nullable private Expiry expiry;

    @SerializedName("topup")
    @Expose
    @Nullable private TopUp topUp;

    @SerializedName("api_version")
    @Expose
    @Nullable private String apiVersion;

    @SerializedName("card_threeDSecure")
    @Expose
    private boolean cardThreeDSecure;


    @SerializedName("save_card")
    @Expose
    private boolean saveCard;

    @SerializedName("merchant_id")
    @Expose
    @Nullable private String merchantId;

    @SerializedName("product")
    @Expose
    @Nullable private String product;

    @SerializedName("gateway")
    @Expose
    @Nullable private GatewayResponse gatewayResponse;

    @SerializedName("activities")
    @Expose
    @Nullable private ArrayList<Activities>  activities;

    @SerializedName("auto_reversed")
    @Expose
    @Nullable private String autoReversed;




    @Nullable
    public Card getCard() {
        return card;
    }

    @Nullable
    public Acquirer getAcquirer() {
        return acquirer;
    }

    @Nullable
    public Expiry getExpiry() {
        return expiry;
    }


    /**
     * Gets id.
     *
     * @return Charge identifier.
     */
    public String getId() {
        return id;
    }

    /**
     *  get merchant id
     * @return merchant object
     */
    public Merchant getMerchant() {
        return merchant;
    }

    /**
     * Gets user selected amount.
     *
     * @return Charge user selected amount.
     */
    public double getSelectedAmount() {
        return selectedAmount;
    }

    /**
     * Gets user selected currency.
     *
     * @return user selected  currency.
     */
    public String getSelectedCurrency() {
        return selectedCurrency;
    }

    /**
     * Gets amount.
     *
     * @return Charge amount.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Gets currency.
     *
     * @return Transaction currency.
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Gets customer.
     *
     * @return Customer information.
     */
    public TapCustomer getCustomer() {
        return customer;
    }

    /**
     * Is live mode boolean.
     *
     * @return Defines whether the charge is in real environment.
     */
    public boolean isLiveMode() {
        return liveMode;
    }

    /**
     * Gets object.
     *
     * @return Object type. For charge, “charge” always.
     */
    public String getObject() {
        return object;
    }

    /**
     * Gets authenticate.
     *
     * @return Required authentication options (if any).
     */
    public Authenticate getAuthenticate() {
        return authenticate;
    }

    /**
     * Gets redirect.
     *
     * @return Charge redirect.
     */
    public TrackingURL getRedirect() {
        return redirect;
    }

    /**
     * Gets post.
     *
     * @return the post
     */
    public @Nullable TrackingURL getPost() {

        return post;
    }

    /**
     * Gets source.
     *
     * @return Charge source
     */
    public Source getSource() {
        return source;
    }

    /**
     * Gets status.
     *
     * @return Charge status.
     */
    public ChargeStatus getStatus() {
        return status;
    }

    /**
     * Is three d secure boolean.
     *
     * @return Defines if 3D secure is required
     */
    public boolean isThreeDSecure() {
        return threeDSecure;
    }

    /**
     * Gets transaction.
     *
     * @return Transaction details.
     */
    public TransactionDetails getTransaction() {
        return transaction;
    }

    /**
     * Gets description.
     *
     * @return Charge description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets metadata.
     *
     * @return Charge metadata.
     */
    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    /**
     * Gets reference.
     *
     * @return Merchant reference object.
     */
    public Reference getReference() {
        return reference;
    }

    /**
     * Gets receipt.
     *
     * @return Receipt settings.
     */
    public Receipt getReceipt() {
        return receipt;
    }

    /**
     * Gets response.
     *
     * @return Charge response code and message.
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Gets statement descriptor.
     *
     * @return Statement descriptor.
     */
    public String getStatementDescriptor() {
        return statementDescriptor;
    }

    /**
     * Gets destinations
     * @return
     */
    @Nullable
    public Destinations getDestinations() {
        return destinations;
    }


    @Nullable
    public TopUp getTopUp() {
        return topUp;
    }

    @Nullable
    public String getApiVersion() {
        return apiVersion;
    }

    public boolean isCardThreeDSecure() {
        return cardThreeDSecure;
    }

    public boolean isSaveCard() {
        return saveCard;
    }

    @Nullable
    public String getMerchantId() {
        return merchantId;
    }

    @Nullable
    public String getProduct() {
        return product;
    }

    @Nullable
    public GatewayResponse getGatewayResponse() {
        return gatewayResponse;
    }

    @Nullable
    public ArrayList<Activities> getActivities() {
        return activities;
    }


    @Nullable
    public String getAutoReversed() {
        return autoReversed;
    }
}
