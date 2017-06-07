# Android Courier Application (Firebase Auth/Database/Cloud Functions)
Source code for **Firebase in Android** series on [doksaprosta](http://doksaprosta.wordpress.com) blog.
It is also a **simple** courier application for Android phones.
## Features
1. Sign up and sign in activity with code for utilizing [Firebase Authentication](https://firebase.google.com/docs/auth/) (E-mail/Password)
2. [Firebase Realtime Database](https://firebase.google.com/docs/database/) usage for tracking users account details and orders.
3. [Firebase Notifications](https://firebase.google.com/docs/cloud-messaging/) for informing couriers about new orders and clients about delivery status ([Firebase Cloud Functions](https://firebase.google.com/docs/functions/)).
4. Simple e-mail sending function for finished orders (to a defined e-mail address).
5. Users can place orders (Google Places and Maps API).
6. Users can track their current orders.
7. Couriers can change the status of the order they deliver.
8. Users can edit their account informations.
9. Users can lookup their previous orders.
10. There are also simple About and Pricelist Fragments.

<div><p align="center"><img src="https://github.com/MiWy/CourierApplication/blob/master/capp.png" alt="navbar" width="306" height="500">
<img src="https://github.com/MiWy/CourierApplication/blob/master/capp2.png" alt="ordersubmit" width="306" height="500"></p></div>

## Installation
After importing/copying files you need to connect to your [Firebase project](https://firebase.google.com/docs/android/setup) and add [Google API for Maps/Places](https://developers.google.com/places/) (it is referenced in source code as 'google_maps_key' string).
If planning on using this project make sure to change 'PriceList.java' class, since it has really raw implementation.
## Usage
Source code is described in detail on [doksaprosta](http://doksaprosta.wordpress.com) blog. If there's a part that is not currently covered, send me a message or an [email](mailto:tryoutsapps@gmail.com).
## Disclaimer
The code was written mainly in MVP pattern though probably not in the perfect way (first time using any sort of architectural pattern :)). 

The condition I was trying to satisfy was to isolate Activity/Fragment code from data manipulation and for Presenters to be left without any Android imports.

Code's quality surely could be better. Even when I'm writing this Readme I can see dozen of places where I could code differently. 

### What isn't there
Exceptions aren't handled properly in several cases, activities and fragments' lifecycles aren't always properly taken care of, margins and other UI things were pretty much ignored, etc. If time will allow, I'll update the code. I was just trying to show that even without solid coding experience you can actually implement some sort of back-end to your Android applications.

## License
Feel free to do whatever it is you want with this code as long as license allows it (rights and limitations in [License](https://github.com/MiWy/CourierApplication/blob/master/License.txt) file).
Copyright 2017 Michał Wyrwa
Mozilla Public License 2.0.
