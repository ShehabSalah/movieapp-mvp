# movieapp-mvp
This version of the app is called movieapp-mvp, and provides a foundation for other samples in this project. The sample aims to:
- Provide a basic Model-View-Presenter (MVP) architecture without using any architectural frameworks.
- Act as a reference point for comparing and contrasting the other samples in this project.

## Screenshots
<img src="https://user-images.githubusercontent.com/16334887/34884731-51a45c7e-f7c6-11e7-9034-f867bc03bf30.png" width="250"/> <img src="https://user-images.githubusercontent.com/16334887/34884881-c8b36e36-f7c6-11e7-9045-e4a1a4a66c98.png" width="250"/> <img src="https://user-images.githubusercontent.com/16334887/34884920-edd43218-f7c6-11e7-9b2e-7c68566f74ee.png" width="250"/> <img src="https://user-images.githubusercontent.com/16334887/34884954-07e67bde-f7c7-11e7-9e92-8192407a93a8.png" width="250"/> <img src="https://user-images.githubusercontent.com/16334887/34884999-2febb6b2-f7c7-11e7-8949-7987b4181ce0.png" width="250"/> <img src="https://user-images.githubusercontent.com/16334887/34885031-480857c8-f7c7-11e7-85f2-1831977b8de5.png" width="250"/>

## Libraries
This version of the app uses some other libraries:
- Picasso: used for loading, processing, caching and displaying remote and local images.
- ButterKnife: used for perform injection on objects, views and OnClickListeners.
- CardView: used for representing the information in a card manner with a drop shadow and corner radius which looks consistent across the platform.
- RecyclerView: The RecyclerView widget is a more advanced and flexible version of ListView.
- GSON: Gson is a Java library that can be used to convert Java Objects into their JSON representation. It can also be used to convert a JSON string to an equivalent Java object.
- Retrofit: This library used to send HTTP request to the server and retrieve response.
- ROOM Library: Room provides an abstraction layer over SQLite to allow fluent database access while harnessing the full power of SQLite.
- BlurView Library: It blurs its underlying content and draws it as a background for its children.

# The Movie DB API Key is required.
In order for the movieapp-mvp app to function properly as of January 7th, 2018 an API key for themoviedb.org must be included with the build.

Include the unique key for the build by adding the following line to util/Constants.java or find the TODO Line.

<code>
API_KEY = "";
</code>
<br/>
<br/>

## License
```
Copyright (C) 2018 Shehab Salah

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
  
  http://www.apache.org/licenses/LICENSE-2.0
  
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
