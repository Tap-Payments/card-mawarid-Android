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
		}
	}
```
Step 2. Add the dependency
```kotlin
	dependencies {
	        implementation 'com.github.Tap-Payments:card-mawarid-Android:Tag'
	}
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




