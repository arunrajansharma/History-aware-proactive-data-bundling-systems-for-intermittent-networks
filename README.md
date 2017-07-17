# History-aware-proactive-data-bundling-systems-for-intermittent-networks
 
Components :
1. Node js server + Python Script
2. Node js client for http/ws performance testing
3. A simple android app

This project is largely inspired from a IEEE SECON 2006 paper with the similar title that tries to address the issue of handling poor connectivity in mobile devices. The intermittent connectivity, which results from this lack of end-to-end connection, is a dominant problem that leads to user frustration. In this project we have implemented a user browsing history aware system that is able to fulfill user request even in the presence of a poor network


The system we have developed stores the pages that it deems most important to the user and quickly pushes them to the user when the user has connectivity. To speedup the process of transferring data, we used Web sockets. We observed very impressive speedup using Web Sockets as compared to the standard HTTP. We explain more about this later in the report. The complete system consists of a server that is deployed on a public cloud and a mobile client which is an Android app. There can be multiple instances of the mobile client. We also developed a simple JavaScript client for testing purposes and to compare Web Sockets with HTTP (it was difficult to do these things in Android). The system supports multiple users at a time and deploys several smart features on the server.

Since the android app was initially proving difficult to build owing to our lack of experience with android, for the purpose of testing we also wrote a simple client in node.js. Once we built the android app, the server code was stable, so testing it was easier.
First we wanted to find out if using Web Sockets was the right thing to do. So, in our makeshift node.js client we add code to use either HTTP or Web Sockets. We also made a change at the server so that it can accept HTTP requests. The main thing we wanted to evaluate was how fast the server is able to push data to the client when a client reconnects. We need not worry too much about when the client connects for the first time and sends its history. This will happen only once, and the user will obviously need to do it when he has a decent connection. So for our test, we assume that the client has already sent its history, has then lost its connection and is now reconnecting.
We start counting the time just before the client sends a connect request and stop counting when the client receives all the data. When using WebSockets, the data is pushed directly by the server. When using HTTP, the client sends a GET request to the server. The server then sends the data in response. We varied the size of the zip file that the server sends to the client.



Team:
> Arun Rajan
> Farhaan Jalia
> Amit Gupta

