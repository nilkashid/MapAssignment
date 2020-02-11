# MapAssignment
### Prerequisites
As this is public repo I have remove certain keys and files like google map sdk API keys and google-services.json
To run this project you need to add Map SDK key in google_map_api.xml and need to add google-services.json.
Follow this article to do so https://developers.google.com/maps/documentation/android-sdk/get-api-key

### Component Used / Architecture followed
Google map sdk, Places api, map routing apis, firebase realtime databse, java client for routing apis(provided by google).

Followed MVVM architecture.
Created map.module as separate package so that in future we can have it as a separate module.
Also it will be easily replaceable. Main app dependancy on this module is minimal. Open interface to access map module is MapProviderImpl

### Todos / Enhancement
Even Java client is working fine with Andorid application it is not recommended to use it. Better approach will
set up own server as a proxy server in between google API and client API.

Factory pattern can be implement in between actual app and map provider so that it will more loosely coupled

Currently not using any user base data saving mechanism so previous location data is overriding previous data.



