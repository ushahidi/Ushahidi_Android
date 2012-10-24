# Ushahidi Android app #

Ushahidi is an open source platform for democratizing information, increasing transparency and lowering the barriers for individuals to share their stories. 

The Ushahidi Android app synchronizes with any Ushahidi deployment allowing viewing and creation of incident reports on the go. 

The app supports loading of multiple deployments at one time, quick filtering through reports, exploring incident locations on the map, viewing report photos, news article, media as well as sharing reports via email, SMS or Twitter. Once the data has been downloaded, the app can function without an internet connection, allowing accurate collection of data utilizing the device's camera and GPS capabilities.

For more information visit:

* [About Ushahidi](http://www.ushahidi.com)
* [Issue Tracker](https://github.com/ushahidi/Ushahidi_Android/issues)
* [API Documentation For Ushahidi](https://wiki.ushahidi.com/display/WIKI/REST+API)
* [Code Repository](http://github.com/ushahidi/Ushahidi_Android)

### How To White-Label The App ###

Duplicate the **/Themes/Ushahidi** folder and rename with your theme name, for example **/Themes/MyTheme**. Note, you can use **/Beijing** or **/Inherity** themes as an example.
  
### Application Icon ###

Replace the files **"/Themes/MyTheme/res/drawable-hdpi/icon.png" (72 x 72), "/Themes/MyTheme/res/drawable-ldpi/icon.png" (36px x 36px) and "/Themes/MyTheme/res/drawable-mdpi/icon.png"(48px x 48px)** with your custom designed application icon. 

### Splash Screen ###

Replace the file **"/Themes/MyTheme/res/drawable/splash.png"** with your custom designed splash image. It should be of the size 320px x 480px

### Dashboard Background ###

Replace the file **"/Themes/MyTheme/res/drawable/dashboard.png" (320px x 480px)** with your custom dashboard background image. 

### About Background ###

Replace the file **"/Themes/MyTheme/res/drawable/about.png" (320px x 440px)** with your custom about background image.

### Deployment Logo ###

Replace the file **"/Themes/MyTheme/res/drawable/logo.png" (260px x 68px)** with your deployment logo.

### Notification Image ###

Replace the file **"/Themes/MyTheme/res/drawable/notification.png" (21px x 21px)** with your custom notification image.

### Report Placeholder ###

Replace the file **"/Themes/MyTheme/res/drawable/report.png" (50px x 50px)** with your placeholder if report images

### Color Codes ###

In the **"/Themes/MyTheme/res/values/theme.xml"**, replace the various hexadecimal color codes with your own colors that match your deployment, for example <color name="table_odd_row_color">#edd5c9</color> 

### Deployment Information ###

In the **"/Themes/MyTheme/res/values/theme.xml"**, specify the 'app_title' and also various URLs for your deployment: 'deployment_url', 'team_url', 'media_url', 'twitter_url', 'facebook_url' and 'contact_url'. Note, if you do not have a Facebook or Twitter account, leaving these values blank will hide these About screen buttons.

### Deployment Text ###
 
In the **"/Themes/MyTheme/res/values/theme.xml"**, update the 'about_text' with a description of your deployment.

### How To Build The App ###

To build any of the white-labled app, read the **BUILDING.txt** file in their 
respective directory. It has all the information you need to successfully 
build any of them. 
