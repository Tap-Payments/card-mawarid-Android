# card-mawarid-Android
[](https://github.com/Tap-Payments/card-mawarid-Android)
The Tap Card Android SDK makes it quick and easy to build an excellent payment experience in your Android app. We provide powerful and customizable UI screens and elements that can be used out-of-the-box to collect your users' payment details. We also expose the low-level APIs that power those UIs so that you can build fully custom experiences.

Learn about our [Tap Identity Authentication](https://tappayments.api-docs.io/2.0/authentication) to verify the identity of your users on Android.

Get started with our [documentation guide](https://www.tap.company/eg/en/developers) and [example projects](https://github.com/Tap-Payments/card-mawarid-Android/tree/main/app.)

Table of contents
- [Features](https://github.com/Tap-Payments/card-mawarid-Android#features)
- [Native UI](https://github.com/Tap-Payments/card-mawarid-Android#native-ui)
- [Installation](https://github.com/Tap-Payments/card-mawarid-Android#installation)
- [Data Configuration](https://github.com/Tap-Payments/card-mawarid-Android#data-configuration)
- [Single line code initilization](https://github.com/Tap-Payments/card-mawarid-Android#single-line-initialzation)
- [Optional Configurations](https://github.com/Tap-Payments/card-mawarid-Android#optional-configurations)
- [TapCardInputDelegate](https://github.com/Tap-Payments/card-mawarid-Android#tapCardInputDelegate)
- [Tokenization](https://github.com/Tap-Payments/card-mawarid-Android#tokenization)

## [](https://github.com/Tap-Payments/card-mawarid-Android#features)Features

**Simplified security**: We make it simple for you to collect sensitive data such as credit card numbers and remain PCI compliant. This means the sensitive data is sent directly to Tap instead of passing through your server.

- Drag and drop UI for card form collection.
- Ability to add any localisation needed, supports EN/AR localisation by default.

### [](https://github.com/Tap-Payments/card-mawarid-Android#NativeUI)Native UI

We provide native screens and elements to collect card payment details. Our card element is a prebuilt UI that combines all the steps required to collecting, validating, tokenizing and saving a card details - into a single view that displays within your UI flow.

<p align="center">
</p>

### [](https://github.com/Tap-Payments/card-mawarid-Android#Installation)Installation

### Include goSellSDK library as a dependency module in your project
---
1. Clone checkoutSDK library from Tap repository
   ```
       https://github.com/Tap-Payments/card-mawarid-Android.git
    ```
2. Add goSellSDK library to your project settings.gradle file as following
    ```java
        include ':library', ':YourAppName'
    ```
3. Setup your project to include checkout as a dependency Module.
1. File -> Project Structure -> Modules -> << your project name >>
2. Dependencies -> click on **+** icon in the screen bottom -> add Module Dependency
3. select checkout library

<a name="installation_with_jitpack"></a>
### Installation with JitPack
---
[JitPack](https://jitpack.io/) is a novel package repository for JVM and Android projects. It builds Git projects on demand and provides you with ready-to-use artifacts (jar, aar).

To integrate goSellSDK into your project add it in your **root** `build.gradle` at the end of repositories:
```kotlin
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
                        jcenter()

		}
	}
```
Step 2. Add the dependency
```kotlin
	dependencies {
	        implementation 'com.github.Tap-Payments:card-mawarid-Android:1.0.1'
	}
```

Step 3. Add in gradle.properties
```
android.useAndroidX=true
android.enableJetifier=true
```
### [](https://github.com/Tap-Payments/card-mawarid-Android#DataConfig)Data Configuration

You will need to configure the Tap Card KIT before using it, by providing your data. This will enable the card kit to load your merchant's data and ready to process card related operations (e.g. tokenization, authorization and charge.)

It is a must this data is passed before displaying the card kit UI component.


Configuration code:

```kotlin
// Create the data configuration model with below fields
/****
 * @param secretKeys  pass your SDK keys, which you get upon integrating with TAP.
 * @param packageId to be passed in DataConfig
 * @param merchantId to be passed in DataConfig(Optional)
 * @param sdkMode pass the needed sdk mode (sandbox or production). Optional, default is sandbox
 */
val dataConfiguration = TapCardDataConfiguration("sk_test_kovrMB0mupFJXfNZWx6Etg5y","company.tap.goSellSDKExample" ,null, SdkMode.SAND_BOX)
```
### [](https://github.com/Tap-Payments/card-mawarid-Android-Android#SLC)Single line initialization

- Drag and drop the `TapCardInputView` from the XML into your UI as follows:
```kotlin
 <company.tap.tapcardsdk.open.CardInputForm
android:id="@+id/cardInputForm"
android:layout_width="match_parent"
android:layout_height="wrap_content"
/>

```
- Connect `CardInputForm` as follows:

// Initialize the CardForm

```kotlin
dataConfiguration.addCardInputDelegate(this) //** Required ** Add Delegate
dataConfiguration.initCardForm(this, dataConfig,cardInputForm)
```
## [](https://github.com/Tap-Payments/card-mawarid-Android-Androidt#custom-theme)Custom Theme

We made sure that as an UI element, you can customize the look and feel of it to match your own app's UX. The `theme` is represented a `JSON` data used to render the elements in light and dark modes. If not passed, the default tap theme will be used.

**Custom local theme**

You can provide `CardInputForm` UI element with a custom local theme. The local theme will be in the form of a JSON file.

```kotlin

/**
    Handles  the card forum engine with the required theme on demand. It takes below
    - Parameter resources: The local resources required to handle theme //Optional
    - Parameter urlString: The urlString to load theme from URL //Optional
    - Parameter urlPathLocal: The local resources path required to handle theme
    - Parameter fileName: The fileName to identify theme
     */
dataConfiguration.setTheme(context: Context?, resources: Resources?, urlString:String?, urlPathLocal: Int?, fileName:String?)
```

Things to note:

- Make sure the file is in your project directory.

- Light mode is mandatory.

- Dark mode is optional, if not passed then light mode will be used in both display modes.



### [](https://github.com/Tap-Payments/card-mawarid-Android#custom-localisation)Custom Localisation

We made sure that as an UI element, you can customize the look and feel of it to match your own app's UX. The `localisation` is represented as a `JSON` data used to localise the elements in different locales. If not passed, the default tap localisation will be used.

**Custom local & remote localisation**

You can provide `CardInputForm` UI element with a custom local localisation. The local localisation will be in the form of a JSON file.


Apply local/remote localisation:

```kotlin
    /**
    Handles  the card forum engine with the required locale on demand. It takes below
- Parameter resources: The local resources required to handle locale //Optional
- Parameter languageString: The languageString to load locale as user choice URL //Optional
- Parameter urlPathLocal: The local resources path required to handle locale
-  Parameter urlString:  The urlString to load locale from URL //Optional
 */
dataConfiguration.setLocale (context: Context ,languageString: String,urlString: String?,resources: Resources?,urlPathLocal: Int?)
```

Things to note::

- Make sure the file is in your project directory.

- You can put in any locale you need by just adding an entry for the locale as it shows for AR & EN in [here](https://firebasestorage.googleapis.com/v0/b/tapcardcheckout.appspot.com/o/CustomLocalisation.json?alt=media&token=6ef4023a-7082-47f3-86da-e47731abf098)

### [](https://github.com/Tap-Payments/card-mawarid-Android#tapCardInputDelegate)TapCardInputDelegate

**TapCardInputDelegate** is an interface which you may want to implement to receive cardformready/started/error/card saving status updates and update your user interface accordingly when payment window closes.
Below are listed down all available callbacks:

<a name="card_success_callback"></a>
### Card Success Callback

Notifies the receiver that payment has succeed.

#### Declaration
*Kotlin:*
```kotlin
- fun cardTokenizedSuccessfully(token: Token , saveCardEnabled:Boolean)
```
#### Arguments

**token**: Successful token object.

<a name="card_failure_callback"></a>
### Card Failure Callback

Notifies the receiver that payment has failed.
#### Declaration

*Kotlin:*
```kotlin
- fun cardTokenizedFailed(goSellError: GoSellError?)
```

#### Arguments

**goSellError**: goSellError object that has failed.

<a name="backendunknown_failure_callback"></a>
### Backend Unknown Error Callback

Notifies the receiver that something wrong at Tap.
#### Declaration

*Kotlin:*
```kotlin
- fun backendUnknownError(message: String?)
```

#### Arguments

**message**: message string that has failed.


<a name="cardform_isready_callback"></a>
### CardForm IS Ready Callback

Notifies the receiver that card form is ready to use.
#### Declaration

*Kotlin:*
```kotlin
- fun cardFormIsReady()
```

<a name="cardform_checkbox_status_callback"></a>
### SaveCard CheckBoxStatus  Callback

Notifies the receiver if user checked/unchecked to save the card.
#### Declaration

*Kotlin:*
```kotlin
- fun saveCardCheckBoxStatus(saveCardEnabled: Boolean)
```
#### Arguments

**saveCardEnabled**: returns boolean of user selection.

<a name="cardform_checkbox_status_callback"></a>
### SaveCard CheckBoxStatus  Callback

Notifies the receiver if user checked/unchecked to save the card.
#### Declaration

*Kotlin:*
```kotlin
- fun saveCardCheckBoxStatus(saveCardEnabled: Boolean)
```
#### Arguments

**saveCardEnabled**: returns boolean of user selection.

**saveCardEnabled**: returns boolean of user selection.

<a name="cardform_card_data_validation_callback"></a>
### Card Data Validation  Callback

Notifies the receiver if card form is valid and can proceed with further actions.
#### Declaration

*Kotlin:*
```kotlin
- fun cardDataValidation(cardValidation: CardValidation)
```
#### Arguments

**cardValidation**: returns cardValidation status .

### [](https://github.com/Tap-Payments/card-mawarid-Android#tokenization)Tokenization

**Tokenization** Tokenize straightaway as below, once the cardForm data is valid

*Kotlin*
```kotlin
 dataConfiguration.startTokenize(cardInputForm, context)
```
