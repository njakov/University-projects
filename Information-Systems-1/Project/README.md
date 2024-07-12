## Project Description
This project develops an HTTP API for a video streaming platform using JAX-RS.
The system includes a user application, a central server, and three subsystems. 
The client application, constructed with Retrofit, creates REST requests, sends them to the central server, and retrieves the results of these requests. 
The central server processes the REST requests and forwards them to the subsystems using JMS (Java Message Service).
Each subsystem operates with its own independent MySQL database.

For more details, please refer to the [project specifications.](Information-Systems-1/Project/13E114IS1_domaci_23_24.pdf)

